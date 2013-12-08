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

import de.keyle.dungeoncraft.util.schematic.Schematic;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.Chunk;
import net.minecraft.server.v1_7_R1.ChunkSection;
import net.minecraft.server.v1_7_R1.World;

public class DungeonChunkGenerator extends Thread {
    private final World world;
    private final int chunkX;
    private final int chunkZ;
    private final DungeonCraftChunkGenerator generator;

    public DungeonChunkGenerator(World world, int chunkX, int chunkZ, DungeonCraftChunkGenerator generator) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.generator = generator;
    }

    public void run() {
        DungeonManager.DungeonField field = DungeonManager.getDungeonFieldForChunk(chunkX, chunkZ);
        Schematic schematic = DungeonManager.getSchematicForDungeonField(field);

        if (schematic != null) {

            int schematicHeight = schematic.getHeight();
            Chunk chunk = new Chunk(world, chunkX, chunkZ);

            int sectionCount = (int) Math.ceil(schematicHeight / 16.D);
            for (int i = 0; i < sectionCount; i++) {
                chunk.i()[i] = generateSectionBlocks(i, chunkX, chunkZ, schematic, field);
            }
            setBiomes(chunk, chunkX, chunkZ, schematic, field);
            chunk.initLighting();

            // make the chunk ready (faked)
            chunk.lit = true;
            chunk.m = true;
            chunk.done = true;
            // ----------------------

            generator.addChunk(chunk);
        }
    }

    public ChunkSection generateSectionBlocks(int section, int chunkX, int chunkZ, Schematic schematic, DungeonManager.DungeonField field) {
        int startChunkX = field.getChunkX();
        int startChunkZ = field.getChunkZ();
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLenght();
        byte[] blocks = schematic.getBlocks();
        byte[] data = schematic.getData();

        ChunkSection newChunkSection = new ChunkSection(section, true); //ToDo Check 2nd parameter
        int yOffset = section * 16;

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < 16; ++y) {
                for (int z = 0; z < 16; ++z) {
                    int index = getSchematicIndex(startChunkX, startChunkZ, chunkX, chunkZ, x, y + yOffset, z, schematicLength, schematicWidth);
                    if (index != -1) {
                        newChunkSection.setTypeId(x, y, z, Block.e(blocks[index]));
                        newChunkSection.setData(x, y, z, data[index]);
                    }
                }
            }
        }
        return newChunkSection;
    }

    public byte[] setBiomes(Chunk chunk, int chunkX, int chunkZ, Schematic schematic, DungeonManager.DungeonField field) {
        int startChunkX = field.getChunkX();
        int startChunkZ = field.getChunkZ();
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLenght();
        byte[] schematicBiomes = schematic.getBiomes();
        byte[] biomes = chunk.m();

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; ++z) {
                int index = getSchematicIndex(startChunkX, startChunkZ, chunkX, chunkZ, x, z, schematicLength, schematicWidth);
                if (index != -1) {
                    biomes[x + z * 16] = schematicBiomes[index];
                }
            }
        }

        return biomes;
    }

    public static int getSchematicIndex(int startChunkX, int startChunkZ, int chunkX, int chunkZ, int x, int y, int z, int schematicLength, int schematicWidth) {
        if (x >= schematicWidth - (chunkX - startChunkX) * 16) {
            return -1;
        }
        if (z >= schematicLength - (chunkZ - startChunkZ) * 16) {
            return -1;
        }
        return (y * 16 * 16) + ((z + (chunkZ - startChunkZ) * 16) * 16) + (x + (chunkX - startChunkX) * 16);
    }

    public static int getSchematicIndex(int startChunkX, int startChunkZ, int chunkX, int chunkZ, int x, int z, int schematicLength, int schematicWidth) {
        if (x >= schematicWidth - (chunkX - startChunkX) * 16) {
            return -1;
        }
        if (z >= schematicLength - (chunkZ - startChunkZ) * 16) {
            return -1;
        }
        return ((z + (chunkZ - startChunkZ) * 16) * 16) + (x + (chunkX - startChunkX) * 16);
    }
}