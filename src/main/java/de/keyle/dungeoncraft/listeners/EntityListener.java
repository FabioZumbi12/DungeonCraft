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

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.DungeonField;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.scripting.Trigger;
import de.keyle.dungeoncraft.entity.types.CraftDungeonCraftEntity;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import net.minecraft.server.v1_7_R1.ItemStack;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class EntityListener implements Listener {
    @EventHandler
    public void onEntitiyDeath(final EntityDeathEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            LivingEntity entity = event.getEntity();
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(EntityDeathEvent.class);
                for (Trigger trigger : triggers) {
                    trigger.execute(event.getEntity(), event.getDroppedExp(), event.getDrops());
                }
            }

            if(entity instanceof CraftDungeonCraftEntity) {
                Player killer = entity.getKiller();
                Party party = DungeonCraftPlayer.getPlayer(killer).getParty();
                CraftDungeonCraftEntity entityTemplate = (CraftDungeonCraftEntity) entity;
                for(DungeonCraftPlayer player : party.getPartyMembers()) {
                    if(entityTemplate.getHandle().expToDrop != 0) {
                        player.giveEXP((int) Math.ceil(entityTemplate.getHandle().expToDrop / party.getPartyStrength()));
                    }
                }
            }
        }
    }

    @EventHandler
    public void onCreatureSpawn(final CreatureSpawnEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(CreatureSpawnEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(event.getEntity(), event.getLocation(), event.getSpawnReason())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityInteract(final EntityInteractEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(EntityInteractEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(event.getEntity(), event.getBlock())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }

    @EventHandler
    public void onEntityDamageByEntity(final EntityDamageByEntityEvent event) {
        Location l = event.getEntity().getLocation();
        if (l.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
            DungeonField position = DungeonFieldManager.getDungeonFieldForChunk(l.getChunk().getX(), l.getChunk().getZ());
            Dungeon d = DungeonManager.getDungeonAt(position);
            if (d != null) {
                List<Trigger> triggers = d.getTriggerRegistry().getTriggers(EntityDamageByEntityEvent.class);
                for (Trigger trigger : triggers) {
                    if (trigger.execute(event.getEntity(), event.getDamager(), event.getDamage(), event.getCause())) {
                        event.setCancelled(true);
                    }
                }
            }
        }
    }
}