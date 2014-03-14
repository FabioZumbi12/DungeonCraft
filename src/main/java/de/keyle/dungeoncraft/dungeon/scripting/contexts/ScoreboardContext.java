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

import org.bukkit.Bukkit;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;

@SuppressWarnings("unused")
public class ScoreboardContext {
    private final Scoreboard scoreboard;

    public final String DUMMY = "dummy";
    public final String HEALTH = "health";

    public final DisplaySlot SIDEBAR = DisplaySlot.SIDEBAR;
    public final DisplaySlot BELOW_NAME = DisplaySlot.BELOW_NAME;
    public final DisplaySlot PLAYER_LIST = DisplaySlot.PLAYER_LIST;

    public ScoreboardContext(Scoreboard scoreboard) {
        this.scoreboard = scoreboard;
    }

    public void createObjective(String name, String criteria) {
        if (scoreboard.getObjective(name) != null) {
            scoreboard.registerNewObjective(name, criteria);
        }
    }

    public void setObjectiveDisplayName(String name, String displayName) {
        Objective objective = scoreboard.getObjective(name);
        if (objective != null) {
            objective.setDisplayName(displayName);
        }
    }

    public void setObjectiveDisplaySlot(String name, DisplaySlot displaySlot) {
        Objective objective = scoreboard.getObjective(name);
        if (objective != null) {
            objective.setDisplaySlot(displaySlot);
        }
    }

    public void setObjectiveScore(String name, String playername, int score) {
        Objective objective = scoreboard.getObjective(name);
        if (objective != null) {
            objective.getScore(Bukkit.getOfflinePlayer(playername)).setScore(score);
        }
    }

    public int getObjectiveScore(String name, String playername) {
        Objective objective = scoreboard.getObjective(name);
        if (objective != null) {
            return objective.getScore(Bukkit.getOfflinePlayer(playername)).getScore();
        }
        return 0;
    }
}