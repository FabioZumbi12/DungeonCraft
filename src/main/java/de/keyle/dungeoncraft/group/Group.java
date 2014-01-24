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

import de.keyle.dungeoncraft.api.group.DungeonCraftGroup;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Group implements DungeonCraftGroup {
    private UUID uuid;
    private List<DungeonCraftPlayer> members = new ArrayList<DungeonCraftPlayer>();
    private DungeonCraftPlayer leader;

    public Group(DungeonCraftPlayer leader) {
        this.uuid = UUID.randomUUID();
        this.leader = leader;
        addPlayer(leader);
    }

    public void addPlayer(Player player) {
        addPlayer(DungeonCraftPlayer.getPlayer(player));
    }

    public void addPlayer(DungeonCraftPlayer player) {
        if (!this.containsPlayer(player)) {
            members.add(player);
            player.setGroup(this);
            sendMessage(player.getName() + " joined the group.");
        }
    }

    public void removePlayer(Player player) {
        removePlayer(DungeonCraftPlayer.getPlayer(player));

    }

    public void removePlayer(DungeonCraftPlayer player) {
        if (this.containsPlayer(player)) {
            this.members.remove(player);
            player.setGroup(null);
            sendMessage(player.getName() + " has left the group.");
        }
    }

    public boolean containsPlayer(Player player) {
        return containsPlayer(DungeonCraftPlayer.getPlayer(player));
    }

    public boolean containsPlayer(DungeonCraftPlayer player) {
        return members.contains(player);
    }

    public void sendMessage(String message) {
        for (DungeonCraftPlayer player : members) {
            if (player.isOnline()) {
                player.getPlayer().sendMessage(message);
            }
        }
    }

    @Override
    public int getGroupStrength() {
        return members.size();
    }

    @Override
    public List<DungeonCraftPlayer> getGroupMembers() {
        return this.members;
    }

    @Override
    public DungeonCraftPlayer getGroupLeader() {
        return leader;
    }

    @Override
    public void disbandGroup() {
        members.clear();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String toString() {
        String tmp = "Group ID: " + uuid.toString() + "\n";
        int i = 0;
        for (DungeonCraftPlayer player : members) {
            tmp += "Member " + ++i + ": " + player.toString() + "\n";
        }
        return tmp;
    }

    public boolean equals(Object o) {
        if (o instanceof Group) {
            Group group = (Group) o;
            if (group.getGroupMembers().size() != this.members.size()) {
                for (int i = 0; i < this.members.size(); i++) {
                    if (!this.members.get(i).equals(group.getGroupMembers().get(i))) {
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