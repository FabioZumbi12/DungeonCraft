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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

import java.util.Collections;
import java.util.Set;

public class DungeonManager {
    private static BiMap<Dungeon, DungeonField> dungeon2Field = HashBiMap.create();
    private static BiMap<DungeonField, Dungeon> field2dungeon = dungeon2Field.inverse();

    public static void addDungeon(Dungeon dungeon) {
        if (dungeon2Field.containsKey(dungeon)) {
            return;
        }
        if (field2dungeon.containsKey(dungeon.position)) {
            return;
        }
        dungeon2Field.put(dungeon, dungeon.position);
    }

    public static Set<Dungeon> getDungeons() {
        return Collections.unmodifiableSet(dungeon2Field.keySet());
    }

    public static boolean removeDungeon(Dungeon dungeon) {
        return dungeon2Field.remove(dungeon) != null;
    }

    public static boolean removeDungeon(DungeonField field) {
        return field2dungeon.remove(field) != null;
    }
}