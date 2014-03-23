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

package de.keyle.dungeoncraft.entity.ai.attack.ranged;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import net.minecraft.server.v1_7_R2.*;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftSnowball;

public class Snowball extends EntitySnowball implements Projectile {
    protected float damage = 0;

    public Snowball(World world, EntityDungeonCraft entityLiving) {
        super(world, entityLiving);
    }

    @Override
    public EntityDungeonCraft getShooter() {
        return (EntityDungeonCraft) this.shooter;
    }

    public void setDamage(float damage) {
        this.damage = damage;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new CraftSnowball(this.world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    @Override
    public void a(NBTTagCompound nbtTagCompound) {
    }

    @Override
    public void b(NBTTagCompound nbtTagCompound) {
    }

    @Override
    protected void a(MovingObjectPosition paramMovingObjectPosition) {
        if (paramMovingObjectPosition.entity != null) {
            paramMovingObjectPosition.entity.damageEntity(DamageSource.projectile(this, getShooter()), damage);
        }
        for (int i = 0; i < 8; i++) {
            this.world.addParticle("snowballpoof", this.locX, this.locY, this.locZ, 0.0D, 0.0D, 0.0D);
        }
        die();
    }
}