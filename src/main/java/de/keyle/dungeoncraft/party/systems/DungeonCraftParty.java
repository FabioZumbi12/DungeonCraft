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
import de.keyle.dungeoncraft.util.locale.Locales;

public class DungeonCraftParty extends de.keyle.dungeoncraft.party.Party {
    private DropoutStack<DungeonCraftPlayer> invites = new DropoutStack<DungeonCraftPlayer>();
    private boolean friendlyFire = false;

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
        if (isPlayerInvited(invitedPlayer)) {
            return;
        }
        invites.add(invitedPlayer);
        String inviteMessage = Locales.getString("Message.Party.Invite", invitedPlayer.getPlayer());
        inviteMessage = inviteMessage.replace("{0}", getPartyLeader().getName());
        inviteMessage = inviteMessage.replace("{1}", "/dcparty join " + leader.getName() + " " + getUuid().toString());
        BukkitUtil.sendMessageRaw(invitedPlayer.getPlayer(), inviteMessage);
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
        sendMessage("Message.Party.Leader.Change", player.getName());
    }

    public void addPlayer(DungeonCraftPlayer player) {
        super.addPlayer(player);
        if (isPlayerInvited(player)) {
            invites.remove(player);
        }
    }

    public void setFriendlyFire(boolean friendlyFire) {
        this.friendlyFire = friendlyFire;
    }

    public boolean isFriendlyFireEnabled() {
        return friendlyFire;
    }

    @Override
    public PartyType getPartyType() {
        return PartyType.DUNGEONCRAFT;
    }
}