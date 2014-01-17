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
import de.keyle.dungeoncraft.group.Group;

import java.util.Collections;
import java.util.Set;

public class DungeonManager {
    private static BiMap<Dungeon, DungeonField> dungeon2Field = HashBiMap.create();
    private static BiMap<DungeonField, Dungeon> field2dungeon = dungeon2Field.inverse();

    private static BiMap<Dungeon, Group> dungeon2group = HashBiMap.create();
    private static BiMap<Group, Dungeon> group2dungeon = dungeon2group.inverse();

    public static void addDungeon(Dungeon dungeon) {
        if (dungeon2Field.containsKey(dungeon)) {
            return;
        }
        if (field2dungeon.containsKey(dungeon.getPosition())) {
            return;
        }
        if (dungeon2group.containsKey(dungeon)) {
            return;
        }
        if (group2dungeon.containsKey(dungeon.getPlayerGroup())) {
            return;
        }
        dungeon2Field.put(dungeon, dungeon.getPosition());
        dungeon2group.put(dungeon, dungeon.getPlayerGroup());
    }

    public static Dungeon getDungeonAt(DungeonField position) {
        return field2dungeon.get(position);
    }

    public static Dungeon getDungeonFor(Group playerGroup) {
        return group2dungeon.get(playerGroup);
    }

    public static Set<Dungeon> getDungeons() {
        return Collections.unmodifiableSet(dungeon2Field.keySet());
    }

    public static boolean removeDungeon(Dungeon dungeon) {
        dungeon2group.remove(dungeon);
        return dungeon2Field.remove(dungeon) != null;
    }

    public static boolean removeDungeon(DungeonField field) {
        Dungeon dungeon = field2dungeon.remove(field);
        if (dungeon != null) {
            dungeon2group.remove(dungeon);
            return true;
        }
        return false;
    }

    public static boolean removeDungeon(Group playerGroup) {
        Dungeon dungeon = group2dungeon.remove(playerGroup);
        if (dungeon != null) {
            dungeon2Field.remove(dungeon);
            return true;
        }
        return false;
    }
}