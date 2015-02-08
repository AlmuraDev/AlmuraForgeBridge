/*
 * This file is part of Almura Forge Bridge.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
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
package com.almuramc.almuraforgebridge;

import com.google.common.base.Charsets;
import com.greatmancode.craftconomy3.tools.events.bukkit.events.EconomyChangeEvent;

import net.ess3.api.events.NickChangeEvent;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.RegisteredServiceProvider;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.event.ResidenceChangedEvent;
import com.bekvon.bukkit.residence.event.ResidenceCreationEvent;
import com.bekvon.bukkit.residence.event.ResidenceDeleteEvent;
import com.bekvon.bukkit.residence.event.ResidenceFlagChangeEvent;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import com.bekvon.bukkit.residence.protection.CuboidArea;
import com.bekvon.bukkit.residence.protection.ResidencePermissions;

import java.nio.ByteBuffer;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class BridgeNetwork implements Listener {
    public static final String CHANNEL = "AM|BUK";
    public static final byte DISCRIMINATOR_DISPLAY_NAME = 0;
    public static final byte DISCRIMINATOR_CURRENCY = 1;
    public static final byte DISCRIMINATOR_ADDITIONAL_WORLD_INFORMATION = 2;
    public static final byte DISCRIMINATOR_RESIDENCE_INFO = 3;
    private static final Locale LOCALE_EN = new Locale("en", "US");
    private static final NumberFormat FORMAT_NUMBER_EN = NumberFormat.getCurrencyInstance(LOCALE_EN);
    private static Economy economy;

    public BridgeNetwork() {
        final RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);
        if (economyProvider != null) {
            economy = economyProvider.getProvider();
        }
    }
    
    public static void sendDisplayName(Player player, String username, String displayName) {
        final ByteBuffer buf = ByteBuffer.allocate(username.getBytes(Charsets.UTF_8).length + displayName.getBytes(Charsets.UTF_8).length + 4);
        writeUTF8String(buf, username);
        writeUTF8String(buf, displayName);       
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_DISPLAY_NAME, ((ByteBuffer) buf.flip()).array()));
    }

    public static void sendCurrencyAmount(Player player, double amount) {
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_CURRENCY, ((ByteBuffer) ByteBuffer.allocate(8).putDouble(amount).flip()).array()));
    }

    public static void sendAdditionalWorldInfo(Player player, String worldName, int currentPlayers, int maxPlayers) {
        final ByteBuffer buf = ByteBuffer.allocate(worldName.getBytes(Charsets.UTF_8).length + 10);
        writeUTF8String(buf, worldName);
        buf.putInt(currentPlayers);
        buf.putInt(maxPlayers);
        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_ADDITIONAL_WORLD_INFORMATION, ((ByteBuffer) buf.flip()).array()));
    }

    public static void sendResidenceInfo(Player player, ClaimedResidence res) {
        System.out.println("Player: " + player.getName() + " Res: " + res);
        if (res == null) {
            final ByteBuffer buf = ByteBuffer.allocate(1);
            buf.put((byte) 0);
            player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_RESIDENCE_INFO, ((ByteBuffer) buf.flip()).array()));
            return;
        }

        final ResidencePermissions resperms = res.getPermissions();
        final OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(res.getOwner());

        final String resName = res.getName();
        final String ownersName = res.getOwner();
        String lastOnline = "";

        if (offlinePlayer != null) {
            if (offlinePlayer.isOnline()) {
                lastOnline = "Last Online: Now";
            } else {
                if (offlinePlayer.getLastPlayed() == 0) {
                    lastOnline = "Last Online: Unavailable";
                } else {
                    lastOnline = "Last Online: " + formatDateDiff(offlinePlayer.getLastPlayed());
                }
            }
        }

        String leaseCost = "";
        if (Residence.getLeaseManager().leaseExpires(resName)) {
            leaseCost = FORMAT_NUMBER_EN.format(leaseCost);
        } else {
            leaseCost = "No Cost.";
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

        final ByteBuffer buf = ByteBuffer.allocate(resName.getBytes(Charsets.UTF_8).length + ownersName.getBytes(Charsets.UTF_8).length + lastOnline.getBytes(Charsets.UTF_8).length + leaseCost.getBytes(Charsets.UTF_8).length + leaseExpires.getBytes(Charsets.UTF_8).length + resBoundsValue.getBytes(Charsets.UTF_8).length + resSize.getBytes(Charsets.UTF_8).length + bankVault.getBytes(Charsets.UTF_8).length + 55);
        buf.put((byte) 1);
        writeUTF8String(buf, resName);
        writeUTF8String(buf, ownersName);
        writeUTF8String(buf, lastOnline);
        writeUTF8String(buf, leaseCost);
        writeUTF8String(buf, leaseExpires);
        writeUTF8String(buf, resBoundsValue);
        writeUTF8String(buf, resSize);
        writeUTF8String(buf, bankVault);

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

        player.sendPluginMessage(BridgePlugin.getInstance(), CHANNEL, prefixDiscriminator(DISCRIMINATOR_RESIDENCE_INFO, ((ByteBuffer) buf.flip()).array()));

    }

    private static byte[] prefixDiscriminator(byte discriminator, byte[] value) {
        return ((ByteBuffer) ByteBuffer.allocate(value.length + 1).put(discriminator).put(value).flip()).array();
    }

    private static void writeVarInt(ByteBuffer buf, int value) {
        while ((value & -128) != 0)
        {
            buf.put((byte) (value & 127 | 128));
            value >>>= 7;
        }

        buf.put((byte) value);
    }

    private static void writeUTF8String(ByteBuffer buf, String value) {
        byte[] utf8Bytes = value.getBytes(Charsets.UTF_8);
        writeVarInt(buf, utf8Bytes.length);
        buf.put(utf8Bytes);
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

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @SuppressWarnings("deprecation")
            @Override
            public void run() {
                sendCurrencyAmount(event.getPlayer(),economy.getBalance(event.getPlayer().getName()));
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    sendDisplayName(player, event.getPlayer().getName(), event.getPlayer().getDisplayName());
                    sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers());
                    if (player != event.getPlayer()) {
                        sendDisplayName(event.getPlayer(), player.getName(), player.getDisplayName());
                    }
                }
            }
        }, 20L);
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerQuit(final PlayerQuitEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getServer().getOnlinePlayers()) {
                    sendAdditionalWorldInfo(player, player.getWorld().getName(), Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers());
                }
            }
        }, 40L);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChangedWorld(final PlayerChangedWorldEvent event) {
        sendAdditionalWorldInfo(event.getPlayer(), event.getPlayer().getWorld().getName(), Bukkit.getOnlinePlayers().length, Bukkit.getMaxPlayers());
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onNickChanged(NickChangeEvent event) {
        final Player p = Bukkit.getPlayer(event.getController().getName());
        if (p == null) {
            return;
        }

        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            @Override
            public void run() {
                for (Player player : Bukkit.getOnlinePlayers()) {
                    sendDisplayName(player, p.getName(), p.getDisplayName());
                }
            }
        }, 20L);
    }
    
    @EventHandler
    public void onEconomyChange(EconomyChangeEvent event) {
        Player player = Bukkit.getPlayer(event.getAccount());
        if (player != null) {
            sendCurrencyAmount(player, event.getAmount());
        }
    }
    
    
    @EventHandler
    public void onResidenceFlagChangeEvent(final ResidenceFlagChangeEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                if (res != null) {                    
                    for (Player player : res.getPlayersInResidence()) {
                        sendResidenceInfo(player, res);
                    }
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onResidenceChangedEvent(final ResidenceChangedEvent event) {        
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                if (res != null) {                    
                    for (Player player : res.getPlayersInResidence()) {
                        sendResidenceInfo(player, res);
                    }
                }
            }
        }, 20L);
    }

    @EventHandler
    public void onResidenceCreationEvent(final ResidenceCreationEvent event) {
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                if (res != null) {                    
                    for (Player player : res.getPlayersInResidence()) {
                        sendResidenceInfo(player, res);
                    }
                }
            }
        }, 20L);
    }
    
    @EventHandler
    public void onResidenceDeleteEvent(final ResidenceDeleteEvent event) {      
        Bukkit.getScheduler().scheduleSyncDelayedTask(BridgePlugin.getInstance(), new Runnable() {
            public void run() {
                ClaimedResidence res = Residence.getResidenceManager().getByLoc(event.getPlayer().getLocation());
                if (res != null) {                    
                    for (Player player : res.getPlayersInResidence()) {
                        sendResidenceInfo(player, res);
                    }
                }
            }
        }, 20L);
    }
}
