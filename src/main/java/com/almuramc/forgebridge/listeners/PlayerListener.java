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

import com.almuramc.forgebridge.utils.GuiUtil;

import com.bekvon.bukkit.residence.event.ResidenceRenameEvent;
import com.bekvon.bukkit.residence.event.ResidenceOwnerChangeEvent;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import com.bekvon.bukkit.residence.event.ResidenceCommandEvent;
import net.ess3.api.events.NickChangeEvent;
import org.anjocaido.groupmanager.events.GMUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import com.almuramc.forgebridge.BridgePlugin;
import com.almuramc.forgebridge.utils.ServerWorldUtil;
import com.almuramc.forgebridge.utils.TitleUtil;
import com.almuramc.forgebridge.utils.EconUtil;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceChangedEvent;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.bekvon.bukkit.residence.event.ResidenceFlagChangeEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.greatmancode.craftconomy3.tools.events.bukkit.events.EconomyChangeEvent;

public class PlayerListener implements Listener {

    // Protect all guests from PVP damage regardless of location
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEntityDamage(EntityDamageEvent event) {
        if (event.isCancelled()) {
            return;
        }
        if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent attackevent = (EntityDamageByEntityEvent) event;            
            if (attackevent != null) {
                Entity damageSource = attackevent.getDamager();
                if (damageSource instanceof Player) {
                    Player attacker = (Player) damageSource;            
                    if (event.getEntity() instanceof Player && attacker instanceof Player) {
                        Player victim = (Player) event.getEntity();
                        if (victim.getWorld() != Bukkit.getServer().getWorld("WORLD")) {
                            return;
                        }

                        Location location = victim.getLocation();
                        boolean withinX = false;
                        boolean withinZ = false;

                        if (location.getX() >= 1259 && location.getX() <= 1868) {
                            withinX = true;
                        }

                        if (location.getZ() >=7650 && location.getZ() <= 8046) {
                            withinZ = true;
                        }

                        if (withinX && withinZ) {
                            event.setCancelled(true);
                            victim.sendMessage("["+ChatColor.DARK_AQUA + "Newbie Protection" + ChatColor.WHITE + "] - You have been protected against PVP damage because you are within the Newbie Area.");
                            attacker.sendMessage("["+ChatColor.DARK_AQUA + "Newbie Protection" + ChatColor.WHITE + "] - " + victim.getDisplayName() + " has been protected against PVP damage because they are within the Newbie Area.");
                        }
                    }
                }
            }
        }

