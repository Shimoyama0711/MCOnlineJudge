import java.util.Scanner;

public class Test {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int N = sc.nextInt();

        for (int i = 1; i <= N; i++) {
            for (int j = 1; j <= N; j++) {
                if (j >= 2)
                    System.out.print(" ");

                System.out.print(i * j);
            }

            System.out.print("\n");
        }
    }
}
