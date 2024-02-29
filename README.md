## "Minecraftを活用したプログラミング学習Webアプリの開発"
- T5-23 Shimoyama Ayumu

### :book: TL;DR
- 卒業研究のリポジトリです
- これを読んでいる時には恐らく私はいません
- 質問がありますか？
    - Discord：`archwizard7` に連絡してください

### :rice_cracker: Environment

| Key | Value |
| :-- | :-- |
| OS | Windows 11 64-bit |
| IDE | [IntelliJ IDEA Ultimate 2023.3](https://www.jetbrains.com/ja-jp/idea/) |
| [Python](https://www.python.org/) | 3.12.2 |
| [Java](https://www.oracle.com/jp/java/technologies/downloads/) | 21 |
| [MySQL](https://www.mysql.com/jp/) | 8.0.33 |
| [Minecraft](https://www.minecraft.net/ja-jp) / [Spigot](https://www.spigotmc.org/) | 1.20.4 |
| [Flask](https://flask.palletsprojects.com/en/3.0.x/) | 3.0.2 |
| [Bootstrap](https://getbootstrap.com/) | 5.3.3 |
| [jQuery](https://jquery.com/) | 3.7.1 |

### :computer: 実行手順
1. [Pythonをインストール](https://prog-8.com/docs/python-env-win)

2. [MySQLをインストール](https://www.javadrive.jp/mysql/install/)

3. お好きなIDEをインストール（筆者はIntelliJ IDEAを推奨してます）

4. まずこのリポジトリをクローンします

```
$ git clone https://github.com/Shimoyama0711/MCOnlineJudge.git
```

5. 必要なライブラリをインストールします

```
$ pip install flask
$ pip install flask_mysqldb
$ pip install requests
```
6. `app.py` を実行する

```
$ python ./app.py
```

7. `localhost:5000` にアクセスして確認

![導通確認](https://i.imgur.com/gLGXyhJ.png)

8. MySQL で `mconlinejudge` データベースを作成

```sql
CREATE DATABASE `mconlinejudge`;
USE `mconlinejudge`;
```

9. `dumps` フォルダの中にある3つの `.sql` ファイルを **MySQL Workbench** にドラッグドロップして実行する

![MySQL Workbench](https://i.imgur.com/JsTQ4jf.png)

10. サインアップできるか確認しましょう

> http://localhost:5000/signup

![Signup](https://i.imgur.com/iaiI2ly.png)

これがでればok

![Success](https://i.imgur.com/LXagQW0.png)

11. 採点サーバーを実行

`./src/MyServer.java` を実行してください

![Run MyServer.java](https://i.imgur.com/bfPSVGd.png)

12. ためしに `Tutorial-1` を解いてみましょう

```java
import java.util.Scanner;

class Main {
	public static void main(String[] args) {
    	Scanner sc = new Scanner(System.in);
      
      	String S = sc.next();
      	int a = sc.nextInt();
      	int b = sc.nextInt();
      
      	System.out.println(S);
      	System.out.println(a + b);
    }
}
```

**AC** が出たら動作確認は終わりです

![](https://i.imgur.com/hSdBpIl.png)
