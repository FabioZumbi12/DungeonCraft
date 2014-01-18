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
import de.keyle.dungeoncraft.dungeon.*;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntrance;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntranceRegistry;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.DungeonRegion;
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.group.GroupManager;
import de.keyle.dungeoncraft.util.Configuration;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;

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
        Location eventTo = event.getTo();
        if (eventTo.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(eventTo.getChunk().getX(), eventTo.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                Vector playerPoint = new Vector(eventTo.getBlockX(), eventTo.getBlockY(), eventTo.getBlockZ());
                DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                List<DungeonRegion> regionsAt = d.getRegionRegistry().getRegionsAt(playerPoint);
                for (DungeonRegion region : DungeonRegion.getPlayerRegions(dungeonCraftPlayer)) {
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
                for (DungeonRegion region : regionsAt) {
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
        } else {
            DungeonEntrance entrance = DungeonEntranceRegistry.getEntranceAt(eventTo);
            if (entrance != null) {
                DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                Group group = GroupManager.getGroupByPlayer(dungeonCraftPlayer);
                if (group != null) {
                    if (DungeonManager.getDungeonFor(group) != null) {
                        Dungeon dungeon = DungeonManager.getDungeonFor(group);
                        if (dungeon != null) {
                            if (dungeon.isReady()) {
                                event.setCancelled(true);
                                dungeon.teleport(dungeonCraftPlayer);
                            } else {
                                event.getPlayer().sendMessage("The Dungeon isn't ready yet!");
                            }
                        } else {
                            event.getPlayer().sendMessage("The leader of your group must enter this dungeon first!");
                        }
                    } else {
                        if (group.getGroupLeader().equals(dungeonCraftPlayer)) {
                            DungeonBase base = entrance.getDungeonBase();
                            if (group.getGroupStrength() >= base.getMinPlayerCount()) {
                                if (group.getGroupStrength() >= base.getMinPlayerCount()) {
                                    Dungeon d = new Dungeon(entrance.getDungeonName(), entrance.getDungeonBase(), group);
                                    d.setExitLocation(entrance.getExitLocation());
                                    DungeonManager.addDungeon(d);
                                    event.getPlayer().sendMessage("Dungeon loading . . .");
                                } else {
                                    event.getPlayer().sendMessage("Your group is to small!");
                                }
                            } else {
                                event.getPlayer().sendMessage("Your group is to small! You need at least " + base.getMinPlayerCount() + " players.");
                            }
                        } else {
                            event.getPlayer().sendMessage("The leader of your group must enter this Dungeon first!");
                        }
                    }
                } else {
                    event.getPlayer().sendMessage("You can not enter a dungeon without a group!");
                }
                event.setCancelled(true);
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

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
        if (event.isCancelled() || event.getPlayer() == null) {
            return;
        }
        Player player = event.getPlayer();
        String cmd = event.getMessage().split("\\s+")[0];
        cmd.replaceAll("/", "");

        if (!player.isOp()) {
            if (player.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
                Dungeon dungeon = DungeonManager.getDungeonAt(DungeonFieldManager.getDungeonFieldForChunk(player.getLocation().getChunk().getX(), player.getLocation().getChunk().getZ()));
                if (dungeon != null) {
                    if (Configuration.ALLOWED_COMMANDS.contains(cmd)) {
                        return;
                    }
                    if (dungeon.getDungeonBase().getAllowedCommands().contains(cmd)) {
                        return;
                    }
                    event.setCancelled(true);
                    event.getPlayer().sendMessage("You are not allowed to use /" + cmd + " here!");
                }
            }
        }
    }
}