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
package com.almuramc.almuraforgebridge;

import com.google.common.base.Charsets;
import net.ess3.api.events.NickChangeEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import java.nio.ByteBuffer;

public class BridgeNetwork implements Listener {
    public static final String CHANNEL = "AM|BUK";
    public static final byte DISCRIMINATOR_DISPLAY_NAME = 0;
    public static final byte DISCRIMINATOR_CURRENCY = 1;

    public static void sendDisplayName(Player player, String displayName) {
        final ByteBuffer buf = ByteBuffer.allocate(displayName.getBytes(Charsets.UTF_8).length + 2);
        writeUTF8String(buf, displayName);
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_DISPLAY_NAME, ((ByteBuffer) buf.flip()).array()));
    }

    public static void sendCurrencyAmount(Player player, double amount) {
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_CURRENCY, ((ByteBuffer) ByteBuffer.allocate(8).putDouble(amount).flip()).array()));
    }

    private static byte[] prefixDiscriminator(byte discriminator, byte[] value) {
        return ((ByteBuffer) ByteBuffer.allocate(value.length + 1).put(discriminator).put(value).flip()).array();
    }

    private static void writeVarInt(ByteBuffer buf, int value) {
        while ((value & -128) != 0)
        {
            buf.put((byte) (value & 127 | 128));
            value >>>= 7;
        }

        buf.put((byte) value);
    }

    private static void writeUTF8String(ByteBuffer buf, String value) {
        byte[] utf8Bytes = value.getBytes(Charsets.UTF_8);
        writeVarInt(buf, utf8Bytes.length);
        buf.put(utf8Bytes);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                sendDisplayName(event.getPlayer(), event.getPlayer().getDisplayName());
            }
        }, 20L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNickChanged(NickChangeEvent event) {
        final Player player = Bukkit.getPlayer(event.getAffected().getName());
        if (player == null) {
            return;
        }
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                sendDisplayName(player, player.getDisplayName());
            }
        }, 20L);
    }
}
