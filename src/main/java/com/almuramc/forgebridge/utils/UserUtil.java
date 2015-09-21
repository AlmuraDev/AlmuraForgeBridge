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

import org.anjocaido.groupmanager.GroupManager;
import org.anjocaido.groupmanager.data.Group;
import org.anjocaido.groupmanager.data.User;
import org.anjocaido.groupmanager.dataholder.OverloadedWorldHolder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;


public class UserUtil {
    public static boolean changeUserGroup(Player player, String groupName) {
        OverloadedWorldHolder dataHolder = null;

        if (player != null) {
            dataHolder = ((GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldData(player);
        }

        if (dataHolder != null) {
            Group auxGroup = dataHolder.getGroup(groupName);
            User auxUser = dataHolder.getUser(player.getName());
            if (auxGroup != null && auxUser != null) {
                auxUser.setGroup(auxGroup);
                return true;
            }
        }
        return false;
    }

    public static boolean addUserPermission(Player player, String permission) {
        OverloadedWorldHolder dataHolder = null;

        if (player != null) {
            dataHolder = ((GroupManager) Bukkit.getPluginManager().getPlugin("GroupManager")).getWorldsHolder().getWorldData(player);
        }

        if (dataHolder != null) {
            User auxUser = dataHolder.getUser(player.getName());
            if (!auxUser.hasSamePermissionNode(permission)) {
                auxUser.addPermission(permission);
                return true;
            }
        }
        return false;
    }
}
