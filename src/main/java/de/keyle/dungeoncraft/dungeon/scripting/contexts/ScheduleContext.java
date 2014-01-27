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

import de.keyle.dungeoncraft.api.util.Scheduler;
import de.keyle.dungeoncraft.dungeon.Dungeon;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Function;

import java.util.HashMap;
import java.util.Map;

@SuppressWarnings("unused")
public class ScheduleContext {
    protected final Dungeon dungeon;
    protected Map<Function, Integer> tasks = new HashMap<Function, Integer>();

    public ScheduleContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void delayedTask(final Function function, long delay) {
        int taskId = dungeon.getSchedule().scheduleDelayedTask(new Scheduler() {
            @Override
            public void schedule() {
                Context ctx = Context.enter();
                try {
                    function.call(ctx, function.getParentScope(), null, new Object[0]);
                } finally {
                    Context.exit();
                }
            }
        }, delay);
        tasks.put(function, taskId);
    }

    public void repeatingTask(final Function function, long interval) {
        repeatingTask(function, interval, 0);
    }

    public void repeatingTask(final Function function, long interval, long delay) {
        int taskId = dungeon.getSchedule().scheduleRepeatingTask(new Scheduler() {
            @Override
            public void schedule() {
                Context ctx = Context.enter();
                try {
                    function.call(ctx, function.getParentScope(), null, new Object[0]);
                } finally {
                    Context.exit();
                }
            }
        }, interval, delay);
        tasks.put(function, taskId);
    }

    public void cancelTask(Function function) {
        if (tasks.containsKey(function)) {
            dungeon.getSchedule().cancelTask(tasks.get(function));
        }
    }

    public void cancelAllTasks() {
        for (int taskId : tasks.values()) {
            dungeon.getSchedule().cancelTask(taskId);
        }
    }
}