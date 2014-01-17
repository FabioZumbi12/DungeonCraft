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
import de.keyle.dungeoncraft.dungeon.DungeonBaseRegistry;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntrance;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntranceRegistry;
import de.keyle.dungeoncraft.util.vector.Region;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TestCommand {

    @Command(name = "dctest")
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof Player) {
            Player p = (Player) args.getSender();
            /*
            DungeonCraftPlayer player = DungeonCraftPlayer.getPlayer((Player) args.getSender());
            Group g = GroupManager.newGroup(player);
            Dungeon d = new Dungeon("testDungeon", base, g);
            d.setExitLocation(((Player) args.getSender()).getLocation());
            DungeonManager.addDungeon(d);
            */
            Location playerLocation = p.getLocation();
            Vector min = new Vector(playerLocation.getBlockX() + 2, playerLocation.getBlockY(), playerLocation.getBlockZ() - 1);
            Vector max = new Vector(playerLocation.getBlockX() + 3, playerLocation.getBlockY(), playerLocation.getBlockZ() + 1);
            Region r = new Region(min, max);
            DungeonEntrance dungeonEntrance = new DungeonEntrance("test_dungeon_1", p.getLocation().getWorld().getName(), r, DungeonBaseRegistry.getDungeonBase("test1"), new Location(p.getLocation().getWorld(), 50D, 31D, -3.5));
            DungeonEntranceRegistry.registerEntrance(dungeonEntrance);
        }
    }
}