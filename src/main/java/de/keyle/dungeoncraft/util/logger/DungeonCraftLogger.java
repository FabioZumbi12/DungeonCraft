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

package de.keyle.dungeoncraft.util.logger;

import org.bukkit.ChatColor;
import org.bukkit.command.ConsoleCommandSender;

public class DungeonCraftLogger {
    private static ConsoleCommandSender consoleCommandSender = null;

    public static void setConsole(ConsoleCommandSender console) {
        consoleCommandSender = console;
    }

    public static void write(String msg) {
        if (consoleCommandSender != null) {
            consoleCommandSender.sendMessage("[" + ChatColor.RED + "D" + ChatColor.DARK_RED + "C" + ChatColor.RESET + "] " + msg);
            DebugLogger.info("(L) " + msg);
        }
    }

    public static void write(String msg, String source) {
        if (consoleCommandSender != null) {
            consoleCommandSender.sendMessage("[" + ChatColor.RED + source + ChatColor.RESET + "] " + msg);
            DebugLogger.info("(L) " + msg, source);
        }
    }
}