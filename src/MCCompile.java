import java.io.*;
import java.nio.charset.Charset;
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

            if (!manifestFlag) {
                System.out.println("MANIFEST.MF の作成に失敗！");
            }
        }
    }

    /**
     * @param uuid コンパイルするJavaファイル名
     */
    public static boolean compile(String uuid) throws IOException {
        ProcessBuilder pb = new ProcessBuilder("javac", "-cp", "./lib/spigot-api-1.20.1-R0.1-SNAPSHOT.jar", "./src/" + uuid + ".java");
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
     * @param uuid プレイヤーのUUIDが含まれたJavaファイル名
     */
    public static boolean createManifestFile(String uuid) throws IOException {
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
        writer.write("Main-Class: " + uuid + ".class\n");
        writer.close();

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
