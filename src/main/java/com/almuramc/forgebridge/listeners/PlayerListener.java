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

import net.ess3.api.events.NickChangeEvent;

import org.anjocaido.groupmanager.events.GMUserEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
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
import com.almuramc.forgebridge.BridgePlugin;
import com.almuramc.forgebridge.utils.ServerWorldUtil;
import com.almuramc.forgebridge.utils.TitleUtil;
import com.almuramc.forgebridge.utils.VaultUtil;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceChangedEvent;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.bekvon.bukkit.residence.event.ResidenceFlagChangeEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.greatmancode.craftconomy3.tools.events.bukkit.events.EconomyChangeEvent;

public class PlayerListener implements Listener {

    // Group Manager's Change Event Listener
    @EventHandler(priority = EventPriority.LOWEST)
    public void onGMUserEvent(GMUserEvent userEvent) {
        final GMUserEvent event = userEvent;
        final Player player = event.getUser().getBukkitPlayer();
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
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

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    TitleUtil.sendDisplayName(player, player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));                        
                }
            }
        }, 5L); //Delayed so this Group Manager has time to change the players group.
    }

    // AlmuraMod's Information Wand.  Prints additional information from Bukkit side of server about the block that was just clicked to client's chat window.
    @SuppressWarnings("deprecation")
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
                    TitleUtil.sendDisplayName(player, player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                    if (!player.getName().equalsIgnoreCase(event.getPlayer().getName())) {                            
                        TitleUtil.sendDisplayName(event.getPlayer(), player.getName(), ChatColor.stripColor(event.getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(event.getPlayer()));
                    }
                }
            }
        }, 20L);
        VaultUtil.sendCurrencyAmount(event.getPlayer(), VaultUtil.economy.getBalance(event.getPlayer().getName()));
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
                    TitleUtil.sendDisplayName(player, player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                    if (!player.getName().equalsIgnoreCase(event.getPlayer().getName())) {                            
                        TitleUtil.sendDisplayName(event.getPlayer(), player.getName(), ChatColor.stripColor(event.getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(event.getPlayer()));
                    }
                }
            }
        }, 10L);
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
                    TitleUtil.sendDisplayName(player, player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));                        
                }
            }
        }, 10L);
    }    

    // Player Teleport event, send critical player/world/display name information to client for AlmuraMod's GUI
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerTeleport(final PlayerTeleportEvent event) {        
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                final ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                ServerWorldUtil.sendResidenceInfo(event.getPlayer(), res);

                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                    TitleUtil.sendDisplayName(player, player.getName(), ChatColor.stripColor(event.getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                    if (!player.getName().equalsIgnoreCase(event.getPlayer().getName())) {                            
                        TitleUtil.sendDisplayName(event.getPlayer(), player.getName(), ChatColor.stripColor(event.getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(event.getPlayer()));
                    }
                }
            }
        }, 10L);
    }

    // CraftConomies Player balance event listener, send critical player/world/display name information to client for AlmuraMod's GUI
    @SuppressWarnings("deprecation")
    @EventHandler
    public void onEconomyChange(EconomyChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getAccount());
        if (player != null) {
            VaultUtil.sendCurrencyAmount(player, event.getAmount());
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
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                if (event.getPlayer() != null) {
                    ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                    if (res != null) {
                        ServerWorldUtil.sendResidenceInfo(event.getPlayer(), res);
                    }
                }
            }
        }, 20L);
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
            }
        }
    }
}