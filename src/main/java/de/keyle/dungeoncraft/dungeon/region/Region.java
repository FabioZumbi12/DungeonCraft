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

package de.keyle.dungeoncraft.dungeon.region;

import com.google.common.collect.ArrayListMultimap;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.vector.Vector;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Region {
    protected final String regionId;
    protected final Vector min;
    protected final Vector max;
    Set<DungeonCraftPlayer> players = new HashSet<DungeonCraftPlayer>();

    private static ArrayListMultimap<DungeonCraftPlayer, Region> playerRegionMultimap = ArrayListMultimap.create();

    public Region(String regionId, Vector min, Vector max) {
        this.regionId = regionId;
        if (min.getX() < 0 || min.getY() < 0 || min.getZ() < 0 || min.getX() >= 1600 || min.getY() >= 256 || min.getZ() >= 1600) {
            throw new IllegalArgumentException("Min vector not inside valid boundaries: " + min);
        }
        if (max.getX() < 0 || max.getY() < 0 || max.getZ() < 0 || max.getX() >= 1600 || max.getY() >= 256 || max.getZ() >= 1600) {
            throw new IllegalArgumentException("Max vector not inside valid boundaries: " + max);
        }
        if (min.getX() >= max.getX() || min.getY() >= max.getY() || min.getZ() >= max.getZ()) {
            throw new IllegalArgumentException("Max vector has to be bigger than the min vector: " + min + " > " + max);
        }
        this.min = min;
        this.max = max;
    }

    public String getRegionId() {
        return regionId;
    }

    public Vector getMin() {
        return min;
    }

    public Vector getMax() {
        return max;
    }

    public boolean isVectorInside(Vector p) {
        return Util.isInsideCuboid(p, min, max);
    }

    public Set<DungeonCraftPlayer> getPlayers() {
        return players;
    }

    public void removePlayer(DungeonCraftPlayer player) {
        players.remove(player);
        playerRegionMultimap.remove(player, this);
    }

    public void addPlayer(DungeonCraftPlayer player) {
        if (!players.contains(player)) {
            players.add(player);
        }
        if (!playerRegionMultimap.get(player).contains(this)) {
            playerRegionMultimap.put(player, this);
        }
    }

    public static List<Region> getPlayerRegions(DungeonCraftPlayer player) {
        return Collections.unmodifiableList(playerRegionMultimap.get(player));
    }

    @Override
    public String toString() {
        return "Region{regionId='" + regionId + '\'' + ", min=" + min + ", max=" + max + '}';
    }
}