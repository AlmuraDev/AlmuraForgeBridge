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

import org.bukkit.event.world.ChunkLoadEvent;

import com.almuramc.forgebridge.BridgePlugin;
import org.bukkit.event.world.WorldUnloadEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;


public class WorldListener implements Listener {
    @EventHandler
    public void onWorldUnload(WorldUnloadEvent event) {        
        if (BridgePlugin.getInstance().debugMode) {
            System.out.println("[BRIDGE WORLD LISTENER] Unloading: " + event.getWorld().getName());
            Thread.dumpStack();
        }
    }

    @EventHandler
    public void onChunkLoad(ChunkLoadEvent event) {
        if (BridgePlugin.getInstance().debugMode) {
            System.out.println("[BRIDGE WORLD LISTENER] loading Chunk: " + event.getChunk().getX() + " / " + event.getChunk().getZ() + " | " + event.getWorld().getName());
            Thread.dumpStack();
        }
    }

}
