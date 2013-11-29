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

package de.keyle.dungeoncraft.dungeon;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.util.schematic.ISchematicReveiver;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import de.keyle.dungeoncraft.util.schematic.SchematicLoader;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.bukkit.World;

import java.io.File;
import java.lang.ref.WeakReference;

public class DungeonBase implements ISchematicReveiver {
    int maxPlayerCount = 0;
    int minPlayerCount = 1;
    String name;
    OrientationVector spawn;
    int timeLimit = 0;
    World.Environment environment = World.Environment.NORMAL;
    boolean isLoading = false;

    WeakReference<Schematic> schematic = null;

    public DungeonBase(String name) {
        this.name = name;
    }

    public boolean hasTimeLimit() {
        return timeLimit > 0;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public int getMinPlayerCount() {
        return minPlayerCount;
    }

    public String getName() {
        return name;
    }

    public OrientationVector getSpawn() {
        return spawn;
    }

    public boolean isSchematicLoaded() {
        return schematic != null && schematic.get() != null;
    }

    public void loadSchematic() {
        if (!isLoading) {
            SchematicLoader schematicLoader = new SchematicLoader(this);
            schematicLoader.start();
            isLoading = true;
        }
    }

    public Schematic getSchematic() {
        if (!isSchematicLoaded()) {
            loadSchematic();
            return null;
        }
        return schematic.get();
    }

    @Override
    public File getSchematicFile() {
        return new File(DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "dungeons" + File.separator + name + File.separator + name + ".schematic");
    }

    public synchronized void setSchematic(Schematic schematic) {
        this.schematic = new WeakReference<Schematic>(schematic);
        isLoading = false;
    }
}