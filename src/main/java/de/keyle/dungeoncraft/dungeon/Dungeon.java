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

package de.keyle.dungeoncraft.dungeon;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.api.dungeon.Result;
import de.keyle.dungeoncraft.api.events.DungeonStartEvent;
import de.keyle.dungeoncraft.api.events.PlayerDungeonEnterEvent;
import de.keyle.dungeoncraft.api.events.PlayerDungeonLeaveEvent;
import de.keyle.dungeoncraft.api.util.Scheduler;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftChunkProvider;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.RegionRegistry;
import de.keyle.dungeoncraft.dungeon.scripting.TriggerRegistry;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.CustomInventory;
import de.keyle.dungeoncraft.util.Schedule;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;
import de.keyle.dungeoncraft.util.logger.DungeonLogger;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.apache.commons.lang.time.DurationFormatUtils;
import org.bukkit.*;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.util.Vector;

import java.util.*;

public class Dungeon implements Scheduler {
    private DungeonLoader loader = null;
    protected boolean isReady = false;
    protected boolean isLoading = false;
    protected boolean first = true;
    protected boolean isCompleted = false;
    protected boolean timeLock = false;
    protected boolean weather = false;
    protected long endTime = 0;
    protected int localTime = 0;
    protected Result result = Result.Running;
    protected Location exitLocation;
    protected Map<DungeonCraftPlayer, OrientationVector> playerSpawn = new HashMap<DungeonCraftPlayer, OrientationVector>();
    protected Map<DungeonCraftPlayer, CustomInventory> enderChests = new HashMap<DungeonCraftPlayer, CustomInventory>();
    protected Set<DungeonCraftPlayer> playersInDungeon = new HashSet<DungeonCraftPlayer>();
    protected final String dungeonName;
    protected final Party playerParty;
    protected final DungeonBase dungeonBase;
    protected final UUID uuid;
    protected final DungeonField position;
    protected final TriggerRegistry triggerRegistry;
    protected final RegionRegistry regionRegistry;
    protected final DungeonLogger dungeonLogger;
    protected final Scoreboard scoreboard;
    protected final Schedule schedule;

    public Dungeon(String dungeonName, DungeonBase dungeonTheme, Party party) {
        this.dungeonName = dungeonName;
        this.dungeonBase = dungeonTheme;
        uuid = UUID.randomUUID();
        position = DungeonFieldManager.getNewDungeonField();
        schedule = new Schedule();
        triggerRegistry = new TriggerRegistry();
        regionRegistry = new RegionRegistry();
        localTime = dungeonBase.getStartTime();
        weather = dungeonBase.getWeather();
        dungeonLogger = new DungeonLogger(this);
        playerParty = party;
        scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    }

    public TriggerRegistry getTriggerRegistry() {
        return triggerRegistry;
    }

    public RegionRegistry getRegionRegistry() {
        return regionRegistry;
    }

    public DungeonLogger getDungeonLogger() {
        return dungeonLogger;
    }

    public Schedule getSchedule() {
        return schedule;
    }

    public boolean isLoading() {
        return isLoading || loader != null;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public UUID getUuid() {
        return uuid;
    }

    public DungeonField getPosition() {
        return position;
    }

    public long getEndTime() {
        return endTime;
    }

    protected synchronized void setLoading() {
        isLoading = true;
        DungeonCraftPlayer partyLeader = playerParty.getPartyLeader();
        if (partyLeader.isOnline()) {
            partyLeader.getPlayer().sendMessage(Locales.getString("Message.Dungeon.Loading", partyLeader));
        }
    }

    protected synchronized void setReady() {
        isReady = true;
        dungeonLogger.info("Dungeon is now ready to use!");
    }

    public synchronized boolean isReady() {
        return isReady;
    }

    public Set<DungeonCraftPlayer> getPlayerList() {
        return Collections.unmodifiableSet(playersInDungeon);
    }

    public Party getPlayerParty() {
        return playerParty;
    }

    public Location getPlayerSpawnLoacation(DungeonCraftPlayer player) {
        OrientationVector ov = getPlayerSpawn(player);
        World world = DungeonCraftPlugin.getPlugin().getServer().getWorld(DungeonCraftWorld.WORLD_NAME);
        return new Location(world, ov.getX() + (position.getX() * 1600), ov.getY() + 0.5D, ov.getZ() + (position.getZ() * 1600), (float) ov.getYaw(), (float) ov.getPitch());
    }

    public OrientationVector getPlayerSpawn(DungeonCraftPlayer player) {
        if (!playerSpawn.containsKey(player)) {
            return dungeonBase.getSpawn();
        }
        return playerSpawn.get(player);
    }

    public void setPlayerSpawn(DungeonCraftPlayer player, OrientationVector pos) {
        playerSpawn.put(player, pos);
    }

    public Location getExitLocation() {
        if (exitLocation == null) {
            return Bukkit.getWorlds().get(0).getSpawnLocation();
        } else {
            return exitLocation;
        }
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
    }

    public Scoreboard getScoreboard() {
        return scoreboard;
    }

    public void setTime(int time) {
        if (time < 0) {
            setTimeLock(true);
            time = Math.abs(time);
        }
        localTime = time % 24000;
        updatePlayerTime();
    }

    public int getLocalTime() {
        return localTime;
    }

    public void setTimeLock(boolean timeLock) {
        this.timeLock = timeLock;
        updatePlayerTime();
    }

    public boolean isTimeLocked() {
        return timeLock;
    }

    public void updatePlayerTime() {
        int worldTime = (int) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME).getTime();
        int worldDifference = 24000 - worldTime + Math.abs(getLocalTime());
        for (DungeonCraftPlayer player : getPlayerList()) {
            if (player.isOnline()) {
                BukkitUtil.setPlayerTime(player.getPlayer(), worldDifference, isTimeLocked());
            }
        }
    }

