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
package com.almuramc.forgebridge.utils;

import org.bukkit.Bukkit;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;

public class InventoryUtil {
    public static ItemStack getFirstByName(Player player, String itemName) {
        for (final ItemStack stack : player.getInventory()) {
            if (stack != null && stack.getType().name() != null && stack.getType().name().equalsIgnoreCase(itemName)) {
                return stack;
            }
        }
        return null;
    }
    
    public static boolean hasItem(Player player, String itemName) {
        for (final ItemStack stack : player.getInventory()) {
            if (stack != null && stack.getType().name() != null && stack.getType().name().equalsIgnoreCase(itemName)) {
                return true;
            }
        }
        return false;
    }
    
    public static boolean removeItem(Player player, String itemName) {
        for (Iterator<ItemStack> iter = player.getInventory().iterator(); iter.hasNext();) {
            final ItemStack stack = iter.next();

            if (stack != null && stack.getType().name() != null && stack.getType().name().equalsIgnoreCase(itemName)) {
                iter.remove();
                return true;
            }
        }
        
        Bukkit.getLogger().severe(" - Tried to remove item: " + itemName + " from user: " + player.getName() + " but if failed somehow.");
        return false;
    }
}
