import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
        if (command.getName().equals("[COMMAND]")) {
            if (args.length == 3) {
                int x = Integer.parseInt(args[0]);
                int y = Integer.parseInt(args[1]);
                int z = Integer.parseInt(args[2]);

                World world = Bukkit.getWorld("MCOJ");
                Location location = new Location(world, x, y, z);
                location.getBlock().setType(Material.GOLD_BLOCK);

                return true;
            } else
                return false;
        }

        return true;
    }
}
