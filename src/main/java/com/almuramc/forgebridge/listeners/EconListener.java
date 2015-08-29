/*
 * This file is part of Almura Forge Bridge.
 *
 * © 2015 AlmuraDev <http://www.almuradev.com/>
 * Almura Forge Bridge is licensed under the GNU General Public License.
 *
 * Almura Forge Bridge is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Almura Forge Bridge is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package com.almuramc.forgebridge.listeners;

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

import com.almuramc.forgebridge.utils.EconUtil;

public class EconListener implements Listener {

    public static final Locale CURRENCY_LOCALE = new Locale("en", "US");
    public static final NumberFormat NUMBER_FORMAT = NumberFormat.getCurrencyInstance(CURRENCY_LOCALE);
    public static final String INPUT_REGEX = "([a-zA-Z-\\s0-9]+)";
    public static final Random RANDOM = new Random();

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        if (!areCoinsLoaded()) {
            Bukkit.getLogger().info("[Bridge] - Skipping death coin drop, coins not loaded");
            return;
        }
        final Player died = event.getEntity();
        final double deathTax = getDropAmountMultiple();
        final double carrying = EconUtil.getBalance(died.getName());
        final double drop = carrying - (carrying * deathTax);
        EconUtil.add(died.getName(), -drop);
        double remaining = dropAmount(died,drop);

        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.getName().equalsIgnoreCase(event.getEntity().getName())) {
                continue;
            }            
            player.sendMessage(ChatColor.AQUA + died.getDisplayName() + " died and dropped: " + ChatColor.GOLD + NUMBER_FORMAT.format(drop-remaining) + "!");
        }
        Bukkit.getLogger().info("[Bridge Economy] - Player: " + died.getName() + " / " + died.getDisplayName() + " dropped: " + NUMBER_FORMAT.format(drop-remaining));
        died.sendMessage("You dropped: " + ChatColor.RED + NUMBER_FORMAT.format(drop-remaining) + "!");
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
        return (lower + (upper - lower) * EconListener.RANDOM.nextDouble()) / 100;
    }

    public double dropAmount(Player player, double amount) {
        double remainingMoney = amount;
        int platinum = (int) (remainingMoney / 1000000);
        remainingMoney -= platinum * 1000000;
        int gold = (int) (remainingMoney / 100000);
        remainingMoney -= gold * 100000;
        int silver = (int) (remainingMoney / 1000);
        remainingMoney -= silver * 1000;
        int copper = (int) (remainingMoney / 100);
        remainingMoney -= copper * 100;

        while (platinum > 0) {
            if (platinum > 64) {            
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYPLATINUMCOIN"), 64, (short) 0)); // $100,000
                platinum-=64;
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYPLATINUMCOIN"), platinum, (short) 0)); // $100,000
                break;
            }                
        }

        while (gold > 0) {
            if (gold > 64) {            
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYGOLDCOIN"), 64, (short) 0)); // $100,000
                gold-=64;
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYGOLDCOIN"), gold, (short) 0)); // $100,000
                break;
            }                
        }

        while (silver > 0) {
            if (silver > 64) {            
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYSILVERCOIN"), 64, (short) 0)); // $100,000
                silver-=64;
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYSILVERCOIN"), silver, (short) 0)); // $100,000
                break;
            }                
        }

        while (copper > 0) {
            if (copper > 64) {            
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYCOPPERCOIN"), 64, (short) 0)); // $100,000
                copper-=64;
            } else {
                player.getWorld().dropItemNaturally(player.getLocation(), new ItemStack(Material.getMaterial("ALMURA_CURRENCYCOPPERCOIN"), copper, (short) 0)); // $100,000
                break;
            }                
        }

        return remainingMoney;
    }
    
    public boolean areCoinsLoaded() {
        if (Material.getMaterial("ALMURA_CURRENCYPLATINUMCOIN") == null)
            return false;
        if (Material.getMaterial("ALMURA_CURRENCYGOLDCOIN") == null)
            return false;
        if (Material.getMaterial("ALMURA_CURRENCYSILVERCOIN") == null)
            return false;
        if (Material.getMaterial("ALMURA_CURRENCYCOPPERCOIN") == null)
            return false;
        return true;
    }
}
