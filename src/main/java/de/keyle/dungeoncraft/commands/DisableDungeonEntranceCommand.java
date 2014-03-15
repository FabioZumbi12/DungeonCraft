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
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntrance;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntranceRegistry;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;

public class DisableDungeonEntranceCommand {
    @Command(name = "dcdisableentrance", aliases = {"dcdde"}, permission = "dungeoncraft.admin.disableentrance")
    public void onCommand(CommandArgs args) {
        if(args.getArgs().size() > 0) {
            String name = args.getArgs().get(0);
            DungeonEntrance entrance = DungeonEntranceRegistry.getEntranceByName(name);
            if(entrance != null) {
                entrance.setEnabled(false);
                DungeonEntranceRegistry.saveEntrances();
                args.getSender().sendMessage(Util.formatText(Locales.getString("Message.Entrance.Disabled", args.getSender()),name));
            } else {
                args.getSender().sendMessage(Util.formatText(Locales.getString("Error.Entrance.Not.Found", args.getSender()),name));
            }
        }
    }
}
