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

import de.keyle.dungeoncraft.util.vector.BlockVector;
import de.keyle.knbt.TagCompound;

import java.util.HashMap;
import java.util.Map;

public class Schematic {
    private final byte[] blocks;
    private final byte[] data;
    private final byte[] biomes;
    private final short width;
    private final short lenght;
    private final short height;
    private final Map<BlockVector, TagCompound> tileEntities = new HashMap<BlockVector, TagCompound>();
    private final Map<BlockVector, TagCompound> entities = new HashMap<BlockVector, TagCompound>();

    public Schematic(byte[] blocks, byte[] data, byte[] biomes, short width, short lenght, short height, Map<BlockVector, TagCompound> tileEntities, Map<BlockVector, TagCompound> entities) {
        this.blocks = blocks;
        this.data = data;
        this.biomes = biomes;
        this.width = width;
        this.lenght = lenght;
        this.height = height;
        this.tileEntities.putAll(tileEntities);
        this.entities.putAll(entities);
    }

    public Schematic(byte[] blocks, byte[] data, short width, short lenght, short height, Map<BlockVector, TagCompound> tileEntities, Map<BlockVector, TagCompound> entities) {
        this.blocks = blocks;
        this.data = data;
        this.biomes = new byte[width * lenght];
        this.width = width;
        this.lenght = lenght;
        this.height = height;
        this.tileEntities.putAll(tileEntities);
        this.entities.putAll(entities);
    }

    public byte[] getBlocks() {
        return blocks;
    }

    public byte[] getData() {
        return data;
    }

    public byte[] getBiomes() {
        return biomes;
    }

    public short getWidth() {
        return width;
    }

    public short getLenght() {
        return lenght;
    }

    public short getHeight() {
        return height;
    }

    public Map<BlockVector, TagCompound> getTileEntities() {
        return tileEntities;
    }

    public Map<BlockVector, TagCompound> getEntities() {
        return entities;
    }
}