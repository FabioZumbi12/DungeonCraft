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

package de.keyle.dungeoncraft.commands;

import de.keyle.command.framework.Command;
import de.keyle.command.framework.CommandArgs;
import de.keyle.dungeoncraft.api.events.PlayerPartyLeaveEvent;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import de.keyle.dungeoncraft.party.PartyManager;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class PartyLeaveCommand {
    @Command(name = "dcparty.leave", aliases = {"dclp", "party.leave"})
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof CraftPlayer) {
            Player player = (Player) args.getSender();
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
            Party party = PartyManager.getPartyByPlayer(dungeonCraftPlayer);
            if (party != null && party instanceof DungeonCraftParty) {
                if (dungeonCraftPlayer.getDungeon() == null) {
                    PlayerPartyLeaveEvent event = new PlayerPartyLeaveEvent(dungeonCraftPlayer, (DungeonCraftParty) party);
                    Bukkit.getPluginManager().callEvent(event);
                    if (!event.isCancelled()) {
                        if (party.getPartyLeader().equals(dungeonCraftPlayer)) {
                            party.disbandParty();
                            player.sendMessage("You've got your party disbanded!");
                        } else {
                            party.removePlayer(dungeonCraftPlayer);
                            player.sendMessage("You left " + party.getPartyLeader().getName() + "'s party!");
                        }
                    }
                    return;
                }
                player.sendMessage("You can not leave this party when you are inside a dungeon!");
                return;
            }
            player.sendMessage("You are not in a party!");
        }
    }
}