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

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.util.LongHash;
import org.bukkit.generator.ChunkGenerator;

public class DungeonCraftWorldServer extends WorldServer {

    public static DungeonCraftWorldServer dungeonCraftWorldServer;
    private DungeonCraftChunkProvider dungeonCrafthunkProviderServer;

    public DungeonCraftWorldServer(MinecraftServer minecraftserver, IDataManager idatamanager, String s, int i, WorldSettings worldsettings, MethodProfiler methodprofiler, World.Environment env, ChunkGenerator gen) {
        super(minecraftserver, idatamanager, s, i, worldsettings, methodprofiler, env, gen);
        if (!(this.worldProvider instanceof DungeonCraftWorldProvider)) {
            this.worldProvider = new DungeonCraftWorldProvider(this);
        }
        dungeonCraftWorldServer = this;
    }

    protected IChunkProvider j() {
        if (!(this.worldProvider instanceof DungeonCraftWorldProvider)) {
            this.worldProvider = new DungeonCraftWorldProvider(this);
        }
        IChunkLoader ichunkloader = this.dataManager.createChunkLoader(this.worldProvider);
        this.chunkProviderServer = new DungeonCraftChunkProvider(this, ichunkloader, new DungeonCraftChunkGenerator(this, getSeed()));
        dungeonCrafthunkProviderServer = (DungeonCraftChunkProvider) this.chunkProviderServer;
        return this.chunkProviderServer;
    }

    protected void g() {
        B(); // -> super.g();

        for (long chunkCoord : this.chunkTickList.popAll()) {
            int chunkX = LongHash.msw(chunkCoord);
            int chunkZ = LongHash.lsw(chunkCoord);

            int blockX = chunkX * 16;
            int blockZ = chunkZ * 16;

            this.methodProfiler.a("getChunk");
            Chunk chunk = getChunkAt(chunkX, chunkZ);

            a(blockX, blockZ, chunk);
            this.methodProfiler.c("tickChunk");
            chunk.b(false);

            this.methodProfiler.c("tickBlocks");
            for (ChunkSection chunksection : chunk.i()) {
                if ((chunksection != null) && (chunksection.shouldTick())) {
                    for (int i2 = 0; i2 < 3; i2++) {
                        this.k = (this.k * 3 + 1013904223);
                        int j2 = this.k >> 2;
                        int k2 = j2 & 0xF;
                        int l2 = j2 >> 8 & 0xF;
                        int i3 = j2 >> 16 & 0xF;
                        Block block = chunksection.getTypeId(k2, i3, l2);

                        if (block.isTicking()) {
                            block.a(this, k2 + blockX, i3 + chunksection.getYPosition(), l2 + blockZ, this.random);
                        }
                    }
                }
            }
            this.methodProfiler.b();
        }
    }

    @Override
    public void doTick() {
        for (Dungeon dungeon : DungeonManager.getDungeons()) {
            dungeon.schedule();
        }
        dungeonCrafthunkProviderServer.schedule();

        this.methodProfiler.c("chunkSource");
        this.chunkProvider.unloadChunks();
        int j = a(1.0F);

        if (j != this.j) {
            this.j = j;
        }
        this.methodProfiler.c("tickPending");
        a(false);
        this.methodProfiler.c("tickBlocks");
        g();
        this.methodProfiler.c("chunkMap");
        this.getPlayerChunkMap().flush();
        this.methodProfiler.c("portalForcer");
        this.t().a(getTime());
        this.methodProfiler.b();
    }
}