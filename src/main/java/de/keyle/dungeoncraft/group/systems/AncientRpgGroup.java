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

import com.ancientshores.AncientRPG.API.AncientRPGPartyJoinEvent;
import com.ancientshores.AncientRPG.API.AncientRPGPartyLeaveEvent;
import com.ancientshores.AncientRPG.API.ApiManager;
import com.ancientshores.AncientRPG.Party.AncientRPGParty;
import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class AncientRpgGroup extends Group implements Listener {
    AncientRPGParty party;

    public AncientRpgGroup(DungeonCraftPlayer leader) {
        super(leader);
        party = ApiManager.getApiManager().getPlayerParty(leader.getPlayer());
        Validate.isTrue(leader.equals(party.mLeader), "Player is not leader of the group");
        for (Player p : party.Member) {
            addPlayer(p);
        }
        Bukkit.getPluginManager().registerEvents(this, DungeonCraftPlugin.getPlugin());
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.ANCIENT;
    }

    @Override
    public void disbandGroup() {
        super.disbandGroup();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerJoinParty(final AncientRPGPartyJoinEvent event) {
        if (event.getParty() == party) {
            addPlayer(event.getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeaveParty(final AncientRPGPartyLeaveEvent event) {
        if (event.getParty() == party) {
            removePlayer(event.getPlayer());
        }
        if (getGroupStrength() == 0 || getGroupLeader().equals(event.getPlayer())) {
            disbandGroup();
        }
    }
}