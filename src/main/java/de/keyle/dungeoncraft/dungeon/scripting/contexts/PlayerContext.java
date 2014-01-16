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
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.Colorizer;
import de.keyle.dungeoncraft.util.vector.OrientationVector;

import java.util.List;

@SuppressWarnings("unused")
public class PlayerContext {
    protected final Dungeon dungeon;

    public PlayerContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public int getPlayerCount() {
        return dungeon.getPlayerList().size();
    }

    public int getPlayerNumber(String name) {
        if (DungeonCraftPlayer.isDungeonCraftPlayer(name)) {
            return dungeon.getPlayerList().indexOf(DungeonCraftPlayer.getPlayer(name)) + 1;
        }
        return -1;
    }

    public String getPlayerName(int playerNumber) {
        List<DungeonCraftPlayer> playerList = dungeon.getPlayerList();
        if (playerNumber < 1 || playerNumber > playerList.size()) {
            return "";
        }
        return dungeon.getPlayerList().get(playerNumber - 1).getName();
    }

    public String getPlayerDisplayName(int playerNumber) {
        List<DungeonCraftPlayer> playerList = dungeon.getPlayerList();
        if (playerNumber < 1 || playerNumber > playerList.size()) {
            return "";
        }
        DungeonCraftPlayer player = playerList.get(playerNumber - 1);
        if (!player.isOnline()) {
            return player.getName();
        }
        return player.getPlayer().getDisplayName();
    }

    public void setPlayerSpawn(int playerNumber, OrientationVector pos) {
        List<DungeonCraftPlayer> playerList = dungeon.getPlayerList();
        if (playerNumber < 1 || playerNumber > playerList.size()) {
            return;
        }
        DungeonCraftPlayer player = dungeon.getPlayerList().get(playerNumber - 1);
        dungeon.setPlayerSpawn(player, pos);
    }

    public void setPlayerSpawn(int playerNumber, int x, int y, int z, double yaw, double pitch) {
        setPlayerSpawn(playerNumber, new OrientationVector(x, y, z, yaw, pitch));
    }

    public OrientationVector getPlayerSpawn(int playerNumber) {
        List<DungeonCraftPlayer> playerList = dungeon.getPlayerList();
        if (playerNumber < 1 || playerNumber > playerList.size()) {
            return dungeon.getDungeonBase().getSpawn();
        }
        DungeonCraftPlayer player = dungeon.getPlayerList().get(playerNumber - 1);
        return dungeon.getPlayerSpawn(player);
    }

    public void sendMessage(int playerNumber, String message) {
        List<DungeonCraftPlayer> playerList = dungeon.getPlayerList();
        if (playerNumber < 1 || playerNumber > playerList.size() || message == null) {
            return;
        }
        DungeonCraftPlayer player = playerList.get(playerNumber - 1);
        if (player.isOnline()) {
            player.getPlayer().sendMessage(Colorizer.setColors(message));
        }
    }

    public void sendMessageRaw(int playerNumber, String message) {
        List<DungeonCraftPlayer> playerList = dungeon.getPlayerList();
        if (playerNumber < 1 || playerNumber > playerList.size()) {
            return;
        }
        DungeonCraftPlayer player = playerList.get(playerNumber - 1);
        if (player.isOnline()) {
            BukkitUtil.sendMessageRaw(player.getPlayer(), message);
        }
    }
}