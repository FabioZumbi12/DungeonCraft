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

package de.keyle.dungeoncraft.dungeon.scripting.contexts;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;

@SuppressWarnings("unused")
public class BlockContext {
    protected final Dungeon dungeon;

    public BlockContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void setBlock(int x, int y, int z, String materialName) {
        setBlock(x, y, z, materialName, (byte) 0);
    }

    public void setBlock(int x, int y, int z, String materialName, byte data) {
        setBlock(x, y, z, materialName, data, "");
    }

    public Block getBlockAt(int x, int y, int z) {
        if (x < 0 || x >= 1600 || y < 0 || y >= 256 || z < 0 || z >= 1600) {
            return null;
        }

        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();

        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);
        return world.getBlockAt(x,y,z);
    }

    public boolean testForBlock(int x, int y, int z, Block b) {
        return getBlockAt(x,y,z).equals(b);
    }

    public void setBlock(int x, int y, int z, String materialName, byte data, String jsonTag) {
        if (x < 0 || x >= 1600 || y < 0 || y >= 256 || z < 0 || z >= 1600) {
            return;
        }
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);
        Block b = world.getBlockAt(x, y, z);

        b.setType(Material.getMaterial(materialName));
        b.setData(data);

        if (jsonTag != null && !jsonTag.equals("")) {
            Location blockLocation = new Location(world, x, y, z);
            BukkitUtil.setTileEntity(blockLocation, jsonTag);
        }
    }

    public void fill(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String materialName) {
        fill(fromX, fromY, fromZ, toX, toY, toZ, materialName, (byte) 0);
    }

    public void fill(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String materialName, byte data) {
        fill(fromX, fromY, fromZ, toX, toY, toZ, materialName, data, "");
    }

    public void fill(int fromX, int fromY, int fromZ, int toX, int toY, int toZ, String materialName, byte data, String jsonTag) {
        if (fromX < 0 || fromX >= 1600 || fromY < 0 || fromY >= 256 || fromZ < 0 || fromZ >= 1600) {
            return;
        } else if (toX < 0 || toX >= 1600 || toY < 0 || toY >= 256 || toZ < 0 || toZ >= 1600) {
            return;
        } else if ((toX - fromX) * (toY - fromY) * (toZ - fromX) > 5000) {
            return;
        }
        if (toX < fromX) {
            int tempX = fromX;
            fromX = toX;
            toX = tempX;
        }
        if (toX < fromX) {
            int tempY = fromY;
            fromY = toY;
            toY = tempY;
        }
        if (toZ < fromZ) {
            int tempZ = fromZ;
            fromZ = toZ;
            toZ = tempZ;
        }
        fromX += dungeon.getPosition().getBlockX();
        fromZ += dungeon.getPosition().getBlockZ();
        toX += dungeon.getPosition().getBlockX();
        toZ += dungeon.getPosition().getBlockZ();
        Material material = Material.getMaterial(materialName);
        for (int x = fromX; x <= toX; x++) {
            for (int y = fromY; y <= toY; y++) {
                for (int z = fromZ; z <= toZ; z++) {
                    World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);
                    Block b = world.getBlockAt(x, y, z);

                    b.setType(material);
                    b.setData(data);

                    if (jsonTag != null && !jsonTag.equals("")) {
                        Location blockLocation = new Location(world, x, y, z);
                        BukkitUtil.setTileEntity(blockLocation, jsonTag);
                    }
                }
            }
        }
    }
}