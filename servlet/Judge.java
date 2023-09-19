import java.io.*;
import java.nio.charset.Charset;
import java.sql.*;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

public class Judge {
    public static void main(String[] args) throws IOException {
        String problem = "tutorial/1";
        String body = """
                // import java.util.Scanner;
                
                public class Main {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        
                        String S = sc.next();
                        int a = sc.nextInt();
                        int b = sc.nextInt();
                        
                        System.out.println(S);
                        System.out.println(a + b);
                    }
                }
                """;

        Date date = new Date();

        // String judgeUUID = "TestCompile";
        String userUUID = "31528c4f241e4f5c805eb7cf482c95ad";
        String judgeUUID = randomUUID();

        // File judgeFile = new File("./servlet/" + judgeUUID + ".java");

        insertSourceDatabase(userUUID, problem, judgeUUID, date.toString(), body, "Judge...");
        saveFile(judgeUUID, body);

        boolean flagCompile = compile(judgeUUID);

        // コンパイルに成功したか //
        if (flagCompile) {
            // ジャッジに成功したか //
            if (judge(problem, judgeUUID)) {
                updateStatus(judgeUUID, "AC");
            } else {
                updateStatus(judgeUUID, "WA");
            }
        } else {
            updateStatus(judgeUUID, "CE");
        }
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
        body = body.replace("public class Main", "public class " + uuid);

        BufferedWriter bw = new BufferedWriter(new FileWriter(judgeFile));
        bw.write(body);
        bw.flush();
        bw.close();
    }

    /**
     * @param judgeId ジャッジID
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
