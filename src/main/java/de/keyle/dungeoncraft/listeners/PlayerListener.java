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
import de.keyle.dungeoncraft.api.events.*;
import de.keyle.dungeoncraft.api.util.MessageException;
import de.keyle.dungeoncraft.dungeon.*;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntrance;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntranceRegistry;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.DungeonRegion;
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import de.keyle.dungeoncraft.party.PartyManager;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.Configuration;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.ItemStack;

import java.util.List;

import static de.keyle.dungeoncraft.api.party.Party.PartyType;

public class PlayerListener implements Listener {
    @EventHandler
    public void onPlayerDeath(final PlayerDeathEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getEntity());
            Dungeon d = dungeonCraftPlayer.getDungeon();
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerDeathEvent.class);
                for (Trigger trigger : triggers) {
                    trigger.execute(dungeonCraftPlayer.getName());
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
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
            Dungeon d = dungeonCraftPlayer.getDungeon();
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerDropItemEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(dungeonCraftPlayer.getName(), event.getItemDrop())) {
                        event.setCancelled(true);
                    }
                }
            }
        }

        if(Dungeon.isDungeonCraftItem(event.getItemDrop().getItemStack())) {
            if(Dungeon.isUndroppableItem(event.getItemDrop().getItemStack())) {
                event.setCancelled(true);
            } else {
                BukkitUtil.makeItemUndespawnable(event.getItemDrop());
            }
        }
    }

    @EventHandler
    public void onPlayerInteractEntity(final PlayerInteractEntityEvent event) {
        Location l = event.getPlayer().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
            Dungeon d = dungeonCraftPlayer.getDungeon();
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerInteractEntityEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(dungeonCraftPlayer.getName(), event.getRightClicked())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerInteract(final PlayerInteractEvent event) {
        Location l = event.getPlayer().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME) && event.getClickedBlock() != null) {
            Location clickedBlockLocation = event.getClickedBlock().getLocation();
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
            DungeonField blockPosition = DungeonFieldManager.getDungeonFieldForChunk(clickedBlockLocation.getChunk().getX(), clickedBlockLocation.getChunk().getZ());
            DungeonField position = dungeonCraftPlayer.getDungeon().getPosition();
            if (blockPosition.equals(position)) {
                Dungeon d = dungeonCraftPlayer.getDungeon();
                if (d != null) {
                    List<Trigger> triggers = d.getTriggerRegistry().getTriggers(PlayerInteractEvent.class);
                    for (Trigger trigger : triggers) {
                        trigger.execute(dungeonCraftPlayer.getName(), event.getAction(), event.getClickedBlock(), event.getBlockFace());
                    }
                }
            } else {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerMoveBlock(final PlayerMoveBlockEvent event) {
        Location eventTo = event.getTo();
        if (eventTo.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
            Dungeon d = dungeonCraftPlayer.getDungeon();
            if (d != null) {
                Vector playerPoint = new Vector(eventTo.getBlockX(), eventTo.getBlockY(), eventTo.getBlockZ());
                List<DungeonRegion> regionsAt = d.getRegionRegistry().getRegionsAt(playerPoint);
                for (DungeonRegion region : DungeonRegion.getPlayerRegions(dungeonCraftPlayer)) {
                    if (!region.isVectorInside(playerPoint)) {
                        PlayerRegionLeaveEvent regionEvent = new PlayerRegionLeaveEvent(d, dungeonCraftPlayer, region);
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
                        PlayerRegionEnterEvent regionEvent = new PlayerRegionEnterEvent(d, dungeonCraftPlayer, region);
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
                PartyType partyType = PartyManager.getPartyType(event.getPlayer());
                if (partyType != PartyType.NONE) {
                    DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(event.getPlayer());
                    Party party;
                    if (partyType == PartyType.DUNGEONCRAFT) {
                        party = dungeonCraftPlayer.getParty();
                    } else {
                        try {
                            party = PartyManager.newParty(dungeonCraftPlayer);
                        } catch (MessageException e) {
                            if (e.getMessageKey().equals("player.not.leader")) {
                                event.getPlayer().sendMessage(Locales.getString("Error.Leader.First", event.getPlayer()));
                                if(eventTo.getBlockY() < event.getFrom().getBlockY()) {
                                    event.getPlayer().teleport(entrance.getExitLocation());
                                } else {
                                    event.setCancelled(true);
                                }
                                return;
                            }
                            party = null;
                        }

                    }
                    if (party != null) {
                        if (DungeonManager.getDungeonFor(party) != null) {
                            Dungeon dungeon = DungeonManager.getDungeonFor(party);
                            if (dungeon != null) {
                                if (dungeon.isCompleted()) {
                                    event.getPlayer().sendMessage(Locales.getString("Message.Dungeon.Complete", event.getPlayer()));
                                } else if (dungeon.isReady()) {
                                    long lockout = dungeonCraftPlayer.getRemainingLockout(dungeon.getDungeonName());
                                    if (lockout > 0) {
                                        event.getPlayer().sendMessage(Util.formatText(Locales.getString("Message.Dungeon.Cooldown.Remaining", event.getPlayer()), DurationFormatUtils.formatDurationWords(lockout, true, true)));
                                    } else {
                                        event.setCancelled(true);
                                        dungeon.teleportIn(dungeonCraftPlayer);
                                        return;
                                    }
                                } else {
                                    event.getPlayer().sendMessage(Locales.getString("Error.Dungeon.Not.Ready", event.getPlayer()));
                                }
                            } else {
                                event.getPlayer().sendMessage(Locales.getString("Error.Leader.First", event.getPlayer()));
                            }
                        } else {
                            if (party.getPartyLeader().equals(dungeonCraftPlayer)) {
                                DungeonBase base = entrance.getDungeonBase();
                                long lockout = dungeonCraftPlayer.getRemainingLockout(entrance.getDungeonName());
                                if (lockout > 0) {
                                    event.getPlayer().sendMessage(Util.formatText(Locales.getString("Message.Dungeon.Cooldown.Remaining", event.getPlayer()), DurationFormatUtils.formatDurationWords(lockout, true, true)));
                                } else {
                                    if (party.getPartyStrength() >= base.getMinPlayerCount()) {
                                        Dungeon d = new Dungeon(entrance.getDungeonName(), entrance.getDungeonBase(), party);
                                        d.setExitLocation(entrance.getExitLocation());
                                        DungeonManager.addDungeon(d);
                                        event.getPlayer().sendMessage(Locales.getString("Message.Dungeon.Loading", event.getPlayer()));
                                    } else {
                                        event.getPlayer().sendMessage(Util.formatText("Error.Party.To.Small.Extended", event.getPlayer(), base.getMinPlayerCount()));
                                    }
                                }
                            } else {
                                event.getPlayer().sendMessage(Locales.getString("Error.Leader.First", event.getPlayer()));
                            }
                        }
                    } else {
                        event.getPlayer().sendMessage(Locales.getString("Error.No.Entry.Without.Party", event.getPlayer()));
                    }
                } else {
                    event.getPlayer().sendMessage(Locales.getString("Error.No.Entry.Without.Party", event.getPlayer()));
                }
                if(eventTo.getBlockY() < event.getFrom().getBlockY()) {
                    event.getPlayer().teleport(entrance.getExitLocation());
                } else {
                    event.setCancelled(true);
                }
            }
        }
    }


    @EventHandler
    public void onPlayerMove(final PlayerMoveEvent event) {
        if (event.getTo().getBlockX() == event.getFrom().getBlockX() && event.getTo().getBlockY() == event.getFrom().getBlockY() && event.getTo().getBlockZ() == event.getFrom().getBlockZ()) {
            return;
        }
        PlayerMoveBlockEvent moveBlockEvent = new PlayerMoveBlockEvent(event.getPlayer(), event.getFrom(), event.getTo());
        Bukkit.getPluginManager().callEvent(moveBlockEvent);
        event.setCancelled(moveBlockEvent.isCancelled());
    }

    @EventHandler
    public void onPlayerEnterRegion(final PlayerRegionEnterEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(PlayerRegionEnterEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute(event.getPlayer().getName(), event.getDungeon(), event.getRegion());
        }
    }

    @EventHandler
    public void onPlayerLeaveRegion(final PlayerRegionLeaveEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(PlayerRegionLeaveEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute(event.getPlayer().getName(), event.getDungeon(), event.getRegion());
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
                    event.getPlayer().sendMessage(Util.formatText(Locales.getString("Error.Commaned.Not.Allowed", event.getPlayer()), cmd));
                }
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(final PlayerQuitEvent event) {
        if (event.getPlayer().getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(event.getPlayer());
            if (player.getDungeon() != null) {
                player.getDungeon().teleportOut(player);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        Dungeon.clearDungeonCraftItems(event.getPlayer().getInventory());
    }

    @EventHandler
    public void onPlayerTeleport(final PlayerTeleportEvent event) {
        if (event.getTo().getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME) && !event.getFrom().getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(event.getPlayer());
            if (player.getDungeon() == null) {
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerLeaveDungeonEvent(final PlayerDungeonLeaveEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(PlayerDungeonLeaveEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute(event.getPlayer().getName());
        }

        //Remove the DungeonCraft Items
        ItemStack[] content = event.getPlayer().getPlayer().getInventory().getContents();
        ItemStack item;
        for(int i = 0; i < content.length;i++) {
            item = content[i];
            if(item != null && Dungeon.isDungeonCraftItem(item)) {
                event.getPlayer().getPlayer().getInventory().setItem(i, null);
                BukkitUtil.dropItem(event.getPlayer().getName(),item);
            }
        }
        event.getPlayer().getPlayer().updateInventory();
    }

    @EventHandler
    public void onPlayerEnterDungeonEvent(final PlayerDungeonEnterEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(PlayerDungeonEnterEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute(event.getPlayer().getName());
        }

        /*CraftWorld w = ((CraftWorld) DungeonCraftPlugin.getPlugin().getServer().getWorld(DungeonCraftWorld.WORLD_NAME));

        for(int x = 0; x < event.getDungeon().getDungeonBase().getSchematic().getLenght(); x++) {
           for(int z = 0; z < event.getDungeon().getDungeonBase().getSchematic().getWidth(); z++) {
               for(int y = 0; y < event.getDungeon().getDungeonBase().getSchematic().getHeight(); y ++)
               w.getHandle().worldProvider.b.m((event.getDungeon().getPosition().getX()) + x, y, (event.getDungeon().getPosition().getZ()) + z);
           }
        }
        */
    }

    @EventHandler
    public void onPlayerInteract2(final PlayerInteractEvent event) {
        Block b = event.getClickedBlock();

        if(b == null) {
            b = event.getPlayer().getTargetBlock(null, 100);
            DungeonCraftLogger.write("lfs x+1: " + b.getRelative(BlockFace.EAST).getLightFromSky());
            DungeonCraftLogger.write("lfb x+1: " + b.getRelative(BlockFace.EAST).getLightFromBlocks());
            DungeonCraftLogger.write("l x+1: " + b.getRelative(BlockFace.EAST).getLightLevel());
            DungeonCraftLogger.write("lfs x-1: " + b.getRelative(BlockFace.WEST).getLightFromSky());
            DungeonCraftLogger.write("lfb x-1: " + b.getRelative(BlockFace.WEST).getLightFromBlocks());
            DungeonCraftLogger.write("l x-1: " + b.getRelative(BlockFace.WEST).getLightLevel());
            DungeonCraftLogger.write("lfs z+1: " + b.getRelative(BlockFace.SOUTH).getLightFromSky());
            DungeonCraftLogger.write("lfb z+1: " + b.getRelative(BlockFace.SOUTH).getLightFromBlocks());
            DungeonCraftLogger.write("l z+1: " + b.getRelative(BlockFace.SOUTH).getLightLevel());
            DungeonCraftLogger.write("lfs z-1: " + b.getRelative(BlockFace.NORTH).getLightFromSky());
            DungeonCraftLogger.write("lfb z-1: " + b.getRelative(BlockFace.NORTH).getLightFromBlocks());
            DungeonCraftLogger.write("l z-1: " + b.getRelative(BlockFace.NORTH).getLightLevel());
        } else {
            DungeonCraftLogger.write("lfs: " + b.getRelative(event.getBlockFace()).getLightFromSky());
            DungeonCraftLogger.write("lfb: " + b.getRelative(event.getBlockFace()).getLightFromBlocks());
            DungeonCraftLogger.write("l: " + b.getRelative(event.getBlockFace()).getLightLevel());
        }
    }

    @EventHandler
    public void onPlayerDamageByPlayer(final EntityDamageByEntityEvent event) {
        if (!(event.getEntity() instanceof Player) || !(event.getDamager() instanceof Player)) {
            return;
        }
        Player damager = (Player) event.getDamager();
        Player victim = (Player) event.getEntity();
        if (!DungeonCraftPlayer.isDungeonCraftPlayer(damager) || !DungeonCraftPlayer.isDungeonCraftPlayer(victim)) {
            return;
        }
        DungeonCraftPlayer dungeonDamager = DungeonCraftPlayer.getPlayer(damager);
        DungeonCraftPlayer dungeonVictim = DungeonCraftPlayer.getPlayer(victim);

        Party p = dungeonVictim.getParty();
        if (p != null && p instanceof DungeonCraftParty) {
            DungeonCraftParty party = (DungeonCraftParty) p;
            if (!party.isFriendlyFireEnabled() && dungeonDamager.getParty().equals(party)) {
                event.setCancelled(true);
            }
        }
    }
}