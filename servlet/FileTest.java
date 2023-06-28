import java.io.File;

public class FileTest {
    public static void main(String[] args) {
        File file = new File("./servlet/compile/Main.java");

        System.out.println(file.exists());
    }
}
