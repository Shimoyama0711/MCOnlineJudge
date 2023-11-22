/**
 * Basic-1 - Place Golden Block
 * @author ArchWizard7
 */

package me.ArchWizard7.MCOnlineJudge.Judge;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class Basic2 {
    /**
     * テストケースをすべて判定し、MySQLデータベースに結果を上書きします
     * @param sender コマンド実行者。ここでは一般にサーバーを表します
     * @param judgeId ジャッジID
     * @return ジャッジに成功したかどうかを返します
     */
    public boolean judge(CommandSender sender, String judgeId) throws SQLException, JsonProcessingException {
        Server server = Bukkit.getServer();
        World world = server.getWorld("MCOJ");
        assert world != null;

        ObjectMapper mapper = new ObjectMapper();
        ObjectNode node = mapper.createObjectNode();

        String status = "AC";
        int score = 0;

        String[] caseNames = {
                "example_1",
                "example_2",
                "example_3",
        };
        int[] xArray = { 12, -1, 0 };
        int[] yArray = { 34, -1, 0 };
        int[] zArray = { 56, -1, 0 };
        Material[] materials = { Material.EMERALD_BLOCK };

        for (int i = 0; i < caseNames.length; i++) {
            int x = xArray[i];
            int y = yArray[i];
            int z = zArray[i];

            world.getBlockAt(x, y, z).setType(Material.AIR); // 初期化

            server.dispatchCommand(sender, judgeId + " " + x + " " + y + " " + z);
            server.broadcastMessage(judgeId + " " + x + " " + y + " " + z);

            boolean flag = isGoldenBlock(x, y, z);
            node.put(caseNames[i], flag ? "AC" : "WA"); // JSON ノードに追加

            if (!flag)
                status = "WA";
            else
                score += 20;

            // world.getBlockAt(x, y, z).setType(Material.AIR); // もとに戻す
        }

        String json = mapper.writeValueAsString(node);

        String sqlURL = "jdbc:mysql://localhost:3306/mconlinejudge";
        String USER = "root";
        String PASS = "BTcfrLkK1FFU";
        String SQL = "UPDATE sources SET status = ?, score = ?, cases = ? WHERE judge_id = ?";

        Connection conn = DriverManager.getConnection(sqlURL, USER, PASS);
        conn.setAutoCommit(true);
        PreparedStatement ps = conn.prepareStatement(SQL);
        ps.setString(1, status);
        ps.setInt(2, score);
        ps.setString(3, json);
        ps.setString(4, judgeId);

        ps.executeUpdate();
        conn.close();

        return true;
    }

    /**
     * ワールド "MCOJ" の座標 ({@code x}, {@code y}, {@code z}) が金ブロックかどうかを判定します
     * @param x x 座標
     * @param y y 座標
     * @param z z 座標
     * @return 金ブロックかどうかを返します
     */
    public static boolean isGoldenBlock(int x, int y, int z) {
        World world = Bukkit.getWorld("MCOJ");
        assert world != null;
        return world.getBlockAt(x, y, z).getType() == Material.GOLD_BLOCK;
    }
}
