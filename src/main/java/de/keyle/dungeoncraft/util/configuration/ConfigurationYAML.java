/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2011-2014 Keyle & xXLupoXx
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

package de.keyle.dungeoncraft.util.configuration;

import de.keyle.dungeoncraft.util.logger.DebugLogger;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;

public class ConfigurationYaml {
    public File yamlFile;
    private FileConfiguration config;

    public ConfigurationYaml(String path) {
        this(new File(path));
    }

    public ConfigurationYaml(File file) {
        yamlFile = file;
        config = new YamlConfiguration();
        try {
            config.load(yamlFile);
        } catch (Exception ignored) {
        }
        file.setWritable(true);
        file.setReadable(true);
    }

    public FileConfiguration getConfig() {
        return config;
    }

    public boolean saveConfig() {
        try {
            config.save(yamlFile);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
            return false;
        }
    }

    public void clearConfig() {
        config = new YamlConfiguration();
    }
}