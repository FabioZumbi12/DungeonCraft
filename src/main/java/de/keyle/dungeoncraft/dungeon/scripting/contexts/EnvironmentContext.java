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

@SuppressWarnings("unused")
public class EnvironmentContext {
    protected final Dungeon dungeon;

    public EnvironmentContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void setWeather(boolean weather) {
        dungeon.setWeather(weather);
    }

    public boolean getWeather() {
        return dungeon.getWeather();
    }

    /*
    // Not working properly
    public void setSkyDarkness(float value) {
        value = Math.max(Math.min(value, 1F), 0);

        for(DungeonCraftPlayer player : dungeon.getPlayerList()) {
            if(player.isOnline()) {
                BukkitUtil.setPlayerGameState(player.getPlayer(),7,value);
            }
        }
    }
    */

    public void setTime(int time) {
        dungeon.setTime(time);
    }

    public int getTime() {
        return dungeon.getLocalTime();
    }

    public void setTimeLock(boolean timeLock) {
        dungeon.setTimeLock(timeLock);
    }

    public boolean isTimeLocked() {
        return dungeon.isTimeLocked();
    }
}