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

import de.keyle.dungeoncraft.util.IScheduler;
import de.keyle.dungeoncraft.util.SchedulePlaner;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.chunkio.ChunkIOExecutor;
import org.bukkit.craftbukkit.v1_7_R1.util.LongHash;

import java.util.ArrayList;
import java.util.List;

public class DungeonCraftChunkGenerator extends ChunkProviderServer implements IScheduler {
    protected ChunkRegionLoader loader = null;

    public static DungeonCraftChunkGenerator chunkloader;

    private EmptyChunk emptyChunk;

    private final List<DungeonChunkLoadedEvent> events = new ArrayList<DungeonChunkLoadedEvent>();

    public DungeonCraftChunkGenerator(WorldServer worldserver, IChunkLoader ichunkloader) {
        super(worldserver, ichunkloader, null);
        if (ichunkloader instanceof ChunkRegionLoader) {
            loader = (ChunkRegionLoader) ichunkloader;
        }
        chunkProvider = this;

        chunkloader = this;
        emptyChunk = new EmptyChunk(worldserver, 0, 0);
        SchedulePlaner.addTask(this, 1);
    }

    public void queueUnloadDungeonChunk(int chunkX, int chunkZ) {
        this.unloadQueue.add(chunkX, chunkZ);

        Chunk c = this.chunks.get(LongHash.toLong(chunkX, chunkZ));
        if (c != null) {
            c.mustSave = true;
        }
    }

    public void printLoadedChunkCoords() {
        for (Chunk chunk : chunks.values()) {
            DungeonCraftLogger.write("X: " + chunk.locX + " Z:" + chunk.locZ);
        }
    }

    public void queueUnload(int chunkX, int chunkZ) {
        // DungeonCraftLogger.write("queueUnload(x: " + chunkX + " - z: " + chunkZ + ")");
        // Chunk loading is handled by the plugin
    }

    public Chunk getChunkAt(int i, int j) {
        Chunk c = getChunkAt(i, j, null);
        DungeonCraftLogger.write("getChunkAt: " + c);
        return c;
    }

    public Chunk getChunkAt(int i, int j, Runnable runnable) {
        this.unloadQueue.remove(i, j);
        Chunk chunk = this.chunks.get(LongHash.toLong(i, j));

        if (chunk == null && runnable != null && loader != null && loader.chunkExists(this.world, i, j)) {
            ChunkIOExecutor.queueChunkLoad(this.world, loader, this, i, j, runnable);
            return null; //new EmptyChunk(world, i, j);
        }

        if (chunk == null) {
            chunk = loadChunk(i, j);
            if (chunk == null) {
                return null; //new EmptyChunk(world, i, j);
            }

            this.chunks.put(LongHash.toLong(i, j), chunk);
            chunk.addEntities();

            //ToDo maybe throw a ChunkLoadEvent

            chunk.a(this, this, i, j);
        }

        if (runnable != null) {
            runnable.run();
        }

        return chunk;
    }

    public Chunk getOrCreateChunk(int i, int j) {
        //ToDo Generate Chunk
        return this.emptyChunk;
    }

    public void generateDungeonChunk(int chunkX, int chunkZ) {
        DungeonChunkGenerator dg = new DungeonChunkGenerator(this.world, chunkX, chunkZ, this);
        dg.start();
    }

    @SuppressWarnings("SynchronizeOnNonFinalField") // "chunks" is never assigned again
    public void addChunk(Chunk chunk) {
        synchronized (chunks) {
            chunks.put(LongHash.toLong(chunk.locX, chunk.locZ), chunk);
        }
        synchronized (events) {
            DungeonChunkLoadedEvent event = new DungeonChunkLoadedEvent(chunk.locX, chunk.locZ);
            events.add(event);
        }
    }

    public boolean unloadChunks() {
        for (int i = 0; i < 50; i++) {
            long chunkcoordinates = this.unloadQueue.popFirst();
            Chunk chunk = this.chunks.get(chunkcoordinates);
            if (chunk != null) {
                chunk.removeEntities();
                saveChunk(chunk);
                saveChunkNOP(chunk);
                this.chunks.remove(chunkcoordinates);
            }
            if (this.unloadQueue.isEmpty()) {
                break;
            }
        }
        if (this.loader != null) {
            this.loader.a();
        }
        return true;
    }

    @Override
    public void schedule() {
        synchronized (events) {
            if (!events.isEmpty()) {
                for (DungeonChunkLoadedEvent event : events) {
                    Bukkit.getPluginManager().callEvent(event);
                }
                events.clear();
            }
        }
    }
}