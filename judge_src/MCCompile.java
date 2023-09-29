import java.io.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.UUID;

public class MCCompile {
    public static void main(String[] args) throws IOException {
        String judgeId = "Temporary";
        // String judgeId = randomUUID();
        // File mainFile = new File("./src/" + judgeId + "/" + judgeId + ".java");

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
            } else {
                System.out.println("MANIFEST.MF の作成に失敗しました");
            }
        }
    }

    /**
     * コンパイル
     * @param judgeId コンパイルするJavaファイル名（ジャッジのUUID）
     */
    public static boolean compileMC(String judgeId) throws IOException {
        File dir = new File("./" + judgeId);

        ProcessBuilder pb = new ProcessBuilder("javac", "-d", "./", "-cp", "./lib/spigot-api-1.20.1-R0.1-SNAPSHOT.jar", "./src/" + judgeId + "/" + judgeId + ".java");
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
        File dir = new File("./src/" + judgeId + "/META-INF");

        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("META-INF フォルダの作成に失敗しました");
                return false;
            }
        }

        File manifestFile = new File("./src/" + judgeId + "/META-INF/MANIFEST.MF");

        if (!manifestFile.exists()) {
            if (!manifestFile.createNewFile()) {
                System.out.println("MANIFEST.MF の作成に失敗しました");
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
        ProcessBuilder pb = new ProcessBuilder("jar", "cvfm", judgeId + ".jar", "./src/" + judgeId + "/META-INF/MANIFEST.MF", "./" + judgeId + "/" + judgeId + ".class", "./plugin.yml");
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

        return flag;
    }

    /**
     * [judgeId].jar をサーバーの plugin フォルダーにコピーします
     * @param judgeId プレイヤーのUUID
     * @return コピーに成功したかどうか？
     */
    public static boolean copyJarFile(String judgeId) {
        Path from = Paths.get(judgeId + ".jar");
        // Path to = Paths.get("C:/Users/ayumu/Desktop/Development/Server_1.20.1/plugins/" + judgeId + ".jar");
        Path to = Paths.get("C:/Users/ayumu/Desktop/Development/MinecraftServer/[1.20.2]MCOnlineJudge/plugins/" + judgeId + ".jar");

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
