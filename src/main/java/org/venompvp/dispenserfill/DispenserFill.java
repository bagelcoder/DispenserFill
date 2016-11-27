package org.venompvp.dispenserfill;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Dispenser;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;


public class DispenserFill extends JavaPlugin {

    private int radius = 10;

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            return false;
        }
        Player player = (Player) sender;

        if (player.getItemInHand() != null || player.getItemInHand().getType() != Material.AIR) {
            List<Location> dispeners = getNearbyDispensers(radius, player.getLocation());
            if(dispeners.isEmpty()) {
                player.sendMessage(ChatColor.RED + "There are no nearby dispensers.");
                return true;
            }
            int filled = 0;

            loop:
            for (int x = player.getItemInHand().getAmount(); x > 0; ) {
                for (Location location : dispeners) {
                    if (location.getBlock().getState() instanceof Dispenser) {
                        Dispenser dispenser = (Dispenser) location.getBlock().getState();

                        if (dispenser.getInventory().addItem(new ItemStack(player.getItemInHand().getType(), 1)).isEmpty()) {
                            x--;
                            dispenser.update(true, true);
                            player.getItemInHand().setAmount(x);

                            if (x == 0) {
                                player.getItemInHand().setType(Material.AIR);
                                player.setItemInHand(null);
                                player.updateInventory();
                            }
                        } else {
                            filled++;

                            if (filled == dispeners.size()) {
                                break loop;
                            }
                        }
                    }
                }
            }
            player.sendMessage(ChatColor.GREEN + "All dispensers near you have been filled");
        }
        return false;
    }

    private List<Location> getNearbyDispensers(int radius, Location location) {
        List<Location> locations = new ArrayList<Location>();

        for (int x = (radius * -1); x <= radius; x++) {
            for (int y = (radius * -1); y <= radius; y++) {
                for (int z = (radius * -1); z <= radius; z++) {
                    Block block = location.getWorld().getBlockAt(location.getBlockX() + x, location.getBlockY() + y, location.getBlockZ() + z);

                    if(block.getType() == Material.DISPENSER) {
                        if(block.getState() instanceof Dispenser) {
                            locations.add(block.getLocation());
                        }
                    }
                }
            }
        }
        return locations;
    }
}
