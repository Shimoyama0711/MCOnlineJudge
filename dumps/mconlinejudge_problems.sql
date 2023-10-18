-- MySQL dump 10.13  Distrib 8.0.33, for Win64 (x86_64)
--
-- Host: localhost    Database: mconlinejudge
-- ------------------------------------------------------
-- Server version	8.0.33

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `problems`
--

DROP TABLE IF EXISTS `problems`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `problems` (
  `id` varchar(64) NOT NULL,
  `title` varchar(128) DEFAULT NULL,
  `time` int DEFAULT NULL,
  `memory` int DEFAULT NULL,
  `difficulty` int DEFAULT NULL,
  `score` int DEFAULT NULL,
  `problem_statement` text,
  `conditions` text,
  `input` text,
  `output` text,
  `hints` text,
  `input_examples` text,
  `output_examples` text,
  `comments` text,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `problems`
--

LOCK TABLES `problems` WRITE;
/*!40000 ALTER TABLE `problems` DISABLE KEYS */;
INSERT INTO `problems` VALUES ('basic/1','Basic-1 - Place Golden Block',2,1024,1,100,'整数 $x,\\ y,\\ z$ が与えられます。\nワールド <code>MCOJ</code> の座標 $(x,\\ y,\\ z)$ に <b>金ブロック</b> を設置するようなプログラムを作成してください。','ブロックが設置されるワールドの名前は <code>MCOJ</code>~$-100 @leq x,\\ z @leq 100$~$-64 @leq y @leq 319$','/[COMMAND] $x$ $y$ $z$','(なし)','/hints/basic-1-1','/[COMMAND] 3 5 7,/[COMMAND] -17 -9 -31','(なし),(なし)','変数の順番に気をつけましょう。,負の数が与えられる場合もあります。'),('basic/2','Basic-2 - Villager or Zombie?',2,1024,1,100,'整数 $x,\\ y,\\ z$ が与えられます。\n ここで、ワールド <code>MCOJ</code> の座標 $(x,\\ y,\\ z)$ に置かれているブロックが\n <b>エメラルドブロック</b> ならば村人を、\n <b>丸石</b> であればゾンビを、\n ワールド <code>MCOJ</code> の座標 $(x,\\ y+1,\\ z)$ に召喚してください。','ブロックが設置されるワールドの名前は <code>MCOJ</code>~$-100 @leq x,\\ z @leq 100$~$-64 @leq y @leq 318$','/[COMMAND] $x$ $y$ $z$','(なし)','/hints/basic-2-1','/[COMMAND] 12 34 56 8 21 3,/[COMMAND] -1 -1 -1 1 1 1,/[COMMAND] 0 0 0 0 0 0','(なし),(なし),(なし)','変数が入力される順番に気をつけましょう。,負の数が与えられる場合もあります。,同じ座標が与えられる場合もあります'),('tutorial/1','Tutorial-1 - Hello, Java World!',2,1024,1,100,'文字列 $S$ と整数 $a,\\ b$ が与えられます。\n1行目に $S$ を、2行目に $a + b$ の計算結果を出力してください 。','$S$ は英小文字のみからなる。~$1 @leq |S| @leq 50$~$-100 @leq a,@ b @leq 100$','$S$\\n$a$ $b$','問題文の形式の沿って出力せよ。','/hints/01.html','hello\n5 3,hogehoge\n-100 100','hello\n8,hogehoge\n0','改行を忘れないようにしましょう。,負の数が与えられる場合もあります。'),('tutorial/2','Tutorial-2 - Multiplication Table',2,1024,1,100,'整数 $N$ が与えられます。\n各整数 $i,\\ j\\ (1 \\leq i,\\ j \\leq N)$ について、$i$ 行目 $j$ 列目に $i \\times j$ の計算結果を空白区切りで出力してください。','$1 @leq N @leq 100$','$N$','$N$ 行にわたって出力せよ。','/hints/for.html','9','1 2 3 4 5 6 7 8 9\n2 4 6 8 10 12 14 16 18\n3 6 9 12 15 18 21 24 27\n4 8 12 16 20 24 28 32 36\n5 10 15 20 25 30 35 40 45\n6 12 18 24 30 36 42 48 54\n7 14 21 28 35 42 49 56 63\n8 16 24 32 40 48 56 64 72\n9 18 27 36 45 54 63 72 81','空白や改行に気をつけましょう。');
/*!40000 ALTER TABLE `problems` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-10-18 15:08:21
