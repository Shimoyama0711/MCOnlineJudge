import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.*;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
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

                JsonNode node = mapper.readValue(json, JsonNode.class);

                String uuid = node.get("uuid").asText();
                String judgeId = randomUUID();
                String problem = node.get("problem").asText();
                String date = node.get("date").asText();
                String body = node.get("body").asText();

                // ソース一覧のデータベースに追加 //
                insertSourceDatabase(uuid, problem, judgeId, date, body, "Judge...");

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
            }

            if (t.getRequestURI().toString().equals("/mc-judge")) {
                ObjectMapper mapper = new ObjectMapper();

                JsonNode node = mapper.readValue(json, JsonNode.class);

                String uuid = node.get("uuid").asText();
                String judgeId = randomUUID();
                String problem = node.get("problem").asText();
                String date = node.get("date").asText();
                String body = node.get("body").asText();

                // ソース一覧のデータベースに追加 //
                insertSourceDatabase(uuid, problem, judgeId, date, body, "Judge...");

                // ファイルを保存 //
                saveMCFile(judgeId, body);

                boolean compileFlag = compileMC(judgeId);

                if (compileFlag) {
                    boolean manifestFlag = createManifestFile(judgeId);

                    if (manifestFlag) {
                        boolean yamlFlag = createYAMLFile(judgeId);

                        if (yamlFlag) {
                            boolean buildFlag = buildJarFile(judgeId);

                            if (buildFlag) {
                                boolean copyFlag = copyJarFile(judgeId);

                                if (copyFlag) {
                                    boolean deleteFlag = deleteJarFile(judgeId);
                                }
                            }
                        }
                    }
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
     * @param judgeId ジャッジID
     * @param date 日付の文字列
     * @param body 本文
     * @param status 状態
     */
    public static void insertSourceDatabase(String uuid, String problem, String judgeId, String date, String body, String status) {
        try {
            String sqlURL = "jdbc:mysql://localhost:3306/mconlinejudge";
            String USER = "root";
            String PASS = "BTcfrLkK1FFU";
            String SQL = "INSERT INTO mconlinejudge.sources (uuid, problem, judge_id, date, body, status, cases) VALUES (?, ?, ?, ?, ?, ?, ?)";

            Connection conn = DriverManager.getConnection(sqlURL, USER, PASS);
            conn.setAutoCommit(true);
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, uuid);
            ps.setString(2, problem);
            ps.setString(3, judgeId);
            ps.setString(4, date);
            ps.setString(5, body);
            ps.setString(6, status);
            ps.setString(7, "{}");

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    // NORMAL JUDGE FUNCTIONS //

    /**
     * @param judgeId ジャッジID。ハイフンなしのUUID形式
     * @param body 本文。
     */
    public static void saveFile(String judgeId, String body) throws IOException {
        File judgeFile = new File("./servlet/" + judgeId + ".java");
        body = body.replace("class Main", "class " + judgeId);

        BufferedWriter bw = new BufferedWriter(new FileWriter(judgeFile));
        bw.write(body);
        bw.flush();
        bw.close();
    }

    public static void saveMCFile(String judgeId, String body) throws IOException {
        File dir = new File("./src/" + judgeId);

        if (!dir.mkdirs()) {
            System.out.println("Failed to create directory: " + judgeId);
            return;
        }

        File judgeFile = new File(dir.getPath() + "/" + judgeId + ".java");
        body = body.replace("class Main", "class " + judgeId);

        BufferedWriter bw = new BufferedWriter(new FileWriter(judgeFile));
        bw.write(body);
        bw.flush();
        bw.close();
    }

    /**
     * @param judgeId ジャッジID。ハイフンなしのUUID形式
     */
    public static void deleteFile(String judgeId, boolean compileSuccess) {
        File javaFile = new File("./servlet/" + judgeId + ".java");
        File classFile = new File("./servlet/" + judgeId + ".class");

        if (!javaFile.delete())
            System.out.println(javaFile.getName() + " の削除に失敗しました");

        if (compileSuccess)
            if (!classFile.delete())
                System.out.println(classFile.getName() + " の削除に失敗しました");
    }

    /**
     * @param judgeId ジャッジID。ハイフンなしのUUID形式
     */
    public static boolean compile(String judgeId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("javac", "./servlet/" + judgeId + ".java");
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
     * @param judgeId 保存したJavaファイルのuuid
     * @return 問題に正解したかどうか
     * @throws IOException ファイルの入出力エラー時に例外を発生させます
     */
    public static boolean judge(String problem, String judgeId) throws IOException {
        boolean result = true;

        File dir = new File("./servlet/testCase/" + problem); // 問題に対応するファイルディレクトリ
        File[] testCases = dir.listFiles(); // ディレクトリの中のファイル一覧

        assert testCases != null;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        for (File testCase : testCases) {
            String name = testCase.getName();

            // 拡張子を除いて in と out を調査 //

            if (!name.contains("_out")) {
                int lastDot = name.lastIndexOf(".");
                String sub = name.substring(0, lastDot); // 拡張子を除いたファイル名

                ProcessBuilder pb = new ProcessBuilder("java", judgeId);

                pb.directory(new File("./servlet"));
                File in = new File("./servlet/testCase/" + problem + "/" + sub + ".txt");
                pb.redirectInput(in);

                Process process = pb.start();

                File out = new File("./servlet/testCase/" + problem + "/" + sub + "_out.txt");
                BufferedReader outReader = new BufferedReader(new FileReader(out));
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("MS932")));

                // 既に false ならば無視
                boolean flag = compare2BR(outReader, reader);

                if (flag) {
                    node.put(sub, "AC");
                } else {
                    result = false;
                    node.put(sub, "WA");
                }
            }
        }

        String json = mapper.writeValueAsString(node);
        updateCases(judgeId, json);

        return result;
    }

    /**
     * コンパイル
     * @param judgeId コンパイルするJavaファイル名（ジャッジのUUID）
     */
    public static boolean compileMC(String judgeId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("javac", "-d", "./" + judgeId, "-cp", "./lib/spigot-api-1.20.2-R0.1-SNAPSHOT.jar", "./src/" + judgeId + "/" + judgeId + ".java");
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

        if (!flag)
            System.out.println("コンパイルに失敗！");

        return flag;
    }


    /**
     * マニフェストファイルの作成
     * @param judgeId ジャッジのUUIDが含まれたJavaファイル名
     */
    public static boolean createManifestFile(String judgeId) throws IOException {
        File dir = new File("./src/" + judgeId + "/META-INF");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("META-INF フォルダの作成に失敗！");
                return false;
            }
        }

        File manifestFile = new File(dir.getPath() + "/MANIFEST.MF");

        if (!manifestFile.exists()) {
            if (!manifestFile.createNewFile()) {
                System.out.println("MANIFEST.MF の作成に失敗！");
                return false;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(manifestFile));
        writer.write("Main-Class: " + judgeId + "." + judgeId + ".class\n");
        writer.close();

        return true;
    }

    /**
     * プレイヤーのUUIDに対応したYAMLファイルを作成します
     * @param judgeId ジャッジID
     * @return YAMLファイルの作成に成功したか？
     */
    public static boolean createYAMLFile(String judgeId) throws IOException {
        String body =
                "name: " + judgeId + "\n" +
                        "main: " + judgeId + "." + judgeId + "\n" +
                        "version: 1.0\n" +
                        "api-version: 1.20\n" +
                        "commands:\n" +
                        "  " + judgeId + ":\n" +
                        "    usage: This is a MCOnlineJudge Plugin Command.";

        File yamlFile = new File("./plugin.yml");

        if (!yamlFile.exists()) {
            if (!yamlFile.createNewFile()) {
                System.out.println("YAMLファイルの作成に失敗しました");
                return false;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(yamlFile));
        writer.write(body);

        writer.flush();
        writer.close();

        return true;
    }

    /**
     * jarファイルをビルドします
     * @param judgeId ジャッジID
     * @return ビルドに成功したかどうか？
     */
    public static boolean buildJarFile(String judgeId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("jar", "cvfm", "./" + judgeId + ".jar", "./src/" + judgeId + "/META-INF/MANIFEST.MF", "./" + judgeId + "/" + judgeId + ".class", "./plugin.yml");

        System.out.println("jar cvfm ./" + judgeId + ".jar ./src/" + judgeId + "/META-INF/MANIFEST.MF ./" + judgeId + "/" + judgeId + ".class ./plugin.yml");

        // ProcessBuilder pb = new ProcessBuilder("jar", "cvfm", judgeId + ".jar", "./src/" + judgeId + "/" + judgeId + ".class", "./plugin.yml");
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

        if (!flag)
            System.out.println("ビルドに失敗！");

        return flag;
    }

    /**
     * [judgeId].jar をサーバーの plugin フォルダーにコピーします
     * @param judgeId プレイヤーのUUID
     * @return コピーに成功したかどうか？
     */
    public static boolean copyJarFile(String judgeId) {
        Path from = Paths.get("./"  + judgeId + ".jar");
        // Path to = Paths.get("C:/Users/ayumu/Desktop/Development/Server_1.20.1/plugins/" + judgeId + ".jar");
        Path to = Paths.get("C:/Users/ayumu/Desktop/Development/MinecraftServer/[1.20.2]MCOnlineJudge/plugins/" + judgeId + ".jar");

        try {
            Files.copy(from, to);
        } catch (IOException e) {
            e.printStackTrace();
            System.out.println(".jar ファイルのコピーに失敗！");
            return false;
        }

        return true;
    }

    /**
     * [judgeId].jar の元データを削除します
     * @param judgeId ジャッジのUUID
     * @return JAR ファイルの削除に成功したかどうか？
     */
    public static boolean deleteJarFile(String judgeId) {
        File jarFile = new File("./" + judgeId + ".jar");

        if (!jarFile.delete()) {
            System.out.println(judgeId + ".jar の削除に失敗しました");
            return false;
        }

        return true;
    }

    // MINECRAFT JUDGE FUNCTIONS //



    // OTHER FUNCTIONS //

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
            String SQL = "UPDATE mconlinejudge.sources SET status = ? WHERE judge_id = ?";

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

    /**
     * 対応するジャッジIDの cases 部分を変更します
     * @param judgeId ジャッジID。
     * @param json JSON形式で cases を変更
     */
    public static void updateCases(String judgeId, String json) {
        try {
            String sqlURL = "jdbc:mysql://localhost:3306/mconlinejudge";
            String USER = "root";
            String PASS = "BTcfrLkK1FFU";
            String SQL = "UPDATE mconlinejudge.sources SET cases = ? WHERE judge_id = ?";

            Connection conn = DriverManager.getConnection(sqlURL, USER, PASS);
            conn.setAutoCommit(true);
            PreparedStatement ps = conn.prepareStatement(SQL);
            ps.setString(1, json);
            ps.setString(2, judgeId);

            ps.executeUpdate();
            conn.close();
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }
}