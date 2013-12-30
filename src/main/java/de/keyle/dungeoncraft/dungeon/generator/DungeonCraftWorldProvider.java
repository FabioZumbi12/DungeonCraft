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

import net.minecraft.server.v1_7_R1.IChunkProvider;
import net.minecraft.server.v1_7_R1.World;
import net.minecraft.server.v1_7_R1.WorldProvider;
import net.minecraft.server.v1_7_R1.WorldType;

public class DungeonCraftWorldProvider extends WorldProvider {

    DungeonCraftChunkGenerator worldProvider;

    public DungeonCraftWorldProvider(World world) {
        super();
        this.b = world;
        b();
        worldProvider = new DungeonCraftChunkGenerator(world, world.getSeed());
        this.type = WorldType.NORMAL;
    }

    public IChunkProvider getChunkProvider() {
        return worldProvider;
    }

    public String getName() {
        return "Dungeon";
    }

    @Override
    public boolean canSpawn(int i, int j) {
        return true;
    }
}