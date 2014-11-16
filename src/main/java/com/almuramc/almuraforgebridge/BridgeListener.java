/*
 * This file is part of Almura Control Panel.
 *
 * © 2013 AlmuraDev <http://www.almuradev.com/>
 * Almura Control Panel is licensed under the GNU General Public License.
 *
 * Almura Control Panel is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Almura Control Panel is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License. If not,
 * see <http://www.gnu.org/licenses/> for the GNU General Public License.
 */
package com.almuramc.almuracontrolpanel;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.plugin.java.JavaPlugin;

import me.zford.jobs.Jobs;

public class AlmuraListener extends JavaPlugin implements Listener{

	private static AlmuraControlPanel instance;

	public static AlmuraControlPanel getInstance() {
		return instance;
	}

	@EventHandler(priority=EventPriority.LOW, ignoreCancelled=true)
	public void onBlockPlace(BlockPlaceEvent event) {
		Block block = event.getBlock();
		if (block == null)
			return;

		// Stone Brick
		if (block.getType() == Material.BRICK) {	        	
			if (!Jobs.hasRequiredJobAndLevel(event.getPlayer(), "Builder", 10)) {
				messagePlayer(event.getPlayer(), "Builder", 10, block);
				event.setCancelled(true);
				return;
			}
		}

		// Stone Brick Stairs
		if (block.getType() == Material.BRICK_STAIRS) {	        	
			if (!Jobs.hasRequiredJobAndLevel(event.getPlayer(), "Builder", 10)) {
				messagePlayer(event.getPlayer(), "Builder", 10, block);
				event.setCancelled(true);
				return;
			}
		}

		// Glowstone
		if (block.getType() == Material.GLOWSTONE) {	        	
			if (!Jobs.hasRequiredJobAndLevel(event.getPlayer(), "Builder", 15)) {
				messagePlayer(event.getPlayer(), "Builder", 15, block);
				event.setCancelled(true);
				return;
			}
		}


	}

	public void messagePlayer(Player player, String job, int jobLevel, Block block) {
		player.sendMessage("[Jobs] - you need to be a " + job + ", lvl " + jobLevel + " in order to place block: " + block.getType().name());
	}
}