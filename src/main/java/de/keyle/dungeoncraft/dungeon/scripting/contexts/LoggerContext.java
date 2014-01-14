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

package de.keyle.dungeoncraft.dungeon.scripting.contexts;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.util.logger.DebugLogger;

@SuppressWarnings("unused")
public class LoggerContext {
    protected final Dungeon dungeon;

    public LoggerContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void info(String message) {
        DebugLogger.info(message, "DC] D:[" + dungeon.getDungeonName());
    }

    public void warning(String message) {
        DebugLogger.warning(message, "DC] D:[" + dungeon.getDungeonName());
    }

    public void severe(String message) {
        DebugLogger.severe(message, "DC] D:[" + dungeon.getDungeonName());
    }
}