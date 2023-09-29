package me.ArchWizard7.MCOnlineJudge;

import com.fasterxml.jackson.core.JsonProcessingException;
import me.ArchWizard7.MCOnlineJudge.Judge.Basic1;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.SQLException;

public class Main extends JavaPlugin implements Listener {
    @Override
    public void onEnable() {
        super.onEnable();
    }

    @Override
    public void onDisable() {
        super.onDisable();
    }

    @Override
    public void onLoad() {
        super.onLoad();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        /*
         * /judge [judge_id] [problem_id]
         */
        if (command.getName().equalsIgnoreCase("judge")) {
            if (args.length != 2)
                return false;

            String judgeId = args[0];
            String problemId = args[1];

            if (problemId.equals("basic/1")) {
                try {
                    return new Basic1().judge(sender, judgeId);
                } catch (SQLException | JsonProcessingException e) {
                    throw new RuntimeException(e);
                }
            }
        }

        return super.onCommand(sender, command, label, args);
    }
}
