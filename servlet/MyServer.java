import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
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
import java.util.Random;
import java.util.UUID;

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

            if (t.getRequestURI().toString().equals("/judge")) {
                ObjectMapper mapper = new ObjectMapper();

                try {
                    JsonNode node = mapper.readValue(json, JsonNode.class);
                    String pattern = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'";
                    SimpleDateFormat sdf = new SimpleDateFormat(pattern);

                    String uuid = node.get("uuid").asText();
                    String judgeId = randomUUID();
                    String problem = node.get("problem").asText();
                    Date date = sdf.parse(node.get("date").asText());
                    String body = node.get("body").asText();

                    // ソース一覧のデータベースに追加 //
                    insertSourceDatabase(uuid, problem, judgeId, date.toString(), body, "Judge...");

                    // ファイルを保存 //
                    saveFile(judgeId, body);

                    boolean flagCompile = compile(judgeId);

                    // コンパイルに成功したか //
                    if (flagCompile) {
                        // ジャッジに成功したか //
                        if (judge(problem, judgeId)) {
                            updateStatus(judgeId, "AC");
                        } else {
                            updateStatus(judgeId, "WA");
                        }
                    } else {
                        updateStatus(judgeId, "CE");
                    }

                    // 一時ファイルを削除 //
                    deleteFile(judgeId, flagCompile);
                } catch (ParseException e) {
                    System.out.println(e.getMessage());
                }
            }
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

    /**
     * sources テーブルに挿入します
     * @param uuid ユーザーのuuid
     * @param problem 問題のID
     * @param date 日付の文字列
     * @param body 本文
     * @param status 状態
     */
    public static void insertSourceDatabase(String uuid, String problem, String judgeId, String date, String body, String status) {
        try {
            String sqlURL = "jdbc:mysql://localhost:3306/mconlinejudge";
            String USER = "root";
            String PASS = "BTcfrLkK1FFU";
            String SQL = "INSERT INTO sources (uuid, problem, judge_id, date, body, status) VALUES (?, ?, ?, ?, ?, ?)";

            Connection conn = DriverManager.getConnection(sqlURL, USER, PASS);
            conn.setAutoCommit(true);
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, uuid);
            ps.setString(2, problem);
            ps.setString(3, judgeId);
            ps.setString(4, date);
            ps.setString(5, body);
            ps.setString(6, status);

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * @param uuid ハイフンなしのUUID形式
     * @param body 本文。
     */
    public static void saveFile(String uuid, String body) throws IOException {
        File judgeFile = new File("./servlet/" + uuid + ".java");
        body = body.replace("class Main", "class " + uuid);

        BufferedWriter bw = new BufferedWriter(new FileWriter(judgeFile));
        bw.write(body);
        bw.flush();
        bw.close();
    }

    /**
     * @param uuid ハイフンなしのUUID形式
     */
    public static void deleteFile(String uuid, boolean compileSuccess) {
        File javaFile = new File("./servlet/" + uuid + ".java");
        File classFile = new File("./servlet/" + uuid + ".class");

        if (!javaFile.delete())
            System.out.println(javaFile.getName() + " の削除に失敗しました");

        if (compileSuccess)
            if (!classFile.delete())
                System.out.println(classFile.getName() + " の削除に失敗しました");
    }

    /**
     * @param uuid コンパイルするJavaファイル名
     */
    public static boolean compile(String uuid) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("javac", "./servlet/" + uuid + ".java");
        Process process = pb.start();

        boolean flag = true;

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("MS932")))) {
            String line = reader.readLine();

            while (line != null) {
                System.out.println(line);
                line = reader.readLine();
            }
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getErrorStream(), Charset.forName("MS932")))) {
            String line = reader.readLine();

            while (line != null) {
                if (line.length() > 0)
                    flag = false;
                line = reader.readLine();
            }
        }

        return flag;
    }

    /**
     * @param problem 問題ID
     * @param uuid 保存したJavaファイルのuuid
     * @return 問題に正解したかどうか
     * @throws IOException ファイルの入出力エラー時に例外を発生させます
     */
    public static boolean judge(String problem, String uuid) throws IOException {
        boolean flag = true;

        File dir = new File("./servlet/testCase/" + problem); // 問題に対応するファイルディレクトリ
        File[] testCases = dir.listFiles(); // ディレクトリの中のファイル一覧

        assert testCases != null;

        for (File testCase : testCases) {
            String name = testCase.getName();

            // 拡張子を除いて in と out を調査 //

            if (!name.contains("_out")) {
                int lastDot = name.lastIndexOf(".");
                String sub = name.substring(0, lastDot); // 拡張子を除いたファイル名

                ProcessBuilder pb = new ProcessBuilder("java", uuid);

                pb.directory(new File("./servlet"));
                File in = new File("./servlet/testCase/" + problem + "/" + sub + ".txt");
                pb.redirectInput(in);

                Process process = pb.start();

                File out = new File("./servlet/testCase/" + problem + "/" + sub + "_out.txt");
                BufferedReader outReader = new BufferedReader(new FileReader(out));
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("MS932")));

                // 既に false ならば無視
                if (flag)
                    flag = compare2BR(outReader, reader);
            }
        }

        return flag;
    }

    /**
     * @return ハイフン抜きでかつ1文字目が 'a'～'f' のUUIDが返されます
     */
    public static String randomUUID() {
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString().replace("-", "");
        StringBuilder sb = new StringBuilder(uuidString);
        char c = sb.charAt(0);

        if (Character.isDigit(c)) {
            Random r = new Random();
            sb.setCharAt(0, (char) ('a' + r.nextInt(5)));
        }

        return sb.toString();
    }

    /**
     * 2つの BufferedReader の内容が完全一致しているかどうか？
     * @param reader1 BufferedReader 1個目
     * @param reader2 BufferedReader 2個目
     * @return 等しいかどうか
     * @throws IOException 入出力エラー時に例外を発生します
     */
    public static boolean compare2BR(BufferedReader reader1, BufferedReader reader2) throws IOException {
        String line1 = reader1.readLine();
        String line2 = reader2.readLine();

        while (line1 != null && line2 != null) {
            if (!line1.equals(line2))
                return false;

            line1 = reader1.readLine();
            line2 = reader2.readLine();
        }

        // どちらかのReaderが終端に達していない場合は一致しないと判断する
        return line1 == null && line2 == null;
    }

    /**
     * sources テーブルの status を更新する
     * @param judgeId ジャッジのID
     * @param status 状態
     */
    public static void updateStatus(String judgeId, String status) {
        try {
            String sqlURL = "jdbc:mysql://localhost:3306/mconlinejudge";
            String USER = "root";
            String PASS = "BTcfrLkK1FFU";
            String SQL = "UPDATE sources SET status = ? WHERE judge_id = ?";

            Connection conn = DriverManager.getConnection(sqlURL, USER, PASS);
            conn.setAutoCommit(true);
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, status);
            ps.setString(2, judgeId);

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}