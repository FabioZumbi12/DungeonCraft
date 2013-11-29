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
import org.bukkit.World;
import org.bukkit.block.Biome;
import org.bukkit.generator.BlockPopulator;
import org.bukkit.generator.ChunkGenerator;

import java.util.Arrays;
import java.util.List;
import java.util.Random;

public class DungeonGenerator extends ChunkGenerator {
    @Override
    public List<BlockPopulator> getDefaultPopulators(World world) {
        return Arrays.asList((BlockPopulator) new DungeonPopulator());
    }

    @Override
    public boolean canSpawn(World world, int x, int z) {
        return true;
    }

    @Override
    public byte[][] generateBlockSections(World world, Random random, int chunkX, int chunkZ, BiomeGrid biomes) {
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

            for (int x = 0; x < 16; ++x) {
                for (int y = 0; y < schematicHeight; ++y) {
                    for (int z = 0; z < 16; ++z) {
                        int index = getSchematicIndex(startChunkX, startChunkZ, chunkX, chunkZ, x, y, z, schematicLength, schematicWidth);
                        if (index != -1) {
                            setBlock(result, x, y, z, blocks[index]);
                        }
                        biomes.setBiome(x, z, Biome.FOREST);
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

    void setBlock(byte[][] result, int x, int y, int z, byte blockId) {
        if (result[y >> 4] == null) {
            result[y >> 4] = new byte[4096];
        }
        result[y >> 4][((y & 0xF) << 8) | (z << 4) | x] = blockId;
    }
}