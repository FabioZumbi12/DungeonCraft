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

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.dungeon.DungeonBase;
import de.keyle.dungeoncraft.dungeon.DungeonBaseRegistry;
import de.keyle.dungeoncraft.util.config.ConfigurationJson;
import de.keyle.dungeoncraft.util.vector.Region;
import de.keyle.dungeoncraft.util.vector.Vector;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.io.File;

public class DungeonEntranceLoader {
    private ConfigurationJson config;

    public DungeonEntranceLoader() {
        File entranceFile = new File(DungeonCraftPlugin.getPlugin().getDataFolder(), "entrances.json");
        config = new ConfigurationJson(entranceFile);
        if (entranceFile.exists()) {
            loadEntrances();
        }
    }

    protected void loadEntrances() {
        config.load();

        JSONObject json = config.getJSONObject();
        if (json.containsKey("entrances")) {
            Object entrancesObject = json.get("entrances");
            if (entrancesObject instanceof JSONArray) {
                JSONArray entrances = (JSONArray) entrancesObject;
                for (Object entranceObject : entrances) {
                    if (entranceObject instanceof JSONObject) {
                        JSONObject entrance = (JSONObject) entranceObject;
                        if (!entrance.containsKey("name") || !entrance.containsKey("base") || !entrance.containsKey("exit") || !entrance.containsKey("min") || !entrance.containsKey("max")) {
                            continue;
                        }
                        String baseName = entrance.get("base").toString();
                        if (!DungeonBaseRegistry.hasDungeonBase(baseName)) {
                            continue;
                        }
                        DungeonBase dungeonBase = DungeonBaseRegistry.getDungeonBase(baseName);

                        String dungeonName = entrance.get("name").toString();

                        String worldName = entrance.get("world").toString();
                        if (Bukkit.getWorld(worldName) == null) {
                            continue;
                        }

                        boolean enabled = Boolean.parseBoolean(entrance.get("enabled").toString());

                        Object minObject = entrance.get("min");
                        Vector minVector;
                        if (minObject instanceof JSONObject) {
                            JSONObject min = (JSONObject) minObject;
                            if (!min.containsKey("x") || !min.containsKey("y") || !min.containsKey("z")) {
                                continue;
                            }
                            String stringX = min.get("x").toString();
                            String stringY = min.get("y").toString();
                            String stringZ = min.get("z").toString();
                            if (!NumberUtils.isNumber(stringX) || !NumberUtils.isNumber(stringY) || !NumberUtils.isNumber(stringZ)) {
                                continue;
                            }
                            minVector = new Vector(Integer.parseInt(stringX), Integer.parseInt(stringY), Integer.parseInt(stringZ));
                        } else {
                            continue;
                        }
                        Vector maxVector;
                        Object maxObject = entrance.get("max");
                        if (maxObject instanceof JSONObject) {
                            JSONObject max = (JSONObject) maxObject;
                            if (!max.containsKey("x") || !max.containsKey("y") || !max.containsKey("z")) {
                                continue;
                            }
                            String stringX = max.get("x").toString();
                            String stringY = max.get("y").toString();
                            String stringZ = max.get("z").toString();
                            if (!NumberUtils.isNumber(stringX) || !NumberUtils.isNumber(stringY) || !NumberUtils.isNumber(stringZ)) {
                                continue;
                            }
                            maxVector = new Vector(Integer.parseInt(stringX), Integer.parseInt(stringY), Integer.parseInt(stringZ));
                        } else {
                            continue;
                        }
                        Region region = new Region(minVector, maxVector);

                        Location exitLocation;
                        Object exitObject = entrance.get("exit");
                        if (exitObject instanceof JSONObject) {
                            JSONObject exit = (JSONObject) exitObject;
                            if (!exit.containsKey("x") || !exit.containsKey("y") || !exit.containsKey("z") || !exit.containsKey("world")) {
                                continue;
                            }
                            String stringWorld = exit.get("world").toString();
                            String stringX = exit.get("x").toString();
                            String stringY = exit.get("y").toString();
                            String stringZ = exit.get("z").toString();
                            if (Bukkit.getWorld(stringWorld) == null || !NumberUtils.isNumber(stringX) || !NumberUtils.isNumber(stringY) || !NumberUtils.isNumber(stringZ)) {
                                continue;
                            }
                            exitLocation = new Location(Bukkit.getWorld(stringWorld), Double.parseDouble(stringX), Double.parseDouble(stringY), Double.parseDouble(stringZ));
                            if (exit.containsKey("yaw")) {
                                String stringYaw = exit.get("yaw").toString();
                                if (NumberUtils.isNumber(stringYaw)) {
                                    exitLocation.setYaw(Float.parseFloat(stringYaw));
                                }
                            }
                            if (exit.containsKey("pitch")) {
                                String stringPitch = exit.get("pitch").toString();
                                if (NumberUtils.isNumber(stringPitch)) {
                                    exitLocation.setPitch(Float.parseFloat(stringPitch));
                                }
                            }
                        } else {
                            continue;
                        }

                        DungeonEntrance dungeonEntrance = new DungeonEntrance(dungeonName, worldName, region, dungeonBase, exitLocation, enabled);
                        DungeonEntranceRegistry.registerEntrance(dungeonEntrance);
                    }
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public void saveEntrances() {
        config.load();

        JSONObject json = config.getJSONObject();
        JSONArray entrances = new JSONArray();
        json.put("entrances", entrances);

        for (DungeonEntrance entrance : DungeonEntranceRegistry.getAllEntrances()) {
            entrances.add(entrance.toJSONObject());
        }

        config.save();
    }
}