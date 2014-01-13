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

package de.keyle.dungeoncraft.entity.util;

public enum EquipmentSlot {
    Weapon(0),
    Boots(1),
    Leggins(2),
    Chestplate(3),
    Helmet(4);

    int slot;

    EquipmentSlot(int slot) {
        this.slot = slot;
    }

    public static EquipmentSlot getSlotById(int id) {
        for (EquipmentSlot slot : EquipmentSlot.values()) {
            if (slot.getSlotId() == id) {
                return slot;
            }
        }
        return EquipmentSlot.Weapon;
    }

    public int getSlotId() {
        return this.slot;
    }
}