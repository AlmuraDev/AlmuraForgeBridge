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
package com.almuramc.forgebridge.message;

import com.google.common.base.Optional;
import com.google.common.collect.Maps;

import java.util.Map;

public class MessageRegistar {
    private static Map<Class<? extends IPluginMessage>, Byte> CLASSES_WITH_DISCRIMINATOR = Maps.newHashMap();
    private static Map<Class<? extends IPluginMessage>, Class<? extends IPluginMessageHandler<?, ?>>> HANDLERS_BY_CLASSES = Maps
            .newHashMap();

    public static <REQ extends IPluginMessage, REPLY extends IPluginMessage> void registerMessage(Class<? extends IPluginMessageHandler<REQ, REPLY>>
            messageHandler, Class<REQ> requestMessageType, int discriminator) {
        CLASSES_WITH_DISCRIMINATOR.put(requestMessageType, (byte) discriminator);
        HANDLERS_BY_CLASSES.put(requestMessageType, messageHandler);
    }

    @SuppressWarnings("unchecked")
    public static Optional<IPluginMessage> fromDiscriminator(byte disciminator) {
        Optional<IPluginMessage> optPluginMessage = Optional.absent();

        for (Map.Entry<Class<? extends IPluginMessage>, Byte> entry : CLASSES_WITH_DISCRIMINATOR.entrySet()) {
            if (entry.getValue().equals(disciminator)) {
                try {
                    optPluginMessage = (Optional<IPluginMessage>) Optional.of(entry.getKey().newInstance());
                } catch (Exception e) {
                    break;
                }
            }
        }

        return optPluginMessage;
    }

    @SuppressWarnings("unchecked")
    public static Optional<IPluginMessageHandler<?, ?>> getHandler(Class<? extends IPluginMessage> clazz) {
        Optional<IPluginMessageHandler<?, ?>> optPluginMessageHandler = Optional.absent();
        final Class<IPluginMessageHandler<?, ?>> pluginMessageHandlerClass = (Class<IPluginMessageHandler<?, ?>>) HANDLERS_BY_CLASSES.get(clazz);

        if (pluginMessageHandlerClass != null) {
            try {
                optPluginMessageHandler = Optional.<IPluginMessageHandler<?,?>>of((IPluginMessageHandler) pluginMessageHandlerClass.newInstance());
            } catch (Exception e) {
                // Ignore
            }
        }

        return optPluginMessageHandler;
    }
}
