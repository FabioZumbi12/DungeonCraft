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

package de.keyle.dungeoncraft.util.vector;

public class BlockVector extends Vector {
    public BlockVector(Vector pt) {
        super(pt);
    }

    public BlockVector(int x, int y, int z) {
        super(x, y, z);
    }

    public BlockVector(float x, float y, float z) {
        super(x, y, z);
    }

    public BlockVector(double x, double y, double z) {
        super(x, y, z);
    }

    public boolean equals(Object obj) {
        if (!(obj instanceof Vector)) {
            return false;
        }
        Vector other = (Vector) obj;
        return (int) other.getX() == (int) this.x && (int) other.getY() == (int) this.y && (int) other.getZ() == (int) this.z;
    }

    public int hashCode() {
        return (int) this.x << 19 ^ (int) this.y << 12 ^ (int) this.z;
    }
}