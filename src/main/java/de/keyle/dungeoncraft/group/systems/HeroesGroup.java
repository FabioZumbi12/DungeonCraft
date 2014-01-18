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

package de.keyle.dungeoncraft.group.systems;

import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.api.events.HeroJoinPartyEvent;
import com.herocraftonline.heroes.api.events.HeroLeavePartyEvent;
import com.herocraftonline.heroes.characters.Hero;
import com.herocraftonline.heroes.characters.party.HeroParty;
import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.Group;
import de.keyle.dungeoncraft.util.PluginSupportManager;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

public class HeroesGroup extends Group implements Listener {
    HeroParty party;

    public HeroesGroup(DungeonCraftPlayer leader) {
        super(leader);
        Heroes heroes = PluginSupportManager.getPluginInstance(Heroes.class);
        Hero heroPlayer = heroes.getCharacterManager().getHero(Bukkit.getPlayerExact(leader.getName()));
        party = heroPlayer.getParty();
        Validate.isTrue(party.getLeader() != heroPlayer, "Player is not leader of the group");
        for (Hero h : heroPlayer.getParty().getMembers()) {
            addPlayer(h.getPlayer());
        }
        Bukkit.getPluginManager().registerEvents(this, DungeonCraftPlugin.getPlugin());
    }

    @Override
    public GroupType getGroupType() {
        return GroupType.HEROES;
    }

    @Override
    public void disbandGroup() {
        super.disbandGroup();
        HandlerList.unregisterAll(this);
    }

    @EventHandler
    public void onPlayerJoinParty(final HeroJoinPartyEvent event) {
        if (event.getParty() == party) {
            addPlayer(event.getHero().getPlayer());
        }
    }

    @EventHandler
    public void onPlayerLeaveParty(final HeroLeavePartyEvent event) {
        if (event.getParty() == party) {
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer(event.getHero().getPlayer());
            if (player.getDungeon() == null) {
                removePlayer(player);
            } else {
                event.getHero().getPlayer().sendMessage("You can not leave this group when you are inside a dungeon!");
                event.setCancelled(true);
            }
            removePlayer(event.getHero().getPlayer());
        }
        if (getGroupStrength() == 0 || getGroupLeader().equals(event.getHero().getPlayer())) {
            disbandGroup();
        }
    }
}