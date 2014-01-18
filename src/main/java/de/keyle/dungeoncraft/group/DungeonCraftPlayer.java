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

package de.keyle.dungeoncraft.group;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DungeonCraftPlayer {

    private static List<DungeonCraftPlayer> playerList = new ArrayList<DungeonCraftPlayer>();
    private Map<String, Integer> dungeonLockout = new HashMap<String, Integer>();
    private Dungeon dungeon = null;
    private Group group = null;

    private final String playerName;
    private String lastLanguage = "en";

    public DungeonCraftPlayer(String playerName) {
        this.playerName = playerName;
    }

    public DungeonCraftPlayer(Player player) {
        this.playerName = player.getName();
    }

    public String getName() {
        return this.playerName;
    }

    public static boolean isDungeonCraftPlayer(String name) {
        for (DungeonCraftPlayer player : playerList) {
            if (player.getName().equals(name)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isDungeonCraftPlayer(Player player) {
        return isDungeonCraftPlayer(player.getName());
    }

    public static DungeonCraftPlayer getPlayer(String name) {
        for (DungeonCraftPlayer player : playerList) {
            if (player.getName().equals(name)) {
                return player;
            }
        }

        DungeonCraftPlayer tmpPlayer = new DungeonCraftPlayer(name);
        playerList.add(tmpPlayer);
        return tmpPlayer;
    }

    public void setDungeon(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void setGroup(Group group) {
        this.group = group;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public Group getGroup() {
        return group;
    }

    public boolean hasLockout(String instanceName) {
        return this.dungeonLockout.containsKey(instanceName);
    }

    public int getRemainingLockout(String instanceName) {
        if (hasLockout(instanceName)) {
            return this.dungeonLockout.get(instanceName);
        }
        return 0;
    }

    public void setDungenLockout(String instanceName, int timeinminutes) {
        this.dungeonLockout.put(instanceName, timeinminutes);
    }

    public static DungeonCraftPlayer getPlayer(Player player) {
        return getPlayer(player.getName());
    }

    public static List<DungeonCraftPlayer> getAllPlayers() {
        return playerList;
    }

    public String getLanguage() {
        if (isOnline()) {
            lastLanguage = BukkitUtil.getPlayerLanguage(getPlayer());
        }
        return lastLanguage;
    }

    public Player getPlayer() {
        return Bukkit.getServer().getPlayerExact(playerName);
    }

    public boolean isOnline() {
        return getPlayer() != null && getPlayer().isOnline(); // This could cause lag on bigger servers.
    }

    public boolean equals(Object o) {
        if (o == null) {
            return false;
        } else if (o instanceof Player) {
            Player player = (Player) o;
            return playerName.equals(player.getName());
        } else if (o instanceof DungeonCraftPlayer) {
            return this == o;
        }
        return false;
    }
}