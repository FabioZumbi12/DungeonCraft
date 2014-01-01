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

package de.keyle.dungeoncraft.group.systems;

import com.gmail.nossr50.datatypes.party.Party;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import com.gmail.nossr50.party.PartyManager;
import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class McMmoGroup extends Group implements Listener {
    Party party;

    public McMmoGroup(DungeonCraftPlayer leader) {
        super(leader);
        party = PartyManager.getPlayerParty(leader.getName());
        Validate.isTrue(leader.getName().equals(party.getLeader()), "Player is not leader of the group");
        for (Player member : party.getOnlineMembers()) {
            addPlayer(member);
        }
        Bukkit.getPluginManager().registerEvents(this, DungeonCraftPlugin.getPlugin());
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.MCMMO;
    }

    @Override
    public void disbandGroup() {
        super.disbandGroup();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPartyChange(final McMMOPartyChangeEvent event) {
        if (event.getNewParty() != null && event.getNewParty().equals(party.getName())) {
            if (event.getReason() == McMMOPartyChangeEvent.EventReason.JOINED_PARTY) {
                addPlayer(event.getPlayer());
            } else if (event.getReason() == McMMOPartyChangeEvent.EventReason.CHANGED_PARTIES) {
                addPlayer(event.getPlayer());
            }
        } else if (event.getOldParty() != null && event.getOldParty().equals(party.getName())) {
            if (event.getReason() == McMMOPartyChangeEvent.EventReason.KICKED_FROM_PARTY) {
                removePlayer(event.getPlayer());
            } else if (event.getReason() == McMMOPartyChangeEvent.EventReason.LEFT_PARTY) {
                removePlayer(event.getPlayer());
            } else if (event.getReason() == McMMOPartyChangeEvent.EventReason.CHANGED_PARTIES) {
                removePlayer(event.getPlayer());
            }
        }
    }
}