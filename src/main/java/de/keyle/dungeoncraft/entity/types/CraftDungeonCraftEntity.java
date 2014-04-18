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

package de.keyle.dungeoncraft.entity.types;

import net.minecraft.server.v1_7_R3.EntityCreature;
import org.bukkit.craftbukkit.v1_7_R3.CraftServer;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftCreature;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftLivingEntity;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class CraftDungeonCraftEntity extends CraftCreature {
    protected EntityDungeonCraft dungeonCraftEntity;

    public CraftDungeonCraftEntity(CraftServer server, EntityDungeonCraft entityDungeonCraft) {
        super(server, entityDungeonCraft);
        dungeonCraftEntity = entityDungeonCraft;
    }

    @Override
    public void _INVALID_damage(int amount) {
        damage((double) amount);
    }

    @Override
    public void _INVALID_damage(int amount, Entity source) {
        damage((double) amount, source);
    }

    @Override
    public int _INVALID_getHealth() {
        return (int) getHealth();
    }

    @Override
    public int _INVALID_getLastDamage() {
        return (int) getLastDamage();
    }

    @Override
    public int _INVALID_getMaxHealth() {
        return (int) getMaxHealth();
    }

    @Override
    public void _INVALID_setHealth(int health) {
        setHealth((double) health);
    }

    @Override
    public void _INVALID_setLastDamage(int damage) {
        setLastDamage((double) damage);
    }

    @Override
    public void _INVALID_setMaxHealth(int health) {
    }

    public boolean canMove() {
        return dungeonCraftEntity.canMove();
    }

    @Override
    public EntityDungeonCraft getHandle() {
        return dungeonCraftEntity;
    }

    public EntityType getDungeonCraftEntityType() {
        return EntityType.Bat; //ToDo Change that
    }

    @Override
    public org.bukkit.entity.EntityType getType() {
        return org.bukkit.entity.EntityType.UNKNOWN;
    }

    @Override
    public void setHealth(double health) {
        if (health < 0) {
            health = 0;
        }
        if (health > getMaxHealth()) {
            health = getMaxHealth();
        }
        super.setHealth(health);
    }

    public void setTarget(LivingEntity target) {
        EntityCreature entity = getHandle();
        if (target == null) {
            entity.target = null;
        } else if (target instanceof CraftLivingEntity) {
            dungeonCraftEntity.setGoalTarget(((CraftLivingEntity) target).getHandle());
        }
    }

    @Override
    public String toString() {
        return "CraftDungeonCraftEntity{type=" + getDungeonCraftEntityType() + "}";
    }
}