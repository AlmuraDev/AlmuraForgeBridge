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

import com.almuradev.almura.econ.EconListener;

import org.anjocaido.groupmanager.events.GMUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Chicken;
import org.bukkit.entity.Cow;
import org.bukkit.entity.Creeper;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Pig;
import org.bukkit.entity.Player;
import org.bukkit.entity.Sheep;
import org.bukkit.entity.TNTPrimed;
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

    // Color setups for membership levels 
    private static String superadminColor = "" + ChatColor.DARK_RED;
    private static String superadmin = ChatColor.WHITE + "[" + superadminColor + "Super-Admin" + ChatColor.WHITE + "] -  ";

    private static String adminColor = "" + ChatColor.RED;
    private static String admin = ChatColor.WHITE + "[" + adminColor + "Admin" + ChatColor.WHITE + "] -  ";

    private static String spongeleaderColor1 = "" + ChatColor.GOLD;
    private static String spongeleaderColor2 = "" + ChatColor.BLUE;
    private static String spongeleader = ChatColor.WHITE + "[" + spongeleaderColor1 + "Sponge " + spongeleaderColor2 + "Leader" + ChatColor.WHITE + "] - ";

    private static String moderatorColor = "" + ChatColor.BLUE;
    private static String moderator = ChatColor.WHITE + "[" + moderatorColor + "Moderator" + ChatColor.WHITE + "] -  ";

    private static String veteranColor = "" + ChatColor.GOLD;
    private static String veteran = ChatColor.WHITE + "[" + veteranColor + "Veteran" + ChatColor.WHITE + "] -  ";

    private static String contributorColor = "" + ChatColor.DARK_AQUA;
    private static String contributor = ChatColor.WHITE + "[" + contributorColor + "Contributor" + ChatColor.WHITE + "] -  ";

    private static String memberColor = "" + ChatColor.DARK_GREEN;
    private static String member = ChatColor.WHITE + "[" + memberColor + "Member" + ChatColor.WHITE + "] -  ";

    private static String guestColor = "" + ChatColor.GRAY;
    private static String guest = ChatColor.WHITE + "[" + guestColor + "Guest" + ChatColor.WHITE + "] -  ";

    private static String survivalColor = "" + ChatColor.LIGHT_PURPLE;
    private static String survival = ChatColor.WHITE + "[" + survivalColor + "Survival" + ChatColor.WHITE + "] -  ";

    private static String newbieColor = "" + ChatColor.LIGHT_PURPLE;
    private static String newbie = ChatColor.WHITE + "[" + newbieColor + "Newbie" + ChatColor.WHITE + "] -  ";

    // Enter and Leave messages
    private static String enterMessage = ", has joined the server.";
    private static String leaveMessage = ", has left the server.";

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
        pm.registerEvents(new EconListener(), this);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onGMUserEvent(GMUserEvent userEvent) {
        final GMUserEvent event = userEvent;
        final Player player = event.getUser().getBukkitPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(this, new Runnable() {
            public void run() {                
                if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("contributor"))) {
                    Bukkit.broadcastMessage(
                            ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE + " has been granted: [" + ChatColor.GOLD + event
                            .getUser().getGroupName() + ChatColor.WHITE + "]");
                    Bukkit.broadcastMessage(ChatColor.WHITE + "Almura Thanks " + ChatColor.GOLD + player.getDisplayName() + ChatColor.WHITE
                            + " for their donation.  It is very much appreciated.");
                }

                if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("member"))) {
                    Bukkit.broadcastMessage(
                            ChatColor.DARK_PURPLE + player.getDisplayName() + ChatColor.WHITE + " has been promoted to: [" + ChatColor.GOLD
                            + event.getUser().getGroupName() + ChatColor.WHITE + "]");
                }
            }
        }, 5L);
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

    public static void broadcastLogin(Player player) {

        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            Bukkit.broadcastMessage(spongeleader + player.getDisplayName() + enterMessage);
            return;
        }

        if (!player.hasPlayedBefore()) {
            Bukkit.broadcastMessage(newbie + player.getDisplayName() + ", has joined the server for the First Time!");
            return;
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(superadmin + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] -  " + player.getDisplayName() + enterMessage);
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Harbinger..." + ChatColor.WHITE + "] -  " + player.getDisplayName() + enterMessage);
            } else {
                Bukkit.broadcastMessage(admin + player.getDisplayName() + enterMessage);
            }
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(moderator + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(veteran + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(contributor + player.getDisplayName() + enterMessage);
            return;
        }


        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(member + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(guest + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(survival + player.getDisplayName() + enterMessage);
            return;
        }
    }

    public static void broadcastLogout(Player player) {

        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            Bukkit.broadcastMessage(spongeleader + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(superadmin + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] -  " + player.getDisplayName() + leaveMessage);
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Harbinger..." + ChatColor.WHITE + "] -  " + player.getDisplayName() + leaveMessage);
            } else {
                Bukkit.broadcastMessage(admin + player.getDisplayName() + leaveMessage);
            }
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(moderator + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(veteran + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(contributor + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(member + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(guest + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(survival + player.getDisplayName() + leaveMessage);
            return;
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

    public static String getCustomTitle(Player player) {        
        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            return (spongeleaderColor1 + "Sponge " + spongeleaderColor2 + "Leader");            
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            return (superadminColor + "SuperAdmin");
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                return (ChatColor.GOLD + "Destroyer of Worlds");
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                return (ChatColor.GOLD + "Harbinger");
            } else {
                return (adminColor + "Admin");
            }
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            return (moderatorColor + "Moderator");            
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            return (veteranColor + "Veteran");            
        }

        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            return (contributorColor + "Contributor");            
        }

        if (player.hasPermission("member.title") && !player.hasPermission("contributor.title")) {
            return (memberColor + "Member");            
        }

        if (!player.hasPlayedBefore()) {
            return (newbieColor + "Newbie");            
        }

        if (player.hasPermission("guest.title") && !player.hasPermission("member.title")) {
            return (guestColor + "Guest");            
        }

        if (player.hasPermission("survival.title") && !player.hasPermission("member.title")) {
            return (survivalColor + "Survival");            
        }
        return null;

    }

    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("bridge-info")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    if (args.length == 0) {
                        displayInfo((Player) sender, false, false);
                        return true;
                    }
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions, requires OP.");
                }
            } else {
                displayInfo(null, true, false);
            }
        }
        if (cmd.getName().equalsIgnoreCase("bridge-info-debug")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    if (args.length == 0) {
                        displayInfo((Player) sender, false, false);
                        return true;
                    }
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions, requires OP.");
                }
            } else {
                displayInfo(null, true, true);
            }
        }
        if (cmd.getName().equalsIgnoreCase("bridge-clear-items")) {
            if (sender instanceof Player) {
                if (sender.isOp()) {
                    if (args.length == 0) {
                        clearItems((Player) sender, false);
                        return true;
                    }
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions, requires OP.");
                }
            } else {
                displayInfo(null, true, false);
            }
        }
        return false;
    }

    public void clearItems(Player player, boolean console) {
        int removedCount = 0;
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.isDead())
                    continue;
                if (e instanceof Item) {
                    e.remove();
                    removedCount++;
                }
            }
        }
        if (console) {
            System.out.println("[Bridge Item Cleanup] - Removed: " + removedCount);
        } else {
            if (player != null) {
                player.sendMessage("[Bridge Item Cleanup] - Removed: " + removedCount);
            }
        }
    }

    public void displayInfo(Player player, boolean console, boolean debug) {
        int mobcount = 0;
        int totalMobs = 0;
        int animalCount = 0;
        int totalAnimals = 0;
        int itemcount = 0;
        int totalItems = 0;
        int totalChunks = 0;
        int totalPlayers = 0;

        if (!console && player != null) {
            player.sendMessage("---------------------------------------------------------------------------------------------");
        }
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.isDead())
                    continue;
                if (e instanceof Player) {
                } else if (e instanceof Monster) {
                    mobcount++;
                } else if (e instanceof Animals) {
                    animalCount++;
                } else if (e instanceof Item) {
                    itemcount++;
                }
                if (console) {
                    System.out.println("Entity: " + e.getType() + " Age: " + ((e.getTicksLived()/20)/60) + " minutes, Location: " + e.getLocation());
                }
            }
            if (console) {
                System.out.println(w.getName() + " - Chunks: " + w.getLoadedChunks().length + " Players: " + w.getPlayers().size() + " Items: " + itemcount + " Monsters: " + mobcount + " Animals: " + animalCount);
            } else {
                if (player !=null) {
                    player.sendMessage(ChatColor.DARK_GREEN + w.getName() + ChatColor.WHITE + " - Chunks: " + ChatColor.RED + w.getLoadedChunks().length + ChatColor.WHITE + " Players: " + ChatColor.GREEN + w.getPlayers().size() + ChatColor.WHITE + " Items: " + ChatColor.AQUA + itemcount + ChatColor.WHITE + " Monsters: " + ChatColor.GOLD + mobcount + ChatColor.WHITE + " Animals: " + ChatColor.DARK_AQUA + animalCount);
                }
            }
            totalAnimals = totalAnimals + animalCount;
            totalMobs = totalMobs + mobcount;
            totalItems = totalItems + itemcount;
            totalChunks = totalChunks + w.getLoadedChunks().length;
            totalPlayers = totalPlayers + w.getPlayers().size();
            mobcount = 0;
            animalCount = 0;
            itemcount = 0;
        }

        if (console) {
            System.out.println("Totals - Chunks: " + totalChunks + " Players: " + totalPlayers + " Items: " + totalItems + " Monsters: " + totalMobs + " Animals: " + totalAnimals);                
        } else {
            if (player !=null) {
                player.sendMessage("---------------------------------------------------------------------------------------------");
                player.sendMessage(ChatColor.RED + "Totals" + ChatColor.WHITE + " - Chunks: " + ChatColor.RED + totalChunks + ChatColor.WHITE + " Players: " + ChatColor.GREEN + totalPlayers + ChatColor.WHITE + " Items: " + ChatColor.AQUA + totalItems + ChatColor.WHITE + " Monsters: " + ChatColor.GOLD + totalMobs + ChatColor.WHITE + " Animals: " + ChatColor.DARK_AQUA + totalAnimals);
            }
        }
    }
}
