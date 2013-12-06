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

import net.minecraft.server.v1_6_R3.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.util.LongHash;
import org.bukkit.generator.ChunkGenerator;

public class DungeonCraftWorldServer extends WorldServer {
    public DungeonCraftWorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, String s, int i, WorldSettings worldsettings, MethodProfiler methodprofiler, IConsoleLogManager iconsolelogmanager, World.Environment env, ChunkGenerator gen) {
        super(minecraftserver, idatamanager, s, i, worldsettings, methodprofiler, iconsolelogmanager, env, gen);
    }

    protected IChunkProvider j() {
        IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);
        this.chunkProviderServer = new DungeonCraftChunkGenerator(this, ichunkloader);

        return this.chunkProviderServer;
    }

    protected void g() {
        C(); // -> super.g();

        for (long chunkCoord : this.chunkTickList.popAll()) {
            int chunkX = LongHash.msw(chunkCoord);
            int chunkZ = LongHash.lsw(chunkCoord);

            int blockX = chunkX * 16;
            int blockZ = chunkZ * 16;

            this.methodProfiler.a("getChunk");
            Chunk chunk = getChunkAt(chunkX, chunkZ);

            a(blockX, blockZ, chunk);
            this.methodProfiler.c("tickChunk");
            chunk.k();

            this.methodProfiler.c("tickTiles");
            for (ChunkSection chunksection : chunk.i()) {
                if ((chunksection != null) && (chunksection.shouldTick())) {
                    for (int j2 = 0; j2 < 3; j2++) {
                        this.k = (this.k * 3 + 1013904223);
                        int i2 = this.k >> 2;
                        int k2 = i2 & 0xF;
                        int l2 = i2 >> 8 & 0xF;
                        int i3 = i2 >> 16 & 0xF;
                        int j3 = chunksection.getTypeId(k2, i3, l2);

                        Block block = Block.byId[j3];

                        if ((block != null) && (block.isTicking())) {
                            block.a(this, k2 + blockX, i3 + chunksection.getYPosition(), l2 + blockZ, this.random);
                        }
                    }
                }
            }
            this.methodProfiler.b();
        }
    }

    private boolean canSpawn(int x, int z) {
        return true;
    }
}