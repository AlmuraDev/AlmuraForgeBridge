package com.almuradev.almura.econ;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class EconListener implements Listener {
    
    public static final Locale CURRENCY_LOCALE = new Locale("en", "US");
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance(CURRENCY_LOCALE);
    public static final String INPUT_REGEX = "([a-zA-Z-\\s0-9]+)";
    public static final Random RANDOM = new Random();
    
    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!event.getEntity().hasPermission("econ.death")) {
            return;
        }
        final Player died = event.getEntity();
        final double deathTax = getDropAmountMultiple();
        final double carrying = VaultUtil.getBalance(died.getName());
        final double drop = carrying - (carrying * deathTax);
        VaultUtil.add(died.getName(), -drop);
        died.sendMessage("You dropped: " + ChatColor.RED + NUMBER_FORMAT.format(drop) + "!");        
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(event.getEntity().getName())) {
                continue;
            }
            player.sendMessage(ChatColor.AQUA + died.getDisplayName() + " died and dropped: " + ChatColor.GOLD + NUMBER_FORMAT.format(drop) + "!");
            dropAmount(player,drop);
        }
    }
    
    public double getDropAmountMultiple() {
        final String raw = "25-75";
        final String[] parsed = raw.split("-");
        double lower, upper = 0;
        //Parse lower and upper
        try {
            lower = Double.parseDouble(parsed[0]);
        } catch (Exception e) {
            lower = 0;
        }
        if (parsed.length == 2) {
            try {
                upper = Double.parseDouble(parsed[1]);
            } catch (Exception e) {
                upper = 0;
            }
        }
        //Pick random from range
        return (lower + (upper - lower) * this.RANDOM.nextDouble()) / 100;
    }
    
    public void dropAmount(Player player, double amount) {
        
        ItemStack platinumCoin = new ItemStack(Material.getMaterial("ALMURACOIN_PLATINUMCOIN"), 1, (short) 0); // $1,000,000
        ItemStack goldCoin = new ItemStack(Material.getMaterial("ALMURACOIN_GOLDCOIN"), 1, (short) 0); // $100,000
        ItemStack silverCoin = new ItemStack(Material.getMaterial("ALMURACOIN_SILVERCOIN"), 1, (short) 0); // $1,000
        ItemStack copperCoin = new ItemStack(Material.getMaterial("ALMURACOIN_COPPERCOIN"), 1, (short) 0); // $100

        // player.getWorld().dropItemNaturally(player.getLocation(), stacks???);
    }
}
