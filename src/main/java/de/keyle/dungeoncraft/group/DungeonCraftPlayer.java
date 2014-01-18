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

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class DungeonCraftPlayer {
    private static Map<String, DungeonCraftPlayer> playerList = new HashMap<String, DungeonCraftPlayer>();
    private Map<String, Long> dungeonLockout = new HashMap<String, Long>();
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
        return playerList.containsKey(name);
    }

    public static boolean isDungeonCraftPlayer(Player player) {
        return isDungeonCraftPlayer(player.getName());
    }

    public static DungeonCraftPlayer getPlayer(String name) {
        if (playerList.containsKey(name)) {
            return playerList.get(name);
        }

        DungeonCraftPlayer tmpPlayer = new DungeonCraftPlayer(name);
        playerList.put(name, tmpPlayer);
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
        return getRemainingLockout(instanceName) > 0L;
    }

    public Long getRemainingLockout(String instanceName) {
        if (dungeonLockout.containsKey(instanceName)) {
            long lockout = this.dungeonLockout.get(instanceName) - System.currentTimeMillis();
            if (lockout < 0L) {
                lockout = 0L;
                dungeonLockout.remove(instanceName);
            }
            return lockout;
        }
        return 0L;
    }

    public void setDungenLockout(String instanceName, long time) {
        this.dungeonLockout.put(instanceName, System.currentTimeMillis() + time);
    }

    public static DungeonCraftPlayer getPlayer(Player player) {
        return getPlayer(player.getName());
    }

    public static Collection<DungeonCraftPlayer> getAllPlayers() {
        return Collections.unmodifiableCollection(playerList.values());
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