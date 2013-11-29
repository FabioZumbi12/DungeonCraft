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

package de.keyle.dungeoncraft.util.schematic;

import de.keyle.dungeoncraft.util.vector.BlockVector;
import org.spout.nbt.*;
import org.spout.nbt.stream.NBTInputStream;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.GZIPInputStream;

public class SchematicLoader extends Thread {
    private ISchematicReveiver schematicReceiver;

    public SchematicLoader(ISchematicReveiver schematicReceiver) {
        this.schematicReceiver = schematicReceiver;
    }

    public void run() {
        Schematic schematic;
        try {
            schematic = loadSchematic(schematicReceiver.getSchematicFile());
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        schematicReceiver.setSchematic(schematic);
    }

    public static Schematic loadSchematic(File file) throws IOException {

        if (!file.exists()) {
            throw new IllegalArgumentException("Schematic file not found");
        }

        FileInputStream stream = new FileInputStream(file);
        NBTInputStream nbtStream = new NBTInputStream(new GZIPInputStream(stream));

        CompoundTag schematicTag = (CompoundTag) nbtStream.readTag();
        if (!schematicTag.getName().equals("Schematic")) {
            throw new IllegalArgumentException("Tag \"Schematic\" does not exist or is not first");
        }

        CompoundMap schematic = schematicTag.getValue();
        if (!schematic.containsKey("Blocks")) {
            throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
        }

        short width = getChildTag(schematic, "Width", ShortTag.class).getValue();
        short length = getChildTag(schematic, "Length", ShortTag.class).getValue();
        short height = getChildTag(schematic, "Height", ShortTag.class).getValue();

        String materials = getChildTag(schematic, "Materials", StringTag.class).getValue();
        if (!materials.equals("Alpha")) {
            throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
        }

        byte[] blocks = getChildTag(schematic, "Blocks", ByteArrayTag.class).getValue();
        byte[] blockData = getChildTag(schematic, "Data", ByteArrayTag.class).getValue();

        ListTag<CompoundTag> tileEntitiesTag = getChildTag(schematic, "TileEntities", ListTag.class);
        //ListTag<CompoundTag> entitiesTag = getChildTag(schematic, "Entities", ListTag.class);

        Map<BlockVector, CompoundMap> tileEntities = new HashMap<BlockVector, CompoundMap>();
        for (CompoundTag compound : tileEntitiesTag.getValue()) {
            CompoundMap tileEntity = compound.getValue();

            if (!tileEntity.containsKey("x")) {
                continue;
            } else if (!tileEntity.containsKey("y")) {
                continue;
            } else if (!tileEntity.containsKey("z")) {
                continue;
            }
            int x = ((IntTag) tileEntity.get("x")).getValue();
            int y = ((IntTag) tileEntity.get("y")).getValue();
            int z = ((IntTag) tileEntity.get("z")).getValue();

            BlockVector v = new BlockVector(x, y, z);
            tileEntities.put(v, tileEntity);
        }

        return new Schematic(blocks, blockData, width, length, height, tileEntities);
    }

    private static <T extends Tag> T getChildTag(CompoundMap items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        Tag tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}