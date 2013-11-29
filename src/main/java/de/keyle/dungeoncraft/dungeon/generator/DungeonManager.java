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

package de.keyle.dungeoncraft.dungeon.generator;

import de.keyle.dungeoncraft.util.schematic.Schematic;

import java.util.HashMap;
import java.util.Map;

public class DungeonManager {

    private static Map<DungeonField, Schematic> fieldSchematicMap = new HashMap<DungeonField, Schematic>();

    public static DungeonField getNewDungeonField() {
        return new DungeonField(0, 0);
    }

    public static void assignSchematicToDungeonField(DungeonField field, Schematic schematic) {
        fieldSchematicMap.put(field, schematic);
    }

    public static void dissociateSchematicFromDungeonField(DungeonField field) {
        fieldSchematicMap.remove(field);
    }

    public static Schematic getSchematicForChunk(int chunkX, int chunkZ) {
        DungeonField field = new DungeonField(chunkX / 100, chunkZ / 100);

        return fieldSchematicMap.get(field);
    }

    public static Schematic getSchematicForDungeonField(DungeonField field) {
        return fieldSchematicMap.get(field);
    }

    public static DungeonField getDungeonFieldForChunk(int chunkX, int chunkZ) {
        return new DungeonField(chunkX / 100, chunkZ / 100);
    }

    public static class DungeonField implements Comparable<DungeonField> {
        private int x, z;

        public DungeonField(int x, int z) {
            this.x = x;
            this.z = z;
        }

        public int getX() {
            return x;
        }

        public int getZ() {
            return z;
        }

        public int getBlockX() {
            return 1600 * x;
        }

        public int getBlockZ() {
            return 1600 * z;
        }

        public int getChunkX() {
            return 100 * x;
        }

        public int getChunkZ() {
            return 100 * z;
        }

        @Override
        public int compareTo(DungeonField other) {
            if (this.x != other.x) {
                return this.x > other.x ? 1 : -1;
            }
            if (this.z != other.z) {
                return this.z > other.z ? 1 : -1;
            }
            return 0;
        }

        @Override
        public boolean equals(Object o) {
            if (o == null || !(o instanceof DungeonField)) {
                return false;
            }
            DungeonField that = (DungeonField) o;

            if (x != that.x) {
                return false;
            }
            if (z != that.z) {
                return false;
            }
            return true;
        }

        @Override
        public int hashCode() {
            int result = x;
            result = 31 * result + z;
            return result;
        }
    }
}