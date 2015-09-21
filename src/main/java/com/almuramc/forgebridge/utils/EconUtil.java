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
/*
 * This file is part of Reserve.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Reserve is licensed under the GNU General Public License.
 *
 * Reserve is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Reserve is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package com.almuramc.forgebridge.utils;

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.ChatColor;
import com.almuramc.forgebridge.BridgePlugin;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.RegisteredServiceProvider;

@SuppressWarnings("deprecation")
public final class EconUtil {

    public static final Economy economy;
    public static final Permission permission;


    static {
        final RegisteredServiceProvider<Economy> ersp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        economy = ersp == null ? null : ersp.getProvider() == null ? null : ersp.getProvider();
        final RegisteredServiceProvider<Permission> prsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permission = prsp == null ? null : prsp.getProvider() == null ? null : prsp.getProvider();
    }

    public static void sendCurrencyAmount(Player player, double amount) {
        final ByteBuf buf = PacketUtil.createPacketBuffer(PacketUtil.DISCRIMINATOR_CURRENCY);
        buf.writeDouble(amount);
        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, buf.array());
    }

    public static double getBalance(String name) {
        if (!hasEconomy()) {
            throw new IllegalStateException("Tried to perform economy actions but no economy service installed!");
        }
        return economy.getBalance(name);
    }

    public static void add(String name, double amount) {
        if (!hasEconomy()) {
            throw new IllegalStateException("Tried to perform economy actions but no economy service installed!");
        }
        if (amount > 0) {
            economy.depositPlayer(name, amount);
        } else if (amount < 0) {
            economy.withdrawPlayer(name, Math.abs(amount));
        }
    }

    public static boolean hasBalance(String name, double balance) {
        if (!hasEconomy()) {
            throw new IllegalStateException("Tried to perform economy actions but no economy service installed!");
        }
        return economy.has(name, balance);
    }

    public static boolean hasPermission(String name, String world, String perm) {
        if (!hasPermissions()) {
            throw new IllegalStateException("Tried to perform permission actions but no permission service installed!");
        }
        return permission.has(world, name, perm);
    }

    public static boolean hasEconomy() {
        return economy != null;
    }

    public static boolean hasPermissions() {
        return permission != null;
    }

    public static double getCoinValue(ItemStack item) {
        if (item.getType() == Material.getMaterial("ALMURA_CURRENCYCOPPERCOIN")) {
            return 100;
        }
        if (item.getType() == Material.getMaterial("ALMURA_CURRENCYSILVERCOIN")) {
            return 1000;
        }
        if (item.getType() == Material.getMaterial("ALMURA_CURRENCYGOLDCOIN")) {
            return 100000;
        }
        if (item.getType() == Material.getMaterial("ALMURA_CURRENCYPLATINUMCOIN")) {
            return 1000000;
        }
        return 0;        
    }

    public static boolean isBankingBlock(Block block) {        
        if (block != null && block.getType() == Material.getMaterial("ALMURA_CURRENCYDEPOSITBOX")) {
            return true;        
        }
        return false;
    }

    public static boolean isPassportBlock(Block block) {
        if (block != null && block.getType() == Material.getMaterial("ALMURA_CURRENCYPASSPORTBOX")) {
            return true;        
        }
        return false;
    }    

    public static boolean isCityTokenBlock(Block block) {
        if (block != null && block.getType() == Material.getMaterial("ALMURA_CURRENCYCITYTOKENBOX")) {
            return true;        
        }
        return false;
    }

    public static boolean hasAllCityTokens(Player player) {
        boolean hasFrenor = false;
        boolean hasSoretta = false;
        boolean hasMintos = false;
        boolean hasSanteem = false;
        boolean hasTempe = false;
        boolean hasElfville = false;
        boolean hasStancia = false;
        boolean hasCaves = false;

        if (player != null) {
            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_FRENOR")) {
                hasFrenor = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Frenor" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_SORETTA")) {
                hasSoretta = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Soreta" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_MINTOS")) {
                hasMintos = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Mintos" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_SANTEEM")) {
                hasSanteem = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Santeem" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_TEMPE")) {
                hasTempe = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Tempe" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_ELFVILLE")) {
                hasElfville = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Elfville" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_STANCIA")) {
                hasStancia = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Stancia" + ChatColor.WHITE + " City Token.");
            }

            if (InventoryUtil.hasItem(player, "ALMURA_CURRENCYTOKEN_CAVES")) {
                hasCaves = true;
            } else {
                player.sendMessage(ChatColor.DARK_AQUA + "[City Tokens]" + ChatColor.WHITE + " - Missing: " + ChatColor.BLUE + "Caves" + ChatColor.WHITE + " City Token.");
            }

            if (hasFrenor && hasSoretta && hasMintos && hasSanteem && hasTempe && hasElfville && hasStancia && hasCaves) {
                if (UserUtil.changeUserGroup(player, "citizen")) {
                    // Feedback message to player should come via Broadcast from PlayerListener (Group Changed Event).

                    // Wipe Tokens from Inventory.
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_FRENOR", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_SORETTA", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_MINTOS", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_SANTEEM", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_TEMPE", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_ELFVILLE", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_STANCIA", 1);
                    InventoryUtil.removeItem(player, "ALMURA_CURRENCYTOKEN_CAVES", 1);

                    Bukkit.getLogger().info(" - Player: " + player.getName() + " granted Almura Citizenship.");
                } else {
                    Bukkit.getLogger().severe(" - Tried up move user: " + player.getName() + " to Citizen group but if failed somehow.");
                }
            }

        }
        return false;
    }
}
