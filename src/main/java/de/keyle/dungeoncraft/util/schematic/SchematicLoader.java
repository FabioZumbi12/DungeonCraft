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
import de.keyle.knbt.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

        TagCompound schematicTag = TagStream.readTag(stream, true);
        if (schematicTag == null) {
            schematicTag = TagStream.readTag(stream, false);
            if (schematicTag == null) {
                throw new IllegalArgumentException("Can not read tags");
            }
        }

        if (!schematicTag.containsKeyAs("Blocks", TagByteArray.class)) {
            throw new IllegalArgumentException("Schematic file is missing a \"Blocks\" tag");
        }

        short width = getChildTag(schematicTag, "Width", TagShort.class).getShortData();
        short length = getChildTag(schematicTag, "Length", TagShort.class).getShortData();
        short height = getChildTag(schematicTag, "Height", TagShort.class).getShortData();

        String materials = getChildTag(schematicTag, "Materials", TagString.class).getStringData();
        if (!materials.equals("Alpha")) {
            throw new IllegalArgumentException("Schematic file is not an Alpha schematic");
        }

        byte[] blocks = getChildTag(schematicTag, "Blocks", TagByteArray.class).getByteArrayData();
        byte[] blockData = getChildTag(schematicTag, "Data", TagByteArray.class).getByteArrayData();
        byte[] biomeData;
        if (schematicTag.containsKeyAs("Biomes", TagByteArray.class)) {
            biomeData = getChildTag(schematicTag, "Biomes", TagByteArray.class).getByteArrayData();
        } else {
            biomeData = new byte[length * width];
        }

        TagList tileEntitiesTag = getChildTag(schematicTag, "TileEntities", TagList.class);
        TagList entitiesTag = getChildTag(schematicTag, "Entities", TagList.class);

        List<TagBase> readOnlyList = tileEntitiesTag.getReadOnlyList();
        Map<BlockVector, TagCompound> tileEntities = new HashMap<BlockVector, TagCompound>();

        for (int i = 0; i < readOnlyList.size(); i++) {
            TagCompound tileEntity = tileEntitiesTag.getTagAs(i, TagCompound.class);

            if (!tileEntity.containsKeyAs("x", TagInt.class)) {
                continue;
            } else if (!tileEntity.containsKeyAs("y", TagInt.class)) {
                continue;
            } else if (!tileEntity.containsKeyAs("z", TagInt.class)) {
                continue;
            }
            int x = ((TagInt) tileEntity.get("x")).getIntData();
            int y = ((TagInt) tileEntity.get("y")).getIntData();
            int z = ((TagInt) tileEntity.get("z")).getIntData();

            BlockVector v = new BlockVector(x, y, z);
            tileEntities.put(v, tileEntity);
        }

        List<TagBase> readOnlyEntityList = entitiesTag.getReadOnlyList();
        Map<BlockVector, TagCompound> entities = new HashMap<BlockVector, TagCompound>();

        for (int i = 0; i < readOnlyEntityList.size(); i++) {
            TagCompound entity = entitiesTag.getTagAs(i, TagCompound.class);

            int x = 0;
            int y = 0;
            int z = 0;

            if (entity.containsKeyAs("TileX", TagInt.class)) {
                x = ((TagInt) entity.get("TileX")).getIntData();
            } else if (entity.containsKeyAs("TileY", TagInt.class)) {
                y = ((TagInt) entity.get("TileY")).getIntData();
            } else if (entity.containsKeyAs("TileZ", TagInt.class)) {
                z = ((TagInt) entity.get("TileZ")).getIntData();
            }

            BlockVector v = new BlockVector(x, y, z);
            entities.put(v, entity);
        }

        return new Schematic(blocks, blockData, biomeData, width, length, height, tileEntities, entities);
    }

    private static <T extends TagBase> T getChildTag(TagCompound items, String key, Class<T> expected) throws IllegalArgumentException {
        if (!items.getCompoundData().containsKey(key)) {
            throw new IllegalArgumentException("Schematic file is missing a \"" + key + "\" tag");
        }
        TagBase tag = items.get(key);
        if (!expected.isInstance(tag)) {
            throw new IllegalArgumentException(key + " tag is not of tag type " + expected.getName());
        }
        return expected.cast(tag);
    }
}