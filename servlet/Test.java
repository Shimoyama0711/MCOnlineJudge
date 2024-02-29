import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

public class Test extends JavaPlugin {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (command.getName().equals("[COMMAND]")) {
            int x = Integer.parseInt(args[0]);
            int y = Integer.parseInt(args[1]);
            int z = Integer.parseInt(args[2]);
            int N = Integer.parseInt(args[3]);

            World world = Bukkit.getWorld("MCOJ");
            assert world != null;

            for (int ly = 0; ly <= (N - 1); ly++) {
                for (int lx = (x - ly); lx <= (x + ly); lx++) {
                    for (int lz = (z - ly); lz <= (z + ly); lz++) {
                        Block block = world.getBlockAt(lx, y - ly, lz);
                        block.setType(Material.GOLD_BLOCK);
                    }
                }
            }
        }

        return true;
    }
}
