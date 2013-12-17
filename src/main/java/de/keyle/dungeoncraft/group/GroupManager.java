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

package de.keyle.dungeoncraft.group;

import com.ancientshores.AncientRPG.API.ApiManager;
import com.ancientshores.AncientRPG.Party.AncientRPGParty;
import com.gmail.nossr50.api.PartyAPI;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import de.keyle.dungeoncraft.util.PluginSupportManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class GroupManager {

    private enum GroupTypes {
        HEROES, ANCIENT, MCMMO, DUNGEONCRAFT, NONE
    }

    public static boolean isInGroup(Player player) {
        return isInGroupEnum(player) != GroupTypes.NONE;
    }

    public static GroupTypes isInGroupEnum(Player player) {
        if (PluginSupportManager.isPluginUsable("Heroes")) {
            try {
                Heroes heroes = PluginSupportManager.getPluginInstance(Heroes.class);
                Hero heroPlayer = heroes.getCharacterManager().getHero(player);
                if (heroPlayer.getParty() != null && heroPlayer.getParty().getMembers().size() > 1) {
                    return GroupTypes.HEROES;
                }
            } catch (Exception ignored) {
            }
        } else if (PluginSupportManager.isPluginUsable("mcMMO")) {
            try {
                if (PartyAPI.getMembers(player) != null && PartyAPI.getMembers(player).size() > 1) {
                    return GroupTypes.MCMMO;
                }
            } catch (Exception ignored) {
            }
        } else if (PluginSupportManager.isPluginUsable("AncientRPG")) {
            try {
                ApiManager api = ApiManager.getApiManager();
                AncientRPGParty party = api.getPlayerParty(player);
                if (party != null && party.getMemberNumber() > 1) {
                    return GroupTypes.ANCIENT;
                }

            } catch (Exception ignored) {
            }
        } else {
            if (Group.getGroupByPlayer(player) != null && Group.getGroupByPlayer(player).getPartyCount() > 1) {
                return GroupTypes.DUNGEONCRAFT;
            }
        }
        return GroupTypes.NONE;
    }

    //Returns complete group or null if player is not in group
    public static List<DungeonCraftPlayer> getGroup(Player player) {
        List<DungeonCraftPlayer> ret = new ArrayList<DungeonCraftPlayer>();
        switch (isInGroupEnum(player)) {
            case HEROES:
                Heroes heroes = PluginSupportManager.getPluginInstance(Heroes.class);
                Hero heroPlayer = heroes.getCharacterManager().getHero(player);
                for (Hero h : heroPlayer.getParty().getMembers()) {
                    ret.add(DungeonCraftPlayer.getPlayer(h.getPlayer()));
                }
                return ret;

            case ANCIENT:
                ApiManager api = ApiManager.getApiManager();
                AncientRPGParty party = api.getPlayerParty(player);
                for (Player p : party.Member) {
                    ret.add(DungeonCraftPlayer.getPlayer(p));
                }
                return ret;

            case MCMMO:
                for (String s : PartyAPI.getMembers(player)) {
                    ret.add(DungeonCraftPlayer.getPlayer(s));
                }
                return ret;

            case DUNGEONCRAFT:
                return Group.getGroupByPlayer(player).getPlayers();

            case NONE:
                return null;

        }
        return null;
    }

    public static int getGroupStrength(Player player) {
        List<DungeonCraftPlayer> tmp = getGroup(player);
        return tmp == null ? -1 : tmp.size();
    }
}