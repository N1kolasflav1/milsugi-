package matasugepula;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import java.util.UUID;

public class matasugepula extends JavaPlugin {
    private final Set<UUID> used = new HashSet<>();
    private final Random random = new Random();

    @Override
    public void onEnable() {
        getCommand("tpfar").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players can use this command.");
            return true;
        }
        Player player = (Player) sender;
        if (used.contains(player.getUniqueId())) {
            player.sendMessage("§cYou already used this command!");
            return true;
        }

        player.sendMessage("§7Searching a safe location...");
        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_PLING, 1f, 1f);

        Bukkit.getScheduler().runTaskAsynchronously(this, () -> {
            Location safeLoc = findSafeLocation(player.getWorld());
            if (safeLoc == null) {
                player.sendMessage("§cCould not find a safe location!");
                return;
            }
            Bukkit.getScheduler().runTask(this, () -> {
                player.teleport(safeLoc);
                used.add(player.getUniqueId());
                player.sendTitle("§aTeleported!", "§7Good luck!", 10, 60, 20);
                player.sendMessage("§aYou have been teleported far away.");
                player.playSound(player.getLocation(), Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1f);
            });
        });

        return true;
    }

    private Location findSafeLocation(World world) {
        for (int i = 0; i < 20; i++) {
            int distance = 1000 + random.nextInt(2500); // 1000–3500
            double angle = random.nextDouble() * 2 * Math.PI;
            int x = (int) (Math.cos(angle) * distance);
            int z = (int) (Math.sin(angle) * distance);
            int y = world.getHighestBlockYAt(x, z);
            Location loc = new Location(world, x + 0.5, y + 1, z + 0.5);
            Material block = world.getBlockAt(x, y, z).getType();
            if (block == Material.WATER || block == Material.LAVA) continue;
            if (!block.isSolid()) continue;
            return loc;
        }
        return null;
    }
}