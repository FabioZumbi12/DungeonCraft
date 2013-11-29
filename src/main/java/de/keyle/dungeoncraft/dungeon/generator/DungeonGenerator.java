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

import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import net.minecraft.server.v1_6_R3.BiomeBase;
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DungeonGenerator extends ChunkGenerator {
    private static final Biome[] BIOME_MAPPING = new Biome[BiomeBase.biomes.length];

    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator) new DungeonPopulator());
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomeGrid) {
        DungeonManager.DungeonField field = DungeonManager.getDungeonFieldForChunk(chunkX, chunkZ);
        Schematic schematic = DungeonManager.getSchematicForDungeonField(field);

        byte[][] result = new byte[world.getMaxHeight() / 16][];

        if (schematic != null) {

            int startChunkX = field.getChunkX();
            int startChunkZ = field.getChunkZ();
            int schematicLength = schematic.getLenght();
            int schematicWidth = schematic.getWidth();
            int schematicHeight = schematic.getHeight();
            byte[] blocks = schematic.getBlocks();
            byte[] biomes = schematic.getBiomes();

            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < schematicHeight; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        int index = getSchematicIndex(startChunkX, startChunkZ, chunkX, chunkZ, x, y, z, schematicLength, schematicWidth);
                        if (index != -1) {
                            setBlock(result, x, y, z, blocks[index]);
                        }
                        index = getSchematicIndex(startChunkX, startChunkZ, chunkX, chunkZ, x, z, schematicLength, schematicWidth);
                        if (index != -1) {
                            biomeGrid.setBiome(x, z, getBiomeById(biomes[index]));
                        } else {
                            biomeGrid.setBiome(x, z, Biome.OCEAN);
                        }
                    }
                }
            }
        } else {
            DungeonCraftLogger.write("No Schematic found! (" + chunkX + " - " + chunkZ + ")");
        }
        return result;
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

    void setBlock(byte[][] result, int x, int y, int z, byte blockId) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blockId;
    }

    public static Biome getBiomeById(int id) {
        if (BIOME_MAPPING.length <= id) {
            return Biome.OCEAN;
        }
        Biome biome = BIOME_MAPPING[id];
        return biome != null ? biome : Biome.OCEAN;
    }

    static {
        BIOME_MAPPING[BiomeBase.SWAMPLAND.id] = Biome.SWAMPLAND;
        BIOME_MAPPING[BiomeBase.FOREST.id] = Biome.FOREST;
        BIOME_MAPPING[BiomeBase.TAIGA.id] = Biome.TAIGA;
        BIOME_MAPPING[BiomeBase.DESERT.id] = Biome.DESERT;
        BIOME_MAPPING[BiomeBase.PLAINS.id] = Biome.PLAINS;
        BIOME_MAPPING[BiomeBase.HELL.id] = Biome.HELL;
        BIOME_MAPPING[BiomeBase.SKY.id] = Biome.SKY;
        BIOME_MAPPING[BiomeBase.RIVER.id] = Biome.RIVER;
        BIOME_MAPPING[BiomeBase.EXTREME_HILLS.id] = Biome.EXTREME_HILLS;
        BIOME_MAPPING[BiomeBase.OCEAN.id] = Biome.OCEAN;
        BIOME_MAPPING[BiomeBase.FROZEN_OCEAN.id] = Biome.FROZEN_OCEAN;
        BIOME_MAPPING[BiomeBase.FROZEN_RIVER.id] = Biome.FROZEN_RIVER;
        BIOME_MAPPING[BiomeBase.ICE_PLAINS.id] = Biome.ICE_PLAINS;
        BIOME_MAPPING[BiomeBase.ICE_MOUNTAINS.id] = Biome.ICE_MOUNTAINS;
        BIOME_MAPPING[BiomeBase.MUSHROOM_ISLAND.id] = Biome.MUSHROOM_ISLAND;
        BIOME_MAPPING[BiomeBase.MUSHROOM_SHORE.id] = Biome.MUSHROOM_SHORE;
        BIOME_MAPPING[BiomeBase.BEACH.id] = Biome.BEACH;
        BIOME_MAPPING[BiomeBase.DESERT_HILLS.id] = Biome.DESERT_HILLS;
        BIOME_MAPPING[BiomeBase.FOREST_HILLS.id] = Biome.FOREST_HILLS;
        BIOME_MAPPING[BiomeBase.TAIGA_HILLS.id] = Biome.TAIGA_HILLS;
        BIOME_MAPPING[BiomeBase.SMALL_MOUNTAINS.id] = Biome.SMALL_MOUNTAINS;
        BIOME_MAPPING[BiomeBase.JUNGLE.id] = Biome.JUNGLE;
        BIOME_MAPPING[BiomeBase.JUNGLE_HILLS.id] = Biome.JUNGLE_HILLS;
    }
}