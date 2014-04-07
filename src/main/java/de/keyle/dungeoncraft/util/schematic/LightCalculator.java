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

package de.keyle.dungeoncraft.util.schematic;

import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import net.minecraft.server.v1_7_R2.Block;

import java.util.concurrent.atomic.AtomicInteger;

public class LightCalculator {
    public static final int VERSION = 1;
    public static int MAX_THREAD_COUNT = Runtime.getRuntime().availableProcessors();

    private final int schematicWidth;
    private final int schematicLength;
    private final int schematicHeight;
    byte[] blocks;

    public final byte[] skylight;
    public final byte[] blocklight;
    private final int[] heightMap;

    private enum Direction {
        UP, DOWN, NORTH, SOUTH, WEST, EAST, SELF
    }

    public LightCalculator(Schematic schematic) {
        schematicWidth = schematic.getWidth();    // X
        schematicLength = schematic.getLenght();  // Z
        schematicHeight = schematic.getHeight();  // Y

        blocks = schematic.getBlocks();

        skylight = new byte[schematicWidth * schematicLength * schematicHeight];
        blocklight = new byte[schematicWidth * schematicLength * schematicHeight];
        heightMap = new int[schematicWidth * schematicLength];

        createHeightMap();

        final int xThreads = (int) Math.ceil(schematicWidth / 100.);
        final int zThreads = (int) Math.ceil(schematicLength / 100.);

        final AtomicInteger actualX = new AtomicInteger(0);
        final AtomicInteger actualZ = new AtomicInteger(0);

        final AtomicInteger counter = new AtomicInteger(xThreads * zThreads);
        final AtomicInteger threadCount = new AtomicInteger(0);

        do {
            if (threadCount.get() < MAX_THREAD_COUNT && actualZ.get() < zThreads) {
                counter.decrementAndGet();
                threadCount.incrementAndGet();
                final int xFrom = actualX.get() * 100;
                final int zFrom = actualZ.get() * 100;
                final int xTo = Math.min(xFrom + 100, schematicWidth);
                final int zTo = Math.min(zFrom + 100, schematicLength);
                DungeonCraftLogger.write("Calculate light from (" + xFrom + "|" + zFrom + ") to (" + xTo + "|" + zTo + ")");
                //ToDo "reuse" threads
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        spreadSkyLight(xFrom, zFrom, xTo, zTo);
                        threadCount.decrementAndGet();
                    }
                };
                thread.start();
                if (actualX.incrementAndGet() == xThreads) {
                    actualX.set(0);
                    actualZ.incrementAndGet();
                }
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        } while (counter.get() > 0 || threadCount.get() > 0);

