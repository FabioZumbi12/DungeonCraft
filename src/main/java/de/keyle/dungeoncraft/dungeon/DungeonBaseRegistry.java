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

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class DungeonBaseRegistry {
    private static Map<String, DungeonBase> bases = new HashMap<String, DungeonBase>();

    public static Collection<DungeonBase> getDungeonBases() {
        return bases.values();
    }

    public static void addDungeonBase(DungeonBase base) {
        if (!bases.containsKey(base.getName())) {
            bases.put(base.getName(), base);
        }
    }

    public static boolean hasDungeonBase(String basename) {
        return bases.containsKey(basename);
    }

    public static DungeonBase getDungeonBase(String basename) {
        return bases.get(basename);
    }

    public static void removeDungeonBase(DungeonBase base) {
        bases.remove(base.getName());
    }

    public static void removeDungeonBase(String baseName) {
        bases.remove(baseName);
    }
}