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

package de.keyle.dungeoncraft.dungeon.generator;

import net.minecraft.server.v1_7_R1.Chunk;
import net.minecraft.server.v1_7_R1.ChunkSection;
import net.minecraft.server.v1_7_R1.World;
import org.bukkit.block.BlockFace;

import java.util.Arrays;

public class DungeonCraftChunk extends Chunk {
    public ChunkSection[] sections;


    public DungeonCraftChunk(World world, int posX, int posZ) {
        super(world, posX, posZ);
        sections = i();
        Arrays.fill(this.c, true);
    }

    public void createHeightMap() {
        int maxSection = h();
        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; z++) {
                int y = maxSection * 16 - 1;
                while (y > 0) {
                    if (b(x, y - 1, z) == 0) {
                        y--;
                    } else {
                        this.heightMap[getHeightKey(x,z)] = y;
                        break;
                    }
                }
            }
        }
    }

    public void initLight() {
        int topY = (h()+1) * 16;
        int x, y, z;
        ChunkSection section;
        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                for (y = this.heightMap[getHeightKey(x,z)]; y < topY; y++) {
                    if ((section = sections[y >> 4]) == null) {
                        break;
                    }
                    section.setSkyLight(x, y & 0xf, z, 15);
                }
            }
        }
    }

    public void spreadLight() {
        int topY = (h()+1) * 16;
        int x, y, z;
        for (x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                for (y = this.heightMap[getHeightKey(x,z)]; y < topY; y++) {
                    if (sections[y >> 4] == null) {
                        break;
                    }
                    spreadLight(BlockFace.SELF, x, y & 0xf, z, 15);
                }
            }
        }
    }

    public void spreadLight(BlockFace face, int x, int y, int z, int lightLevel) {
        if(lightLevel <= 0) {
            return;
        }
        ChunkSection s = sections[y >> 4];
        if(s == null) {
            return;
        }
        lightLevel -= s.getTypeId(x,y&0xF,z).k() + 1;
        if(lightLevel <= 0) {
            lightLevel = 0;
        }
        if(lightLevel < s.getSkyLight(x,y & 0xF,z)) {
            return;
        }
        s.setSkyLight(x,y & 0xF,z,lightLevel);

        int actualHightLevel = heightMap[getHeightKey(x,z)];
        if(face != BlockFace.EAST && x > 0 && heightMap[getHeightKey(x-1,z)] > actualHightLevel) {
            spreadLight(BlockFace.WEST, x-1,y,z,lightLevel);
        }
        if(face != BlockFace.WEST && x < 15 && heightMap[getHeightKey(x+1,z)] > actualHightLevel) {
            spreadLight(BlockFace.EAST, x+1,y,z,lightLevel);
        }
        if(face != BlockFace.SOUTH && z > 0 && heightMap[getHeightKey(x,z-1)] > actualHightLevel) {
            spreadLight(BlockFace.NORTH, x,y,z-1,lightLevel);
        }
        if(face != BlockFace.NORTH && z < 15 && heightMap[getHeightKey(x,z+1)] > actualHightLevel) {
            spreadLight(BlockFace.SOUTH, x,y,z+1,lightLevel);
        }
        /*
        if(face != BlockFace.UP && y > 0) {
            spreadLight(BlockFace.DOWN, x,y-1,z,lightLevel);
        }
        if(face != BlockFace.DOWN && sections[(y+1) >> 4] != null) {
            spreadLight(BlockFace.UP, x,y+1,z,lightLevel);
        }
        */
    }

    private int getHeightKey(int x, int z) {
        return x | (z << 4);
    }
}