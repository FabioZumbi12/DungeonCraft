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
import de.keyle.dungeoncraft.api.util.Scheduler;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class SchedulePlaner {
    private static final Map<Scheduler, Integer> tasks = new HashMap<Scheduler, Integer>();

    private SchedulePlaner() {
    }

    public static void stopTimers() {
        if (tasks.size() > 0) {
            DebugLogger.info("Timer stop");
            for (int timerID : tasks.values()) {
                Bukkit.getScheduler().cancelTask(timerID);
            }
            tasks.clear();
        }
    }

    public static void reset() {
        stopTimers();
    }

    public static void addTask(final Scheduler task, int interval) {
        int timer = Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCraftPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                task.schedule();
            }
        }, 0L, interval);
        tasks.put(task, timer);
    }

    public static void removeTask(Scheduler task) {
        tasks.remove(task);
    }
}