    public void setWeather(boolean weather) {
        this.weather = weather;
        updatePlayerWeather();
    }

    public boolean getWeather() {
        return weather;
    }

    public void updatePlayerWeather() {
        for (DungeonCraftPlayer player : getPlayerList()) {
            if (player.isOnline()) {
                player.getPlayer().setPlayerWeather(getWeather() ? WeatherType.DOWNFALL : WeatherType.CLEAR);
            }
        }
    }

    public CustomInventory getEnderChest(final DungeonCraftPlayer player) {
        if (enderChests.containsKey(player)) {
            return enderChests.get(player);
        } else {
            CustomInventory newEnderChest = new CustomInventory("Dungeon Ender Chest", 27) {
                @Override
                public String getInventoryName() {
                    return "[" + ChatColor.RED + "DC" + ChatColor.RESET + "] " + Locales.getString("Terms.Tile.EnderChest", player);
                }
            };
            enderChests.put(player, newEnderChest);
            return newEnderChest;
        }
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void cleanUp() {
        if (!isCompleted) {
            return;
        }
        for (OfflinePlayer player : scoreboard.getPlayers()) {
            scoreboard.resetScores(player);
        }
        for (Objective objective : scoreboard.getObjectives()) {
            objective.unregister();
        }
        DungeonCraftChunkProvider.chunkloader.unloadDungeonField(position);
        enderChests.clear();

        dungeonLogger.info("--- Dungeon END ---");
        dungeonLogger.closeFileHandler();
    }

    public Result getResult() {
        return result;
    }

    public void completeDungeon(Result result) {
        dungeonLogger.info("Dungeon completet with status: " + result.name());
        this.result = result;
        isCompleted = true;
        for (DungeonCraftPlayer player : new HashSet<DungeonCraftPlayer>(getPlayerList())) {
            if (player.isOnline()) {
                teleportOut(player);
                player.getPlayer().resetPlayerTime();
            }
            player.setDungenLockout(this.getDungeonName(), getDungeonBase().getPlayerLockoutTime());
        }
        if (!(playerParty instanceof DungeonCraftParty)) {
            dungeonLogger.info("Party disbanded");
            playerParty.disbandParty();
        }
        schedule.cancelAllTasks();
    }

    public void teleportIn(DungeonCraftPlayer p) {
        if (!isCompleted() && p.isOnline() && playerParty.containsPlayer(p)) {
            playersInDungeon.add(p);
            p.setDungeon(this);
            p.getPlayer().teleport(getPlayerSpawnLoacation(p), PlayerTeleportEvent.TeleportCause.PLUGIN);
            p.getPlayer().setVelocity(new Vector());
            BukkitUtil.setPlayerEnvironment(p.getPlayer(), dungeonBase.getEnvironment());
            updatePlayerTime();
            updatePlayerWeather();
            // There must be another way to update the inventory properly
            p.getPlayer().updateInventory();

            Bukkit.getPluginManager().callEvent(new PlayerDungeonEnterEvent(this, p));
            if (endTime > 0) {
                p.getPlayer().sendMessage(Util.formatText(Locales.getString("Message.Dungeon.Time.Remaining", p), DurationFormatUtils.formatDurationWords(endTime - System.currentTimeMillis(), true, true)));
            }
        }
    }

    public void teleportOut(DungeonCraftPlayer p) {
        if (playersInDungeon.contains(p)) {
            playersInDungeon.remove(p);
            p.setDungeon(null);
            if (p.isOnline()) {
                p.getPlayer().teleport(exitLocation, PlayerTeleportEvent.TeleportCause.PLUGIN);
                p.getPlayer().setVelocity(new Vector());
                BukkitUtil.setPlayerEnvironment(p.getPlayer(), dungeonBase.getEnvironment());
                updatePlayerTime();
                updatePlayerWeather();
                clearDungeonCraftItems(p.getPlayer().getInventory());
            }
            Bukkit.getPluginManager().callEvent(new PlayerDungeonLeaveEvent(this, p));
        }
    }

    public static void clearDungeonCraftItems(Inventory inv) {
        ItemStack[] content = inv.getContents();
        ItemStack item;
        for (int i = 0; i < content.length; i++) {
            item = content[i];
            if (item != null && isDungeonCraftItem(item)) {
                inv.setItem(i, null);
            }
        }
    }

    public static boolean isDungeonCraftItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore.size() > 0 && lore.contains(ChatColor.RESET + "❱❱❱ " + ChatColor.DARK_RED + "D" + ChatColor.DARK_GRAY + "ungeon" + ChatColor.DARK_RED + "C" + ChatColor.DARK_GRAY + "raft" + ChatColor.GRAY + " Item" + ChatColor.RESET + " ❰❰❰")) {
                    return true;
                }
            }
        }
        return false;
    }

    public static void markAsDungeonCraftItem(ItemStack item) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<String>();
            }
            if (lore.size() > 0 && lore.contains(ChatColor.RESET + "❱❱❱ " + ChatColor.DARK_RED + "D" + ChatColor.DARK_GRAY + "ungeon" + ChatColor.DARK_RED + "C" + ChatColor.DARK_GRAY + "raft" + ChatColor.GRAY + " Item" + ChatColor.RESET + " ❰❰❰")) {
                return;
            }
            lore.add(ChatColor.RESET + "❱❱❱ " + ChatColor.DARK_RED + "D" + ChatColor.DARK_GRAY + "ungeon" + ChatColor.DARK_RED + "C" + ChatColor.DARK_GRAY + "raft" + ChatColor.GRAY + " Item" + ChatColor.RESET + " ❰❰❰");
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static void markAsUndroppableItem(ItemStack item) {
        if (item != null) {
            ItemMeta meta = item.getItemMeta();
            List<String> lore = meta.getLore();
            if (lore == null) {
                lore = new ArrayList<String>();
            }
            if (lore.size() > 0 && lore.contains(ChatColor.RESET + "   ▶▶▶" + ChatColor.DARK_BLUE + "U" + ChatColor.BLUE + "ndroppable" + ChatColor.RESET)) {
                return;
            }
            lore.add(ChatColor.RESET + "   ▶▶▶" + ChatColor.DARK_BLUE + "U" + ChatColor.BLUE + "ndroppable" + ChatColor.RESET);
            meta.setLore(lore);
            item.setItemMeta(meta);
        }
    }

    public static boolean isUndroppableItem(ItemStack item) {
        if (item != null && item.hasItemMeta()) {
            ItemMeta meta = item.getItemMeta();
            if (meta.hasLore()) {
                List<String> lore = meta.getLore();
                if (lore.size() > 0 && lore.contains(ChatColor.RESET + "   ▶▶▶" + ChatColor.DARK_BLUE + "U" + ChatColor.BLUE + "ndroppable" + ChatColor.RESET)) {
                    return true;
                }
            }
        }
        return false;
    }

    @Override
    public void schedule() {
        if (isReady()) {
            if (first) {
                getDungeonLogger().info("Hanging Paintings");
                loader.spawnEntities();
                getDungeonLogger().info("Ohhhh such paintings");

                unlockSchematic();
                loader = null;
                first = false;

                if (dungeonBase.hasTimeLimit()) {
                    endTime = System.currentTimeMillis() + (dungeonBase.getTimeLimit() * 1000);
                }

                Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));

                teleportIn(playerParty.getPartyLeader());
                for (DungeonCraftPlayer player : playerParty.getPartyMembers()) {
                    if (player.isOnline() && !player.equals(playerParty.getPartyLeader())) {
                        player.getPlayer().sendMessage(Util.formatText(Locales.getString("Message.Dungeon.Ready", player), getDungeonName()));
                    }
                }
            }
            if (endTime > 0 && System.currentTimeMillis() >= endTime) {
                for (DungeonCraftPlayer p : getPlayerList()) {
                    if (p.isOnline()) {
                        p.getPlayer().sendMessage(Locales.getString("Error.Time.Over", p));
                    }
                }
                completeDungeon(Result.Failure);
            }
            if (!timeLock) {
                localTime++;
            }
            return;
        }
        if (!isLoading && loader == null) {
            loader = new DungeonLoader(this);
            loader.startLoader();
            if (loader.isInQueue()) {
                DungeonCraftPlayer partyLeader = playerParty.getPartyLeader();
                if (partyLeader.isOnline()) {
                    partyLeader.getPlayer().sendMessage(Locales.getString("Message.Dungeon.InQueue", partyLeader));
                }
            }
        }
    }

    public void lockSchematic() {
        DungeonFieldManager.assignSchematicToDungeonField(position, dungeonBase.getSchematic());
    }

    public void unlockSchematic() {
        DungeonFieldManager.dissociateSchematicFromDungeonField(position);
    }

    public DungeonBase getDungeonBase() {
        return dungeonBase;
    }
}