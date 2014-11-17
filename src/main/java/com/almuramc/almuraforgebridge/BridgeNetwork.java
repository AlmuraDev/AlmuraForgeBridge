package com.almuramc.almuraforgebridge;

import org.bukkit.entity.Player;

import java.nio.ByteBuffer;

public class BridgeNetwork {
    public static final String CHANNEL = "AM|BUK";
    public static final int DISCRIMINATOR_DISPLAY_NAME = 0;
    public static final int DISCRIMINATOR_CURRENCY = 1;

    public static void sendDisplayName(Player player, String displayName) {
        final byte[] rawBytes = displayName.getBytes();
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_DISPLAY_NAME, rawBytes));
    }

    public static void sendCurrencyAmount(Player player, double amount) {
        final byte[] rawBytes = ((ByteBuffer) ByteBuffer.allocate(8).putDouble(amount).flip()).array();
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_CURRENCY, rawBytes));
    }

    private static byte[] prefixDiscriminator(int discriminator, byte[] value) {
        final ByteBuffer buf = ByteBuffer.allocate(value.length + 4);
        buf.putInt(discriminator);
        buf.put(value);
        buf.flip();
        return buf.array();
    }
}
