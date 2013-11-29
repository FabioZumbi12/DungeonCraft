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
import de.keyle.dungeoncraft.util.vector.Vector;
import net.minecraft.server.v1_6_R3.Block;
import net.minecraft.server.v1_6_R3.NBTTagCompound;
import net.minecraft.server.v1_6_R3.TileEntity;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_6_R3.CraftWorld;
import org.bukkit.generator.BlockPopulator;

import java.lang.reflect.Field;
import java.util.Random;

public class DungeonPopulator extends BlockPopulator {
    private static Field nmsBlock_isTileEntityField;

    private NBTTagCompound nbtData = null;

    static {
        try {
            nmsBlock_isTileEntityField = Block.class.getDeclaredField("isTileEntity");
            nmsBlock_isTileEntityField.setAccessible(true);
        } catch (Throwable e) {
            nmsBlock_isTileEntityField = null;
        }
    }

    @Override
    public void populate(World world, Random random, Chunk chunk) {

    }

    public static boolean set(World world, Vector position, Schematic block) {
        NBTTagCompound data = null;
        if (!hasTileEntity(world.getBlockTypeIdAt(position.getBlockX(), position.getBlockY(), position.getBlockZ()))) {
            return false;
        }


        if (data != null) {
            TileEntity te = ((CraftWorld) world).getHandle().getTileEntity(position.getBlockX(), position.getBlockY(), position.getBlockZ());

            if (te != null) {
                te.a(data);
                return true;
            }
        }

        return false;
    }

    public static boolean hasTileEntity(int type) {
        Block nmsBlock = Block.byId[type];
        if (nmsBlock == null) {
            return false;
        }
        try {
            return nmsBlock_isTileEntityField.getBoolean(nmsBlock);
        } catch (IllegalAccessException e) {
            return false;
        }
    }
}