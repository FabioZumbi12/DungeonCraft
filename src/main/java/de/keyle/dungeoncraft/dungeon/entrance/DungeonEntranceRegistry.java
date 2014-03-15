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

package de.keyle.dungeoncraft.dungeon.entrance;

import com.google.common.collect.ArrayListMultimap;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.bukkit.Location;

import java.util.Collection;
import java.util.List;

public class DungeonEntranceRegistry {
    private static ArrayListMultimap<String, DungeonEntrance> entrances = ArrayListMultimap.create();
    private static DungeonEntranceLoader entranceLoader = null;

    public static DungeonEntrance getEntranceAt(Location location) {
        List<DungeonEntrance> worldEntrances = entrances.get(location.getWorld().getName());
        if (worldEntrances.size() > 0) {
            Vector vec = new Vector(location.getBlockX(), location.getBlockY(), location.getBlockZ());
            for (DungeonEntrance entrance : worldEntrances) {
                if (entrance.getEntraceRegion().isVectorInside(vec)) {
                    return entrance;
                }
            }
        }
        return null;
    }

    public static void registerEntrance(DungeonEntrance entrance) {
        entrances.put(entrance.getWorldName(), entrance);
    }

    public static Collection<DungeonEntrance> getAllEntrances() {
        return entrances.values();
    }

    public static void saveEntrances() {
        if (entranceLoader == null) {
            entranceLoader = new DungeonEntranceLoader();
        }
        entranceLoader.saveEntrances();
    }

    public static void loadEntrances() {
        if (entranceLoader == null) {
            entranceLoader = new DungeonEntranceLoader();
        }
    }

    public static DungeonEntrance getEntranceByName(String name) {
        for(DungeonEntrance dungeonEntrance : getAllEntrances()) {
            if(dungeonEntrance.dungeonName.equals(name)) {
                return dungeonEntrance;
            }
        }
        return null;
    }
}