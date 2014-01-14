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

package de.keyle.dungeoncraft.listeners;

import de.keyle.dungeoncraft.api.events.DungeonStartEvent;
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.List;

public class DungeonListener implements Listener {
    @EventHandler
    public void onDungeonStart(final DungeonStartEvent event) {
        List<Trigger> triggers = event.getDungeon().getTriggerRegistry().getTriggers(DungeonStartEvent.class);
        for (Trigger trigger : triggers) {
            trigger.execute();
        }
    }
}