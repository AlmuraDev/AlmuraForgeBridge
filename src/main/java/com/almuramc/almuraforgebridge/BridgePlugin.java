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
 * This file is part of Almura Control Panel.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Almura Control Panel is licensed under the GNU General Public License.
 *
 * Almura Control Panel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Almura Control Panel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.almuramc.almuraforgebridge;

import org.anjocaido.groupmanager.events.GMUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
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
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
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

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player player = null;
        if (sender instanceof Player) {
            player = (Player) sender;
        }

        if (cmd.getName().equalsIgnoreCase("bridgeupdate")) {
            if (player == null) {
                sender.sendMessage("Almura Control Panel cannot be run from the server console.");
            } else {
                // Todo:
            }
            return true;
        }
        return false;
    }

    @EventHandler
    public void onGMUserEvent(GMUserEvent userEvent) {        
        final GMUserEvent event = userEvent;
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            @SuppressWarnings("deprecation")
            public void run() {
                Player player = Bukkit.getPlayer(event.getUser().getName());               
                if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("veteran"))) {
                    Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE + " has been promoted to: [" + ChatColor.GOLD + event.getUser().getGroupName() + ChatColor.WHITE + "]");
                    Bukkit.broadcastMessage(ChatColor.WHITE + "Thank you, " + ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE + " for your donation and continued support.");
                }

                if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("member"))) {
                    Bukkit.broadcastMessage(ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE + " has been promoted to: [" + ChatColor.GOLD + event.getUser().getGroupName() + ChatColor.WHITE + "]");                        
                }
            }
        }, 20L);        
    }  

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        if (event.getPlayer().getItemInHand().getType() == Material.getMaterial("ALMURA_WANDINFORMATION")) {
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
    
    @EventHandler(priority=EventPriority.HIGHEST)
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        for (Player player : getServer().getOnlinePlayers()) {
            //AlmuraTitle(player);
        }
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event)  {
        final Player player = event.getPlayer();
        event.setJoinMessage("");
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {            
            public void run() {
                AlmuraBroadcastLogin(player); //Have to use delayed login to allow nicknames to be set prior to broadcast.
            }
        }, 20L);
    }

    @EventHandler(priority=EventPriority.LOWEST)
    public void onPlayerQuit(PlayerQuitEvent event) {
        event.setQuitMessage("");
        final Player player = event.getPlayer();

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura SuperAdmin" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            return;
        }	

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..."  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            } else {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura Admin"  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            }
            return;
        }

        if (player.hasPermission("developer.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "Developer"  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Moderator"  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");				
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Veteran" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + "Member" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GRAY + "Guest" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Survival" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has left the server.");
            return;
        }
    }

    public void AlmuraBroadcastLogin(Player player) {

        if (!player.hasPlayedBefore()) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Newbie" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server for the First Time!");
            return;
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura SuperAdmin" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }	

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..."  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            } else {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_RED + "Almura Admin"  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            }
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.AQUA + "Moderator"  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Developer.title") && !player.hasPermission("admin.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.DARK_AQUA + "Developer"  + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "Veteran" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GREEN + "Member" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GRAY + "Guest" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {			
            Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.LIGHT_PURPLE + "Survival" + ChatColor.WHITE + "] -  " + player.getDisplayName() + ", has joined the server.");
            return;
        }
    }

    public void AlmuraTitle(Player player) {

        if (player.hasPermission("admin.title") && player.isOp()) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.DARK_RED+"Almura SuperAdmin" + ChatColor.RESET);
            return;
        }	

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                player.setCustomName(player.getDisplayName()+"\n"+ChatColor.GOLD+"Destroyer of Worlds" + ChatColor.RESET);
            } else { 
                player.setCustomName(player.getDisplayName()+"\n"+ChatColor.DARK_RED+"Almura Admin" + ChatColor.RESET);
            }
            return;
        }

        if (player.hasPermission("spoutteam.title") && !player.hasPermission("Admin.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.DARK_BLUE+"SpoutTeam" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("mod.title") && !player.hasPermission("Admin.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.DARK_BLUE+"Moderator" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("Dev.title") && !player.hasPermission("admin.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.DARK_RED+"Developer" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("CreativeMember.title") && !player.hasPermission("Mod.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.LIGHT_PURPLE+"CreativeMember" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("Ultra.title") && !player.hasPermission("CreativeMember.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.GOLD+"UltraMember" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("SuperMember.title") && !player.hasPermission("UltraMember.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.DARK_GREEN+"SuperMember" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("SuperMember.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.YELLOW+"Member" + ChatColor.RESET);
            return;
        }

        if (!player.hasPlayedBefore()) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.LIGHT_PURPLE + "Newbie" + ChatColor.RESET);
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            player.setCustomName(player.getDisplayName()+"\n"+ChatColor.GRAY+"Guest" + ChatColor.RESET);
            return;
        }	
    }	

    public void specialOptions(Player player) {
        // Abby
        if (player.getName().equalsIgnoreCase("mcsfam")) {
            //player.setCanFly(true);
        }
    }

    @EventHandler
    public void onEntityDeath(EntityDeathEvent event){
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
        
        if (event.getEntityType().getName().equalsIgnoreCase("MOCREATURES-KOMODODRAGON")) {
            Material material = Material.getMaterial("ALMURA_INGREDIENTSLIZARD");
            if (material != null) {
                event.getDrops().add(new ItemStack(material, 4));
            }
        }
    }
}
