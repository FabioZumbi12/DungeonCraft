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
import de.keyle.dungeoncraft.util.config.ConfigurationYaml;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.bukkit.configuration.file.FileConfiguration;

public class RegionLoader {
    private final Dungeon dungeon;

    public RegionLoader(Dungeon d) {
        dungeon = d;
        loadRegions();
    }

    private void loadRegions() {
        if (dungeon.getDungeonBase().getRegionFile().exists()) {
            ConfigurationYaml configurationYaml = new ConfigurationYaml(dungeon.getDungeonBase().getRegionFile());
            FileConfiguration config = configurationYaml.getConfig();

            for (String key : config.getKeys(false)) {
                int minX = config.getInt(key + ".min.x", -1);
                int minY = config.getInt(key + ".min.y", -1);
                int minZ = config.getInt(key + ".min.z", -1);
                int maxX = config.getInt(key + ".max.x", -1);
                int maxY = config.getInt(key + ".max.y", -1);
                int maxZ = config.getInt(key + ".max.z", -1);

                Vector min = new Vector(minX, minY, minZ);
                Vector max = new Vector(maxX, maxY, maxZ);

                try {
                    Region r = new Region(key, min, max);
                    dungeon.getRegionRegistry().addRegion(r);
                    DebugLogger.info("d:[" + dungeon.getDungeonName() + "] Loaded Region: " + r);
                } catch (IllegalArgumentException e) {
                    DebugLogger.warning("d:[" + dungeon.getDungeonName() + "] " + e.getMessage());
                }
            }
        }
    }
}