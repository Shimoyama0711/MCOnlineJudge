import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Sphere extends JavaPlugin {
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
        if (sender instanceof Player p) {
            if (command.getName().equals("test-command")) {
                if (args.length == 1) {
                    try {
                        World world = p.getWorld();
                        int size = Integer.parseInt(args[0]);
                        long count = (long) size * size * size;

                        if (count <= 65536) {
                            Location l = p.getLocation();
                            int X = l.getBlockX();
                            int Y = l.getBlockY();
                            int Z = l.getBlockZ();

                            for (int i = -size; i <= size; i++) {
                                for (int j = -size; j <= size; j++) {
                                    for (int k = -size; k <= size; k++) {
                                        if (Math.sqrt((i*i) + (j*j) + (k*k)) <= size) {
                                            Block block = world.getBlockAt((X + i), (Y + j), (Z + k));
                                            block.setType(Material.GOLD_BLOCK);
                                        }
                                    }
                                }
                            }
                        } else {
                            p.sendMessage("§cブロックの個数が多すぎます (" + count + " > 65536)");
                            return false;
                        }
                    } catch (NumberFormatException e) {
                        p.sendMessage("§c数字を入力してください");
                        return false;
                    }
                } else {
                    p.sendMessage("§c引数が正しくありません");
                }
            }
        }

        return true;
    }
}
