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

package de.keyle.dungeoncraft.dungeon;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

public class DungeonBaseLoader {
    public DungeonBaseLoader() {
        loadBases();
    }

    private void loadBases() {
        File basesFolder = new File(DungeonCraftPlugin.getPlugin().getDataFolder(), "dungeons");
        Set<String> installedPlugins = new HashSet<String>();
        for (Plugin plugin : DungeonCraftPlugin.getPlugin().getServer().getPluginManager().getPlugins()) {
            installedPlugins.add(plugin.getName().toLowerCase());
        }
        if (basesFolder.exists()) {
            File[] files = basesFolder.listFiles();
            if (files != null) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        DungeonBase base = new DungeonBase(f.getName());
                        if(BukkitUtil.getBukkitBuild() < base.bukkitBuild) {
                            DungeonCraftLogger.write("Not loading " + base.getName() + " because it requires bukkit build " + base.bukkitBuild + " or greater!");
                            continue;
                        }
                        if(!installedPlugins.containsAll(base.requiredPlugins)) {
                            DungeonCraftLogger.write("Not loading " + base.getName() + " because it requires plugins you don't have!");
                            continue;
                        }
                        DungeonCraftLogger.write("d: " + f.getName());
                        DungeonBaseRegistry.addDungeonBase(base);
                    }
                }
            }
        }
    }
}
