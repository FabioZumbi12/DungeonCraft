/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2011-2013 Keyle & xXLupoXx
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
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.util.IScheduler;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dungeon implements IScheduler {
    protected boolean isReady = false;
    protected boolean isLoading = false;
    protected boolean first = true;
    protected final String dungeonName;
    protected final DungeonBase dungeonBase;
    protected final UUID uuid;
    protected final DungeonField position;

    protected List<DungeonCraftPlayer> playerList = new ArrayList<DungeonCraftPlayer>();

    public Dungeon(String dungeonName, DungeonBase dungeonTheme) {
        this.dungeonName = dungeonName;
        this.dungeonBase = dungeonTheme;
        uuid = UUID.randomUUID();
        position = DungeonFieldManager.getNewDungeonField();
    }

    public Dungeon(String dungeonName, DungeonBase dungeonTheme, Group group) {
        this(dungeonName, dungeonTheme);
        for (DungeonCraftPlayer player : group.getGroupMembers()) {
            playerList.add(player);
        }
    }

    public synchronized void setReady() {
        isReady = true;
        DungeonCraftLogger.write("Dungeon is now ready to use!");
    }

    public synchronized boolean isRready() {
        return isReady;
    }

    public void addPlayer(DungeonCraftPlayer player) {
        if (!playerList.contains(player)) {
            playerList.add(player);
        }
    }

    @Override
    public void schedule() {
        if (isRready()) {
            if (first) {
                unlockSchematic();
                DungeonCraftLogger.write("Ok Lets do something");
                first = false;

                World world = DungeonCraftPlugin.getPlugin().getServer().getWorld("dctestworld");
                OrientationVector ov = dungeonBase.getSpawn();
                Location spawn = new Location(world, ov.getX() + (position.getX() * 1600), ov.getY(), ov.getZ() + (position.getZ() * 1600), (float) ov.getYaw(), (float) ov.getPitch());

                for (DungeonCraftPlayer p : playerList) {
                    if (p.isOnline()) {
                        p.getPlayer().teleport(spawn);
                    }
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
}