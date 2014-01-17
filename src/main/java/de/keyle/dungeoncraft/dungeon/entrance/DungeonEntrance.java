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

import de.keyle.dungeoncraft.dungeon.DungeonBase;
import de.keyle.dungeoncraft.util.vector.Region;
import org.bukkit.Location;
import org.json.simple.JSONObject;

public class DungeonEntrance {
    protected final String worldName;
    protected final Region entraceRegion;
    protected final String dungeonName;
    protected final DungeonBase dungeonBase;
    protected final Location exitLocation;

    public DungeonEntrance(String dungeonName, String worldName, Region region, DungeonBase dungeonBase, Location exitLocation) {
        this.worldName = worldName;
        this.entraceRegion = region;
        this.dungeonName = dungeonName;
        this.dungeonBase = dungeonBase;
        this.exitLocation = exitLocation;
    }

    public Region getEntraceRegion() {
        return entraceRegion;
    }

    public String getDungeonName() {
        return dungeonName;
    }

    public DungeonBase getDungeonBase() {
        return dungeonBase;
    }

    public Location getExitLocation() {
        return exitLocation;
    }

    public String getWorldName() {
        return worldName;
    }

    @SuppressWarnings("unchecked")
    public JSONObject toJSONObject() {
        JSONObject json = new JSONObject();

        JSONObject min = new JSONObject();
        min.put("x", entraceRegion.getMin().getBlockX());
        min.put("y", entraceRegion.getMin().getBlockY());
        min.put("z", entraceRegion.getMin().getBlockZ());
        json.put("min", min);

        JSONObject max = new JSONObject();
        max.put("x", entraceRegion.getMax().getBlockX());
        max.put("y", entraceRegion.getMax().getBlockY());
        max.put("z", entraceRegion.getMax().getBlockZ());
        json.put("max", max);

        JSONObject exit = new JSONObject();
        exit.put("x", exitLocation.getX());
        exit.put("y", exitLocation.getY());
        exit.put("z", exitLocation.getZ());
        exit.put("yaw", exitLocation.getYaw());
        exit.put("pitch", exitLocation.getPitch());
        exit.put("world", exitLocation.getWorld().getName());
        json.put("exit", exit);

        json.put("world", worldName);
        json.put("name", dungeonName);
        json.put("base", dungeonBase.getName());

        return json;
    }
}