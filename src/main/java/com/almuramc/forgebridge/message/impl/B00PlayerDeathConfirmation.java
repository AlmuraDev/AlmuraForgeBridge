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

import net.minecraft.util.io.netty.buffer.ByteBufUtil;

import java.nio.charset.Charset;

import org.bukkit.Location;
import com.almuramc.forgebridge.BridgePlugin;
import com.almuramc.forgebridge.utils.ServerWorldUtil;
import com.almuramc.forgebridge.message.IPluginMessage;
import com.almuramc.forgebridge.message.IPluginMessageHandler;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

/**
 * Sent from Almura when a Player dies and has made a decision on accepting the death penalty.
 */
public class B00PlayerDeathConfirmation implements IPluginMessage, IPluginMessageHandler<B00PlayerDeathConfirmation, B00PlayerDeathConfirmation> {
    public boolean acceptsRespawnPenalty = false;
    private int x, y, z;
    private String world;
    private Player player;

    public B00PlayerDeathConfirmation() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        acceptsRespawnPenalty = buf.readBoolean();
        this.x = buf.readInt();
        this.y = buf.readInt();
        this.z = buf.readInt();       
        this.world = new String(buf.array(), Charset.forName("UTF-8"));
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(acceptsRespawnPenalty);
    }

    @Override
    public B00PlayerDeathConfirmation onMessage(B00PlayerDeathConfirmation message, Player source) {
        // TODO Player accepted the respawn penalty, what do?
        if (message.acceptsRespawnPenalty) {
            this.player = source;
            this.x = message.x;
            this.y = message.y;
            this.z = message.z;
            this.world = message.world;
            Bukkit.getLogger().info("Accepted Respawn Penalty? " + message.acceptsRespawnPenalty);
            
            Bukkit.getLogger().info("X: " + message.x + " Y: " + message.y + " Z: " + message.z + " World: " + message.world);
            Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
                @Override
                public void run() {
                    if (world.equalsIgnoreCase("Dakara")) {
                        Location location = new Location(Bukkit.getWorld("world"),x, y, z);
                        player.teleport(location);
                    } else {
                        Location location = new Location(Bukkit.getWorld(world),x, y, z);
                        player.teleport(location);
                    }                   
                }
            }, 10L);      
        }

        // TODO Just return null for now. Bridge may send return messages in the future.
        return null;
    }
}
