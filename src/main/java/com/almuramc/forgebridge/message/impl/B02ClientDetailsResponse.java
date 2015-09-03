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
package com.almuramc.forgebridge.message.impl;

import com.almuramc.forgebridge.BridgeConfiguration;

import com.almuramc.forgebridge.message.IPluginMessage;
import com.almuramc.forgebridge.message.IPluginMessageHandler;
import com.almuramc.forgebridge.utils.PacketUtil;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;

public class B02ClientDetailsResponse implements IPluginMessage, IPluginMessageHandler<B02ClientDetailsResponse,B02ClientDetailsResponse> {

    public HashSet<String> names, modNames;

    public B02ClientDetailsResponse() {}

    @Override
    public void fromBytes(ByteBuf buf) {
        int count = buf.readInt();
        System.out.println("Client Details: Tweaker Count [" + count + "].");
        if (count > 0) {
            names = Sets.newHashSet();

            for (int i = 0; i < count; i++) {
                names.add(PacketUtil.readUTF8String(buf));
            }
        }

        count = buf.readInt();
        System.out.println("Client Details: Mod Count [" + count + "]");
        if (count > 0) {
            modNames = Sets.newHashSet();

            for (int i = 0; i < count; i++) {
                modNames.add(PacketUtil.readUTF8String(buf));
            }
        }
    }

    @Override
    public void toBytes(ByteBuf buf) {

    }

    @Override
    public B02ClientDetailsResponse onMessage(B02ClientDetailsResponse message, Player source) {
        if (source.isOp()) {
            return null;
        }
        if (message.names != null) {
            for (String name : message.names) {
                if (!BridgeConfiguration.isTweakerAllowed(name.toLowerCase())) {
                    Bukkit.getLogger().warning("Player: " + source.getName() + " tried to use mod" + name);
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (player.hasPermission("admin.title")) {
                            player.sendMessage("Player: " + source.getName() + " attempted to join while using tweaker: " + name);
                        }
                    }
                    source.kickPlayer("You are not allowed to use tweaker: " + name) ;
                    return null;
                }

            }
        }

        if (message.modNames != null) {
            for (String name : message.modNames) {
                if (!BridgeConfiguration.isModAllowed(name.toLowerCase())) {
                    Bukkit.getLogger().warning("Player: " + source.getName() + " tried to use mod: " + name);
                    for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                        if (player.hasPermission("admin.title")) {
                            player.sendMessage("Player: " + source.getName() + " attempted to join while using mod: " + name);
                        }
                    }
                    source.kickPlayer("You are not allowed to use mod: " + name) ;
                    return null;
                }
            }
        }
        return null;
    }
}
