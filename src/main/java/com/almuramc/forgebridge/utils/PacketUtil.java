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
package com.almuramc.forgebridge.utils;

import java.nio.ByteBuffer;

import com.google.common.base.Charsets;

public class PacketUtil {
    public static final String CHANNEL = "AM|BUK";
    public static final byte DISCRIMINATOR_DISPLAY_NAME = 0;    
    public static final byte DISCRIMINATOR_ADDITIONAL_WORLD_INFORMATION = 2;
    public static final byte DISCRIMINATOR_RESIDENCE_INFO = 3;

    public static byte[] prefixDiscriminator(byte discriminator, byte[] value) {
        return ((ByteBuffer) ByteBuffer.allocate(value.length + 1).put(discriminator).put(value).flip()).array();
    }

    public static void writeVarInt(ByteBuffer buf, int value) {
        while ((value & -128) != 0)
        {
            buf.put((byte) (value & 127 | 128));
            value >>>= 7;
        }

        buf.put((byte) value);
    }

    public static void writeUTF8String(ByteBuffer buf, String value) {
        byte[] utf8Bytes = value.getBytes(Charsets.UTF_8);
        writeVarInt(buf, utf8Bytes.length);
        buf.put(utf8Bytes);
    }
}
