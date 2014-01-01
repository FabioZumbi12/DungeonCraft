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

import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.group.GroupManager;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.DropoutStack;
import de.keyle.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class DungeonCraftGroup extends Group {
    private boolean open = false;
    private DropoutStack<DungeonCraftPlayer> invites = new DropoutStack<DungeonCraftPlayer>();

    public DungeonCraftGroup(DungeonCraftPlayer leader) {
        super(leader);
    }

    public void invitePlayer(DungeonCraftPlayer invitedPlayer) {
        if (GroupManager.getGroupByPlayer(invitedPlayer) != null) {
            return;
        }
        invites.add(invitedPlayer);
        FancyMessage message = new FancyMessage("You have been invited to ")
                .then(getGroupLeader().getName())
                .color(ChatColor.AQUA)
                .then("'s group. Join the group by clicking ")
                .then("here")
                .command("/dcjoingroup " + getUuid().toString())
                .color(ChatColor.GOLD)
                .style(ChatColor.UNDERLINE)
                .then(".");
        BukkitUtil.sendMessageRaw(invitedPlayer.getPlayer(), message.toJSONString());
    }

    public boolean isPlayerInvited(DungeonCraftPlayer player) {
        return invites != null && invites.contains(player);
    }

    public void addPlayer(DungeonCraftPlayer player) {
        super.addPlayer(player);
        if (isPlayerInvited(player)) {
            invites.remove(player);
        }
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.DUNGEONCRAFT;
    }
}