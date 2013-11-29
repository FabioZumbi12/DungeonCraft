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

import de.keyle.dungeoncraft.dungeon.generator.DungeonManager;
import de.keyle.dungeoncraft.util.IScheduler;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Dungeon implements IScheduler {
    protected boolean isReady = false;
    protected boolean isLoaded = false;
    protected final String dungeonName;
    protected final DungeonBase dungeonBase;
    protected final UUID uuid;
    protected final DungeonManager.DungeonField position;

    protected List<Player> playerList = new ArrayList<Player>();

    public Dungeon(String dungeonName, DungeonBase dungeonTheme) {
        this.dungeonName = dungeonName;
        this.dungeonBase = dungeonTheme;
        uuid = UUID.randomUUID();
        position = DungeonManager.getNewDungeonField();
    }

    public void setReady() {
        isReady = true;
        DungeonCraftLogger.write("Dungeon is now ready to use!");
    }

    boolean first = true;

    @Override
    public void schedule() {
        if (isReady) {
            if (first) {
                DungeonCraftLogger.write("Ok Lets do something");
                first = false;
            }
        }
        if (!isLoaded) {
            if (!dungeonBase.isSchematicLoaded()) {
                dungeonBase.loadSchematic();
            } else {
                lockSchematic(dungeonBase.getSchematic());
            }
        }
    }

    public void lockSchematic(Schematic schematic) {
        DungeonManager.assignSchematicToDungeonField(position, schematic);
    }

    public void unlockSchematic() {
        DungeonManager.dissociateSchematicFromDungeonField(position);
    }
}