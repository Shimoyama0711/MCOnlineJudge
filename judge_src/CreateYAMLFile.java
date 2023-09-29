import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CreateYAMLFile {
    public static void main(String[] args) throws IOException {
        String uuid = "31528c4f241e4f5c805eb7cf482c95ad";
        String judgeId = "Temporary";

        System.out.println("Result => " + createYAMLFile(uuid, judgeId));
    }

    /**
     * プレイヤーのUUIDに対応したYAMLファイルを作成します
     * @param uuid プレイヤーのUUID
     * @return YAMLファイルの作成に成功したか？
     */
    public static boolean createYAMLFile(String uuid, String judgeId) throws IOException {
        String body =
                "name: " + uuid + "\n" +
                "main: Main\n" +
                "version: 1.0\n" +
                "api-version: 1.20\n" +
                "commands:\n" +
                "  " + uuid + ":\n" +
                "    usage: This is a MCOnlineJudge Plugin Command.";

        File yamlFile = new File("./src/" + judgeId + "/plugin.yml");

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
}
