-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: mconlinejudge
-- ------------------------------------------------------
-- Server version	8.0.31

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
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
INSERT INTO `problems` VALUES ('tutorial/1','Tutorial-1 - Hello, Java World!',2,1024,1,100,'譁・ｭ怜・ $S$ 縺ｨ謨ｴ謨ｰ $a,\\ b$ 縺御ｸ弱∴繧峨ｌ縺ｾ縺吶・n1陦檎岼縺ｫ $S$ 繧偵・陦檎岼縺ｫ $a + b$ 縺ｮ險育ｮ礼ｵ先棡繧貞・蜉帙＠縺ｦ縺上□縺輔＞ 縲・,'$S$ 縺ｯ闍ｱ蟆乗枚蟄励・縺ｿ縺九ｉ縺ｪ繧九・$1 @leq |S| @leq 50$~$-100 @leq a,@ b @leq 100$','$S$\\n$a$ $b$','蝠城｡梧枚縺ｮ蠖｢蠑上・豐ｿ縺｣縺ｦ蜃ｺ蜉帙○繧医・,'/hints/01.html','hello\n5 3,hogehoge\n-100 100','hello\n8,hogehoge\n0','謾ｹ陦後ｒ蠢倥ｌ縺ｪ縺・ｈ縺・↓縺励∪縺励ｇ縺・・雋縺ｮ謨ｰ縺御ｸ弱∴繧峨ｌ繧句ｴ蜷医ｂ縺ゅｊ縺ｾ縺吶・),('tutorial/2','Tutorial-2 - Multiplication Table',2,1024,1,100,'謨ｴ謨ｰ $N$ 縺御ｸ弱∴繧峨ｌ縺ｾ縺吶・n蜷・紛謨ｰ $i,\\ j\\ (1 \\leq i,\\ j \\leq N)$ 縺ｫ縺､縺・※縲・i$ 陦檎岼 $j$ 蛻礼岼縺ｫ $i \\times j$ 縺ｮ險育ｮ礼ｵ先棡繧堤ｩｺ逋ｽ蛹ｺ蛻・ｊ縺ｧ蜃ｺ蜉帙＠縺ｦ縺上□縺輔＞縲・,'$1 @leq N @leq 100$','$N$','$N$ 陦後↓繧上◆縺｣縺ｦ蜃ｺ蜉帙○繧医・,'/hints/for.html','9','1 2 3 4 5 6 7 8 9\n2 4 6 8 10 12 14 16 18\n3 6 9 12 15 18 21 24 27\n4 8 12 16 20 24 28 32 36\n5 10 15 20 25 30 35 40 45\n6 12 18 24 30 36 42 48 54\n7 14 21 28 35 42 49 56 63\n8 16 24 32 40 48 56 64 72\n9 18 27 36 45 54 63 72 81','遨ｺ逋ｽ繧・隼陦後↓豌励ｒ縺､縺代∪縺励ｇ縺・・);
/*!40000 ALTER TABLE `problems` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sources`
--

DROP TABLE IF EXISTS `sources`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sources` (
  `id` int NOT NULL AUTO_INCREMENT,
  `uuid` varchar(64) DEFAULT NULL,
  `problem` varchar(256) DEFAULT NULL,
  `judge_id` varchar(64) DEFAULT NULL,
  `date` varchar(64) DEFAULT NULL,
  `body` text,
  `status` varchar(32) DEFAULT NULL,
  `cases` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sources`
--

LOCK TABLES `sources` WRITE;
/*!40000 ALTER TABLE `sources` DISABLE KEYS */;
INSERT INTO `sources` VALUES (9,'31528c4f241e4f5c805eb7cf482c95ad','tutorial/1','b7896eccae874eafbda75da21a495137','2023/07/09 01:21:38','import java.util.Scanner;\n\npublic class Main {\n	public static void main(String[] args) {\n		Scanner sc = new Scanner(System.in);\n		\n		String S = sc.next();\n		int a = sc.nextInt();\n		int b = sc.nextInt();\n		\n		System.out.println(S);\n		System.out.println(a / b);\n	}\n}','WA','{\"case_01\": \"WA\", \"case_02\": \"WA\", \"case_03\": \"WA\", \"case_04\": \"WA\", \"case_05\": \"WA\", \"sample_01\": \"WA\", \"sample_02\": \"WA\"}'),(10,'31528c4f241e4f5c805eb7cf482c95ad','tutorial/1','e80e53cbdc6146b7a312c9b0e67dd4b9','2023/07/09 01:22:01','import java.util.Scanner;\n\npublic class Main {\n	public static void main(String[] args) {\n		Scanner sc = new Scanner(System.in);\n		\n		String S = sc.next();\n		int a = sc.nextInt();\n		int b = sc.nextInt();\n		\n		System.out.println(S);\n		System.out.println(a * b);\n	}\n}','WA','{\"case_01\": \"AC\", \"case_02\": \"WA\", \"case_03\": \"WA\", \"case_04\": \"WA\", \"case_05\": \"AC\", \"sample_01\": \"WA\", \"sample_02\": \"WA\"}'),(11,'31528c4f241e4f5c805eb7cf482c95ad','tutorial/1','d02de553cd7d4f38aef6ab2f49dcc07e','2023/07/09 01:22:27','import java.util.Scanner;\n\npublic class Main {\n	public static void main(String[] args) {\n		Scanner sc = new Scanner(System.in);\n		\n		String S = sc.next();\n		int a = sc.nextInt();\n		int b = sc.nextInt();\n		\n		System.out.println(S);\n		System.out.println(a + b);\n	}\n}','AC','{\"case_01\": \"AC\", \"case_02\": \"AC\", \"case_03\": \"AC\", \"case_04\": \"AC\", \"case_05\": \"AC\", \"sample_01\": \"AC\", \"sample_02\": \"AC\"}'),(12,'31528c4f241e4f5c805eb7cf482c95ad','tutorial/1','c6ceaa3e1c274512bb8b4c1fb3994aac','2023/07/09 02:01:12','import java.util.Scanner;\n\npublic class Main {\n    public static void main(String[] args) {\n        Scanner scanner = new Scanner(System.in);\n\n        // 譁・ｭ怜・ S 繧貞・蜉媾n        String s = scanner.nextLine();\n\n        // 謨ｴ謨ｰ a, b 繧貞・蜉媾n        int a = scanner.nextInt();\n        int b = scanner.nextInt();\n\n        // a + b 縺ｮ邨先棡繧定ｨ育ｮ予n        int result = a + b;\n\n        // 譁・ｭ怜・ S 繧貞・蜉媾n        System.out.println(s);\n\n        // a + b 縺ｮ邨先棡繧貞・蜉媾n        System.out.println(result);\n    }\n}','AC','{\"case_01\": \"AC\", \"case_02\": \"AC\", \"case_03\": \"AC\", \"case_04\": \"AC\", \"case_05\": \"AC\", \"sample_01\": \"AC\", \"sample_02\": \"AC\"}');
/*!40000 ALTER TABLE `sources` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `email` varchar(127) NOT NULL,
  `mcid` varchar(45) DEFAULT NULL,
  `password` varchar(64) DEFAULT NULL,
  `uuid` varchar(45) DEFAULT NULL,
  `created_at` datetime DEFAULT NULL,
  PRIMARY KEY (`email`),
  UNIQUE KEY `email_UNIQUE` (`email`),
  UNIQUE KEY `uuid_UNIQUE` (`uuid`),
  UNIQUE KEY `mcid_UNIQUE` (`mcid`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('m19284@g.metro-cit.ac.jp','ArchWizard7','4c716d4cf211c7b7d2f3233c941771ad0507ea5bacf93b492766aa41ae9f720d','31528c4f241e4f5c805eb7cf482c95ad','2023-09-11 19:19:56');
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-27  7:13:59
