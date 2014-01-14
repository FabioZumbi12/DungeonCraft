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
import de.keyle.dungeoncraft.dungeon.DungeonField;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerRespawnEvent;

import java.util.List;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerDeathEvent.class);
                for (Trigger trigger : triggers) {
                    trigger.execute(event.getEntity());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerDropItem(final PlayerDropItemEvent event) {
        Location l = event.getPlayer().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerDropItemEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(event.getPlayer(), event.getItemDrop())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        Location l = event.getPlayer().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerInteractEntityEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(event.getPlayer(), event.getRightClicked())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerRespawn(final PlayerRespawnEvent event) {
        Location l = event.getPlayer().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerRespawnEvent.class);
                for (Trigger trigger : triggers) {
                    trigger.execute(event.getPlayer(), event.getRespawnLocation());
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Location l = event.getPlayer().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            Location clickedBlockLocation = event.getClickedBlock().getLocation();
            DungeonField blockPosition = DungeonFieldManager.getDungeonFieldForChunk(clickedBlockLocation.getChunk().getX(), clickedBlockLocation.getChunk().getZ());
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            if (blockPosition.equals(position)) {
                Dungeon d = DungeonManager.getDungeonAt(position);
                if (d != null) {
                    List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerInteractEvent.class);
                    for (Trigger trigger : triggers) {
                        trigger.execute(event.getPlayer(), event.getAction(), event.getClickedBlock(), event.getBlockFace());
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }
}