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

import de.keyle.dungeoncraft.dungeon.DungeonField;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import net.minecraft.server.v1_7_R1.Block;
import net.minecraft.server.v1_7_R1.Chunk;
import net.minecraft.server.v1_7_R1.ChunkSection;
import net.minecraft.server.v1_7_R1.World;

public class DungeonChunkGenerator extends Thread {
    private final World world;
    private final int chunkX;
    private final int chunkZ;
    private final DungeonCraftChunkProvider provider;

    public DungeonChunkGenerator(World world, int chunkX, int chunkZ, DungeonCraftChunkProvider provider) {
        this.world = world;
        this.chunkX = chunkX;
        this.chunkZ = chunkZ;
        this.provider = provider;
    }

    public void run() {
        DungeonField field = DungeonFieldManager.getDungeonFieldForChunk(chunkX, chunkZ);
        Schematic schematic = DungeonFieldManager.getSchematicForDungeonField(field);

        if (schematic != null) {

            int schematicHeight = schematic.getHeight();
            Chunk chunk = new Chunk(world, chunkX, chunkZ);

            int sectionCount = (int) Math.ceil(schematicHeight / 16.D);
            for (int i = 0; i < sectionCount; i++) {
                chunk.i()[i] = generateSectionBlocks(i, chunkX - field.getChunkX(), chunkZ - field.getChunkZ(), schematic);
            }
            setBiomes(chunk, chunkX - field.getChunkX(), chunkZ - field.getChunkZ(), schematic);
            //chunk.initLighting(); //causes ModificationException in:
            // Collections.sort(entityplayer.chunkCoordIntPairQueue, new ChunkCoordComparator(entityplayer));

            // make the chunk ready (faked)
            chunk.lit = true;
            chunk.m = true;
            chunk.done = true;
            // ----------------------

            provider.addChunk(chunk);
            //DungeonCraftLogger.write("Generated Chunk from schematic at X(" + chunkX + ") Z(" + chunkZ + ")");
        }
    }

    public ChunkSection generateSectionBlocks(int section, int chunkX, int chunkZ, Schematic schematic) {
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLenght();
        byte[] blocks = schematic.getBlocks();
        byte[] data = schematic.getData();

        ChunkSection newChunkSection = new ChunkSection(section, true); //ToDo Check 2nd parameter
        int yOffset = section * 16;
        int maxY = schematic.getHeight() > yOffset ? 16 : yOffset - schematic.getHeight();

        int maxIndex = schematicWidth * schematicLength * schematic.getHeight();

        for (int x = 0; x < 16; x++) {
            for (int y = 0; y < maxY; ++y) {
                for (int z = 0; z < 16; ++z) {
                    int index = getSchematicIndex(chunkX, chunkZ, x, y + yOffset, z, schematicLength, schematicWidth);
                    if (index != -1) {
                        if (index >= maxIndex) {
                            continue;
                        }
                        newChunkSection.setTypeId(x, y, z, Block.e(blocks[index]));
                        newChunkSection.setData(x, y, z, data[index]);
                    }
                }
            }
        }
        return newChunkSection;
    }

    public byte[] setBiomes(Chunk chunk, int chunkX, int chunkZ, Schematic schematic) {
        int schematicWidth = schematic.getWidth();
        int schematicLength = schematic.getLenght();
        byte[] schematicBiomes = schematic.getBiomes();
        byte[] biomes = chunk.m();

        int maxIndex = schematicWidth * schematicLength;

        for (int x = 0; x < 16; x++) {
            for (int z = 0; z < 16; ++z) {
                int index = getSchematicIndex(chunkX, chunkZ, x, z, schematicLength, schematicWidth);
                if (index != -1) {
                    if (index >= maxIndex) {
                        continue;
                    }
                    biomes[x + z * 16] = schematicBiomes[index];
                }
            }
        }

        return biomes;
    }

    public static int getSchematicIndex(int chunkX, int chunkZ, int x, int y, int z, int schematicLength, int schematicWidth) {
        if (x >= schematicWidth - chunkX * 16) {
            return -1;
        }
        if (z >= schematicLength - chunkZ * 16) {
            return -1;
        }
        return (y * schematicWidth * schematicLength) + ((z + chunkZ * 16) * schematicWidth) + (x + chunkX * 16);
    }

    public static int getSchematicIndex(int chunkX, int chunkZ, int x, int z, int schematicLength, int schematicWidth) {
        if (x >= schematicWidth - chunkX * 16) {
            return -1;
        }
        if (z >= schematicLength - chunkZ * 16) {
            return -1;
        }
        return ((z + chunkZ * 16) * schematicWidth) + (x + chunkX * 16);
    }
}