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

package de.keyle.dungeoncraft.util.config;

import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import org.bukkit.ChatColor;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;

public class ConfigurationJson {
    public File jsonFile;
    private JSONObject config;

    public ConfigurationJson(String path) {
        this(new File(path));
    }

    public ConfigurationJson(File file) {
        jsonFile = file;
    }

    public JSONObject getJSONObject() {
        if (config == null) {
            config = new JSONObject();
        }
        return config;
    }

    public boolean load() {
        if(!jsonFile.exists()) {
            return false;
        }
        config = new JSONObject();
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(jsonFile));
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(reader);
            config = (JSONObject) obj;
        } catch (ParseException e) {
            DungeonCraftLogger.write(ChatColor.RED + "Could not parse/load " + jsonFile.getName());
            DebugLogger.warning("Could not parse/load " + jsonFile.getName());
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
            return false;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException ignored) {
                }
            }
        }
        return true;
    }

    public boolean save() {
        try {
            // http://jsonformatter.curiousconcept.com/
            // http://jsoneditoronline.org/
            BufferedWriter writer = new BufferedWriter(new FileWriter(jsonFile));
            writer.write(config.toJSONString());
            writer.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
            return false;
        }
    }

    public void clearConfig() {
        config = new JSONObject();
    }
}