import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.UUID;

public class Judge {
    public static void main(String[] args) throws IOException {
        String problem = "tutorial/1";
        String body = """
                import java.util.Scanner;
                
                public class Main {
                    public static void main(String[] args) {
                        Scanner sc = new Scanner(System.in);
                        int a = sc.nextInt();
                        int b = sc.nextInt();
                        
                        System.out.println(a + b);
                    }
                }
                """;

        UUID uuid = UUID.randomUUID();
        File judgeFile = new File(uuid + ".java");

        BufferedWriter bw = new BufferedWriter(new FileWriter(judgeFile));
        bw.write(body);
        bw.flush();
        bw.close();
    }
}
