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

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.api.events.PlayerEnterRegionEvent;
import de.keyle.dungeoncraft.api.events.PlayerLeaveRegionEvent;
import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.DungeonField;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.Region;
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.List;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getEntity());
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerDeathEvent.class);
                for (Trigger trigger : triggers) {
                    trigger.execute(dungeonCraftPlayer);
                }

                // cancel respawn screen
                final Location respawnLocation = d.getPlayerSpawnLoacation(dungeonCraftPlayer);
                final Player player = event.getEntity();
                player.teleport(respawnLocation);
                player.setHealth(player.getMaxHealth());
                Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        player.teleport(respawnLocation);
                        player.setHealth(player.getMaxHealth());
                    }
                }, 1L);
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
                DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerDropItemEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(dungeonCraftPlayer, event.getItemDrop())) {
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
                DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerInteractEntityEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(dungeonCraftPlayer, event.getRightClicked())) {
                        event.setCancelled(true);
                    }
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
                    DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                    List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerInteractEvent.class);
                    for (Trigger trigger : triggers) {
                        trigger.execute(dungeonCraftPlayer, event.getAction(), event.getClickedBlock(), event.getBlockFace());
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return;
        }
        Location l = event.getTo();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                Vector playerPoint = new Vector(l.getBlockX(), l.getBlockY(), l.getBlockZ());
                DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                List<Region> regionsAt = d.getRegionRegistry().getRegionsAt(playerPoint);
                for (Region region : Region.getPlayerRegions(dungeonCraftPlayer)) {
                    if (!region.isVectorInside(playerPoint)) {
                        PlayerLeaveRegionEvent regionEvent = new PlayerLeaveRegionEvent(d, dungeonCraftPlayer, region);
                        Bukkit.getPluginManager().callEvent(regionEvent);
                        if (regionEvent.isCancelled()) {
                            event.setCancelled(true);
                        } else {
                            region.removePlayer(dungeonCraftPlayer);
                        }
                    }
                }
                for (Region region : regionsAt) {
                    if (!region.getPlayers().contains(dungeonCraftPlayer)) {
                        PlayerEnterRegionEvent regionEvent = new PlayerEnterRegionEvent(d, dungeonCraftPlayer, region);
                        Bukkit.getPluginManager().callEvent(regionEvent);
                        if (regionEvent.isCancelled()) {
                            event.setCancelled(true);
                        } else {
                            region.addPlayer(dungeonCraftPlayer);
                        }
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerEnterRegion(final PlayerEnterRegionEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(PlayerEnterRegionEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute(event.getPlayer(), event.getDungeon(), event.getRegion());
        }
    }

    @EventHandler
    public void onPlayerLeaveRegion(final PlayerLeaveRegionEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(PlayerLeaveRegionEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute(event.getPlayer(), event.getDungeon(), event.getRegion());
        }
    }
}