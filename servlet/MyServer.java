import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class MyServer {
    public static void main(String[] args) throws IOException {
        int port = 8080; // ポート番号

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new MyHandler());
        System.out.println("WebServer wakes up. Port: " + port);
        server.start();
    }
}

class MyHandler implements HttpHandler {
    public void handle(HttpExchange t) throws IOException {
        System.out.println("*************************");

        String startLine = t.getRequestMethod() + " " +
                t.getRequestURI().toString() + " " +
                t.getProtocol();
        System.out.println(startLine);

        Headers reqHeaders = t.getResponseHeaders();
        for (String name : reqHeaders.keySet()) {
            System.out.println(name + ": " + reqHeaders.getFirst(name));
        }

        InputStream is = t.getRequestBody();
        byte[] b = is.readAllBytes();
        is.close();

        if (b.length != 0) {
            String json = new String(b, StandardCharsets.UTF_8);

            System.out.println();
            System.out.println(json);

            insertSourceDatabase(json);
        }

        String resBody = switch (t.getRequestURI().toString()) {
            case "/hello" -> "{\"message\": \"Hello, World!\"}";
            case "/foobar" -> """
                    {
                        "foo": "bar",
                        "hogehoge": 114514
                    }
                    """;
            default -> "{\"message\": \"200 OK\"}";
        };

        // Response //

        Headers resHeaders = t.getResponseHeaders();
        resHeaders.set("Content-Type", "application/json");
        resHeaders.set("Last-Modified", ZonedDateTime.now(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME));
        resHeaders.set("Server",
                "MyServer (" +
                        System.getProperty("java.vm.name") + " " +
                        System.getProperty("java.vm.vendor") + " " +
                        System.getProperty("java.vm.version") + ")"
        );

        int statusCode = 200;
        long contentLength = resBody.getBytes(StandardCharsets.UTF_8).length;
        t.sendResponseHeaders(statusCode, contentLength);

        OutputStream os = t.getResponseBody();
        os.write(resBody.getBytes());
        os.close();
    }

    public static void insertSourceDatabase(String json) {
        ObjectMapper mapper = new ObjectMapper();
        try {
            JsonNode node = mapper.readValue(json, JsonNode.class);
            String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
            SimpleDateFormat sdf = new SimpleDateFormat(pattern);

            String uuid = node.get("uuid").asText();
            String problem = node.get("problem").asText();
            Date date = sdf.parse(node.get("date").asText());
            java.sql.Date sqlDate = new java.sql.Date(date.getTime());
            String body = node.get("body").asText();

            String sqlURL = "jdbc:mysql://localhost:3306/mconlinejudge";
            String USER = "root";
            String PASS = "BTcfrLkK1FFU";
            String SQL = "INSERT INTO sources (uuid, problem, date, body) VALUES (?, ?, ?, ?)";

            Connection conn = DriverManager.getConnection(sqlURL, USER, PASS);
            conn.setAutoCommit(true);
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, uuid);
            ps.setString(2, problem);
            ps.setDate(3, sqlDate);
            ps.setString(4, body);

            // System.out.println(ps);

            ps.executeUpdate();
            conn.close();
        } catch (JsonProcessingException | ParseException | SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}