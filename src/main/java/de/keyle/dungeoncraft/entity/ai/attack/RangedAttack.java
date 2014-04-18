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

package de.keyle.dungeoncraft.entity.ai.attack;

import de.keyle.dungeoncraft.api.entity.ai.AIGoal;
import de.keyle.dungeoncraft.entity.ai.attack.ranged.*;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import net.minecraft.server.v1_7_R3.EntityArrow;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.MathHelper;
import net.minecraft.server.v1_7_R3.World;

public class RangedAttack extends AIGoal {
    private final EntityDungeonCraft entityDungeonCraft;
    private EntityLiving target;
    private int shootTimer;
    private float walkSpeedModifier;
    private int lastSeenTimer;
    private float rangeSquared;
    private Projectile.ProjectileTypes projectileTypes = Projectile.ProjectileTypes.Arrow;

    public RangedAttack(EntityDungeonCraft entityDungeonCraft, float walkSpeedModifier, float range) {
        this(entityDungeonCraft, walkSpeedModifier, range, Projectile.ProjectileTypes.Arrow);
    }

    public RangedAttack(EntityDungeonCraft entityDungeonCraft, float walkSpeedModifier, float range, Projectile.ProjectileTypes projectileTypes) {
        this.entityDungeonCraft = entityDungeonCraft;
        this.shootTimer = -1;
        this.lastSeenTimer = 0;
        this.walkSpeedModifier = walkSpeedModifier;
        this.rangeSquared = (range * range);
        this.projectileTypes = projectileTypes;
    }

    @Override
    public boolean shouldStart() {
        EntityLiving goalTarget = this.entityDungeonCraft.getGoalTarget();

        if (goalTarget == null || !goalTarget.isAlive() || !entityDungeonCraft.canMove()) {
            return false;
        }
        double space = this.entityDungeonCraft.e(goalTarget.locX, goalTarget.boundingBox.b, goalTarget.locZ);
        if (space < 16) {
            return false;
        }
        this.target = goalTarget;
        return true;
    }

    @Override
    public boolean shouldFinish() {
        if (entityDungeonCraft.getGoalTarget() == null || !target.isAlive() || !entityDungeonCraft.canMove()) {
            return true;
        }
        if (this.entityDungeonCraft.e(target.locX, target.boundingBox.b, target.locZ) < 16) {
            return true;
        }
        return false;
    }

    @Override
    public void finish() {
        this.target = null;
        this.lastSeenTimer = 0;
        this.shootTimer = -1;

        this.entityDungeonCraft.petNavigation.getParameters().removeSpeedModifier("RangedAttack");
    }

    @Override
    public void tick() {
        double distanceToTarget = this.entityDungeonCraft.e(this.target.locX, this.target.boundingBox.b, this.target.locZ);
        boolean canSee = this.entityDungeonCraft.getEntitySenses().canSee(this.target);

        if (canSee) {
            this.lastSeenTimer++;
        } else {
            this.lastSeenTimer = 0;
        }

        if ((distanceToTarget <= this.rangeSquared) && (this.lastSeenTimer >= 20)) {
            this.entityDungeonCraft.petNavigation.getParameters().removeSpeedModifier("RangedAttack");
            this.entityDungeonCraft.petNavigation.stop();
        } else {
            this.entityDungeonCraft.petNavigation.getParameters().addSpeedModifier("RangedAttack", walkSpeedModifier);
            this.entityDungeonCraft.petNavigation.navigateTo(this.target);
        }

        this.entityDungeonCraft.getControllerLook().a(this.target, 30.0F, 30.0F);

        if (--this.shootTimer <= 0) {
            if (distanceToTarget < this.rangeSquared && canSee) {
                shootProjectile(this.target, 1, getProjectileTypes());
                this.shootTimer = 20;
            }
        }
    }

    private Projectile.ProjectileTypes getProjectileTypes() {
        return projectileTypes;
    }

