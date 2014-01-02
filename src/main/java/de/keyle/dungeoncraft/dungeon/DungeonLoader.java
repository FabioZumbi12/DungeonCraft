/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2011-2014 Keyle & xXLupoXx
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

package de.keyle.dungeoncraft.dungeon;

import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftChunkProvider;
import de.keyle.dungeoncraft.util.schematic.Schematic;

public class DungeonLoader extends Thread {
    private final Dungeon dungeon;
    private int callbackCount = 0;

    public DungeonLoader(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void run() {
        DungeonBase dungeonBase = dungeon.dungeonBase;
        Schematic schematic;

        while (true) {
            schematic = dungeonBase.getSchematic();
            if (schematic != null) {
                break;
            }
        }
        dungeon.lockSchematic();

        int xCount = (int) Math.ceil(schematic.getLenght() / 16.);
        int zCount = (int) Math.ceil(schematic.getWidth() / 16.);

        callbackCount = xCount * zCount;

        for (int x = 0; x < xCount; x++) {
            for (int z = 0; z < zCount; z++) {
                DungeonCraftChunkProvider.chunkloader.generateChunkAt(dungeon.position.getX() + x, dungeon.position.getZ() + z, new Runnable() {
                    @Override
                    public void run() {
                        callbackCount--;
                    }
                });
            }
        }
        while (true) {
            if (callbackCount <= 0) {
                break;
            }
        }
        dungeon.setReady();
    }
}