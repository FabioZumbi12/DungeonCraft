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
import de.keyle.dungeoncraft.api.util.MessageException;
import de.keyle.dungeoncraft.party.systems.AncientRpgParty;
import de.keyle.dungeoncraft.party.systems.DungeonCraftParty;
import de.keyle.dungeoncraft.party.systems.HeroesParty;
import de.keyle.dungeoncraft.party.systems.McMmoParty;
import de.keyle.dungeoncraft.util.PluginSupportManager;
import org.bukkit.entity.Player;

public class PartyManager {
    public static Party newParty(DungeonCraftPlayer player) throws MessageException {
        switch (getPartyType(player.getPlayer())) {
            case ANCIENT:
                return new AncientRpgParty(player);
            case HEROES:
                return new HeroesParty(player);
            case MCMMO:
                return new McMmoParty(player);
            default:
                return new DungeonCraftParty(player);
        }
    }

    public static boolean isInParty(Player player) {
        return getPartyType(player) != Party.PartyType.NONE;
    }

    public static Party.PartyType getPartyType(Player player) {
        if (DungeonCraftPlayer.isDungeonCraftPlayer(player)) {
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
            Party party = dungeonCraftPlayer.getParty();
            if (party != null) {
                if (party instanceof DungeonCraftParty) {
                    return Party.PartyType.DUNGEONCRAFT;
                } else if (party instanceof McMmoParty) {
                    return Party.PartyType.MCMMO;
                } else if (party instanceof HeroesParty) {
                    return Party.PartyType.HEROES;
                } else if (party instanceof AncientRpgParty) {
                    return Party.PartyType.ANCIENT;
                }
            }
        }
        if (PluginSupportManager.isPluginUsable("Heroes")) {
            try {
                Heroes heroes = PluginSupportManager.getPluginInstance(Heroes.class);
                Hero heroPlayer = heroes.getCharacterManager().getHero(player);
                if (heroPlayer.getParty() != null) {
                    return Party.PartyType.HEROES;
                }
            } catch (Exception ignored) {
            }
        }
        if (PluginSupportManager.isPluginUsable("mcMMO")) {
            try {
                if (PartyAPI.inParty(player)) {
                    return Party.PartyType.MCMMO;
                }
            } catch (Exception ignored) {
            }
        }
        if (PluginSupportManager.isPluginUsable("AncientRPG")) {
            try {
                ApiManager api = ApiManager.getApiManager();
                AncientRPGParty party = api.getPlayerParty(player);
                if (party != null) {
                    return Party.PartyType.ANCIENT;
                }
            } catch (Exception ignored) {
            }
        }
        return Party.PartyType.NONE;
    }
}