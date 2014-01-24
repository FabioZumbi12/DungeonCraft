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

package de.keyle.dungeoncraft.party;

import com.ancientshores.AncientRPG.API.ApiManager;
import com.ancientshores.AncientRPG.Party.AncientRPGParty;
import com.gmail.nossr50.api.PartyAPI;
import com.herocraftonline.heroes.Heroes;
import com.herocraftonline.heroes.characters.Hero;
import de.keyle.dungeoncraft.api.party.DungeonCraftParty;
import de.keyle.dungeoncraft.party.systems.AncientRpgParty;
import de.keyle.dungeoncraft.party.systems.HeroesParty;
import de.keyle.dungeoncraft.party.systems.McMmoParty;
import de.keyle.dungeoncraft.util.PluginSupportManager;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PartyManager {

    private static List<Party> parties = new ArrayList<Party>();

    public static Party newParty(DungeonCraftPlayer player) {
        Party newParty;
        switch (isInPartyEnum(player.getPlayer())) {
            case ANCIENT:
                newParty = new AncientRpgParty(player);
                parties.add(newParty);
                break;
            case HEROES:
                newParty = new HeroesParty(player);
                parties.add(newParty);
                break;
            case MCMMO:
                newParty = new McMmoParty(player);
                parties.add(newParty);
                break;
            default:
                newParty = new de.keyle.dungeoncraft.party.systems.DungeonCraftParty(player);
                parties.add(newParty);
                break;
        }
        return newParty;
    }

    public static List<Party> getParties() {
        return Collections.unmodifiableList(parties);
    }

    public static Party getPartyByPlayer(DungeonCraftPlayer player) {
        for (Party party : parties) {
            if (party.containsPlayer(player)) {
                return party;
            }
        }
        return null;
    }

    public static Party getPartyByPlayer(Player player) {
        for (Party g : parties) {
            if (g.containsPlayer(player)) {
                return g;
            }
        }
        return null;
    }

    public static boolean isInParty(Player player) {
        return isInPartyEnum(player) != DungeonCraftParty.PartyType.NONE;
    }

    public static DungeonCraftParty.PartyType isInPartyEnum(Player player) {
        if (PluginSupportManager.isPluginUsable("Heroes")) {
            try {
                Heroes heroes = PluginSupportManager.getPluginInstance(Heroes.class);
                Hero heroPlayer = heroes.getCharacterManager().getHero(player);
                if (heroPlayer.getParty() != null && heroPlayer.getParty().getMembers().size() > 1) {
                    return DungeonCraftParty.PartyType.HEROES;
                }
            } catch (Exception ignored) {
            }
        } else if (PluginSupportManager.isPluginUsable("mcMMO")) {
            try {
                if (PartyAPI.getMembers(player) != null && PartyAPI.getMembers(player).size() > 1) {
                    return DungeonCraftParty.PartyType.MCMMO;
                }
            } catch (Exception ignored) {
            }
        } else if (PluginSupportManager.isPluginUsable("AncientRPG")) {
            try {
                ApiManager api = ApiManager.getApiManager();
                AncientRPGParty party = api.getPlayerParty(player);
                if (party != null && party.getMemberNumber() > 1) {
                    return DungeonCraftParty.PartyType.ANCIENT;
                }

            } catch (Exception ignored) {
            }
        } else {
            if (getPartyByPlayer(player) != null && getPartyByPlayer(player).getPartyStrength() > 1) {
                return DungeonCraftParty.PartyType.DUNGEONCRAFT;
            }
        }
        return DungeonCraftParty.PartyType.NONE;
    }

    //Returns complete party or null if player is not in party
    public static List<DungeonCraftPlayer> getParty(Player player) {
        List<DungeonCraftPlayer> ret = new ArrayList<DungeonCraftPlayer>();
        switch (isInPartyEnum(player)) {
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
                return getPartyByPlayer(player).getPartyMembers();

            case NONE:
                return null;

        }
        return null;
    }
}