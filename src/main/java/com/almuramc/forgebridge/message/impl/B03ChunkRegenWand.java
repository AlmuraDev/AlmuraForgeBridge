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

import org.bukkit.block.BlockState;

import com.almuramc.forgebridge.message.IPluginMessage;
import com.almuramc.forgebridge.message.IPluginMessageHandler;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

/**
 * Sent from Almura when a Player uses the ChunkRegen wand.
 */
public class B03ChunkRegenWand implements IPluginMessage, IPluginMessageHandler<B03ChunkRegenWand, B03ChunkRegenWand> {
    private Player player;

    public B03ChunkRegenWand() {}

    @Override
    public void fromBytes(ByteBuf buf) {}

    @Override
    public void toBytes(ByteBuf buf) {}

    @Override
    public B03ChunkRegenWand onMessage(B03ChunkRegenWand message, Player source) {
        this.player = source;        
        if (player.hasPermission("admin.title")) {
            player.getWorld().regenerateChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ());
            
            for (BlockState te : player.getWorld().getChunkAt(player.getLocation()).getTileEntities()) {
                System.out.println("TE: " + te.getBlock() + " / " + te.getLocation());
            }

            player.sendMessage("[Regen Wand] - Chunk regenerated, saved: ");
        } else {
            player.sendMessage("[Regen Wand] - Insufficient Permissions.");
        }

        return null;
    }
}