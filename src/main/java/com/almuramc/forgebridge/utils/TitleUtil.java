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

import com.almuramc.forgebridge.BridgePlugin;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class TitleUtil {

    // Color setups for membership levels 
    private static String superadminColor = "" + ChatColor.DARK_RED;
    private static String superadmin = ChatColor.WHITE + "[" + superadminColor + "Super-Admin" + ChatColor.WHITE + "] ";

    private static String adminColor = "" + ChatColor.RED;
    private static String admin = ChatColor.WHITE + "[" + adminColor + "Admin" + ChatColor.WHITE + "] ";

    private static String spongeleaderColor1 = "" + ChatColor.GOLD;
    private static String spongeleaderColor2 = "" + ChatColor.BLUE;
    private static String spongeleader = ChatColor.WHITE + "[" + spongeleaderColor1 + "Sponge " + spongeleaderColor2 + "Leader" + ChatColor.WHITE + "] ";

    private static String moderatorColor = "" + ChatColor.BLUE;
    private static String moderator = ChatColor.WHITE + "[" + moderatorColor + "Moderator" + ChatColor.WHITE + "] ";
    
    private static String devColor = "" + ChatColor.DARK_PURPLE;
    private static String dev = ChatColor.WHITE + "[" + devColor + "SpongeDev" + ChatColor.WHITE + "] ";

    private static String veteranColor = "" + ChatColor.GOLD;
    private static String veteran = ChatColor.WHITE + "[" + veteranColor + "Veteran" + ChatColor.WHITE + "] ";

    private static String contributorColor = "" + ChatColor.DARK_AQUA;
    private static String contributor = ChatColor.WHITE + "[" + contributorColor + "Contributor" + ChatColor.WHITE + "] ";

    private static String memberColor = "" + ChatColor.DARK_GREEN;
    private static String member = ChatColor.WHITE + "[" + memberColor + "Member" + ChatColor.WHITE + "] ";

    private static String guestColor = "" + ChatColor.GRAY;
    private static String guest = ChatColor.WHITE + "[" + guestColor + "Guest" + ChatColor.WHITE + "] ";

    private static String survivalColor = "" + ChatColor.LIGHT_PURPLE;
    private static String survival = ChatColor.WHITE + "[" + survivalColor + "Survival" + ChatColor.WHITE + "] ";

    private static String newbieColor = "" + ChatColor.LIGHT_PURPLE;
    private static String newbie = ChatColor.WHITE + "[" + newbieColor + "Newbie" + ChatColor.WHITE + "] ";

    // Enter and Leave messages
    private static String enterMessage = ", has joined the server.";
    private static String leaveMessage = ", has left the server.";

    public static void broadcastLogin(Player player) {

        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            Bukkit.broadcastMessage(spongeleader + player.getDisplayName() + enterMessage);
            return;
        }

        if (!player.hasPlayedBefore()) {
            Bukkit.broadcastMessage(newbie + player.getDisplayName() + ", has joined the server for the First Time!");
            return;
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(superadmin + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] " + player.getDisplayName() + enterMessage);
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Harbinger..." + ChatColor.WHITE + "] " + player.getDisplayName() + enterMessage);
            } else {
                Bukkit.broadcastMessage(admin + player.getDisplayName() + enterMessage);
            }
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(moderator + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(veteran + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("dev.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(dev + player.getDisplayName() + enterMessage);
            return;
        }
        
        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(contributor + player.getDisplayName() + enterMessage);
            return;
        }


        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(member + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(guest + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(survival + player.getDisplayName() + enterMessage);
            return;
        }
    }

    public static void broadcastLogout(Player player) {

        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            Bukkit.broadcastMessage(spongeleader + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            Bukkit.broadcastMessage(superadmin + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] -  " + player.getDisplayName() + leaveMessage);
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Harbinger..." + ChatColor.WHITE + "] -  " + player.getDisplayName() + leaveMessage);
            } else {
                Bukkit.broadcastMessage(admin + player.getDisplayName() + leaveMessage);
            }
            return;
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(moderator + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            Bukkit.broadcastMessage(veteran + player.getDisplayName() + leaveMessage);
            return;
        }
        
        if (player.hasPermission("dev.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(dev + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(contributor + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Member.title") && !player.hasPermission("veteran.title")) {
            Bukkit.broadcastMessage(member + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Guest.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(guest + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("Member.title")) {
            Bukkit.broadcastMessage(survival + player.getDisplayName() + leaveMessage);
            return;
        }
    }

    public static String getCustomTitle(Player player) {        
        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            return (spongeleaderColor1 + "Sponge " + spongeleaderColor2 + "Leader");            
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            return (superadminColor + "SuperAdmin");
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                return (ChatColor.GOLD + "Destroyer of Worlds");
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                return (ChatColor.GOLD + "Harbinger");
            } else {
                return (adminColor + "Admin");
            }
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            return (moderatorColor + "Moderator");            
        }

        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            return (veteranColor + "Veteran");            
        }

        if (player.hasPermission("dev.title") && !player.hasPermission("veteran.title")) {
            return (contributorColor + "SpongeDev");            
        }
        
        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            return (contributorColor + "Contributor");            
        }

        if (player.hasPermission("member.title") && !player.hasPermission("contributor.title")) {
            return (memberColor + "Member");            
        }

        if (!player.hasPlayedBefore()) {
            return (newbieColor + "Newbie");            
        }

        if (player.hasPermission("guest.title") && !player.hasPermission("member.title")) {
            return (guestColor + "Guest");            
        }

        if (player.hasPermission("survival.title") && !player.hasPermission("member.title")) {
            return (survivalColor + "Survival");            
        }
        return null;

    }

    public static void sendDisplayName(Player player, String username, String displayName) {
        final ByteBuf buf = PacketUtil.createPacketBuffer(PacketUtil.DISCRIMINATOR_DISPLAY_NAME);
        PacketUtil.writeUTF8String(buf, username);
        PacketUtil.writeUTF8String(buf, displayName);       
        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, buf.array());
    }
    
    public static int permissionsLevel(Player player) {       
        
        if (player.hasPermission("admin.title") && player.isOp()) {
            return 1; // OP
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            return 2; // Admin
        }

        if (player.hasPermission("moderator.title") && !player.hasPermission("Admin.title")) {
            return 3; // Moderator            
        }

        if (player.hasPermission("dev.title") && !player.hasPermission("veteran.title")) {
            return 4; // Developer            
        }
        
        if (player.hasPermission("veteran.title") && !player.hasPermission("moderator.title")) {
            return 5; // Veteran or Contributor            
        }        
        
        if (player.hasPermission("contributor.title") && !player.hasPermission("veteran.title")) {
            return 5; // Contributor
        }

        if (player.hasPermission("member.title") && !player.hasPermission("contributor.title")) {
            return 6; // Member  
        }

        if (player.hasPermission("guest.title") && !player.hasPermission("member.title")) {
            return 7; // Guest
        }

        if (player.hasPermission("survival.title") && !player.hasPermission("member.title")) {
            return 8; // Survival
        }
        return 8;
    }
}
