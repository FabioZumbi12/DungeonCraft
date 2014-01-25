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

import de.keyle.dungeoncraft.api.dungeon.Result;
import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.util.Configuration;
import de.keyle.dungeoncraft.util.vector.OrientationVector;

import java.util.HashSet;
import java.util.Set;

@SuppressWarnings("unused")
public class DungeonContext {
    protected final Dungeon dungeon;

    public DungeonContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public OrientationVector getSpawnPoint() {
        return dungeon.getDungeonBase().getSpawn();
    }

    public long getEndTime() {
        return dungeon.getEndTime();
    }

    public long getRemainingTime() {
        return dungeon.getEndTime() == 0 ? 0 : dungeon.getEndTime() - System.currentTimeMillis();
    }

    public boolean isTimeLocked() {
        return dungeon.isTimeLocked();
    }

    public void completeDungeonSuccess() {
        dungeon.completeDungeon(Result.Success);
    }

    public void completeDungeonFailure() {
        dungeon.completeDungeon(Result.Failure);
    }

    public int getPlayerCount() {
        return dungeon.getPlayerList().size();
    }

    public int getMinPlayerCount() {
        return dungeon.getDungeonBase().getMinPlayerCount();
    }

    public String getEnvironmentName() {
        return dungeon.getDungeonBase().getEnvironment().name();
    }

    public String[] getEntityTemplateNames() {
        Set<String> names = dungeon.getDungeonBase().getEntityTemplateRegistry().getTemplateNames();
        return names.toArray(new String[names.size()]);
    }

    public String[] getAllowedCommand() {
        Set<String> commands = new HashSet<String>();
        commands.addAll(dungeon.getDungeonBase().getAllowedCommands());
        commands.addAll(Configuration.ALLOWED_COMMANDS);
        return commands.toArray(new String[commands.size()]);
    }
}