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

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.config.ConfigurationJson;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.json.simple.JSONObject;

import java.io.File;

public class RegionLoader {
    private final Dungeon dungeon;

    public RegionLoader(Dungeon d) {
        dungeon = d;
        loadRegions();
    }

    private void loadRegions() {
        if (dungeon.getDungeonBase().getRegionFile().exists()) {
            File regionFile = dungeon.getDungeonBase().getRegionFile();
            ConfigurationJson jsonConfig = new ConfigurationJson(regionFile);
            jsonConfig.load();

            JSONObject regionsObject = jsonConfig.getJSONObject();
            for (Object regionObject : regionsObject.keySet()) {
                String regionName = regionObject.toString();
                JSONObject region = (JSONObject) regionsObject.get(regionObject);
                if (!region.containsKey("min") || !region.containsKey("max")) {
                    continue;
                }
                JSONObject min = (JSONObject) region.get("min");
                if (!min.containsKey("x") || !min.containsKey("y") || !min.containsKey("z")) {
                    continue;
                }
                JSONObject max = (JSONObject) region.get("max");
                if (!max.containsKey("x") || !max.containsKey("y") || !max.containsKey("z")) {
                    continue;
                }
                String xMinString = min.get("x").toString();
                String yMinString = min.get("y").toString();
                String zMinString = min.get("z").toString();

                String xMaxString = max.get("x").toString();
                String yMaxString = max.get("y").toString();
                String zMaxString = max.get("z").toString();

                if (!Util.isInt(xMinString) || !Util.isInt(yMinString) || !Util.isInt(zMinString)) {
                    continue;
                }

                if (!Util.isInt(xMaxString) || !Util.isInt(yMaxString) || !Util.isInt(zMaxString)) {
                    continue;
                }

                int xMin = Integer.parseInt(xMinString);
                int yMin = Integer.parseInt(yMinString);
                int zMin = Integer.parseInt(zMinString);
                int xMax = Integer.parseInt(xMaxString);
                int yMax = Integer.parseInt(yMaxString);
                int zMax = Integer.parseInt(zMaxString);

                Vector minVector = new Vector(Math.min(xMin, xMax), Math.min(yMin, yMax), Math.min(zMin, zMax));
                Vector maxVector = new Vector(Math.max(xMin, xMax), Math.max(yMin, yMax), Math.max(zMin, zMax));

                try {
                    DungeonRegion r = new DungeonRegion(regionName, minVector, maxVector);
                    dungeon.getRegionRegistry().addRegion(r);
                    dungeon.getDungeonLogger().info("Region loaded: " + r);
                } catch (IllegalArgumentException e) {
                    dungeon.getDungeonLogger().warning("Region: " + e.getMessage());
                }
            }
        }
    }
}