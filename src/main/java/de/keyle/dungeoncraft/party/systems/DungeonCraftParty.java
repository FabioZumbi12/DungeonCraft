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

import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.PartyManager;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.DropoutStack;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;
import de.keyle.fanciful.FancyMessage;
import org.bukkit.ChatColor;

public class DungeonCraftParty extends de.keyle.dungeoncraft.party.Party {
    private DropoutStack<DungeonCraftPlayer> invites = new DropoutStack<DungeonCraftPlayer>();

    public DungeonCraftParty(DungeonCraftPlayer leader) {
        super(leader);
    }

    public void invitePlayer(DungeonCraftPlayer invitedPlayer) {
        if (!invitedPlayer.isOnline()) {
            return;
        }
        if (PartyManager.isInParty(invitedPlayer.getPlayer())) {
            return;
        }
        invites.add(invitedPlayer);
        FancyMessage message = new FancyMessage("You have been invited to ")
                .then(getPartyLeader().getName())
                .color(ChatColor.AQUA)
                .then("'s party. Join the party by clicking ")
                .then("here")
                .command("/dcparty join " + leader.getName() + " " + getUuid().toString())
                .color(ChatColor.GOLD)
                .style(ChatColor.UNDERLINE)
                .then(".");
        BukkitUtil.sendMessageRaw(invitedPlayer.getPlayer(), message.toJSONString());
    }

    public boolean isPlayerInvited(DungeonCraftPlayer player) {
        return invites != null && invites.contains(player);
    }

    public void setLeader(DungeonCraftPlayer player) {
        if (!getPartyMembers().contains(player)) {
            return;
        }
        leader = player;
        invites.clear();
        sendMessage("Message.Leader.Change",player.getName());
    }

    public void addPlayer(DungeonCraftPlayer player) {
        super.addPlayer(player);
        if (isPlayerInvited(player)) {
            invites.remove(player);
        }
    }

    @Override
    public PartyType getPartyType() {
        return PartyType.DUNGEONCRAFT;
    }
}