        /*if (event instanceof EntityDamageByEntityEvent) {
            EntityDamageByEntityEvent attackevent = (EntityDamageByEntityEvent) event;            
            if (attackevent != null) {
                Entity attacker = attackevent.getDamager();
                if (attacker instanceof Player) { 
                    Player victim = (Player) event.getEntity();
                    if (victim instanceof Player) {
                        if (victim.hasPermission("guest.title") && !victim.hasPermission("member.title")) {
                            event.setCancelled(true);
                        }
                    }
                }
            }
        } */
    }

    // Group Manager's Change Event Listener
    @EventHandler(priority = EventPriority.LOWEST)
    public void onGMUserEvent(GMUserEvent userEvent) {
        final GMUserEvent event = userEvent;
        final Player resPlayer = event.getUser().getBukkitPlayer();
        if (resPlayer == null) { // Is null for offline players
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {                
                if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("contributor"))) {
                    Bukkit.broadcastMessage(
                            ChatColor.DARK_PURPLE + resPlayer.getDisplayName() + ChatColor.WHITE + " has been granted: [" + ChatColor.GOLD + event
                            .getUser().getGroupName() + ChatColor.WHITE + "]");
                    Bukkit.broadcastMessage(ChatColor.WHITE + "Almura Thanks " + ChatColor.GOLD + resPlayer.getDisplayName() + ChatColor.WHITE
                            + " for their donation.  It is very much appreciated.");
                }

                if ((GMUserEvent.Action.USER_GROUP_CHANGED == event.getAction()) && (event.getUser().getGroupName().equalsIgnoreCase("member"))) {
                    Bukkit.broadcastMessage(
                            ChatColor.DARK_PURPLE + resPlayer.getDisplayName() + ChatColor.WHITE + " has been promoted to: [" + ChatColor.GOLD
                            + event.getUser().getGroupName() + ChatColor.WHITE + "]");
                }

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    TitleUtil.sendDisplayName(player, resPlayer.getName(), ChatColor.stripColor(resPlayer.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(resPlayer));
                    TitleUtil.sendDisplayName(resPlayer, player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                }
            }
        }, 20L); //Delayed so this Group Manager has time to change the players group.
    }

    // AlmuraMod's Player Interact, many things happen here.
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        // AlmuraMod's Information Wand - Additional Chat information.
        if (event.getPlayer().getItemInHand().getType() == Material.getMaterial("ALMURA_WANDINFORMATION")) {
            if (event.getClickedBlock() != null) {
                event.getPlayer().sendMessage(ChatColor.ITALIC + "Block Information:");
                event.getPlayer().sendMessage(ChatColor.WHITE + "ID: " + ChatColor.RED + event.getClickedBlock().getTypeId());
                event.getPlayer().sendMessage(ChatColor.WHITE + "Material: " + ChatColor.GOLD + event.getClickedBlock().getType());
                event.getPlayer().sendMessage(ChatColor.WHITE + "MetaData: " + ChatColor.AQUA + event.getClickedBlock().getData());
                event.getPlayer().sendMessage(ChatColor.WHITE + "Biome: " + ChatColor.LIGHT_PURPLE + event.getClickedBlock().getBiome() + "\n");
            }
        }

        // Force open the Res Token Confirmation GUI @ the client.
        if (event.getPlayer().getItemInHand().getType() == Material.getMaterial("ALMURA_CURRENCYRESTOKEN")) {
            Bukkit.getLogger().info("[Res Tokens] - Player: " + event.getPlayer().getName() + " / " + event.getPlayer().getDisplayName() + " has attempted to use a res token at: " + event.getPlayer().getLocation());
            GuiUtil.openGui(event.getPlayer(), 1, 0);
        }

        // Banking System
        if (!event.getPlayer().getWorld().getName().equalsIgnoreCase("Othala") && event.getPlayer().getGameMode() != GameMode.CREATIVE && event.getClickedBlock() != null) {
            if (EconUtil.isBankingBlock(event.getClickedBlock())) {
                if (event.getPlayer().getItemInHand() == null || EconUtil.getCoinValue(event.getPlayer().getItemInHand())==0) {
                    event.getPlayer().sendMessage("[" + ChatColor.DARK_AQUA + "Coin Exchange" + ChatColor.WHITE + "] - please put your coins in your hand to deposit them. ");
                } else {                
                    if ((EconUtil.getCoinValue(event.getPlayer().getItemInHand())>0) && EconUtil.isBankingBlock(event.getClickedBlock())) {
                        int quantity = event.getPlayer().getItemInHand().getAmount();
                        double value = EconUtil.getCoinValue(event.getPlayer().getItemInHand());
                        double amountToDeposit = quantity * value;
                        EconUtil.add(event.getPlayer().getName(), amountToDeposit);
                        event.getPlayer().setItemInHand(new ItemStack(Material.AIR));
                        event.getPlayer().sendMessage("[" + ChatColor.DARK_AQUA + "Coin Exchange" + ChatColor.WHITE + "] - Deposited coins in the amount of: " + ChatColor.GOLD + EconListener.NUMBER_FORMAT.format(amountToDeposit));
                        Bukkit.getLogger().info("Deposited coins in the amount of: " + EconListener.NUMBER_FORMAT.format(amountToDeposit) + " for player: " + event.getPlayer().getName());
                    } 
                }
            }
        }
    }

    // Player Change World event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {

        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                ServerWorldUtil.sendResidenceInfo(event.getPlayer(), res);

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    TitleUtil.sendDisplayName(player, event.getPlayer().getName(), ChatColor.stripColor(event.getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(event.getPlayer()));
                    if (!player.getName().equalsIgnoreCase(event.getPlayer().getName())) {
                        TitleUtil.sendDisplayName(event.getPlayer(), player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                    }
                }
            }
        }, 10L);
    }

    // Player Join event, send critical player/world/display name information to client for AlmuraMod's GUI
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        event.setJoinMessage("");
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                ServerWorldUtil.sendResidenceInfo(event.getPlayer(), res);
                // Broadcast Login
                TitleUtil.broadcastLogin(event.getPlayer());
                // Send Title

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    TitleUtil.sendDisplayName(player, event.getPlayer().getName(), ChatColor.stripColor(event.getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(event.getPlayer()));                                                
                    TitleUtil.sendDisplayName(event.getPlayer(), player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                }
            }
        }, 20L);
        EconUtil.sendCurrencyAmount(event.getPlayer(), EconUtil.economy.getBalance(event.getPlayer().getName()));
    }
    // Player Quit event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        event.setQuitMessage("");
        TitleUtil.broadcastLogout(event.getPlayer());
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                }
            }
        }, 20L);
    }

    // Player Change Nickname event, send critical player/world/display name information to client for AlmuraMod's GUI
    @SuppressWarnings("deprecation")
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNickChanged(NickChangeEvent event) {
        final Player p = Bukkit.getPlayer(event.getController().getName());
        if (p == null) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    TitleUtil.sendDisplayName(player, p.getName(), ChatColor.stripColor(p.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(p));
                }
            }
        }, 20L);
    }    

    // CraftConomies Player balance event listener, send critical player/world/display name information to client for AlmuraMod's GUI
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEconomyChange(EconomyChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getAccount());
        if (player != null) {
            EconUtil.sendCurrencyAmount(player, event.getAmount());
        }
    }

    // Player Change Residence event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceFlagChangeEvent(final ResidenceFlagChangeEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                if (event.getPlayer() != null) {
                    ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                    if (res != null) {                   
                        for (Player player : res.getPlayersInResidence()) {
                            if (player != null) {
                                ServerWorldUtil.sendResidenceInfo(player, res);
                            }
                        }
                    }
                }
            }
        }, 20L);
    }

    // Player Change Residence event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceChangedEvent(final ResidenceChangedEvent event) {
        final Location location = event.getPlayer().getLocation();
        if (event.getPlayer() != null) {
            ClaimedResidence res = Residence.getResidenceManager().getByLoc(location);
            ServerWorldUtil.sendResidenceInfo(event.getPlayer(), res);                    
        }

    }

    // Residence Owner Changed event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceRenameEvent(final ResidenceRenameEvent event) {
        for (Player player : event.getResidence().getPlayersInResidence()) {
            if (player != null) {
                ServerWorldUtil.sendResidenceInfo(player, event.getResidence());
            }
        }
    }

    // Residence Owner Changed event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceOwnerChangeEvent(final ResidenceOwnerChangeEvent event) {
        for (Player player : event.getResidence().getPlayersInResidence()) {
            if (player != null) {
                ServerWorldUtil.sendResidenceInfo(player, event.getResidence());
            }
        }
    }

    // Residence Command event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceCommandEvent(final ResidenceCommandEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                if (event.getSender() != null) {
                    @SuppressWarnings("deprecation")
                    Player player = Bukkit.getPlayer(event.getSender().getName());                    
                    if (player != null) {
                        ClaimedResidence res = Residence.getResidenceManager().getByLoc(player.getLocation());
                        if (res != null) {
                            ServerWorldUtil.sendResidenceInfo(player, res);
                        }
                    }
                }
            }
        }, 10L);
    }

    // Player Change Residence event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceCreationEvent(final ResidenceCreationEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                if (event.getPlayer() != null) {                    
                    ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                    if (res != null) {                   
                        for (Player player : res.getPlayersInResidence()) {
                            if (player != null) {
                                ServerWorldUtil.sendResidenceInfo(player, res);
                            }
                        }
                    }
                }
            }
        }, 20L);
    }

    // Player Change Residence event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler
    public void onResidenceDeleteEvent(final ResidenceDeleteEvent event) {
        if (event.getPlayer() != null) {                    
            ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
            if (res != null) {                   
                for (Player player : res.getPlayersInResidence()) {
                    if (player != null) {
                        ServerWorldUtil.sendResidenceInfo(player, res);
                    }
                }
            } else {
                ServerWorldUtil.sendResidenceInfo(event.getPlayer(), res);
            }
        }
    }
}
