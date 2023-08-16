import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SetBlock extends JavaPlugin {
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
        if (sender instanceof Player player) {
            if (command.getName().equals("judge")) {
                if (args.length == 3) {
                    int x = Integer.parseInt(args[0]);
                    int y = Integer.parseInt(args[1]);
                    int z = Integer.parseInt(args[2]);

                    World world = Bukkit.getWorld("MCOnlineJudge");
                    Location location = new Location(world, x, y, z);
                    location.getBlock().setType(Material.GOLD_BLOCK);

                    return true;
                } else {
                    player.sendMessage("§cInvalid arguments.");

                    return false;
                }
            }
        } else {
            System.out.println("Invalid arguments.");
            return false;
        }

        return true;
    }
}
