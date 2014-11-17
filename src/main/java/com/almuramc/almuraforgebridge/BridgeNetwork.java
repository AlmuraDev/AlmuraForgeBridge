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

import org.bukkit.entity.Player;

import java.nio.ByteBuffer;

public class BridgeNetwork {
    public static final String CHANNEL = "AM|BUK";
    public static final int DISCRIMINATOR_DISPLAY_NAME = 0;
    public static final int DISCRIMINATOR_CURRENCY = 1;

    public static void sendDisplayName(Player player, String displayName) {
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_DISPLAY_NAME, displayName.getBytes()));
    }

    public static void sendCurrencyAmount(Player player, double amount) {
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_CURRENCY, ((ByteBuffer) ByteBuffer.allocate(8).putDouble(amount).flip()).array()));
    }

    private static byte[] prefixDiscriminator(int discriminator, byte[] value) {
        return ((ByteBuffer) ByteBuffer.allocate(value.length + 4).putInt(discriminator).put(value).flip()).array();
    }
}
