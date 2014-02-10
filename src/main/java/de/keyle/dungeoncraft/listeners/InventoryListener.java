/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2013-2014 Keyle & xXLupoXx
 * DungeonCraft is licensed under the GNU Lesser General Public License.
 *
 * DungeonCraft is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * DungeonCraft is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.keyle.dungeoncraft.listeners;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;

public class InventoryListener implements Listener {
    @EventHandler
    public void onPlayerItemClick(final InventoryClickEvent event) {
        ItemStack item = event.getCurrentItem();
        if (Dungeon.isDungeonCraftItem(item) && !event.getWhoClicked().getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            event.setCurrentItem(null);
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onInventoryOpen(final InventoryOpenEvent event) {
        if (event.getInventory().getType() == InventoryType.ENDER_CHEST && event.getPlayer().getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            event.setCancelled(true);
        }
    }
}