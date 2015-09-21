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
import org.bukkit.inventory.PlayerInventory;

import java.util.Iterator;
import java.util.ListIterator;
import java.util.Map;

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

    /**
     * Removes some or all {@link Material} from a {@link PlayerInventory}.
     * @param player The player
     * @param materialName The name of the material
     * @param quantity The quantity to remove. Specifying -1 means all.
     * @return True if removal was successful, false otherwise
     */
    public static boolean removeItem(Player player, String materialName, int quantity) {
        final Material material = Material.getMaterial(materialName.toUpperCase());
        if (material != null) {
           final int foundIndex = player.getInventory().first(material);
            if (foundIndex != -1) {
                if (quantity == -1) {
                    player.getInventory().setItem(foundIndex, null);
                } else {
                    final ItemStack stack = player.getInventory().getItem(foundIndex);
                    if (stack.getAmount() - quantity <= 0) {
                        player.getInventory().setItem(foundIndex, null);
                    } else {
                        stack.setAmount(stack.getAmount() - quantity);
                        player.getInventory().setItem(foundIndex, stack);
                    }
                }

                player.updateInventory();
                return true;
            }
        }

        Bukkit.getLogger().severe(" - Tried to modify item: " + materialName + " from player: " + player.getName() + " with quantity [" + (quantity
                == -1 ? "all " : quantity) + "] but it failed somehow.");
        return false;
    }
}
