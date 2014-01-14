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
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import de.keyle.dungeoncraft.dungeon.scripting.TriggerRegistry;
import org.mozilla.javascript.Function;

public abstract class TriggerContext {
    protected final Dungeon dungeon;

    public TriggerContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public abstract void registerTrigger(int id, Function function);

    public abstract void enableTrigger(int id);

    public void enableTrigger(int id, String filename) {
        for (Trigger t : dungeon.getTriggerRegistry().getTriggers(id)) {
            if (t.getName().equals(filename + "_" + TriggerRegistry.getEventClassById(id).getName())) {
                t.setActive(true);
            }
        }
    }

    public abstract void disableTrigger(int id);

    public void disableTrigger(int id, String filename) {
        for (Trigger t : dungeon.getTriggerRegistry().getTriggers(id)) {
            if (t.getName().equals(filename + "_" + TriggerRegistry.getEventClassById(id).getName())) {
                t.setActive(false);
            }
        }
    }
}