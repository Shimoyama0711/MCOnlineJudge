import java.io.File;
import java.util.Random;
import java.util.UUID;

public class MCCompile {
    public static void main(String[] args) {
        File file = new File("./src/MCTest.java");

        String judgeID = randomUUID();

        System.out.println(file.exists());
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

    public static void createJarFile() {

    }
}
