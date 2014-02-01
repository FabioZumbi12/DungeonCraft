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
import net.minecraft.server.v1_7_R1.EnumSkyBlock;
import net.minecraft.server.v1_7_R1.World;

import java.util.Arrays;

public class DungeonCraftChunk extends Chunk {
    public ChunkSection[] sections;
    boolean isGapLightingUpdated;
    boolean isModified;

    public DungeonCraftChunk(World world, int posX, int posZ) {
        super(world, posX, posZ);
        sections = i();
    }

    private int getHeightKey(int x, int z) {
        return (z << 4) | x;
    }

    public void initEmittedLight() {
        int z;
        int y;
        int maxY = (h() + 1) * 16;
        for (int x = 0; x < 16; x++) {
            for (z = 0; z < 16; z++) {
                for (y = 0; y < maxY; y++) {
                    int light = getBlockEmittedLight(x, y, z);
                    if (light != 0) {
                        this.world.c(EnumSkyBlock.BLOCK, (locX * 16) + x, y, (locZ * 16) + z);
                    }
                }
            }
        }
    }

    public int getBlockEmittedLight(int par1, int par2, int par3) {
        return this.getType(par1, par2, par3).m();
    }

    public void generateSkylightMap() {
        int topY = (h() + 1) * 16;
        this.r = Integer.MAX_VALUE;
        int x;
        int z;
        for (x = 0; x < 16; ++x) {
            z = 0;
            while (z < 16) {
                this.b[x + (z << 4)] = -999;
                int lightValue = topY + 16 - 1;
                while (true) {
                    if (lightValue > 0) {
                        if (this.getBlockLightOpacity(x, lightValue - 1, z) == 0) {
                            --lightValue;
                            continue;
                        }
                        this.heightMap[getHeightKey(x, z)] = lightValue;

                        if (lightValue < this.r) {
                            this.r = lightValue;
                        }
                    }
                    if (!this.world.worldProvider.g) {
                        lightValue = 15;
                        int y = this.heightMap[getHeightKey(x, z)];

                        do {
                            lightValue -= this.getBlockLightOpacity(x, y, z);

                            if (lightValue > 0) {
                                ChunkSection section = this.sections[y >> 4];

                                if (section != null) {
                                    section.setSkyLight(x, y & 0xF, z, lightValue);
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
        this.isModified = true;

        Arrays.fill(this.c, true);
        this.isGapLightingUpdated = true;
    }

    public int getBlockLightOpacity(int par1, int par2, int par3) {
        return this.getType(par1, par2, par3).k();
    }

    public void updateSkylight_do(boolean flag) {
        if (this.world.areChunksLoaded(this.locX * 16 + 8, 0, this.locZ * 16 + 8, 16)) {
            for (int x = 0; x < 16; ++x) {
                for (int z = 0; z < 16; ++z) {
                    if (this.c[x + z * 16]) {
                        this.c[x + z * 16] = false;
                        int k = this.b(x, z);
                        int l = this.locX * 16 + x;
                        int i1 = this.locZ * 16 + z;
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

    private void checkSkylightNeighborHeight(int x, int z, int par3) {
        int maxY = this.world.getHighestBlockYAt(x, z);

        if (maxY > par3) {
            this.updateSkylightNeighborHeight(x, z, par3, maxY + 1);
        } else if (maxY < par3) {
            this.updateSkylightNeighborHeight(x, z, maxY, par3 + 1);
        }
    }

    private void updateSkylightNeighborHeight(int par1, int par2, int par3, int par4) {
        if (par4 > par3 && this.world.areChunksLoaded(par1, 0, par2, 16)) {
            for (int var5 = par3; var5 < par4; ++var5) {
                this.world.c(EnumSkyBlock.SKY, par1, var5, par2);
            }
            this.isModified = true;
        }
    }

    public void updateSkylight(boolean flag) {
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
                    if (chunk != null && chunk instanceof DungeonCraftChunk) {
                        ((DungeonCraftChunk) chunk).a(3);
                    }
                    chunk = this.world.getChunkAtWorldCoords(this.locX * 16 + 16, this.locZ * 16);
                    if (chunk != null && chunk instanceof DungeonCraftChunk) {
                        ((DungeonCraftChunk) chunk).a(1);
                    }
                    chunk = this.world.getChunkAtWorldCoords(this.locX * 16, this.locZ * 16 - 1);
                    if (chunk != null && chunk instanceof DungeonCraftChunk) {
                        ((DungeonCraftChunk) chunk).a(0);
                    }
                    chunk = this.world.getChunkAtWorldCoords(this.locX * 16, this.locZ * 16 + 16);
                    if (chunk != null && chunk instanceof DungeonCraftChunk) {
                        ((DungeonCraftChunk) chunk).a(2);
                    }
                }
            } else {
                this.lit = false;
            }
        }
    }

    private boolean fd(int x, int z) {
        int maxSection = this.h();
        boolean flag = false;
        boolean flag1 = false;
        int y;

        for (y = maxSection + 16 - 1; y > 63 || y > 0 && !flag1; --y) {
            int typeId = this.b(x, y, z);

            if (typeId == 255 && y < 63) {
                flag1 = true;
            }
            if (!flag && typeId > 0) {
                flag = true;
            } else if (flag && typeId == 0 && !this.world.t(this.locX * 16 + x, y, this.locZ * 16 + z)) {
                return false;
            }
        }
        while (y > 0) {
            if (this.getType(x, y, z).m() > 0) {
                this.world.t(this.locX * 16 + x, y, this.locZ * 16 + z);
            }
            --y;
        }
        return true;
    }

    private void a(int side) {
        if (this.done) {
            int i;
            if (side == 3) {
                for (i = 0; i < 16; ++i) {
                    this.fd(15, i);
                }
            } else if (side == 1) {
                for (i = 0; i < 16; ++i) {
                    this.fd(0, i);
                }
            } else if (side == 0) {
                for (i = 0; i < 16; ++i) {
                    this.fd(i, 15);
                }
            } else if (side == 2) {
                for (i = 0; i < 16; ++i) {
                    this.fd(i, 0);
                }
            }
        }
    }
}