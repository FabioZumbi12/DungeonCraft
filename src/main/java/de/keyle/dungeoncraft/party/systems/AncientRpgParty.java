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

package de.keyle.dungeoncraft.party.systems;

import com.ancientshores.AncientRPG.API.AncientRPGPartyJoinEvent;
import com.ancientshores.AncientRPG.API.AncientRPGPartyLeaveEvent;
import com.ancientshores.AncientRPG.API.ApiManager;
import com.ancientshores.AncientRPG.Party.AncientRPGParty;
import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class AncientRpgParty extends Party implements Listener {
    AncientRPGParty party;

    public AncientRpgParty(DungeonCraftPlayer leader) {
        super(leader);
        party = ApiManager.getApiManager().getPlayerParty(leader.getPlayer());
        Validate.isTrue(leader.equals(party.mLeader), "Player is not leader of the party.");
        for (Player p : party.Member) {
            addPlayer(p);
        }
        Bukkit.getPluginManager().registerEvents(this, DungeonCraftPlugin.getPlugin());
    }

    @Override
    public PartyType getPartyType() {
        return PartyType.ANCIENT;
    }

    @Override
    public void disbandParty() {
        super.disbandParty();
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
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(event.getPlayer());
            if (player.getDungeon() == null) {
                removePlayer(player);
            } else {
                event.getPlayer().sendMessage("You can not leave this party when you are inside a dungeon!");
                event.setCancelled(true);
            }
        }
        if (getPartyStrength() == 0 || getPartyLeader().equals(event.getPlayer())) {
            disbandParty();
        }
    }
}