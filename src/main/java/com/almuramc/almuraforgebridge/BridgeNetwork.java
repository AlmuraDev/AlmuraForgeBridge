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
