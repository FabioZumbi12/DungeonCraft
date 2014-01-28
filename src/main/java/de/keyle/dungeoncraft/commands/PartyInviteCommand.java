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
import de.keyle.dungeoncraft.party.PartyManager;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class PartyInviteCommand {
    @Command(name = "dcparty.invite", aliases = {"dcip", "party.invite"})
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof CraftPlayer) {
            Player player = (Player) args.getSender();
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
            Party party = dungeonCraftPlayer.getParty();
            if (party != null && party instanceof DungeonCraftParty) {
                if (!party.getPartyLeader().equals(dungeonCraftPlayer)) {
                    player.sendMessage(Locales.getString("Error.Not.Leader",player));
                    return;
                }
                List<String> arguments = args.getArgs();
                if (arguments.size() >= 1) {
                    Player invitedPlayer = Bukkit.getPlayer(arguments.get(0));
                    if (dungeonCraftPlayer.equals(DungeonCraftPlayer.getPlayer(invitedPlayer))) {
                        player.sendMessage(Locales.getString("Error.Selfinvite",player));
                        return;
                    }
                    if (invitedPlayer != null) {
                        DungeonCraftPlayer invitedDungeonCraftPlayer = DungeonCraftPlayer.getPlayer(invitedPlayer);
                        if (PartyManager.isInParty(invitedPlayer)) {
                            player.sendMessage(Util.formatText(Locales.getString("Error.Already.In.Party",player),invitedPlayer.getDisplayName()));
                            return;
                        }
                        DungeonCraftParty dungeonCraftParty = ((DungeonCraftParty) party);
                        if (!dungeonCraftParty.getPartyMembers().contains(invitedDungeonCraftPlayer)) {
                            dungeonCraftParty.invitePlayer(invitedDungeonCraftPlayer);
                            player.sendMessage(Util.formatText(Locales.getString("Message.Invite.Successful",player),invitedPlayer.getDisplayName()));
                        } else {
                            player.sendMessage(Util.formatText(Locales.getString("Error.Already.In.This.Party",player),invitedPlayer.getDisplayName()));
                        }
                        return;
                    }
                    player.sendMessage(Util.formatText(Locales.getString("Error.Not.Online",player),arguments.get(0)));
                }
                return;
            }
            player.sendMessage(Locales.getString("Error.Not.In.Party",player));
        }
    }
}