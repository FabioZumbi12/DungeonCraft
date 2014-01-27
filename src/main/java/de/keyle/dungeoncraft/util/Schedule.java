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
import org.bukkit.Bukkit;

import java.util.HashMap;
import java.util.Map;

public class Schedule {
    private final Map<Scheduler, Integer> tasks = new HashMap<Scheduler, Integer>();

    public int scheduleDelayedTask(final Scheduler task, long delay) {
        int taskId = Bukkit.getScheduler().scheduleSyncDelayedTask(DungeonCraftPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                task.schedule();
            }
        }, delay);
        tasks.put(task, taskId);
        return taskId;
    }

    public int scheduleRepeatingTask(final Scheduler task, long interval) {
        return scheduleRepeatingTask(task, interval, 0);
    }

    public int scheduleRepeatingTask(final Scheduler task, long interval, long delay) {
        int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(DungeonCraftPlugin.getPlugin(), new Runnable() {
            @Override
            public void run() {
                task.schedule();
            }
        }, delay, interval);
        tasks.put(task, taskId);
        return taskId;
    }

    public void cancelTask(Scheduler task) {
        if (tasks.containsKey(task)) {
            Bukkit.getScheduler().cancelTask(tasks.get(task));
        }
    }

    public void cancelTask(int taskId) {
        Bukkit.getScheduler().cancelTask(taskId);
    }

    public void cancelAllTasks() {
        for (int taskId : tasks.values()) {
            Bukkit.getScheduler().cancelTask(taskId);
        }
    }
}