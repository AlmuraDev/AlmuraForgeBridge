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
package com.almuramc.forgebridge.message.impl;

import org.bukkit.inventory.ItemStack;

import com.bekvon.bukkit.residence.Residence;
import com.bekvon.bukkit.residence.protection.ClaimedResidence;
import org.bukkit.Material;
import com.almuramc.forgebridge.BridgePlugin;
import com.almuramc.forgebridge.message.IPluginMessage;
import com.almuramc.forgebridge.message.IPluginMessageHandler;
import com.almuramc.forgebridge.utils.PacketUtil;
import net.minecraft.util.io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

/**
 * Sent from Almura when a Player dies and has made a decision on accepting the death penalty.
 */
public class B01ResTokenConfirmation implements IPluginMessage, IPluginMessageHandler<B01ResTokenConfirmation, B01ResTokenConfirmation> {
    public boolean useToken = false;
    
    public B01ResTokenConfirmation() {

    }

    @Override
    public void fromBytes(ByteBuf buf) {
        useToken = buf.readBoolean();    
    }

    @Override
    public void toBytes(ByteBuf buf) {
        buf.writeBoolean(useToken);
    }

    @Override
    public B01ResTokenConfirmation onMessage(B01ResTokenConfirmation message, Player source) {        
        // TODO Player accepted the respawn penalty, what do?        
        if (useToken && source.getItemInHand().getType() == Material.getMaterial("ALMURA_CURRENCYRESTOKEN")) {
            ClaimedResidence res = Residence.getResidenceManager().getByLoc(source.getLocation());
            if (res == null) {
                source.sendMessage("[Residence] - There is no residence at this location.");

            } else {
                if (Residence.getLeaseManager().leaseExpires(res.getName())) {
                    Residence.getLeaseManager().removeExpireTime(res.getName());
                    source.sendMessage("[Residence] - Lease Removed.");
                    if (source.getItemInHand().getAmount() == 1) {
                        source.setItemInHand(new ItemStack(Material.AIR));
                    } else {
                        source.getItemInHand().setAmount(source.getItemInHand().getAmount()-1);
                    }
                } else {
                    source.sendMessage("[Residence] - There is no lease on the current residence.");
                }
            }
        } 

        // TODO Just return null for now. Bridge may send return messages in the future.
        return null;
    }
}
