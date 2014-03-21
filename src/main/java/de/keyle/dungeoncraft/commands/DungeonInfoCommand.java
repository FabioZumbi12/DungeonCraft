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
import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class DungeonInfoCommand {
    @Command(name = "dcdungeoninfo", aliases = {"dcdi"})
    public void onCommand(CommandArgs args) {
        CommandSender sender = args.getSender();
        if (sender instanceof Player) {
            Player player = (Player) sender;
            DungeonCraftPlayer dungeonCraftPlayer = DungeonCraftPlayer.getPlayer(player);
            Dungeon dungeon = dungeonCraftPlayer.getDungeon();
            if (dungeon != null) {
                player.sendMessage(Util.formatText(Locales.getString("Terms.Common.Dungeon.Name", dungeonCraftPlayer), dungeon.getDungeonName()));
                player.sendMessage(Util.formatText(Locales.getString("Message.Dungeon.Ends.In", dungeonCraftPlayer), Util.getDurationBreakdown(dungeon.getEndTime() - System.currentTimeMillis(), dungeonCraftPlayer)));
            } else {
                player.sendMessage(Util.formatText(Locales.getString("Error.Not.In.Dungeon", dungeonCraftPlayer)));
            }
        }
    }
}
