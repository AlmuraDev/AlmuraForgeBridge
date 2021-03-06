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
package com.almuramc.forgebridge;

import com.almuramc.forgebridge.message.impl.B03ChunkRegenWand;

import com.almuramc.forgebridge.listeners.WorldListener;
import com.almuramc.forgebridge.message.impl.B02ClientDetailsResponse;
import org.bukkit.OfflinePlayer;
import com.almuramc.forgebridge.utils.TitleUtil;
import org.bukkit.ChatColor;
import com.almuramc.forgebridge.message.impl.B01ResTokenConfirmation;
import com.almuramc.forgebridge.listeners.EconListener;
import com.almuramc.forgebridge.listeners.EntityListener;
import com.almuramc.forgebridge.listeners.PlayerListener;
import com.almuramc.forgebridge.message.IPluginMessage;
import com.almuramc.forgebridge.message.IPluginMessageHandler;
import com.almuramc.forgebridge.message.MessageRegistar;
import com.almuramc.forgebridge.message.impl.B00PlayerDeathConfirmation;
import com.almuramc.forgebridge.utils.PacketUtil;
import com.almuramc.forgebridge.utils.ServerWorldUtil;
import com.google.common.base.Optional;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import net.minecraft.util.io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.logging.Level;

public class BridgePlugin extends JavaPlugin implements Listener, PluginMessageListener {

    private static BridgePlugin instance;
    public boolean debugMode = false;

    public static BridgePlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        instance = this;
        BridgeConfiguration.reloadConfig();
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PacketUtil.CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(this, PacketUtil.CHANNEL, this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new EntityListener(), this);
        pm.registerEvents(new EconListener(), this);
        pm.registerEvents(new WorldListener(), this);
        // These packets have to be unique to the environment they are not coded per side.
        // DISCRIMINATOR_DISPLAY_NAME = 0;  
        // DISCRIMINATOR_CURRENCY = 1;
        // DISCRIMINATOR_ADDITIONAL_WORLD_INFORMATION = 2;
        // DISCRIMINATOR_RESIDENCE_INFO = 3;
        // [PlayerAccessory] = 4;
        // DISCRIMINATOR_GUI_CONTROLLER = 5;
        MessageRegistar.registerMessage(B00PlayerDeathConfirmation.class, B00PlayerDeathConfirmation.class, 6);
        MessageRegistar.registerMessage(B01ResTokenConfirmation.class, B01ResTokenConfirmation.class, 7);
        MessageRegistar.registerMessage(B02ClientDetailsResponse.class, B02ClientDetailsResponse.class, 9);
        MessageRegistar.registerMessage(B03ChunkRegenWand.class, B03ChunkRegenWand.class, 10);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("bridge")) {
            sender.sendMessage("[Almura Bridge] - missing arguments.");
            return false;
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("title")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.info")) {

                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        ServerWorldUtil.sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().size(), Bukkit.getMaxPlayers());
                        TitleUtil.sendDisplayName(player, sender.getName(), ChatColor.stripColor(((OfflinePlayer) sender).getPlayer().getDisplayName()) + "\n" + TitleUtil.getCustomTitle(((OfflinePlayer) sender).getPlayer()));                                                
                        TitleUtil.sendDisplayName(((OfflinePlayer) sender).getPlayer(), player.getName(), ChatColor.stripColor(player.getDisplayName()) + "\n" + TitleUtil.getCustomTitle(player));
                    }
                    Bukkit.getLogger().info("[Almura Bridge] - Sent Titles and World Info");
                    sender.sendMessage("[Almura Bridge] - Sent Titles");
                    return true;
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions.");
                    return false;
                }
            } else {
                ServerWorldUtil.displayInfo(null, true, false);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("info")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.info")) {
                    ServerWorldUtil.displayInfo((Player) sender, false, false);
                    return true;
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions.");
                    return false;
                }
            } else {
                ServerWorldUtil.displayInfo(null, true, false);
                return true;
            }
        }
        
        if (args.length > 0 && args[0].equalsIgnoreCase("regen")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.debug")) {
                    ((Player) sender).getWorld().regenerateChunk(((Player) sender).getLocation().getChunk().getX(),((Player) sender).getLocation().getChunk().getZ());
                    sender.sendMessage("[Almura Bridge] - Chunk Regened..");
                    return true;
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions.");
                    return false;
                }
            } else {
                //
                return false;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("debug")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.debug")) {
                    ServerWorldUtil.displayInfo((Player) sender, false, false);
                    return true;
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions.");
                    return false;
                }
            } else {
                ServerWorldUtil.displayInfo(null, true, true);
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("config")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.config")) {
                    BridgeConfiguration.reloadConfig();
                    sender.sendMessage("[Almura Bridge] - Configuration Reloaded.");
                    return true;
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions.");
                    return false;
                }
            } else {
                BridgeConfiguration.reloadConfig();
                return true;
            }
        }

        if (args.length > 0 && args[0].equalsIgnoreCase("consoledebug")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.config")) {
                    sender.sendMessage("[Almura Bridge] - Debug Mode: " + setDebug());
                    return true;
                } else {
                    sender.sendMessage("[Almura Bridge] - Insufficient Permissions.");
                    return false;
                }
            } else {
                Bukkit.getLogger().severe("[Almura Bridge] - Debug Mode: " + setDebug());
                return true;
            }
        }

        return false;
    }

    public boolean setDebug() {
        debugMode = !debugMode;
        Bukkit.getLogger().log(Level.SEVERE, "Debug Mode: " + debugMode);
        return debugMode;
    }

    @Override
    @SuppressWarnings("unchecked")
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if (debugMode) {
            System.out.println("Channel received! " + s);
            System.out.println("Dumping payload...");
            for (byte b : bytes) {
                System.out.println(b);
            }
        }
        if ("AM|BUK".equalsIgnoreCase(s)) {
            final ByteBuf buf = Unpooled.wrappedBuffer(bytes);
            byte discriminator;
            try {
                discriminator = buf.readByte();
            } catch (IndexOutOfBoundsException ignored) {
                Bukkit.getLogger().log(Level.SEVERE, "Channel [AM|BUK] sent message with no discriminator!");
                return;
            }
            //Bukkit.getLogger().info("Discriminator [" + discriminator + "] provided.");
            final Optional<IPluginMessage> optPluginMessage = MessageRegistar.fromDiscriminator(discriminator);
            if (optPluginMessage.isPresent()) {
                final IPluginMessage message = optPluginMessage.get();
                try {
                    message.fromBytes(buf);
                } catch (Exception e) {
                    getLogger().log(Level.SEVERE, "Could not decode Plugin Message [" + message.getClass().getSimpleName() + "]", e);
                    return;
                }

                final Optional<IPluginMessageHandler<?, ?>> optPluginMessageHandler = MessageRegistar.getHandler(message.getClass());
                if (optPluginMessage.isPresent()) {
                    final IPluginMessageHandler pluginMessageHandler = optPluginMessageHandler.get();
                    try {
                        pluginMessageHandler.onMessage(message, player);
                    } catch (Exception e) {
                        getLogger().log(Level.SEVERE, "Could not handle Plugin Message [" + message.getClass().getSimpleName() + " in Plugin "
                                + "Message Handler [" + pluginMessageHandler.getClass().getSimpleName() + "]", e);

                    }
                }
            }
        }
    }
}
