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
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.Colorizer;
import de.keyle.dungeoncraft.util.vector.OrientationVector;

import java.util.Set;

@SuppressWarnings("unused")
public class PlayerContext {
    protected final Dungeon dungeon;

    public PlayerContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public int getPlayerCount() {
        return dungeon.getPlayerList().size();
    }

    public String[] getPlayerNames() {
        Set<DungeonCraftPlayer> players = dungeon.getPlayerList();
        String[] playerNames = new String[players.size()];
        int i = 0;
        for (DungeonCraftPlayer player : players) {
            playerNames[i++] = player.getName();
        }
        return playerNames;
    }

    public String getPlayerDisplayName(String name) {
        DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(name);
        if (player.isOnline()) {
            return player.getPlayer().getDisplayName();
        }
        return "";
    }

    public void setPlayerSpawn(String playerName, OrientationVector pos) {
        DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(playerName);
        dungeon.setPlayerSpawn(player, pos);
    }

    public void setPlayerSpawn(String playerName, int x, int y, int z, double yaw, double pitch) {
        setPlayerSpawn(playerName, new OrientationVector(x, y, z, yaw, pitch));
    }

    public OrientationVector getPlayerSpawn(String playerName) {
        DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(playerName);
        return dungeon.getPlayerSpawn(player);
    }

    public void sendMessageToAllPlayers(String message) {
        for (DungeonCraftPlayer player : dungeon.getPlayerList()) {
            if (player.isOnline()) {
                player.getPlayer().sendMessage(Colorizer.setColors(message));
            }
        }
    }

    public void sendMessage(String playerName, String message) {
        DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(playerName);
        if (player.isOnline()) {
            player.getPlayer().sendMessage(Colorizer.setColors(message));
        }
    }

    public void sendMessageRaw(String playerName, String message) {
        DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(playerName);
        if (player.isOnline()) {
            BukkitUtil.sendMessageRaw(player.getPlayer(), message);
        }
    }
}