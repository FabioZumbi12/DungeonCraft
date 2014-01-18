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

package de.keyle.dungeoncraft.util;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.HashSet;
import java.util.Set;

public class Configuration {
    public static FileConfiguration config;

    public static Set<String> ALLOWED_COMMANDS = new HashSet<String>();

    public static void setDefault() {
        config.addDefault("commands.allowed", new String[]{"kill", "w", "me", "tell", "msg", "help", "?"});


        config.options().copyDefaults(true);
        DungeonCraftPlugin.getPlugin().saveConfig();
    }

    public static void loadConfiguration() {
        ALLOWED_COMMANDS.clear();
        for (String cmd : config.getStringList("commands.allowed")) {
            ALLOWED_COMMANDS.add(cmd.toLowerCase());
        }
    }
}