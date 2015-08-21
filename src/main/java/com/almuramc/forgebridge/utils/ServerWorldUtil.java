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
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.entity.Animals;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;

import com.almuramc.forgebridge.BridgePlugin;
import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;
import com.google.common.base.Charsets;

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

        if (!console && player != null) {
            player.sendMessage("---------------------------------------------------------------------------------------------");
        }
        for (World w : Bukkit.getServer().getWorlds()) {
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
                    Bukkit.getLogger().info("Entity: " + e.getType() + " Age: " + ((e.getTicksLived()/20)/60) + " minutes, Location: " + e.getLocation());
                }
            }
            if (console) {
                Bukkit.getLogger().info(w.getName() + " - Chunks: " + w.getLoadedChunks().length + " Players: " + w.getPlayers().size() + " Items: " + itemcount + " Monsters: " + mobcount + " Animals: " + animalCount);
            } else {
                if (player !=null) {
                    player.sendMessage(ChatColor.DARK_GREEN + w.getName() + ChatColor.WHITE + " - Chunks: " + ChatColor.RED + w.getLoadedChunks().length + ChatColor.WHITE + " Players: " + ChatColor.GREEN + w.getPlayers().size() + ChatColor.WHITE + " Items: " + ChatColor.AQUA + itemcount + ChatColor.WHITE + " Monsters: " + ChatColor.GOLD + mobcount + ChatColor.WHITE + " Animals: " + ChatColor.DARK_AQUA + animalCount);
                }
            }
            totalAnimals = totalAnimals + animalCount;
            totalMobs = totalMobs + mobcount;
            totalItems = totalItems + itemcount;
            totalChunks = totalChunks + w.getLoadedChunks().length;
            totalPlayers = totalPlayers + w.getPlayers().size();
            mobcount = 0;
            animalCount = 0;
            itemcount = 0;
        }

        if (console) {
            Bukkit.getLogger().info("Totals - Chunks: " + totalChunks + " Players: " + totalPlayers + " Items: " + totalItems + " Monsters: " + totalMobs + " Animals: " + totalAnimals);                
        } else {
            if (player !=null) {
                player.sendMessage("---------------------------------------------------------------------------------------------");
                player.sendMessage(ChatColor.RED + "Totals" + ChatColor.WHITE + " - Chunks: " + ChatColor.RED + totalChunks + ChatColor.WHITE + " Players: " + ChatColor.GREEN + totalPlayers + ChatColor.WHITE + " Items: " + ChatColor.AQUA + totalItems + ChatColor.WHITE + " Monsters: " + ChatColor.GOLD + totalMobs + ChatColor.WHITE + " Animals: " + ChatColor.DARK_AQUA + totalAnimals);
            }
        }
    }

    public static void sendAdditionalWorldInfo(Player player, String worldName, int currentPlayers, int maxPlayers) {
        final ByteBuffer buf = ByteBuffer.allocate(worldName.getBytes(Charsets.UTF_8).length + 10);
        PacketUtil.writeUTF8String(buf, worldName);
        buf.putInt(currentPlayers);
        buf.putInt(maxPlayers);
        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, PacketUtil.prefixDiscriminator(PacketUtil.DISCRIMINATOR_ADDITIONAL_WORLD_INFORMATION, ((ByteBuffer) buf.flip()).array()));
    }

    @SuppressWarnings("deprecation")
    public static void sendResidenceInfo(Player player, ClaimedResidence res) {
        if (res == null) {
            final ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put((byte) 0);
            player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, PacketUtil.prefixDiscriminator(PacketUtil.DISCRIMINATOR_RESIDENCE_INFO, ((ByteBuffer) buf.flip()).array()));
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

        final ByteBuffer buf = ByteBuffer.allocate(resName.getBytes(Charsets.UTF_8).length + ownersName.getBytes(Charsets.UTF_8).length + lastOnline.getBytes(Charsets.UTF_8).length + resLeaseCost.getBytes(Charsets.UTF_8).length + leaseExpires.getBytes(Charsets.UTF_8).length + resBoundsValue.getBytes(Charsets.UTF_8).length + resSize.getBytes(Charsets.UTF_8).length + bankVault.getBytes(Charsets.UTF_8).length + 55);
        buf.put((byte) 1);
        PacketUtil.writeUTF8String(buf, resName);
        PacketUtil.writeUTF8String(buf, ownersName);
        PacketUtil.writeUTF8String(buf, lastOnline);
        PacketUtil.writeUTF8String(buf, resLeaseCost);
        PacketUtil.writeUTF8String(buf, leaseExpires);
        PacketUtil.writeUTF8String(buf, resBoundsValue);
        PacketUtil.writeUTF8String(buf, resSize);
        PacketUtil.writeUTF8String(buf, bankVault);

        buf.put((byte) (resperms.playerHas(player.getName(), "move", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "build", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "bank", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "place", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "destroy", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "use", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "admin", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "butcher", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "mayor", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "container", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "pvp", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "tp", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "melt", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "ignite", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "firespread", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "bucket", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "form", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "lavaflow", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "waterflow", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "creeper", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "tnt", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "monsters", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "animals", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "fly", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "subzone", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "healing", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "piston", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "shear", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "egghatch", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "trample", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "soil", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "stormdamage", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "chat", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "safezone", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "mo-ambient", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "mo-aquatic", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "mo-monsters", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "mo-passive", true) ? 1 : 0));
        buf.put((byte) (resperms.playerHas(player.getName(), "thaumcraft-monsters", true) ? 1 : 0));

        player.sendPluginMessage(BridgePlugin.getInstance(), PacketUtil.CHANNEL, PacketUtil.prefixDiscriminator(PacketUtil.DISCRIMINATOR_RESIDENCE_INFO, ((ByteBuffer) buf.flip()).array()));
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
