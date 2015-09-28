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

    // Color setups for peasantship levels 
    private static String superadminColor = "" + ChatColor.DARK_RED;
    private static String superadmin = ChatColor.WHITE + "[" + superadminColor + "Ancient" + ChatColor.WHITE + "] ";

    private static String adminColor = "" + ChatColor.RED;
    private static String admin = ChatColor.WHITE + "[" + adminColor + "Ancient" + ChatColor.WHITE + "] ";

    private static String spongeleaderColor1 = "" + ChatColor.GOLD;
    private static String spongeleaderColor2 = "" + ChatColor.BLUE;
    private static String spongeleader = ChatColor.WHITE + "[" + spongeleaderColor1 + "Sponge " + spongeleaderColor2 + "Leader" + ChatColor.WHITE + "] ";

    private static String guardianColor = "" + ChatColor.BLUE;
    private static String guardian = ChatColor.WHITE + "[" + guardianColor + "Guardian" + ChatColor.WHITE + "] ";
    
    private static String devColor = "" + ChatColor.DARK_PURPLE;
    private static String dev = ChatColor.WHITE + "[" + devColor + "SpongeDev" + ChatColor.WHITE + "] ";

    private static String councilColor = "" + ChatColor.LIGHT_PURPLE;
    private static String council = ChatColor.WHITE + "[" + councilColor + "Council" + ChatColor.WHITE + "] ";
    
    private static String elderColor = "" + ChatColor.GOLD;
    private static String elder = ChatColor.WHITE + "[" + elderColor + "Elder" + ChatColor.WHITE + "] ";

    private static String protectorColor = "" + ChatColor.DARK_GREEN;
    private static String protector = ChatColor.WHITE + "[" + protectorColor + "Protector" + ChatColor.WHITE + "] ";

    private static String citizenColor = "" + ChatColor.GREEN;
    private static String citizen = ChatColor.WHITE + "[" + citizenColor + "Citizen" + ChatColor.WHITE + "] ";
    
    private static String peasantColor = "" + ChatColor.YELLOW;
    private static String peasant = ChatColor.WHITE + "[" + peasantColor + "Peasant" + ChatColor.WHITE + "] ";

    private static String visitorColor = "" + ChatColor.GRAY;
    private static String visitor = ChatColor.WHITE + "[" + visitorColor + "Visitor" + ChatColor.WHITE + "] ";

    private static String survivalColor = "" + ChatColor.LIGHT_PURPLE;
    private static String survival = ChatColor.WHITE + "[" + survivalColor + "Survival" + ChatColor.WHITE + "] ";

    private static String newbieColor = "" + ChatColor.LIGHT_PURPLE;
    private static String newbie = ChatColor.WHITE + "[" + newbieColor + "Newbie" + ChatColor.WHITE + "] ";

    // Enter and Leave messages
    private static String enterMessage = " has joined the server.";
    private static String leaveMessage = " has left the server.";

    public static void broadcastLogin(Player player) {

        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            Bukkit.broadcastMessage(spongeleader + player.getDisplayName() + enterMessage);
            return;
        }

        if (!player.hasPlayedBefore()) {
            Bukkit.broadcastMessage(newbie + player.getDisplayName() + " has joined the server for the first time!");
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

        if (player.hasPermission("guardian.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(guardian + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("council.title") && !player.hasPermission("guardian.title")) {
            Bukkit.broadcastMessage(council + player.getDisplayName() + enterMessage);
            return;
        }
        
        if (player.hasPermission("elder.title") && !player.hasPermission("council.title")) {
            Bukkit.broadcastMessage(elder + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("dev.title") && !player.hasPermission("elder.title")) {
            Bukkit.broadcastMessage(dev + player.getDisplayName() + enterMessage);
            return;
        }
        
        if (player.hasPermission("protector.title") && !player.hasPermission("elder.title")) {
            Bukkit.broadcastMessage(protector + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("Citizen.title") && !player.hasPermission("protector.title")) {
            Bukkit.broadcastMessage(citizen + player.getDisplayName() + enterMessage);
            return;
        }
        
        if (player.hasPermission("peasant.title") && !player.hasPermission("Citizen.title")) {
            Bukkit.broadcastMessage(peasant + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("visitor.title") && !player.hasPermission("peasant.title")) {
            Bukkit.broadcastMessage(visitor + player.getDisplayName() + enterMessage);
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("peasant.title")) {
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
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Destroyer of Worlds..." + ChatColor.WHITE + "] " + player.getDisplayName() + leaveMessage);
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                Bukkit.broadcastMessage(ChatColor.WHITE + "[" + ChatColor.GOLD + "The Harbinger..." + ChatColor.WHITE + "] " + player.getDisplayName() + leaveMessage);
            } else {
                Bukkit.broadcastMessage(admin + player.getDisplayName() + leaveMessage);
            }
            return;
        }

        if (player.hasPermission("guardian.title") && !player.hasPermission("Admin.title")) {
            Bukkit.broadcastMessage(guardian + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("council.title") && !player.hasPermission("guardian.title")) {
            Bukkit.broadcastMessage(council + player.getDisplayName() + leaveMessage);
            return;
        }
        
        if (player.hasPermission("elder.title") && !player.hasPermission("council.title")) {
            Bukkit.broadcastMessage(elder + player.getDisplayName() + leaveMessage);
            return;
        }
        
        if (player.hasPermission("dev.title") && !player.hasPermission("elder.title")) {
            Bukkit.broadcastMessage(dev + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("protector.title") && !player.hasPermission("elder.title")) {
            Bukkit.broadcastMessage(protector + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Citizen.title") && !player.hasPermission("elder.title")) {
            Bukkit.broadcastMessage(citizen + player.getDisplayName() + leaveMessage);
            return;
        }
        
        if (player.hasPermission("peasant.title") && !player.hasPermission("citizen.title")) {
            Bukkit.broadcastMessage(peasant + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("visitor.title") && !player.hasPermission("peasant.title")) {
            Bukkit.broadcastMessage(visitor + player.getDisplayName() + leaveMessage);
            return;
        }

        if (player.hasPermission("Survival.title") && !player.hasPermission("peasant.title")) {
            Bukkit.broadcastMessage(survival + player.getDisplayName() + leaveMessage);
            return;
        }
    }

    public static String getCustomTitle(Player player) {        
        if (player.getName().equalsIgnoreCase("ninjazidane")) {
            return (spongeleaderColor1 + "Sponge " + spongeleaderColor2 + "Leader");            
        }

        if (player.hasPermission("admin.title") && player.isOp()) {
            return (superadminColor + "Ancient");
        }

        if (player.hasPermission("admin.title") && !player.isOp()) {
            if (player.getName().equalsIgnoreCase("wifee")) {
                return (ChatColor.GOLD + "Destroyer of Worlds");
            } else if (player.getName().equalsIgnoreCase("wolfeyeamd0")) {
                return (ChatColor.GOLD + "Harbinger");
            } else {
                return (adminColor + "Ancient");
            }
        }

        if (player.hasPermission("guardian.title") && !player.hasPermission("Admin.title")) {
            return (guardianColor + "Guardian");            
        }

        if (player.hasPermission("council.title") && !player.hasPermission("guardian.title")) {
            return (councilColor + "Council");            
        }
        
        if (player.hasPermission("elder.title") && !player.hasPermission("council.title")) {
            return (elderColor + "Elder");            
        }

        if (player.hasPermission("dev.title") && !player.hasPermission("elder.title")) {
            return (protectorColor + "SpongeDev");            
        }
        
        if (player.hasPermission("protector.title") && !player.hasPermission("elder.title")) {
            return (protectorColor + "Protector");            
        }

        if (player.hasPermission("citizen.title") && !player.hasPermission("protector.title")) {
            return (citizenColor + "Citizen");            
        }
        
        if (player.hasPermission("peasant.title") && !player.hasPermission("citizen.title")) {
            return (peasantColor + "Peasant");            
        }

        if (!player.hasPlayedBefore()) {
            return (newbieColor + "Newbie");            
        }

        if (player.hasPermission("visitor.title") && !player.hasPermission("peasant.title")) {
            return (visitorColor + "Visitor");            
        }

        if (player.hasPermission("survival.title") && !player.hasPermission("peasant.title")) {
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

        if (player.hasPermission("guardian.title") && !player.hasPermission("Admin.title")) {
            return 3; // guardian            
        }

        if (player.hasPermission("dev.title") && !player.hasPermission("elder.title")) {
            return 4; // Developer            
        }
        
        if (player.hasPermission("elder.title") && !player.hasPermission("guardian.title")) {
            return 5; // elder or protector            
        }        
        
        if (player.hasPermission("protector.title") && !player.hasPermission("elder.title")) {
            return 5; // protector
        }

        if (player.hasPermission("citizen.title") && !player.hasPermission("protector.title")) {
            return 6; // peasant  
        }
        
        if (player.hasPermission("peasant.title") && !player.hasPermission("citizen.title")) {
            return 7; // peasant  
        }

        if (player.hasPermission("visitor.title") && !player.hasPermission("peasant.title")) {
            return 8; // visitor
        }

        if (player.hasPermission("survival.title") && !player.hasPermission("peasant.title")) {
            return 9; // Survival
        }
        return 10;
    }

    public static void sendClientDetailsRequest(Player player) {
        final ByteBuf buf = PacketUtil.createPacketBuffer(PacketUtil.DISCRIMINATOR_CLIENT_DETAILS_REQUEST);
        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, buf.array());
    }
}
