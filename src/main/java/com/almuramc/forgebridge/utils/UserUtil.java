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
