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

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.Wolf;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class EntityListener implements Listener {

    // AlmuraMod's Information Wand.  Print additional information from Bukkit side of server about the entity that was just attacked to the client's chat window.
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        LivingEntity attacker = null;
        Entity defender = event.getEntity();
        if (event.getCause() == DamageCause.ENTITY_ATTACK) {
            EntityDamageByEntityEvent e = (EntityDamageByEntityEvent) event;
            if (e.getDamager() instanceof LivingEntity) {
                attacker = (LivingEntity) e.getDamager();
            }
        }

        if (attacker instanceof Player && defender instanceof LivingEntity && !attacker.equals(defender)) {
            Player player = (Player) attacker;
            if (player.getItemInHand().getType() == Material.getMaterial("ALMURA_WANDINFORMATION")) {
                player.sendMessage(ChatColor.ITALIC + "Entity Information:");
                player.sendMessage(ChatColor.WHITE + "ID: " + ChatColor.RED + event.getEntity().getEntityId());
                player.sendMessage(ChatColor.WHITE + "Type: " + ChatColor.GOLD + event.getEntity().getType());
                player.sendMessage(ChatColor.WHITE + "Type Name: " + ChatColor.GREEN + event.getEntity().getType().getName());
            }
        }
    }

    // AlmuraMod's Animal drops listener.
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
        if (event.getEntity() == null) {
            return;
        }
        if (event.getEntity() instanceof Wolf) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSWOLFMEAT_RAW");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 4));
            }
        }

        if (event.getEntity() instanceof Sheep) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSLAMBCHOP_RAW");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 4));
            }
        }

        if (event.getEntity() instanceof Chicken) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSCHICKENLEG_RAW");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 2));
            }
        }

        if (event.getEntity() instanceof Cow) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSROASTBEEF_RAW");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 3));
            }
        }

        if (event.getEntity() instanceof Creeper) {
            Material material = Material.getMaterial("ALMURA_FOODCREEPERSTEAK");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 2));
            }
        }

        if (event.getEntity() instanceof Pig) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSPORKBELLY_RAW");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 3));
            }
        }

        if (event.getEntityType().getName() != null && event.getEntityType().getName().equalsIgnoreCase("MOCREATURES-KOMODODRAGON")) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSLIZARD");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 4));
            }
        }
    }
}
