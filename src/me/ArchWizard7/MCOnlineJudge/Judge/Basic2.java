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
import org.bukkit.block.Block;
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
                "test_1"
        };
        int[] xArray = { 0, -17,  9, 13 };
        int[] yArray = { 4,  -9, 13, 74 };
        int[] zArray = { 0, -31, -4, 96 };
        int[] NArray = { 5,   9,  1,  7 };

        for (int i = 0; i < caseNames.length; i++) {
            int x = xArray[i];
            int y = yArray[i];
            int z = zArray[i];
            int N = NArray[i];

            // 初期化 //
            for (int ly = y; ly >= y - (N-1); ly--) {
                for (int lx = x - (N-1); lx <= x + (N-1); lx++) {
                    for (int lz = z - (N-1); lz <= z + (N-1); lz++) {
                        world.getBlockAt(lx, ly, lz).setType(Material.AIR);
                    }
                }
            }

            String command = judgeId + " " + x + " " + y + " " + z + " " + N;

            server.dispatchCommand(sender, command);
            server.broadcastMessage(command);

            boolean flag = checkPyramid(x, y, z, N);
            node.put(caseNames[i], flag ? "AC" : "WA"); // JSON ノードに追加

            if (!flag)
                status = "WA";
            else
                score += 25;

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
     * ワールド "MCOJ" にピラミッドが生成されたかチェックします
     * @param x x 座標
     * @param y y 座標
     * @param z z 座標
     * @param N 段数
     * @return 正しいピラミッドかどうかを返します
     */
    public static boolean checkPyramid(int x, int y, int z, int N) {
        World world = Bukkit.getWorld("MCOJ");
        assert world != null;

        // 初期化 //
        int count = 0;

        for (int ly = 0; ly <= (N-1); ly++) {
            for (int lx = x - (N-1); lx <= x + (N-1); lx++) {
                for (int lz = z - (N-1); lz <= z + (N-1); lz++) {
                    Block block = world.getBlockAt(lx, y - ly, lz);

                    boolean inX = lx >= (x - ly) && lx <= (x + ly);
                    boolean inZ = lz >= (z - ly) && lz <= (z + ly);

                    if (inX && inZ) {
                        if (block.getType() != Material.BRICKS)
                            return false;
                    } else {
                        if (block.getType() != Material.AIR)
                            return false;
                    }
                }
            }

            count++;
        }

        return true;
    }
}
