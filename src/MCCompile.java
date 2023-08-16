import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

public class MCCompile {
    public static void main(String[] args) throws IOException {
        String judgeId = "Main";
        // String judgeId = randomUUID();
        File mainFile = new File("./src/Main.java");

        boolean compileFlag = compile(judgeId);

        if (compileFlag) {
            boolean manifestFlag = createManifestFile(judgeId);

            if (manifestFlag) {
                boolean buildFlag = buildJarFile(judgeId);

                if (buildFlag) {
                    boolean copyFlag = copyJarFile(judgeId);

                    if (copyFlag) {
                        boolean deleteFlag = deleteJarFile(judgeId);

                        // return;
                    }
                }
            } else {
                System.out.println("MANIFEST.MF の作成に失敗！");
            }
        }
    }

    /**
     * コンパイル
     * @param judgeId コンパイルするJavaファイル名（ジャッジのUUID）
     */
    public static boolean compile(String judgeId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("javac", "-cp", "./lib/spigot-api-1.20.1-R0.1-SNAPSHOT.jar", "./src/" + judgeId + ".java");
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
     * マニフェストファイルの作成
     * @param judgeId ジャッジのUUIDが含まれたJavaファイル名
     */
    public static boolean createManifestFile(String judgeId) throws IOException {
        File dir = new File("./src/META-INF");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("META-INF フォルダの作成に失敗！");
                return false;
            }
        }

        File manifestFile = new File("./src/META-INF/MANIFEST.MF");

        if (!manifestFile.exists()) {
            if (!manifestFile.createNewFile()) {
                System.out.println("MANIFEST.MF の作成に失敗！");
                return false;
            }
        }

        BufferedWriter writer = new BufferedWriter(new FileWriter(manifestFile));
        writer.write("Main-Class: " + judgeId + ".class\n");
        writer.close();

        return true;
    }

    /**
     * jar ファイルを作成
     * @param judgeId ジャッジのUUID
     * @return ビルドに成功したかどうか？
     */
    public static boolean buildJarFile(String judgeId) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("jar", "cvfm", judgeId + ".jar", "./src/META-INF/MANIFEST.MF", "./src/" + judgeId + ".class", "./src/*.yml");
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
     * [judgeId].jar をサーバーの plugin フォルダーにコピーします
     * @param judgeId ジャッジのUUID
     * @return コピーに成功したかどうか？
     */
    public static boolean copyJarFile(String judgeId) {
        Path from = Paths.get(judgeId + ".jar");
        // Path to = Paths.get("C:/Users/ayumu/Desktop/Development/Server_1.20.1/plugins/" + judgeId + ".jar");
        Path to = Paths.get("C:/Users/ayumu/Desktop/Development/Server_1.20.1/plugins/TestPlugin.jar");

        try {
            Files.copy(from, to);
        } catch (IOException e) {
            e.printStackTrace();
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
        File jarFile = new File(judgeId + ".jar");

        if (!jarFile.delete()) {
            System.out.println(judgeId + ".jar の削除に失敗しました");
            return false;
        }

        return true;
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
}
