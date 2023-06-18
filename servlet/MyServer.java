import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

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
            System.out.println();
            System.out.println(new String(b, StandardCharsets.UTF_8));
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
}
