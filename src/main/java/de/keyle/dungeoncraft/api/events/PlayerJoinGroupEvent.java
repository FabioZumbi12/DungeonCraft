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

package de.keyle.dungeoncraft.api.events;

import de.keyle.dungeoncraft.group.DungeonCraftPlayer;
import de.keyle.dungeoncraft.group.systems.DungeonCraftGroup;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PlayerJoinGroupEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    private final DungeonCraftPlayer player;
    private final DungeonCraftGroup group;
    private boolean isCancelled = false;

    public PlayerJoinGroupEvent(DungeonCraftPlayer player, DungeonCraftGroup group) {
        this.player = player;
        this.group = group;
    }

    public DungeonCraftPlayer getPlayer() {
        return player;
    }

    public DungeonCraftGroup getGroup() {
        return group;
    }

    @Override
    public boolean isCancelled() {
        return isCancelled;
    }

    @Override
    public void setCancelled(boolean cancel) {
        isCancelled = cancel;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    @SuppressWarnings("unused")
    public static HandlerList getHandlerList() {
        return handlers;
    }
}