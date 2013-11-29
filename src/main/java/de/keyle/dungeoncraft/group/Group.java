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

package de.keyle.dungeoncraft.group;

import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Group {

    private UUID uuid;
    private List<DungeonCraftPlayer> players = new ArrayList<DungeonCraftPlayer>();

    public Group() {
        this.uuid = UUID.randomUUID();
    }

    public void addPlayer(Player player) {
        addPlayer(DungeonCraftPlayer.getPlayer(player));

    }

    public void addPlayer(DungeonCraftPlayer player) {
        if (!this.containsPlayer(player)) {
            players.add(player);
        }
    }

    public void deletePlayer(Player player) {
        deletePlayer(DungeonCraftPlayer.getPlayer(player));

    }

    public void deletePlayer(DungeonCraftPlayer player) {
        if (this.containsPlayer(player)) {
            this.players.remove(player);
        }
    }

    public boolean containsPlayer(Player player) {
        return containsPlayer(DungeonCraftPlayer.getPlayer(player));
    }

    public boolean containsPlayer(DungeonCraftPlayer player) {
        return players.contains(player);
    }

    public List<DungeonCraftPlayer> getPlayers() {
        return this.players;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String toString() {
        String tmp = "Group ID: " + uuid.toString() + "\n";
        int i = 0;
        for (DungeonCraftPlayer player : players) {
            tmp += "Member " + ++i + ": " + player.toString() + "\n";
        }
        return tmp;
    }

    public boolean equals(Object o) {
        if (o instanceof Group) {
            Group group = (Group) o;
            if (group.getPlayers().size() != this.players.size()) {
                for (int i = 0; i < this.players.size(); i++) {
                    if (!this.players.get(i).equals(group.getPlayers().get(i))) {
                        return false;
                    }
                }
                return true;
            }
            return false;
        }
        return false;
    }
}