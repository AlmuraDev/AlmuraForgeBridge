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
 * This file is part of Almura Forge Bridge.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
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
package com.almuramc.almuraforgebridge;

import com.almuradev.almura.extension.entity.IExtendedEntityLivingBase;
import org.anjocaido.groupmanager.events.GMUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BridgePlugin extends JavaPlugin implements Listener {

    private static BridgePlugin instance;

    public static BridgePlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {
    }

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, BridgeNetwork.CHANNEL);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(this, this);
        pm.registerEvents(new BridgeNetwork(), this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGMUserEvent(GMUserEvent userEvent) {
        for (final Player player : getServer().getOnlinePlayers()) {
            final GMUserEvent event = userEvent;
            Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
                public void run() {
                    setCustomTitle(player);

                    if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("member"))) {
                        Bukkit.broadcastMessage(
                                ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE + " has been granted: [" + ChatColor.GOLD + event
                                        .getUser().getGroupName() + ChatColor.WHITE + "]");
                        Bukkit.broadcastMessage(ChatColor.WHITE + "Almura Thanks " + ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE
                                                + " for their donation.  It is very much appreciated.");
                    }

                    if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("guest"))) {
                        Bukkit.broadcastMessage(
                                ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE + " has been promoted to: [" + ChatColor.GOLD
                                + event.getUser().getGroupName() + ChatColor.WHITE + "]");
                    }
                }
            }, 20L);

        }
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType() == Material.getMaterial("ALMURA_TOOLSMMWAND_INFORMATION_WAND")) {
            if (event.getClickedBlock() != null) {
                event.getPlayer().sendMessage(ChatColor.ITALIC + "Block Information:");
                event.getPlayer().sendMessage(ChatColor.WHITE + "ID: " + ChatColor.RED + event.getClickedBlock().getTypeId());
                event.getPlayer().sendMessage(ChatColor.WHITE + "Material: " + ChatColor.GOLD + event.getClickedBlock().getType());
                event.getPlayer().sendMessage(ChatColor.WHITE + "MetaData: " + ChatColor.AQUA + event.getClickedBlock().getData());
                event.getPlayer().sendMessage(ChatColor.WHITE + "Biome: " + ChatColor.LIGHT_PURPLE + event.getClickedBlock().getBiome() + "\n");
            }
        }
    }

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
            if (player.getItemInHand().getType() == Material.getMaterial("ALMURA_TOOLSMMWAND_INFORMATION_WAND")) {
                player.sendMessage(ChatColor.ITALIC + "Entity Information:");
                player.sendMessage(ChatColor.WHITE + "ID: " + ChatColor.RED + event.getEntity().getEntityId());
                player.sendMessage(ChatColor.WHITE + "Type: " + ChatColor.GOLD + event.getEntity().getType());
                player.sendMessage(ChatColor.WHITE + "Type Name: " + ChatColor.GREEN + event.getEntity().getType().getName());
            }
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        final Player player = event.getPlayer();
        event.setJoinMessage("");
        broadcastLogin(player);
        setCustomTitle(player);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        Player player = event.getPlayer();

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(
                    ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura SuperAdmin" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(
                        ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] -  " + player.getDisplayName()
                        + ", has left the server.");
            } else {
                Bukkit.broadcastMessage(
                        ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura Admin" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                        + ", has left the server.");
            }
            return;
        }

        if (player.hasPermission("spongeteam.leader.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Sponge " + ChatColor.BLUE + "Leader" + ChatColor.WHITE + "] - " + player.getDisplayName()
                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("developer.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "Developer" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Moderator" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Veteran" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + "Member" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GRAY + "Guest" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has left the server.");
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Survival" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has left the server.");
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event) {
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

        if (event.getEntityType() != null) {
            if (event.getEntityType().getName() != null) {
                if (event.getEntityType().getName().equalsIgnoreCase("MOCREATURES-KOMODODRAGON")) {
                    Material material = Material.getMaterial("ALMURA_INGREDIENTSLIZARD");
                    if (material != null) {
                        event.getDrops().add(new ItemStack(material, 4));
                    }
                }
            }
        }
    }

    public void broadcastLogin(Player player) {

        if (!player.hasPlayedBefore()) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Newbie" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server for the First Time!");
            return;
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(
                    ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura SuperAdmin" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(
                        ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] -  " + player.getDisplayName()
                        + ", has joined the server.");
            } else {
                Bukkit.broadcastMessage(
                        ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura Admin" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                        + ", has joined the server.");
            }
            return;
        }

        if (player.hasPermission("spongeteam.leader.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Sponge " + ChatColor.BLUE + "Leader" + ChatColor.WHITE + "] - " + player.getDisplayName()
                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Moderator" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Developer.title") && !player.hasPermission("admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "Developer" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Veteran" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + "Member" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GRAY + "Guest" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Survival" + ChatColor.WHITE + "] -  " + player.getDisplayName()
                                    + ", has joined the server.");
        }
    }

    public void setCustomTitle(Player player) {
        if (((CraftPlayer) player).getHandle() instanceof IExtendedEntityLivingBase) {
            final IExtendedEntityLivingBase extendedPlayer = (IExtendedEntityLivingBase) ((CraftPlayer) player).getHandle();

            if (player.hasPermission("admin.title") && player.isOp()) {
                extendedPlayer.setTitle(ChatColor.DARK_RED + "Almura SuperAdmin");
                return;
            }

            if (player.hasPermission("admin.title") && !player.isOp()) {
                if (player.getName().equalsIgnoreCase("wifee")) {
                    extendedPlayer.setTitle(ChatColor.GOLD + "Destroyer of Worlds");
                } else {
                    extendedPlayer.setTitle(ChatColor.DARK_RED + "Almura Admin");
                }
                return;
            }

            if (player.hasPermission("spongeteam.leader.title") && !player.hasPermission("Admin.title")) {
                extendedPlayer.setTitle(ChatColor.GOLD + "Sponge " + ChatColor.BLUE + "Leader");
                return;
            }

            if (player.hasPermission("spoutteam.title") && !player.hasPermission("Admin.title")) {
                extendedPlayer.setTitle(ChatColor.DARK_BLUE + "Spout Team");
                return;
            }

            if (player.hasPermission("mod.title") && !player.hasPermission("Admin.title")) {
                extendedPlayer.setTitle(ChatColor.DARK_BLUE + "Moderator");
                return;
            }

            if (player.hasPermission("Dev.title") && !player.hasPermission("admin.title")) {
                extendedPlayer.setTitle(ChatColor.DARK_RED + "Developer");
                return;
            }

            if (player.hasPermission("CreativeMember.title") && !player.hasPermission("Mod.title")) {
                extendedPlayer.setTitle(ChatColor.LIGHT_PURPLE + "CreativeMember");
                return;
            }

            if (player.hasPermission("Ultra.title") && !player.hasPermission("CreativeMember.title")) {
                extendedPlayer.setTitle(ChatColor.GOLD + "UltraMember");
                return;
            }

            if (player.hasPermission("SuperMember.title") && !player.hasPermission("UltraMember.title")) {
                extendedPlayer.setTitle(ChatColor.DARK_GREEN + "SuperMember");
                return;
            }

            if (player.hasPermission("Member.title") && !player.hasPermission("SuperMember.title")) {
                extendedPlayer.setTitle(ChatColor.YELLOW + "Member");
                return;
            }

            if (!player.hasPlayedBefore()) {
                extendedPlayer.setTitle(ChatColor.LIGHT_PURPLE + "Newbie");
                return;
            }

            if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
                extendedPlayer.setTitle(ChatColor.GRAY + "Guest");
            }
        }
    }
}
