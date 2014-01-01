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

package de.keyle.dungeoncraft.commands;

import de.keyle.command.framework.Command;
import de.keyle.command.framework.CommandArgs;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.group.GroupManager;
import de.keyle.dungeoncraft.group.systems.DungeonCraftGroup;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.UUID;

public class JoinGroupCommand {
    @Command(name = "dcjoingroup", aliases = {"dcjg"})
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof CraftPlayer) {
            Player player = (Player) args.getSender();
            if (args.getArgs().size() >= 1) {
                UUID uuid = UUID.fromString(args.getArgs().get(0));
                for (Group g : GroupManager.getGroups()) {
                    if (g instanceof DungeonCraftGroup && g.getUuid().equals(uuid)) {
                        DungeonCraftGroup dungeonCraftGroup = (DungeonCraftGroup) g;
                        DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
                        if (!dungeonCraftGroup.isPlayerInvited(dungeonCraftPlayer)) {
                            continue;
                        }
                        for (DungeonCraftPlayer member : g.getGroupMembers()) {
                            if (member.isOnline()) {
                                member.getPlayer().sendMessage(player.getDisplayName() + " joined your group!");
                            }
                        }
                        g.addPlayer(player);
                        player.sendMessage("You are now in " + dungeonCraftGroup.getGroupLeader().getName() + "' group!");
                        return;
                    }
                }
                player.sendMessage("Group not found!");
                return;
            }
            player.sendMessage("Playername required!");
        }
    }
}