        spreadBlockLight();
    }

    public void createHeightMap() {
        for (int x = 0; x < schematicWidth; x++) {
            for (int z = 0; z < schematicLength; z++) {
                for (int y = schematicHeight - 1; y >= 0; y--) {
                    if (getBlockOpaque(blocks[getSchematicIndex(x, y, z)]) == 0) {
                        skylight[getSchematicIndex(x, y, z)] = 15;
                        continue;
                    }
                    this.heightMap[getHeightKey(x, z)] = y + 1;
                    break;
                }
            }
        }
    }

    public void spreadBlockLight() {
        int y, z, schematicIndex;
        byte emittedLight;
        for (int x = 0; x < schematicWidth; x++) {
            for (z = 0; z < schematicLength; z++) {
                for (y = 0; y < schematicHeight; y++) {
                    schematicIndex = getSchematicIndex(x, y, z);
                    emittedLight = getBlockEmittedLight(blocks[schematicIndex]);
                    if (emittedLight > 0) {
                        blocklight[schematicIndex] = emittedLight;
                        spreadBlockLight(Direction.SELF, x, y, z, emittedLight);
                    }
                }
            }
        }
    }

    public void spreadBlockLight(Direction face, int x, int y, int z, byte lightLevel) {
        if (lightLevel <= 0) {
            return;
        }
        int schematicIndex = getSchematicIndex(x, y, z);
        if (face != Direction.SELF) {
            lightLevel -= getBlockOpaque(blocks[schematicIndex]);
            if (lightLevel <= 0) {
                lightLevel = 0;
            }
            if (lightLevel < blocklight[schematicIndex]) {
                return;
            }
        }
        blocklight[schematicIndex] = lightLevel;
        if (lightLevel <= 0) {
            return;
        }

        --lightLevel;
        if (face != Direction.EAST && x > 0) {
            spreadBlockLight(Direction.WEST, x - 1, y, z, lightLevel);
        }
        if (face != Direction.WEST && x < schematicWidth - 1) {
            spreadBlockLight(Direction.EAST, x + 1, y, z, lightLevel);
        }
        if (face != Direction.SOUTH && z > 0) {
            spreadBlockLight(Direction.NORTH, x, y, z - 1, lightLevel);
        }
        if (face != Direction.NORTH && z < schematicLength - 1) {
            spreadBlockLight(Direction.SOUTH, x, y, z + 1, lightLevel);
        }
        if (face != Direction.UP && y > 0) {
            spreadBlockLight(Direction.DOWN, x, y - 1, z, lightLevel);
        }
        if (face != Direction.DOWN && y < schematicHeight - 1) {
            spreadBlockLight(Direction.UP, x, y + 1, z, lightLevel);
        }
    }

    public void spreadSkyLight(int startX, int startZ, int endX, int endZ) {
        int y, z;
        for (int x = startX; x < endX; x++) {
            for (z = startZ; z < endZ; z++) {
                for (y = this.heightMap[getHeightKey(x, z)]; y < schematicHeight; y++) {
                    spreadSkyLight(Direction.SELF, x, y, z, (byte) 15);
                }
            }
        }
    }

    public void spreadSkyLight(Direction face, int x, int y, int z, byte lightLevel) {
        if (lightLevel <= 0) {
            return;
        }
        int schematicIndex = getSchematicIndex(x, y, z);
        if (face != Direction.SELF) {
            lightLevel -= getBlockOpaque(blocks[schematicIndex]);
            if (lightLevel <= 0) {
                lightLevel = 0;
            }
            if (lightLevel < skylight[schematicIndex]) {
                return;
            }
        }
        skylight[schematicIndex] = lightLevel;

        if (lightLevel <= 0) {
            return;
        }

        --lightLevel;
        if (face != Direction.EAST && x > 0) {
            spreadSkyLight(Direction.WEST, x - 1, y, z, lightLevel);
        }
        if (face != Direction.WEST && x < schematicWidth - 1) {
            spreadSkyLight(Direction.EAST, x + 1, y, z, lightLevel);
        }
        if (face != Direction.SOUTH && z > 0) {
            spreadSkyLight(Direction.NORTH, x, y, z - 1, lightLevel);
        }
        if (face != Direction.NORTH && z < schematicLength - 1) {
            spreadSkyLight(Direction.SOUTH, x, y, z + 1, lightLevel);
        }
        if (face != Direction.UP && y > 0) {
            spreadSkyLight(Direction.DOWN, x, y - 1, z, lightLevel);
        }
        if (face != Direction.DOWN && y < schematicHeight - 1) {
            spreadSkyLight(Direction.UP, x, y + 1, z, lightLevel);
        }
    }

    public int getSchematicIndex(int x, int y, int z) {
        return (y * schematicWidth * schematicLength) + (z * schematicWidth) + x;
    }

    private int getHeightKey(int x, int z) {
        return z * schematicWidth + x;
    }

    public byte getBlockEmittedLight(byte block) {
        int blockID = block & 0xff;
        switch (blockID) {
            case 10:
            case 11:
            case 51:
            case 89:
            case 91:
            case 119:
            case 124:
            case 138:
                return 15;
            case 50:
                return 14;
            case 62:
                return 13;
            case 90:
                return 11;
            case 84:
            case 94:
                return 9;
            case 76:
            case 130:
                return 7;
            case 39:
            case 117:
            case 120:
            case 122:
                return 1;
            default:
                return 0;
        }
    }

    public byte getBlockOpaque(byte blockID) {
        return (byte) (Block.e(blockID & 0xff).k() & 0xf);
    }
}