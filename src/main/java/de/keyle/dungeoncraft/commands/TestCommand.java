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

package de.keyle.dungeoncraft.commands;

import de.keyle.command.framework.Command;
import de.keyle.command.framework.CommandArgs;
import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.DungeonBase;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import org.bukkit.entity.Player;

public class TestCommand {

    DungeonBase base = new DungeonBase("test1");

    @Command(name = "dctest")
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof Player) {
            Dungeon d = new Dungeon("testDungeon", base);
            d.addPlayer(DungeonCraftPlayer.getPlayer((Player) args.getSender()));
            DungeonManager.addDungeon(d);
        }
    }
}