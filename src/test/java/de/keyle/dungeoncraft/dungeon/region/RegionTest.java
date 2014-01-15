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

package de.keyle.dungeoncraft.dungeon.region;

import de.keyle.dungeoncraft.util.vector.Vector;
import org.junit.Test;

public class RegionTest {
    @Test(expected = IllegalArgumentException.class)
    public void minVectorOutOfBoundsTest() {
        Vector min = new Vector(-1, -1, -1);
        Vector max = new Vector(1, 1, 1);

        new Region("test", min, max);
    }

    @Test(expected = IllegalArgumentException.class)
    public void maxVectorOutOfBoundsTest() {
        Vector min = new Vector(1, 1, 1);
        Vector max = new Vector(1600, 256, 1600);

        new Region("test", min, max);
    }

    @Test(expected = IllegalArgumentException.class)
    public void minVectorBiggerThanMaxVectorTest() {
        Vector min = new Vector(2, 2, 2);
        Vector max = new Vector(1, 1, 1);

        new Region("test", min, max);
    }
}