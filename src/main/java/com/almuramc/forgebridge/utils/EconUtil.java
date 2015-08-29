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
    public static final byte DISCRIMINATOR_CURRENCY = 1;

    static {
        final RegisteredServiceProvider<Economy> ersp = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        economy = ersp == null ? null : ersp.getProvider() == null ? null : ersp.getProvider();
        final RegisteredServiceProvider<Permission> prsp = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        permission = prsp == null ? null : prsp.getProvider() == null ? null : prsp.getProvider();
    }

    public static void sendCurrencyAmount(Player player, double amount) {
        final ByteBuf buf = PacketUtil.createPacketBuffer(DISCRIMINATOR_CURRENCY);
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
        if (block.getType() == Material.getMaterial("ALMURA_CURRENCYDEPOSITBOX")) {
            return true;        
        }
        return false;
    }
}
