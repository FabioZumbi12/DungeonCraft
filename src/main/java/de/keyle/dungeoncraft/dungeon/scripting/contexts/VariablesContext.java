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

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@SuppressWarnings("unused")
public class VariablesContext {
    private Map<String, Object> variables = new HashMap<String, Object>();

    public boolean setVariable(String key, Object value) {
        if (variables.containsKey(key)) {
            return false;
        }
        variables.put(key, value);
        return true;
    }

    public boolean updateVariable(String key, Object value) {
        if (!variables.containsKey(key)) {
            return false;
        }
        variables.put(key, value);
        return true;
    }

    public Object getVariable(String key) {
        return variables.get(key);
    }

    public void removeVariable(String key) {
        variables.remove(key);
    }

    public Set<String> getVariablesKeys() {
        return variables.keySet();
    }
}