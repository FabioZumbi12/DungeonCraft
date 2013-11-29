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

public class Vector implements Comparable<Vector> {
    protected final double x;
    protected final double y;
    protected final double z;

    public Vector(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(int x, int y, int z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(float x, float y, float z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public Vector(Vector other) {
        this.x = other.x;
        this.y = other.y;
        this.z = other.z;
    }

    public Vector() {
        this.x = 0.0D;
        this.y = 0.0D;
        this.z = 0.0D;
    }


    public double getX() {
        return this.x;
    }

    public int getBlockX() {
        return (int) Math.round(this.x);
    }

    public double getY() {
        return this.y;
    }

    public int getBlockY() {
        return (int) Math.round(this.y);
    }

    public double getZ() {
        return this.z;
    }

    public int getBlockZ() {
        return (int) Math.round(this.z);
    }

    @Override
    public int compareTo(Vector other) {
        if (this.y != other.y) {
            return Double.compare(this.y, other.y);
        }
        if (this.z != other.z) {
            return Double.compare(this.z, other.z);
        }
        if (this.x != other.x) {
            return Double.compare(this.x, other.x);
        }
        return 0;
    }

    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.x) ^ Double.doubleToLongBits(this.x) >>> 32);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.y) ^ Double.doubleToLongBits(this.y) >>> 32);
        hash = 79 * hash + (int) (Double.doubleToLongBits(this.z) ^ Double.doubleToLongBits(this.z) >>> 32);
        return hash;
    }
}