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

import org.bukkit.block.BlockState;

import org.bukkit.Chunk;
import com.almuramc.forgebridge.BridgePlugin;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class ServerWorldUtil {

    private static final Locale LOCALE_EN = new Locale("en", "US");
    private static final NumberFormat FORMAT_NUMBER_EN = NumberFormat.getCurrencyInstance(LOCALE_EN);

    public static void clearItems(Player player, boolean console) {
        int removedCount = 0;
        for (World w : Bukkit.getServer().getWorlds()) {
            for (Entity e : w.getEntities()) {
                if (e.isDead())
                    continue;
                if (e instanceof Item) {
                    e.remove();
                    removedCount++;
                }
            }
        }
        if (console) {
            Bukkit.getLogger().info("[Bridge Item Cleanup] - Removed: " + removedCount);
        } else {
            if (player != null) {
                player.sendMessage("[Bridge Item Cleanup] - Removed: " + removedCount);
            }
        }
    }

    public static void displayInfo(Player player, boolean console, boolean debug) {
        int mobcount = 0;
        int totalMobs = 0;
        int animalCount = 0;
        int totalAnimals = 0;
        int itemcount = 0;
        int totalItems = 0;
        int totalChunks = 0;
        int totalPlayers = 0;
        int tileEntities = 0;
        int totalTileEntities = 0;

        if (!console && player != null) {
            player.sendMessage("---------------------------------------------------------------------------------------------");
        }


        for (World w : Bukkit.getServer().getWorlds()) {

            for (Chunk c : w.getLoadedChunks()) {
                tileEntities = c.getTileEntities().length + tileEntities;
                if (debug) {
                    for (BlockState t : c.getTileEntities()) {
                        Bukkit.getLogger().info("TileEntity: " + t.getType() + "Location: " + t.getLocation());
                    }
                }

            }

            for (Entity e : w.getEntities()) {
                if (e.isDead())
                    continue;
                if (e instanceof Player) {
                } else if (e instanceof Monster) {
                    mobcount++;
                } else if (e instanceof Animals) {
                    animalCount++;
                } else if (e instanceof Item) {
                    itemcount++;
                }
                if (console) {
                    if (debug) {
                        Bukkit.getLogger().info("Entity: " + e.getType() + " Age: " + ((e.getTicksLived()/20)/60) + " minutes, Location: " + e.getLocation());
                    }                    
                }
            }
            if (console) {
                Bukkit.getLogger().info(w.getName() + " - Chunks: " + w.getLoadedChunks().length + " Players: " + w.getPlayers().size() + " Items: " + itemcount + "TE's: "+ tileEntities + " Mob: " + mobcount + " Animals: " + animalCount);
                //Bukkit.getLogger().info(w.getName() + " - Keep Spawn in Memory?: " + w.getKeepSpawnInMemory());
            } else {
                if (player !=null) {
                    player.sendMessage(ChatColor.DARK_GREEN + w.getName() + ChatColor.WHITE + " - Chunks: " + ChatColor.RED + w.getLoadedChunks().length + ChatColor.WHITE + " Players: " + ChatColor.GREEN + w.getPlayers().size() + ChatColor.WHITE + " Items: " + ChatColor.AQUA + itemcount+ ChatColor.WHITE + " TileEntities: " + ChatColor.AQUA + tileEntities + ChatColor.WHITE + " Mobs " + ChatColor.GOLD + mobcount + ChatColor.WHITE + " Animals: " + ChatColor.DARK_AQUA + animalCount);
                }
            }
            totalAnimals = totalAnimals + animalCount;
            totalMobs = totalMobs + mobcount;
            totalItems = totalItems + itemcount;
            totalChunks = totalChunks + w.getLoadedChunks().length;
            totalPlayers = totalPlayers + w.getPlayers().size();
            totalTileEntities = totalTileEntities + tileEntities;
            mobcount = 0;
            animalCount = 0;
            itemcount = 0;
            tileEntities = 0;
        }

        if (console) {
            Bukkit.getLogger().info("Totals - Chunks: " + totalChunks + " Players: " + totalPlayers + " Items: " + totalItems + " TE's:" + totalTileEntities + " Mobs: " + totalMobs + " Animals: " + totalAnimals);                
        } else {
            if (player !=null) {
                player.sendMessage("---------------------------------------------------------------------------------------------");
                player.sendMessage(ChatColor.RED + "Totals" + ChatColor.WHITE + " - Chunks: " + ChatColor.RED + totalChunks + ChatColor.WHITE + " Players: " + ChatColor.GREEN + totalPlayers + ChatColor.WHITE + " Items: " + ChatColor.AQUA + totalItems + ChatColor.WHITE + " TileEntities: " + ChatColor.AQUA + totalTileEntities + ChatColor.WHITE + " Mobs: " + ChatColor.GOLD + totalMobs + ChatColor.WHITE + " Animals: " + ChatColor.DARK_AQUA + totalAnimals);
            }
        }
    }

    public static void sendAdditionalWorldInfo(Player player, String worldName, int currentPlayers, int maxPlayers) {
        final ByteBuf buf = PacketUtil.createPacketBuffer(PacketUtil.DISCRIMINATOR_ADDITIONAL_WORLD_INFORMATION);
        PacketUtil.writeUTF8String(buf, getFormattedWorldName(worldName));
        PacketUtil.writeUTF8String(buf, worldName);
        buf.writeInt(currentPlayers);
        buf.writeInt(maxPlayers);
        buf.writeInt(TitleUtil.permissionsLevel(player));
        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, buf.array());
    }

    public static String getFormattedWorldName(String worldName) {
        if (worldName.equalsIgnoreCase("world")) {
            return "Asgard";
        }

        if (worldName.equalsIgnoreCase("dakara")) {
            return "Dakara";
        }

        if (worldName.equalsIgnoreCase("atlantis")) {
            return "Atlantis";
        }

        if (worldName.equalsIgnoreCase("celestis")) {
            return "Celestis";
        }

        if (worldName.equalsIgnoreCase("dim1")) {
            return "The End";
        }

        if (worldName.equalsIgnoreCase("dim-1")) {
            return "The Nether";
        }

        if (worldName.equalsIgnoreCase("dim-42")) {
            return "Outer";
        }

        if (worldName.equalsIgnoreCase("dim17")) {
            return "Wyverned";
        }

        if (worldName.equalsIgnoreCase("faerun")) {
            return "Faerun";
        }

        if (worldName.equalsIgnoreCase("netu")) {
            return "Netu";
        }

        if (worldName.equalsIgnoreCase("avalon")) {
            return "Avalon";
        }

        if (worldName.equalsIgnoreCase("keystone")) {
            return "Keystone";
        }

        if (worldName.equalsIgnoreCase("othala")) {
            return "Othala";
        }

        if (worldName.equalsIgnoreCase("redrock")) {
            return "Redrock";
        }

        if (worldName.equalsIgnoreCase("redrock_nether")) {
            return "Redrock Nether";
        }

        if (worldName.equalsIgnoreCase("tollana")) {
            return "Tollana";
        }

        if (worldName.equalsIgnoreCase("zeal")) {
            return "Zeal";
        }

        if (worldName.equalsIgnoreCase("othala")) {
            return "Othala";
        }
        
        if (worldName.equalsIgnoreCase("athos")) {
            return "Athos";
        }
        return "unknown";
    }

    @SuppressWarnings("deprecation")
    public static void sendResidenceInfo(Player player, ClaimedResidence res) {
        final ByteBuf buf = PacketUtil.createPacketBuffer(PacketUtil.DISCRIMINATOR_RESIDENCE_INFO);

        if (res == null) {
            buf.writeBoolean(false);
            player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, buf.array());
            return;
        }

        final ResidencePermissions resperms = res.getPermissions();
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(res.getOwner());

        final String resName = res.getName();
        final String ownersName = res.getOwner();
        String lastOnline = "";

        if (offlinePlayer != null) {
            if (offlinePlayer.isOnline()) {
                lastOnline = "is Online";
            } else {
                if (offlinePlayer.getLastPlayed() == 0) {
                    lastOnline = "Unavailable";
                } else {
                    lastOnline = "" + formatDateDiff(offlinePlayer.getLastPlayed());
                }
            }
        }

        String resLeaseCost = "";
        if (Residence.getLeaseManager().leaseExpires(resName)) {
            int leaseCost = Residence.getLeaseManager().getRenewCost(res);
            resLeaseCost = FORMAT_NUMBER_EN.format(leaseCost);
        } else {
            resLeaseCost = "No Cost.";
        }

        String leaseExpires;
        if (Residence.getLeaseManager().leaseExpires(resName)) {
            leaseExpires = Residence.getLeaseManager().getExpireTime(resName).toString();
        } else {
            leaseExpires = "Does not expire.";
        }

        String areaName = res.getAreaIDbyLoc(player.getLocation());
        String resBoundsValue = "";
        if (areaName != null) {
            CuboidArea area = res.getArea(areaName);
            if (area != null) {
                resBoundsValue = ChatColor.LIGHT_PURPLE + "X: " + area.getHighLoc().getBlockX() + " Y: " + area.getHighLoc().getBlockY() + " Z: " + area.getHighLoc().getBlockZ() + ChatColor.WHITE + " / " + ChatColor.LIGHT_PURPLE + " X: " + area.getLowLoc().getBlockX() + " Y: " + area.getLowLoc().getBlockY() + " Z: " + area.getLowLoc().getBlockZ();
            } else {
                resBoundsValue = "Res Boundary is null";
            }
        }

        String resSize;
        resSize = "" + ChatColor.DARK_GREEN + res.getTotalSize();

        String bankVault;
        bankVault = "" + ChatColor.GOLD + FORMAT_NUMBER_EN.format(res.getBank().getStoredMoney());

        buf.writeBoolean(true);
        PacketUtil.writeUTF8String(buf, resName);
        PacketUtil.writeUTF8String(buf, ownersName);
        PacketUtil.writeUTF8String(buf, lastOnline);
        PacketUtil.writeUTF8String(buf, resLeaseCost);
        PacketUtil.writeUTF8String(buf, leaseExpires);
        PacketUtil.writeUTF8String(buf, resBoundsValue);
        PacketUtil.writeUTF8String(buf, resSize);
        PacketUtil.writeUTF8String(buf, bankVault);

        buf.writeBoolean(resperms.playerHas(player.getName(), "move", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "build", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "bank", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "place", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "destroy", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "use", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "admin", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "butcher", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "mayor", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "container", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "pvp", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "tp", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "melt", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "ignite", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "firespread", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "bucket", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "form", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "lavaflow", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "waterflow", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "creeper", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "tnt", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "monsters", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "animals", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "fly", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "subzone", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "healing", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "piston", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "shear", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "egghatch", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "trample", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "soil", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "stormdamage", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "chat", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "safezone", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "mo-ambient", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "mo-aquatic", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "mo-monsters", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "mo-passive", true));
        buf.writeBoolean(resperms.playerHas(player.getName(), "thaumcraft-monsters", true));

        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, buf.array());
    }

    private static String formatDateDiff(long date) {
        Calendar c = new GregorianCalendar();
        c.setTimeInMillis(date);
        Calendar now = new GregorianCalendar();
        return formatDateDiff(now, c);
    }

    private static String formatDateDiff(Calendar fromDate, Calendar toDate) {
        boolean future = false;
        if (toDate.equals(fromDate)) {
            return ("now");
        }
        if (toDate.after(fromDate)) {
            future = true;
        }

        StringBuilder sb = new StringBuilder();
        int[] types = new int[]
                {
                Calendar.YEAR,
                Calendar.MONTH,
                Calendar.DAY_OF_MONTH,
                Calendar.HOUR_OF_DAY,
                Calendar.MINUTE,
                //Calendar.SECOND
                };
        String[] names = new String[]
                {
                ("year"),
                ("years"),
                ("month"),
                ("months"),
                ("day"),
                ("days"),
                ("hour"),
                ("hours"),
                ("minute"),
                ("minutes"),
                //("second"),
                //("seconds")
                };
        for (int i = 0; i < types.length; i++) {
            int diff = dateDiff(types[i], fromDate, toDate, future);
            if (diff > 0) {
                sb.append(" ").append(diff).append(" ").append(names[i * 2 + (diff > 1 ? 1 : 0)]);
            }
        }
        if (sb.length() == 0) {
            return "moment ago";
        }
        return sb.toString();
    }

    private static int dateDiff(int type, Calendar fromDate, Calendar toDate, boolean future) {
        int diff = 0;
        long savedDate = fromDate.getTimeInMillis();
        while ((future && !fromDate.after(toDate)) || (!future && !fromDate.before(toDate))) {
            savedDate = fromDate.getTimeInMillis();
            fromDate.add(type, future ? 1 : -1);
            diff++;
        }
        diff--;
        fromDate.setTimeInMillis(savedDate);
        return diff;
    }
}
