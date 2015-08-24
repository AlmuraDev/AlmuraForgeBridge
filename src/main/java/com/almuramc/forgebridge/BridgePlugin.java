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

import com.almuramc.forgebridge.listeners.EconListener;
import com.almuramc.forgebridge.listeners.EntityListener;
import com.almuramc.forgebridge.listeners.PlayerListener;
import com.almuramc.forgebridge.utils.PacketUtil;
import com.almuramc.forgebridge.utils.ServerWorldUtil;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class BridgePlugin extends JavaPlugin implements Listener {

    private static BridgePlugin instance;

    public static BridgePlugin getInstance() {
        return instance;
    }

    @Override
    public void onDisable() {}

    @Override
    public void onEnable() {
        instance = this;
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, PacketUtil.CHANNEL);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvents(new PlayerListener(), this);
        pm.registerEvents(new EntityListener(), this);
        pm.registerEvents(new EconListener(), this);
    }

    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!command.getName().equalsIgnoreCase("bridge")) {
            sender.sendMessage("[Almura Bridge] - missing arguments.");
            return false;
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

        if (args.length > 0 && args[0].equalsIgnoreCase("clearitems")) {
            if (sender instanceof Player) {
                if (sender.hasPermission("bridge.clearitems")) {
                    ServerWorldUtil.clearItems((Player) sender, false);
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
        return false;
    }
}
