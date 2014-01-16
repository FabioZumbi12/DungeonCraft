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

package de.keyle.dungeoncraft.util.logger;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.dungeon.Dungeon;
import org.bukkit.ChatColor;

import java.io.File;
import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Logger;

public class DungeonLogger {
    private Logger logger;
    private boolean isEnabled = false;
    private Dungeon dungeon;

    public DungeonLogger(Dungeon dungeon) {
        this.dungeon = dungeon;
        setup();
    }

    public boolean setup() {
        logger = Logger.getLogger("DungeonCraft_" + dungeon.getDungeonBase().getName() + "_" + dungeon.getDungeonName() + "_" + System.currentTimeMillis());
        if (logger.getHandlers().length > 0) {
            for (Handler h : logger.getHandlers()) {
                if (h.toString().equals("DungeonCraft-Dungeon-Logger-FileHandler")) {
                    isEnabled = true;
                    return true;
                }
            }
        }
        try {
            FileHandler fileHandler = new FileHandler(DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "logs" + File.separator + "dungeon" + File.separator + dungeon.getDungeonBase().getName() + "_" + dungeon.getDungeonName() + "_" + System.currentTimeMillis() + ".log", true) {
                @Override
                public String toString() {
                    return "DungeonCraft-Dungeon-Logger-FileHandler";
                }
            };
            logger.setUseParentHandlers(false);
            fileHandler.setFormatter(new LogFormat());
            logger.addHandler(fileHandler);
            isEnabled = true;
            return true;
        } catch (IOException e) {
            isEnabled = false;
            e.printStackTrace();
            return false;
        }
    }

    public void info(String text) {
        if (isEnabled) {
            logger.info(ChatColor.stripColor(text));
        }
    }

    public void warning(String text) {
        if (isEnabled) {
            logger.warning(ChatColor.stripColor(text));
        }
    }

    public void severe(String text) {
        if (isEnabled) {
            logger.severe(ChatColor.stripColor(text));
        }
    }
}