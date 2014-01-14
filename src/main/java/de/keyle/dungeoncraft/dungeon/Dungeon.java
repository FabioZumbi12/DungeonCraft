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
import de.keyle.dungeoncraft.api.events.DungeonStartEvent;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.scripting.TriggerRegistry;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.IScheduler;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.*;

public class Dungeon implements IScheduler {
    protected boolean isReady = false;
    protected boolean isLoading = false;
    protected boolean first = true;
    protected final String dungeonName;
    protected final DungeonBase dungeonBase;
    protected final UUID uuid;
    protected final DungeonField position;
    protected long endTime = 0;
    protected Location exitLocation;
    protected boolean isCompleted = false;
    protected TriggerRegistry triggerRegistry;

    protected List<DungeonCraftPlayer> playerList = new ArrayList<DungeonCraftPlayer>();

    public Dungeon(String dungeonName, DungeonBase dungeonTheme) {
        this.dungeonName = dungeonName;
        this.dungeonBase = dungeonTheme;
        uuid = UUID.randomUUID();
        position = DungeonFieldManager.getNewDungeonField();
        triggerRegistry = new TriggerRegistry();
    }

    public Dungeon(String dungeonName, DungeonBase dungeonTheme, Group group) {
        this(dungeonName, dungeonTheme);
        for (DungeonCraftPlayer player : group.getGroupMembers()) {
            playerList.add(player);
        }
    }

    public TriggerRegistry getTriggerRegistry() {
        return triggerRegistry;
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

    public void addPlayer(DungeonCraftPlayer player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
        }
    }

    public void removePlayer(DungeonCraftPlayer player) {
        playerList.remove(player);
    }

    public List<DungeonCraftPlayer> getPlayerList() {
        return Collections.unmodifiableList(playerList);
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public void setExitLocation(Location exitLocation) {
        this.exitLocation = exitLocation;
    }

    public boolean isCompleted() {
        return isCompleted;
    }

    public void completeDungeon() {
        Iterator<DungeonCraftPlayer> iterator = playerList.iterator();
        while (iterator.hasNext()) {
            DungeonCraftPlayer p = iterator.next();
            if (p.isOnline()) {
                if (exitLocation == null) {
                    p.getPlayer().teleport(Bukkit.getWorlds().get(0).getSpawnLocation());
                } else {
                    p.getPlayer().teleport(exitLocation);
                }
                p.getPlayer().sendMessage("Time is over!");
                iterator.remove();
            }
        }
        unlockSchematic();
        isCompleted = true;
    }

    @Override
    public void schedule() {
        if (isReady()) {
            if (first) {
                unlockSchematic();
                DungeonCraftLogger.write("Ok Lets do something");
                first = false;

                World world = DungeonCraftPlugin.getPlugin().getServer().getWorld(DungeonCraftWorld.WORLD_NAME);
                OrientationVector ov = dungeonBase.getSpawn();
                Location spawn = new Location(world, ov.getX() + (position.getX() * 1600), ov.getY(), ov.getZ() + (position.getZ() * 1600), (float) ov.getYaw(), (float) ov.getPitch());

                for (DungeonCraftPlayer p : playerList) {
                    if (p.isOnline()) {
                        p.getPlayer().teleport(spawn);
                        BukkitUtil.setPlayerEnvironment(p.getPlayer(), dungeonBase.getEnvironment());
                    }
                }
                if (dungeonBase.hasTimeLimit()) {
                    endTime = System.currentTimeMillis() + (dungeonBase.getTimeLimit() * 1000);
                }

                Bukkit.getPluginManager().callEvent(new DungeonStartEvent(this));
            }
            if (endTime > 0) {
                if (System.currentTimeMillis() >= endTime) {
                    completeDungeon();
                }
            }
            //ToDo Weather & Time
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