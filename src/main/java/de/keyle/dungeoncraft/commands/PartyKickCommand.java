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
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PartyKickCommand {
    @Command(name = "dcparty.kick", aliases = {"dcpk", "party.kick"})
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof CraftPlayer) {
            Player player = (Player) args.getSender();
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
            Party party = dungeonCraftPlayer.getParty();
            if (party != null && party instanceof DungeonCraftParty) {
                if (!party.getPartyLeader().equals(dungeonCraftPlayer)) {
                    player.sendMessage("You are not the leader of this party!");
                    return;
                }
                List<String> arguments = args.getArgs();
                if (arguments.size() >= 1) {
                    String kickedPlayername = arguments.get(0);
                    for (DungeonCraftPlayer partyMember : party.getPartyMembers()) {
                        if (kickedPlayername.equalsIgnoreCase(partyMember.getName())) {
                            if (dungeonCraftPlayer.equals(partyMember)) {
                                player.sendMessage("You can't kick yourself!");
                                return;
                            }
                            player.sendMessage(partyMember.getName() + " has been kicked from the party.");
                            partyMember.sendMessage("You have been kicked from the party.");
                            party.removePlayer(partyMember);
                            return;
                        }
                    }
                    player.sendMessage(kickedPlayername + " is not in your party!");
                }
                return;
            }
            player.sendMessage("You are not in a party!");
        }
    }
}