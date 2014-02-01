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

import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.block.BlockFace;

import java.util.Arrays;

public class DungeonCraftChunk extends Chunk {
    public ChunkSection[] sections;
    boolean isGapLightingUpdated;
    boolean isModified;

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
        return (z << 4) | x;
    }

    public int getTopFilledSegment()
    {
        for (int var1 = this.sections.length - 1; var1 >= 0; --var1)
        {
            if (this.sections[var1] != null)
            {
                return this.sections[var1].getYPosition();
            }
        }

        return 0;
    }

    public void generateSkylightMap()
    {
        int topY = (h()+1) * 16;
        this.r = Integer.MAX_VALUE;
        int x;
        int z;
        int counter = 0;

        for (x = 0; x < 16; ++x)
        {
            z = 0;

            while (z < 16)
            {
                this.b[x + (z << 4)] = -999;
                int lightValue = topY + 16 - 1;

                while (true)
                {
                    if (lightValue > 0)
                    {
                        if (this.getBlockLightOpacity(x, lightValue - 1, z) == 0)
                        {
                            --lightValue;
                            continue;
                        }

                        this.heightMap[z << 4 | x] = lightValue;

                        if (lightValue < this.r)
                        {
                            this.r = lightValue;
                        }
                    }

                    if (!this.world.worldProvider.g)
                    {
                        lightValue = 15;
                        int y = this.heightMap[getHeightKey(x,z)];

                        do
                        {
                            lightValue -= this.getBlockLightOpacity(x, y, z);

                            if (lightValue > 0)
                            {
                                ChunkSection section = this.sections[y >> 4];

                                if (section != null)
                                {
                                    section.setSkyLight(x, y & 0xF, z, lightValue);
                                    if(lightValue != 15) {
                                        counter++;
                                    }
                                    this.world.m((this.locX << 4) + x, y, (this.locZ << 4) + z);
                                }
                            }

                            ++y;
                        }
                        while (y < topY && lightValue > 0);
                    }

                    ++z;
                    break;
                }
            }
        }
        if(counter != 0) {
            DungeonCraftLogger.write("<15 " + counter);
        }
        this.isModified = true;

        for (x = 0; x < 16; ++x)
        {
            for (z = 0; z < 16; ++z)
            {
                this.propagateSkylightOcclusion(x, z);
            }
        }
    }

    private void propagateSkylightOcclusion(int par1, int par2)
    {
        this.c[par1 + par2 * 16] = true;
        this.isGapLightingUpdated = true;
    }

    public int getBlockLightOpacity(int par1, int par2, int par3)
    {
        return this.getType(par1, par2, par3).k();

    }

    public void updateSkylight_do(boolean flag)
    {

        if (this.world.areChunksLoaded(this.locX * 16 + 8, 0, this.locZ * 16 + 8, 16)) {
            for (int i = 0; i < 16; ++i) {
                for (int j = 0; j < 16; ++j) {
                    if (this.c[i + j * 16]) {
                        this.c[i + j * 16] = false;
                        int k = this.b(i, j);
                        int l = this.locX * 16 + i;
                        int i1 = this.locZ * 16 + j;
                        int j1 = this.world.g(l - 1, i1);
                        int k1 = this.world.g(l + 1, i1);
                        int l1 = this.world.g(l, i1 - 1);
                        int i2 = this.world.g(l, i1 + 1);

                        if (k1 < j1) {
                            j1 = k1;
                        }

                        if (l1 < j1) {
                            j1 = l1;
                        }

                        if (i2 < j1) {
                            j1 = i2;
                        }

                        this.checkSkylightNeighborHeight(l, i1, j1);
                        this.checkSkylightNeighborHeight(l - 1, i1, k);
                        this.checkSkylightNeighborHeight(l + 1, i1, k);
                        this.checkSkylightNeighborHeight(l, i1 - 1, k);
                        this.checkSkylightNeighborHeight(l, i1 + 1, k);
                        if (flag) {
                            this.world.methodProfiler.b();
                            return;
                        }
                    }
                }
            }

            this.isGapLightingUpdated = false;
        }
    }

    private void checkSkylightNeighborHeight(int par1, int par2, int par3)
    {
        int var4 = this.world.getHighestBlockYAt(par1, par2);

        if (var4 > par3)
        {
            this.updateSkylightNeighborHeight(par1, par2, par3, var4 + 1);
        }
        else if (var4 < par3)
        {
            this.updateSkylightNeighborHeight(par1, par2, var4, par3 + 1);
        }
    }

    private void updateSkylightNeighborHeight(int par1, int par2, int par3, int par4)
    {
        if (par4 > par3 && this.world.areChunksLoaded(par1, 0, par2, 16))
        {
            //DungeonCraftLogger.write("loaded, USNH");
            for (int var5 = par3; var5 < par4; ++var5)
            {
                this.world.c(EnumSkyBlock.SKY, par1, var5, par2);
            }

            this.isModified = true;
        }
    }
    public void updateSkylight(boolean flag)
    {
        if (this.isGapLightingUpdated && !this.world.worldProvider.g && !flag) {
            this.updateSkylight_do(this.world.isStatic);
        }

        this.m = true;
        if (!this.lit && this.done) {
            this.pd();
        }
    }

    public void pd() {
        this.done = true;
        this.lit = true;
        if (!this.world.worldProvider.g) {
            if (this.world.b(this.locX * 16 - 1, 0, this.locZ * 16 - 1, this.locX * 16 + 1, 63, this.locZ * 16 + 1)) {
                for (int i = 0; i < 16; ++i) {
                    for (int j = 0; j < 16; ++j) {
                        if (!this.fd(i, j)) {
                            this.lit = false;
                            break;
                        }
                    }
                }

                if (this.lit) {
                    Chunk chunk = this.world.getChunkAtWorldCoords(this.locX * 16 - 1, this.locZ * 16);
                    if(chunk instanceof DungeonCraftChunk)  {
                        ((DungeonCraftChunk)chunk).a(3);
                    } else {
                        if(chunk != null) {
                            chunk.a(3);
                        }
                    }
                        chunk = this.world.getChunkAtWorldCoords(this.locX * 16 + 16, this.locZ * 16);
                    if(chunk instanceof DungeonCraftChunk)  {
                        ((DungeonCraftChunk)chunk).a(1);
                    }  else {
                        if(chunk != null) {
                            chunk.a(1);
                        }
                    }
                        chunk = this.world.getChunkAtWorldCoords(this.locX * 16, this.locZ * 16 - 1);
                    if(chunk instanceof DungeonCraftChunk)  {
                        ((DungeonCraftChunk)chunk).a(0);
                    }  else {
                        if(chunk != null) {
                            chunk.a(0);
                        }
                    }
                        chunk = this.world.getChunkAtWorldCoords(this.locX * 16, this.locZ * 16 + 16);
                    if(chunk instanceof DungeonCraftChunk)  {
                        ((DungeonCraftChunk)chunk).a(2);
                    }   else {
                        if(chunk != null) {
                            chunk.a(2);
                        }
                    }

                }
            } else {
                this.lit = false;
            }
        }
    }

    private boolean fd(int i, int j) {
        int k = this.h();
        boolean flag = false;
        boolean flag1 = false;

        int l;

        for (l = k + 16 - 1; l > 63 || l > 0 && !flag1; --l) {
            int i1 = this.b(i, l, j);

            if (i1 == 255 && l < 63) {
                flag1 = true;
            }

            if (!flag && i1 > 0) {
                flag = true;
            } else if (flag && i1 == 0 && !this.world.t(this.locX * 16 + i, l, this.locZ * 16 + j)) {
                return false;
            }
        }

        for (; l > 0; --l) {
            if (this.getType(i, l, j).m() > 0) {
                this.world.t(this.locX * 16 + i, l, this.locZ * 16 + j);
            }
        }

        return true;
    }

    private void a(int i) {
        if (this.done) {
            int j;

            if (i == 3) {
                for (j = 0; j < 16; ++j) {
                    this.fd(15, j);
                }
            } else if (i == 1) {
                for (j = 0; j < 16; ++j) {
                    this.fd(0, j);
                }
            } else if (i == 0) {
                for (j = 0; j < 16; ++j) {
                    this.fd(j, 15);
                }
            } else if (i == 2) {
                for (j = 0; j < 16; ++j) {
                    this.fd(j, 0);
                }
            }
        }
    }

}