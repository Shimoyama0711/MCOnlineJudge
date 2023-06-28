import java.io.*;
import java.nio.charset.Charset;
import java.util.Random;
import java.util.UUID;

public class Judge {
    public static void main(String[] args) throws IOException {
        String problem = "tutorial/1";
        String body = """
                import java.util.Scanner;
                
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


        // String uuidString = randomUUID();
        String uuidString = "Test";

        // File judgeFile = new File("./servlet/" + uuidString + ".java");

        saveFile(uuidString, body);
        compile(uuidString);
        judge(problem, uuidString);
    }

    /**
     * @param uuid ハイフンなしのUUID形式。
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
     * @param uuid コンパイルするJavaファイル。
     */
    public static void compile(String uuid) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("javac", "./servlet/" + uuid + ".java");
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("MS932")))) {
            String line;

            while ((line = reader.readLine()) != null)
                System.out.println(line);
        }
    }

    public static boolean judge(String problem, String uuid) throws IOException {
        File dir = new File("./servlet/testCase/" + problem); // 問題に対応するファイルディレクトリ
        File[] testCases = dir.listFiles(); // ディレクトリの中のファイル一覧

        assert testCases != null;

        for (File testCase : testCases) {
            String name = testCase.getName();

            // 拡張子を除いて in と out を調査 //

            if (!name.contains("out")) {
                int lastDot = name.lastIndexOf(".");
                String sub = name.substring(0, lastDot);

                ProcessBuilder pb = new ProcessBuilder("java", uuid);

                pb.directory(new File("./servlet"));
                File in = new File("./servlet/testCase/" + problem + "/" + sub + ".txt");
                pb.redirectInput(in);

                Process process = pb.start();

                // OutputStream stdin = process.getOutputStream();
                // PrintWriter writer = new PrintWriter(stdin);
                // String line = txt.readLine();

                /*
                while (line != null) {
                    writer.write(line);
                    System.out.println("txt: " + line);
                    line = txt.readLine();
                }
                 */

                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream(), Charset.forName("MS932")));
                String line = reader.readLine();

                while (line != null) {
                    System.out.println(line);
                    line = reader.readLine();
                }
            }
        }

        return true;
    }

    /**
     * @return ハイフン抜きでかつ1文字目が 'a'～'f' のUUIDが返されます。
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
}
