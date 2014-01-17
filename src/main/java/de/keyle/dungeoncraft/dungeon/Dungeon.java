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
import de.keyle.dungeoncraft.api.util.Scheduler;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.RegionRegistry;
import de.keyle.dungeoncraft.dungeon.scripting.TriggerRegistry;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.logger.DungeonLogger;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.WeatherType;
import org.bukkit.World;

import java.util.*;

public class Dungeon implements Scheduler {
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
    protected final String dungeonName;
    protected final Group playerGroup;
    protected final DungeonBase dungeonBase;
    protected final UUID uuid;
    protected final DungeonField position;
    protected final TriggerRegistry triggerRegistry;
    protected final RegionRegistry regionRegistry;
    protected final DungeonLogger dungeonLogger;

    public Dungeon(String dungeonName, DungeonBase dungeonTheme, Group group) {
        this.dungeonName = dungeonName;
        this.dungeonBase = dungeonTheme;
        uuid = UUID.randomUUID();
        position = DungeonFieldManager.getNewDungeonField();
        triggerRegistry = new TriggerRegistry();
        regionRegistry = new RegionRegistry();
        localTime = dungeonBase.getStartTime();
        weather = dungeonBase.getWeather();
        dungeonLogger = new DungeonLogger(this);
        playerGroup = group;
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

    public boolean isLoading() {
        return isLoading;
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

    public synchronized void setReady() {
        isReady = true;
        DungeonCraftLogger.write("Dungeon is now ready to use!");
    }

    public synchronized boolean isReady() {
        return isReady;
    }

    public List<DungeonCraftPlayer> getPlayerList() {
        return Collections.unmodifiableList(playerGroup.getGroupMembers());
    }

    public Group getPlayerGroup() {
        return playerGroup;
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
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
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

    public boolean isCompleted() {
        return isCompleted;
    }

    public Result getResult() {
        return result;
    }

    public void completeDungeon(Result result) {
        this.result = result;
        isCompleted = true;
        for (DungeonCraftPlayer player : getPlayerList()) {
            if (player.isOnline()) {
                if (exitLocation == null) {
                    player.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                } else {
                    player.getPlayer().teleport(exitLocation);
                }
                player.getPlayer().resetPlayerTime();
            }
        }
        unlockSchematic();
    }

    public void teleport(DungeonCraftPlayer p) {
        if (playerGroup.containsPlayer(p)) {
            p.getPlayer().teleport(getPlayerSpawnLoacation(p));
            BukkitUtil.setPlayerEnvironment(p.getPlayer(), dungeonBase.getEnvironment());
            updatePlayerTime();
            updatePlayerWeather();
        }
    }

    @Override
    public void schedule() {
        if (isReady()) {
            if (first) {
                unlockSchematic();
                DungeonCraftLogger.write("Ok Lets do something");
                first = false;

                teleport(playerGroup.getGroupLeader());
                for (DungeonCraftPlayer player : getPlayerList()) {
                    if (player.isOnline()) {
                        player.getPlayer().sendMessage("[DC] You can enter " + getDungeonName() + " now!");
                    }
                }

                if (dungeonBase.hasTimeLimit()) {
                    endTime = System.currentTimeMillis() + (dungeonBase.getTimeLimit() * 1000);
                }

                Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));
            }
            if (endTime > 0 && System.currentTimeMillis() >= endTime) {
                for (DungeonCraftPlayer p : getPlayerList()) {
                    if (p.isOnline()) {
                        p.getPlayer().sendMessage("Time is over!");
                    }
                }
                completeDungeon(Result.Failure);
            }
            if (!timeLock) {
                localTime++;
            }
            return;
        }
        if (!isLoading) {
            isLoading = true;
            DungeonLoader loader = new DungeonLoader(this);
            loader.start();
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