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
import de.keyle.dungeoncraft.api.events.PlayerPartyJoinEvent;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import de.keyle.dungeoncraft.party.PartyManager;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class PartyJoinCommand {
    @Command(name = "dcparty.join", aliases = {"dcjp", "party.join"})
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof CraftPlayer) {
            Player player = (Player) args.getSender();
            if (args.getArgs().size() >= 1) {
                UUID uuid = UUID.fromString(args.getArgs().get(0));
                for (Party party : PartyManager.getParties()) {
                    if (party instanceof DungeonCraftParty && party.getUuid().equals(uuid)) {
                        DungeonCraftParty dungeonCraftParty = (DungeonCraftParty) party;
                        DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
                        if (!dungeonCraftParty.isPlayerInvited(dungeonCraftPlayer)) {
                            continue;
                        }
                        PlayerPartyJoinEvent event = new PlayerPartyJoinEvent(dungeonCraftPlayer, (DungeonCraftParty) party);
                        Bukkit.getPluginManager().callEvent(event);
                        if (!event.isCancelled()) {
                            party.addPlayer(player);
                            player.sendMessage("You are now in " + dungeonCraftParty.getPartyLeader().getName() + "' party!");
                        }
                        return;
                    }
                }
                player.sendMessage("Party not found!");
                return;
            }
            player.sendMessage("Playername required!");
        }
    }
}