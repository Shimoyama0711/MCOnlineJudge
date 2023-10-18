-- MySQL dump 10.13  Distrib 8.0.31, for Win64 (x86_64)
--
-- Host: localhost    Database: mconlinejudge
-- ------------------------------------------------------
-- Server version	8.0.31

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
  `score` int DEFAULT '0',
  `cases` json DEFAULT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sources`
--

LOCK TABLES `sources` WRITE;
/*!40000 ALTER TABLE `sources` DISABLE KEYS */;
INSERT INTO `sources` VALUES (9,'31528c4f241e4f5c805eb7cf482c95ad','tutorial/1','b7896eccae874eafbda75da21a495137','2023/07/09 01:21:38','import java.util.Scanner;\n\npublic class Main {\n	public static void main(String[] args) {\n		Scanner sc = new Scanner(System.in);\n		\n		String S = sc.next();\n		int a = sc.nextInt();\n		int b = sc.nextInt();\n		\n		System.out.println(S);\n		System.out.println(a / b);\n	}\n}','WA',0,'{\"case_01\": \"WA\", \"case_02\": \"WA\", \"case_03\": \"WA\", \"case_04\": \"WA\", \"case_05\": \"WA\", \"sample_01\": \"WA\", \"sample_02\": \"WA\"}'),(10,'31528c4f241e4f5c805eb7cf482c95ad','tutorial/1','e80e53cbdc6146b7a312c9b0e67dd4b9','2023/07/09 01:22:01','import java.util.Scanner;\n\npublic class Main {\n	public static void main(String[] args) {\n		Scanner sc = new Scanner(System.in);\n		\n		String S = sc.next();\n		int a = sc.nextInt();\n		int b = sc.nextInt();\n		\n		System.out.println(S);\n		System.out.println(a * b);\n	}\n}','WA',0,'{\"case_01\": \"AC\", \"case_02\": \"WA\", \"case_03\": \"WA\", \"case_04\": \"WA\", \"case_05\": \"AC\", \"sample_01\": \"WA\", \"sample_02\": \"WA\"}'),(21,'31528c4f241e4f5c805eb7cf482c95ad','basic/1','bca748911d934e76ab2c7218b29cabc6','2023/09/30 05:59:45','import org.bukkit.Bukkit;\nimport org.bukkit.Location;\nimport org.bukkit.Material;\nimport org.bukkit.World;\nimport org.bukkit.command.Command;\nimport org.bukkit.command.CommandSender;\nimport org.bukkit.entity.Player;\nimport org.bukkit.plugin.java.JavaPlugin;\n\npublic class Main extends JavaPlugin {\n    @Override\n    public void onEnable() {\n        super.onEnable();\n    }\n\n    @Override\n    public void onDisable() {\n        super.onDisable();\n    }\n\n    @Override\n    public void onLoad() {\n        super.onLoad();\n    }\n\n    @Override\n    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {\n        if (sender instanceof Player player) {\n            if (command.getName().equals(\"[COMMAND]\")) {\n                if (args.length == 3) {\n                    int x = Integer.parseInt(args[0]);\n                    int y = Integer.parseInt(args[1]);\n                    int z = Integer.parseInt(args[2]);\n\n                    World world = Bukkit.getWorld(\"MCOJ\");\n                    Location location = new Location(world, x, y, z);\n                    location.getBlock().setType(Material.GOLD_BLOCK);\n\n                    return true;\n                } else {\n                    player.sendMessage(\"§cInvalid arguments.\");\n\n                    return false;\n                }\n            }\n        } else {\n            System.out.println(\"Invalid arguments.\");\n            return false;\n        }\n\n        return true;\n    }\n}\n','WA',0,'{\"case_1\": \"WA\", \"case_2\": \"WA\", \"case_3\": \"WA\", \"example_1\": \"WA\", \"example_2\": \"WA\"}'),(25,'31528c4f241e4f5c805eb7cf482c95ad','basic/1','a483d33c58fc47e0a289c2f92cba67c0','2023/09/30 12:40:31','import org.bukkit.Bukkit;\nimport org.bukkit.Location;\nimport org.bukkit.Material;\nimport org.bukkit.World;\nimport org.bukkit.command.Command;\nimport org.bukkit.command.CommandSender;\nimport org.bukkit.plugin.java.JavaPlugin;\n\npublic class Main extends JavaPlugin {\n    @Override\n    public void onEnable() {\n        super.onEnable();\n    }\n\n    @Override\n    public void onDisable() {\n        super.onDisable();\n    }\n\n    @Override\n    public void onLoad() {\n        super.onLoad();\n    }\n\n    @Override\n    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {\n        if (command.getName().equals(\"[COMMAND]\")) {\n            if (args.length == 3) {\n                int x = Integer.parseInt(args[0]);\n                int y = Integer.parseInt(args[1]);\n                int z = Integer.parseInt(args[2]);\n\n                World world = Bukkit.getWorld(\"MCOJ\");\n                Location location = new Location(world, 0, 0, 0);\n                location.getBlock().setType(Material.GOLD_BLOCK);\n\n                return true;\n            } else\n                return false;\n        }\n\n        return true;\n    }\n}\n','WA',20,'{\"case_1\": \"AC\", \"case_2\": \"WA\", \"case_3\": \"WA\", \"example_1\": \"WA\", \"example_2\": \"WA\"}');
/*!40000 ALTER TABLE `sources` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-09-30 13:20:37
