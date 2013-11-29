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

package de.keyle.dungeoncraft;

import de.keyle.dungeoncraft.commands.TestCommand;
import de.keyle.dungeoncraft.util.Configuration;
import de.keyle.dungeoncraft.util.DungeonCraftVersion;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class DungeonCraftPlugin extends JavaPlugin {
    private static DungeonCraftPlugin plugin;

    public void onDisable() {
        DungeonCraftLogger.setConsole(null);
        Bukkit.getServer().getScheduler().cancelTasks(plugin);
        DebugLogger.info("DungeonCraft disabled!");

        Bukkit.getServer().unloadWorld("dctestworld", true);
    }

    public void onEnable() {
        plugin = this;

        DungeonCraftVersion.reset();
        DungeonCraftLogger.setConsole(getServer().getConsoleSender());
        Configuration.config = this.getConfig();
        Configuration.setDefault();
        Configuration.loadConfiguration();
        DebugLogger.setup();

        getCommand("dctest").setExecutor(new TestCommand());
    }

    public static DungeonCraftPlugin getPlugin() {
        return plugin;
    }
}