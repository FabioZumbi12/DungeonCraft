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

package de.keyle.dungeoncraft.group.systems;

import com.gmail.nossr50.api.PartyAPI;
import com.gmail.nossr50.events.party.McMMOPartyChangeEvent;
import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class McMmoGroup extends Group implements Listener {
    String partyName;

    public McMmoGroup(DungeonCraftPlayer leader) {
        super(leader);
        partyName = PartyAPI.getPartyName(leader.getPlayer());
        Validate.isTrue(leader.getName().equals(PartyAPI.getPartyLeader(partyName)), "Player is not leader of the group.");
        for (Player member : PartyAPI.getOnlineMembers(partyName)) {
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
        if (event.getNewParty() != null && event.getNewParty().equals(partyName)) {
            addPlayer(event.getPlayer());
        } else if (event.getNewParty() == null && event.getOldParty() != null && event.getOldParty().equals(partyName)) {
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(event.getPlayer());
            if (player.getDungeon() == null) {
                removePlayer(player);
            } else {
                event.getPlayer().getPlayer().sendMessage("You can not leave this group while inside a dungeon!");
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
        if (PartyAPI.getPartyName(event.getPlayer()).equals(partyName)) {
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(event.getPlayer());
            if (!this.getGroupMembers().contains(player)) {
                addPlayer(player);
            }
        }
    }
}