    public void shootProjectile(EntityLiving target, float damage, Projectile.ProjectileTypes projectileTypes) {
        World world = target.world;

        if (projectileTypes == Projectile.ProjectileTypes.Arrow) {
            EntityArrow arrow = new Arrow(world, entityDungeonCraft, target, 1.6F, 1);
            arrow.b(damage);
            arrow.setCritical(false);
            entityDungeonCraft.makeSound("random.bow", 1.0F, 1.0F / (entityDungeonCraft.getRandom().nextFloat() * 0.4F + 0.8F));
            world.addEntity(arrow);
        } else if (projectileTypes == Projectile.ProjectileTypes.Snowball) {
            Snowball snowball = new Snowball(world, entityDungeonCraft);
            double distanceX = target.locX - entityDungeonCraft.locX;
            double distanceY = target.locY + target.getHeadHeight() - 1.100000023841858D - snowball.locY;
            double distanceZ = target.locZ - entityDungeonCraft.locZ;
            float distance20percent = MathHelper.sqrt(distanceX * distanceX + distanceZ * distanceZ) * 0.2F;
            snowball.setDamage(damage);
            snowball.shoot(distanceX, distanceY + distance20percent, distanceZ, 1.6F, 1);
            entityDungeonCraft.makeSound("random.bow", 1.0F, 1.0F / (entityDungeonCraft.getRandom().nextFloat() * 0.4F + 0.8F));
            world.addEntity(snowball);
        } else if (projectileTypes == Projectile.ProjectileTypes.LargeFireball) {
            double distanceX = this.target.locX - entityDungeonCraft.locX;
            double distanceY = this.target.boundingBox.b + this.target.length / 2.0F - (entityDungeonCraft.locY + entityDungeonCraft.length / 2.0F);
            double distanceZ = this.target.locZ - entityDungeonCraft.locZ;
            LargeFireball largeFireball = new LargeFireball(world, entityDungeonCraft, distanceX, distanceY, distanceZ);
            largeFireball.locY = (entityDungeonCraft.locY + entityDungeonCraft.length / 2.0F + 0.5D);
            largeFireball.setDamage(damage);
            world.addEntity(largeFireball);
            world.makeSound(entityDungeonCraft.locX + 0.5D, entityDungeonCraft.locY + 0.5D, entityDungeonCraft.locZ + 0.5D, "mob.ghast.fireball", 1.0F + entityDungeonCraft.getRandom().nextFloat(), entityDungeonCraft.getRandom().nextFloat() * 0.7F + 0.3F);
        } else if (projectileTypes == Projectile.ProjectileTypes.SmallFireball) {
            double distanceX = this.target.locX - entityDungeonCraft.locX;
            double distanceY = this.target.boundingBox.b + this.target.length / 2.5F - (entityDungeonCraft.locY + entityDungeonCraft.length / 2.5F);
            double distanceZ = this.target.locZ - entityDungeonCraft.locZ;
            SmallFireball smallFireball = new SmallFireball(world, entityDungeonCraft, distanceX, distanceY, distanceZ);
            smallFireball.locY = (entityDungeonCraft.locY + entityDungeonCraft.length / 2.0F + 0.5D);
            smallFireball.setDamage(damage);
            world.addEntity(smallFireball);
            world.makeSound(entityDungeonCraft.locX + 0.5D, entityDungeonCraft.locY + 0.5D, entityDungeonCraft.locZ + 0.5D, "mob.ghast.fireball", 1.0F + entityDungeonCraft.getRandom().nextFloat(), entityDungeonCraft.getRandom().nextFloat() * 0.7F + 0.3F);
        } else if (projectileTypes == Projectile.ProjectileTypes.WitherSkull) {
            double distanceX = this.target.locX - entityDungeonCraft.locX;
            double distanceY = this.target.boundingBox.b + this.target.length / 2.5F - (entityDungeonCraft.locY + entityDungeonCraft.length / 2.5F);
            double distanceZ = this.target.locZ - entityDungeonCraft.locZ;
            WitherSkull witherSkull = new WitherSkull(world, entityDungeonCraft, distanceX, distanceY, distanceZ);
            witherSkull.locY = (entityDungeonCraft.locY + entityDungeonCraft.length / 2.0F + 0.5D);
            witherSkull.setDamage(damage);
            world.addEntity(witherSkull);
            world.makeSound(entityDungeonCraft.locX + 0.5D, entityDungeonCraft.locY + 0.5D, entityDungeonCraft.locZ + 0.5D, "mob.wither.shoot", 1.0F + entityDungeonCraft.getRandom().nextFloat(), entityDungeonCraft.getRandom().nextFloat() * 0.7F + 0.3F);
        }
    }
}