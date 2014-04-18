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

import com.google.common.collect.ArrayListMultimap;
import de.keyle.dungeoncraft.dungeon.DungeonField;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import net.minecraft.server.v1_7_R3.*;
import org.bukkit.craftbukkit.v1_7_R3.util.LongHash;

import java.util.ArrayList;
import java.util.List;

public class DungeonCraftChunkProvider extends ChunkProviderServer {
    public static DungeonCraftChunkProvider chunkloader;

    private final ArrayListMultimap<Long, Runnable> callbacks = ArrayListMultimap.create();

    public DungeonCraftChunkProvider(WorldServer worldserver, IChunkLoader ichunkloader, DungeonCraftChunkGenerator chunkProvider) {
        super(worldserver, ichunkloader, chunkProvider);
        chunkloader = this;
    }

    public void generateChunkAt(final int chunkX, final int chunkZ, final Runnable callback) {
        Schematic schematic = DungeonFieldManager.getSchematicForChunk(chunkX, chunkZ);
        //DungeonCraftLogger.write("Schematic found for X(" + chunkX + ") Z(" + chunkZ + ") -> " + (schematic != null));
        if (schematic != null) {
            if (callback != null) {
                synchronized (callbacks) {
                    callbacks.put(LongHash.toLong(chunkX, chunkZ), callback);
                }
            }
            DungeonChunkGenerator generator = new DungeonChunkGenerator(this.world, chunkX, chunkZ, this);
            generator.start();
        }
    }

    public Chunk getChunkAt(final int chunkX, final int chunkZ, final Runnable callback) {
        //DungeonCraftLogger.write("getChunkAt: " + chunkX + " - " + chunkZ + " - runnable: " + (callback != null));
        this.unloadQueue.remove(chunkX, chunkZ);
        Chunk chunk = this.chunks.get(LongHash.toLong(chunkX, chunkZ));

        if (chunk == null) {
            Schematic schematic = DungeonFieldManager.getSchematicForChunk(chunkX, chunkZ);
            //DungeonCraftLogger.write("Schematic found for X(" + chunkX + ") Z(" + chunkZ + ") -> " + (schematic != null));
            if (schematic != null) {
                boolean cancel = false;
                synchronized (callbacks) {
                    if (callbacks.containsKey(LongHash.toLong(chunkX, chunkZ))) {
                        cancel = true;
                    }
                }
                if (callback != null) {
                    synchronized (callbacks) {
                        callbacks.put(LongHash.toLong(chunkX, chunkZ), callback);
                    }
                }
                if (cancel) {
                    return null;
                }
                DungeonChunkGenerator generator = new DungeonChunkGenerator(this.world, chunkX, chunkZ, this);
                generator.start();
                return null;
            }

            chunk = getOrCreateUnusedChunk(chunkX, chunkZ);
            addChunk(chunk);
        }

        if (callback != null) {
            callback.run();
        }

        return chunk;
    }

    public Chunk getOrCreateChunk(int chunkX, int chunkZ) {
        return getChunkAt(chunkX, chunkZ);
    }

    public Chunk getOrCreateUnusedChunk(int chunkX, int chunkZ) {
        //DungeonCraftLogger.write("Generate Grass Chunk at X(" + chunkX + ") Z(" + chunkZ + ")");

        Chunk localChunk = new EmptyChunk(this.world, chunkX, chunkZ);

        //Chunk localChunk = new Chunk(this.world, chunkX, chunkZ);
        ChunkSection localObject2;

        localObject2 = localChunk.i()[0];

        if (localObject2 == null) {
            localObject2 = new ChunkSection(0, !this.world.worldProvider.g);
            localChunk.i()[0] = localObject2;
        }

        for (int n = 0; n < 16; n++) {
            for (int i1 = 0; i1 < 16; i1++) {
                localObject2.setTypeId(n, 0, i1, Blocks.STATIONARY_WATER);
                localObject2.setData(n, 0, i1, 0);
            }
        }

        //Mal auskomentieren
        localChunk.initLighting();
        localChunk.lit = true;
        localChunk.m = true;
        localChunk.done = true;
        localChunk.e();

        return localChunk;
    }

    public List<?> getMobsFor(EnumCreatureType ect, int i, int i1, int i2) {
        return new ArrayList<Object>();
    }

    public Chunk getChunkAt(int chunkX, int chunkZ) {
        return getChunkAt(chunkX, chunkZ, null);
    }

    public void getChunkAt(IChunkProvider ichunkprovider, int chunkX, int chunkZ) {
    }

    public void unloadDungeonField(DungeonField field) {
        int chunkZ = field.getChunkZ();
        int chunkX = field.getChunkX();
        for (int x = 0; x < chunkX; x++) {
            for (int z = 0; z < chunkZ; z++) {
                this.unloadQueue.add(x, z);
            }
        }
    }

    @Override
    public void queueUnload(int x, int z) {
        if (!(getChunkAt(x, z) instanceof DungeonCraftChunk) || DungeonManager.getDungeonAt(DungeonFieldManager.getDungeonFieldForChunk(x, z)) == null) {
            this.unloadQueue.add(x, z);
        }
    }

    public boolean unloadChunks() {
        for (int i = 0; i < 100 && !this.unloadQueue.isEmpty(); i++) {
            long chunkcoordinates = this.unloadQueue.popFirst();
            Chunk chunk = this.chunks.get(chunkcoordinates);
            if (chunk != null) {
                this.chunks.remove(chunkcoordinates);
            }
        }
        return false;
    }

    @SuppressWarnings("SynchronizeOnNonFinalField") // "chunks" is never assigned again
    public void addChunk(final Chunk chunk) {
        long chunkHash = LongHash.toLong(chunk.locX, chunk.locZ);
        synchronized (chunks) {
            chunks.put(chunkHash, chunk);
        }
        synchronized (callbacks) {
            if (callbacks.containsKey(chunkHash)) {
                //DungeonCraftLogger.write("Executed Runnables for chunk at X(" + chunk.locX + ") Z(" + chunk.locZ + ")");
                List<Runnable> runnables = new ArrayList<Runnable>(callbacks.get(chunkHash));
                callbacks.removeAll(chunkHash);
                for (Runnable runnable : runnables) {
                    runnable.run();
                }
            }
        }
    }
}