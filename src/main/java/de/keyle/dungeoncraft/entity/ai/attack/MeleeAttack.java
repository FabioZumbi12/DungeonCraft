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
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.pigzombie.EntityDungeonCraftPigZombie;
import de.keyle.dungeoncraft.entity.types.skeleton.EntityDungeonCraftSkeleton;
import de.keyle.dungeoncraft.entity.types.zombie.EntityDungeonCraftZombie;
import net.minecraft.server.v1_7_R1.EntityLiving;

public class MeleeAttack extends AIGoal {
    EntityDungeonCraft entityDungeonCraft;
    EntityLiving targetEntity;
    double range;
    float walkSpeedModifier;
    private int ticksUntilNextHitLeft = 0;
    private int ticksUntilNextHit;
    private int timeUntilNextNavigationUpdate;

    public MeleeAttack(EntityDungeonCraft entityDungeonCraft, float walkSpeedModifier, double range, int ticksUntilNextHit) {
        this.entityDungeonCraft = entityDungeonCraft;
        this.walkSpeedModifier = walkSpeedModifier;
        this.range = range;
        this.ticksUntilNextHit = ticksUntilNextHit;
    }

    @Override
    public boolean shouldStart() {
        EntityLiving targetEntity = this.entityDungeonCraft.getGoalTarget();
        if (targetEntity == null) {
            return false;
        }
        if (!targetEntity.isAlive()) {
            return false;
        }
        if (this.entityDungeonCraft.e(targetEntity.locX, targetEntity.boundingBox.b, targetEntity.locZ) >= 16) {
            return false;
        }
        this.targetEntity = targetEntity;
        return true;
    }

    @Override
    public boolean shouldFinish() {
        if (this.entityDungeonCraft.getGoalTarget() == null || !this.targetEntity.isAlive()) {
            return true;
        } else if (this.targetEntity != this.entityDungeonCraft.getGoalTarget()) {
            return true;
        }
        if (this.entityDungeonCraft.e(targetEntity.locX, targetEntity.boundingBox.b, targetEntity.locZ) >= 16) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        this.entityDungeonCraft.petNavigation.getParameters().addSpeedModifier("MeleeAttack", walkSpeedModifier);
        this.entityDungeonCraft.petNavigation.navigateTo(this.targetEntity);
        this.timeUntilNextNavigationUpdate = 0;
    }

    @Override
    public void finish() {
        this.entityDungeonCraft.petNavigation.getParameters().removeSpeedModifier("MeleeAttack");
        this.targetEntity = null;
        this.entityDungeonCraft.petNavigation.stop();
    }

    @Override
    public void tick() {
        this.entityDungeonCraft.getControllerLook().a(targetEntity, 30.0F, 30.0F);
        if (--this.timeUntilNextNavigationUpdate <= 0) {
            this.timeUntilNextNavigationUpdate = (4 + this.entityDungeonCraft.getRandom().nextInt(7));
            this.entityDungeonCraft.petNavigation.navigateTo(targetEntity);
        }
        if (Math.sqrt(this.entityDungeonCraft.e(targetEntity.locX, targetEntity.boundingBox.b, targetEntity.locZ)) - (targetEntity.length * (2. / 3.)) <= this.range && this.ticksUntilNextHitLeft-- <= 0) {
            this.ticksUntilNextHitLeft = ticksUntilNextHit;
            if (this.entityDungeonCraft instanceof EntityDungeonCraftPigZombie || this.entityDungeonCraft instanceof EntityDungeonCraftZombie || this.entityDungeonCraft instanceof EntityDungeonCraftSkeleton) {
                this.entityDungeonCraft.aR(); // -> swingItem()
            }
            this.entityDungeonCraft.attack(targetEntity);
        }
    }
}