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
package com.almuramc.forgebridge;

import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class BridgeConfiguration {
    private static FileConfiguration config;
    private static final List<String> ALLOWED_TWEAKERS = new ArrayList<>();
    private static final List<String> ALLOWED_MODS = new ArrayList<>();

    private BridgeConfiguration() {}

    public static boolean isTweakerAllowed(String tweaker) {
        return ALLOWED_TWEAKERS.contains(tweaker);
    }

    public static boolean isModAllowed(String mod) {
        return ALLOWED_MODS.contains(mod);
    }

    public static void reloadConfig() {
        if (!new File(BridgePlugin.getInstance().getDataFolder(), "config.yml").exists()) {
            BridgePlugin.getInstance().saveDefaultConfig();
        }
        config = BridgePlugin.getInstance().getConfig();
        BridgePlugin.getInstance().reloadConfig();

        ALLOWED_TWEAKERS.clear();
        ALLOWED_MODS.clear();

        ALLOWED_TWEAKERS.addAll(config.getStringList("allowed-tweakers"));
        for (String tweaker : ALLOWED_TWEAKERS) {
            BridgePlugin.getInstance().getLogger().info("Allowed tweaker -> " + tweaker);
        }
        ALLOWED_MODS.addAll(config.getStringList("allowed-mods"));
        for (String mod : ALLOWED_MODS) {
            BridgePlugin.getInstance().getLogger().info("Allowed mod -> " + mod);
        }
    }
}
