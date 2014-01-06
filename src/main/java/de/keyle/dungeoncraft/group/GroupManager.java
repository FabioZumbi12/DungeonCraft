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

package de.keyle.dungeoncraft.group;

import com.ancientshores.AncientRPG.API.ApiManager;
import com.ancientshores.AncientRPG.Party.AncientRPGParty;
import com.gmail.nossr50.api.PartyAPI;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import de.keyle.dungeoncraft.group.systems.AncientRpgGroup;
import de.keyle.dungeoncraft.group.systems.DungeonCraftGroup;
import de.keyle.dungeoncraft.group.systems.HeroesGroup;
import de.keyle.dungeoncraft.group.systems.McMmoGroup;
import de.keyle.dungeoncraft.util.PluginSupportManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static de.keyle.dungeoncraft.api.group.DungeonCraftGroup.GroupType;

public class GroupManager {

    private static List<Group> groups = new ArrayList<Group>();

    public static Group newGroup(DungeonCraftPlayer player) {
        Group newGroup;
        switch (isInGroupEnum(player.getPlayer())) {
            case ANCIENT:
                newGroup = new AncientRpgGroup(player);
                groups.add(newGroup);
                break;
            case HEROES:
                newGroup = new HeroesGroup(player);
                groups.add(newGroup);
                break;
            case MCMMO:
                newGroup = new McMmoGroup(player);
                groups.add(newGroup);
                break;
            default:
                newGroup = new DungeonCraftGroup(player);
                groups.add(newGroup);
                break;
        }
        return newGroup;
    }

    public static List<Group> getGroups() {
        return Collections.unmodifiableList(groups);
    }

    public static Group getGroupByPlayer(DungeonCraftPlayer player) {
        for (Group g : groups) {
            if (g.containsPlayer(player)) {
                return g;
            }
        }
        return null;
    }

    public static Group getGroupByPlayer(Player player) {
        for (Group g : groups) {
            if (g.containsPlayer(player)) {
                return g;
            }
        }
        return null;
    }

    public static boolean isInGroup(Player player) {
        return isInGroupEnum(player) != GroupType.NONE;
    }

    public static GroupType isInGroupEnum(Player player) {
        if (PluginSupportManager.isPluginUsable("Heroes")) {
            try {
                Heroes heroes = PluginSupportManager.getPluginInstance(Heroes.class);
                Hero heroPlayer = heroes.getCharacterManager().getHero(player);
                if (heroPlayer.getParty() != null && heroPlayer.getParty().getMembers().size() > 1) {
                    return GroupType.HEROES;
                }
            } catch (Exception ignored) {
            }
        } else if (PluginSupportManager.isPluginUsable("mcMMO")) {
            try {
                if (PartyAPI.getMembers(player) != null && PartyAPI.getMembers(player).size() > 1) {
                    return GroupType.MCMMO;
                }
            } catch (Exception ignored) {
            }
        } else if (PluginSupportManager.isPluginUsable("AncientRPG")) {
            try {
                ApiManager api = ApiManager.getApiManager();
                AncientRPGParty party = api.getPlayerParty(player);
                if (party != null && party.getMemberNumber() > 1) {
                    return GroupType.ANCIENT;
                }

            } catch (Exception ignored) {
            }
        } else {
            if (getGroupByPlayer(player) != null && getGroupByPlayer(player).getGroupStrength() > 1) {
                return GroupType.DUNGEONCRAFT;
            }
        }
        return GroupType.NONE;
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
                return getGroupByPlayer(player).getGroupMembers();

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