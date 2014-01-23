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

package de.keyle.dungeoncraft.dungeon.scripting;

import com.google.common.collect.ArrayListMultimap;
import de.keyle.dungeoncraft.api.events.*;
import org.bukkit.event.Event;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;

import java.util.List;

public class TriggerRegistry {
    public static final int DUNGEON_START = 0;
    public static final int ENTITY_DIES = 10;
    public static final int CREATURE_SPAWN = 11;
    public static final int ENTITY_INTERACT = 12;
    public static final int ENTITY_DAMAGE_BY_ENTITY = 13;
    public static final int PLAYER_DEATH = 20;
    public static final int PLAYER_DROP_ITEM = 21;
    public static final int PLAYER_RIGHTCLICK_ENTITY = 22;
    public static final int PLAYER_RIGHTCLICK_BLOCK = 24;
    public static final int PLAYER_ENTER_REGION_EVENT = 25;
    public static final int PLAYER_LEAVE_REGION_EVENT = 26;
    public static final int PLAYER_ENTER_DUNGEON_EVENT = 27;
    public static final int PLAYER_LEAVE_DUNGEON_EVENT = 28;

    private ArrayListMultimap<Class<? extends Event>, Trigger> triggers = ArrayListMultimap.create();

    public static Class<? extends Event> getEventClassById(int eventId) {
        switch (eventId) {
            case DUNGEON_START:
                return DungeonStartEvent.class;
            case ENTITY_DIES:
                return EntityDeathEvent.class;
            case CREATURE_SPAWN:
                return CreatureSpawnEvent.class;
            case ENTITY_INTERACT:
                return EntityInteractEvent.class;
            case ENTITY_DAMAGE_BY_ENTITY:
                return EntityDamageByEntityEvent.class;
            case PLAYER_DEATH:
                return PlayerDeathEvent.class;
            case PLAYER_DROP_ITEM:
                return PlayerDropItemEvent.class;
            case PLAYER_RIGHTCLICK_ENTITY:
                return PlayerInteractEntityEvent.class;
            case PLAYER_RIGHTCLICK_BLOCK:
                return PlayerInteractEvent.class;
            case PLAYER_ENTER_REGION_EVENT:
                return PlayerEnterRegionEvent.class;
            case PLAYER_LEAVE_REGION_EVENT:
                return PlayerLeaveRegionEvent.class;
            case PLAYER_ENTER_DUNGEON_EVENT:
                return PlayerEnterDungeonEvent.class;
            case PLAYER_LEAVE_DUNGEON_EVENT:
                return PlayerLeaveDungeonEvent.class;
            default:
                return Event.class;
        }
    }

    public List<Trigger> getTriggers(int eventId) {
        return triggers.get(getEventClassById(eventId));
    }

    public List<Trigger> getTriggers(Class<? extends Event> eventClass) {
        return triggers.get(eventClass);
    }

    public void registerTrigger(int eventId, Trigger trigger) {
        Class<? extends Event> eventClass = getEventClassById(eventId);
        if (eventClass != Event.class) {
            triggers.put(eventClass, trigger);
        }
    }
}