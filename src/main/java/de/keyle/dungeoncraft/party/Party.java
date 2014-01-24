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

package de.keyle.dungeoncraft.party;

import de.keyle.dungeoncraft.api.party.DungeonCraftParty;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public abstract class Party implements DungeonCraftParty {
    private UUID uuid;
    private List<DungeonCraftPlayer> members = new ArrayList<DungeonCraftPlayer>();
    protected DungeonCraftPlayer leader;

    public Party(DungeonCraftPlayer leader) {
        this.uuid = UUID.randomUUID();
        this.leader = leader;
        addPlayer(leader);
    }

    public void addPlayer(Player player) {
        addPlayer(DungeonCraftPlayer.getPlayer(player));
    }

    public void addPlayer(DungeonCraftPlayer player) {
        if (!this.containsPlayer(player)) {
            sendMessage(player.getName() + " joined the party.");
            members.add(player);
            player.setParty(this);
        }
    }

    public void removePlayer(Player player) {
        removePlayer(DungeonCraftPlayer.getPlayer(player));

    }

    public void removePlayer(DungeonCraftPlayer player) {
        if (this.containsPlayer(player)) {
            this.members.remove(player);
            player.setParty(null);
            sendMessage(player.getName() + " has left the party.");
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
    public int getPartyStrength() {
        return members.size();
    }

    @Override
    public List<DungeonCraftPlayer> getPartyMembers() {
        return this.members;
    }

    @Override
    public DungeonCraftPlayer getPartyLeader() {
        return leader;
    }

    @Override
    public void disbandParty() {
        for (DungeonCraftPlayer member : members) {
            member.setParty(null);
            if (member.isOnline() && !member.equals(leader)) {
                member.getPlayer().sendMessage("Your party has been disbanded.");
            }
        }
        members.clear();
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public String toString() {
        String tmp = "Party ID: " + uuid.toString() + "\n";
        int i = 0;
        for (DungeonCraftPlayer player : members) {
            tmp += "Member " + ++i + ": " + player.toString() + "\n";
        }
        return tmp;
    }

    public boolean equals(Object o) {
        if (o instanceof Party) {
            Party party = (Party) o;
            if (party.getPartyMembers().size() != this.members.size()) {
                for (int i = 0; i < this.members.size(); i++) {
                    if (!this.members.get(i).equals(party.getPartyMembers().get(i))) {
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