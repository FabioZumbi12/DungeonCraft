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
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.party.Party;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityInteractEvent;

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

            if (entity instanceof CraftDungeonCraftEntity) {
                CraftDungeonCraftEntity craftEntity = (CraftDungeonCraftEntity) entity;
                EntityDungeonCraft entityDungeonCraft = craftEntity.getHandle();

                Player killer = entity.getKiller();
                if (killer != null) {
                    Party party = DungeonCraftPlayer.getPlayer(killer).getParty();
                    for (DungeonCraftPlayer player : party.getPartyMembers()) {
                        if (craftEntity.getHandle().expToDrop != 0) {
                            player.giveEXP((int) Math.ceil(entityDungeonCraft.expToDrop / party.getPartyStrength()));
                        }
                    }
                }

                //Drop the loot
                List<org.bukkit.inventory.ItemStack> drops = lootGenerator(entityDungeonCraft.getLootTable(), entityDungeonCraft.getLootIterations(), entityDungeonCraft.getMaxDrops());
                if (drops != null && !drops.isEmpty()) {
                    for (org.bukkit.inventory.ItemStack item : drops) {
                        l.getWorld().dropItem(l, item);
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

    private List<org.bukkit.inventory.ItemStack> lootGenerator(Map<Float, org.bukkit.inventory.ItemStack> lootTable, int iterations, int maxdrops) {
        List<Float> rolls = new ArrayList<Float>();
        List<org.bukkit.inventory.ItemStack> items = new ArrayList<org.bukkit.inventory.ItemStack>();
        List<org.bukkit.inventory.ItemStack> ret = new ArrayList<org.bukkit.inventory.ItemStack>();
        Random r = new Random();
        for (int i = 0; i < iterations + 1; i++) {
            rolls.add(r.nextFloat() * 100);
        }

        for (float roll : rolls) {
            float lowestKey = 100;
            for (float key : lootTable.keySet()) {
                if (key >= roll && lowestKey > key) {
                    lowestKey = key;
                }
                items.add(lootTable.get(lowestKey));
            }
        }

        if (maxdrops < items.size()) {
            for (int i = 0; i < maxdrops; i++) {
                ret.add(items.get(r.nextInt(items.size())));
            }
        }
        return ret;
    }
}