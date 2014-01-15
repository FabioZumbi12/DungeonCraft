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

package de.keyle.dungeoncraft.entity.ai.movement;

import de.keyle.dungeoncraft.api.entity.ai.AIGoal;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;

public class RandomLookaround extends AIGoal {
    private EntityDungeonCraft entityDungeonCraft;
    private double directionX;
    private double directionZ;
    private int ticksUntilStopLookingAround = 0;

    public RandomLookaround(EntityDungeonCraft entityDungeonCraft) {
        this.entityDungeonCraft = entityDungeonCraft;
    }

    @Override
    public boolean shouldStart() {
        if (this.entityDungeonCraft.getGoalTarget() != null && this.entityDungeonCraft.getGoalTarget().isAlive()) {
            return false;
        }
        if (this.entityDungeonCraft.passenger != null) {
            return false;
        }
        return this.entityDungeonCraft.getRandom().nextFloat() < 0.02F;
    }

    @Override
    public boolean shouldFinish() {
        return this.ticksUntilStopLookingAround <= 0 || this.entityDungeonCraft.passenger != null;
    }

    @Override
    public void start() {
        double circumference = 6.283185307179586D * this.entityDungeonCraft.getRandom().nextDouble();
        this.directionX = Math.cos(circumference);
        this.directionZ = Math.sin(circumference);
        this.ticksUntilStopLookingAround = (20 + this.entityDungeonCraft.getRandom().nextInt(20));
    }

    @Override
    public void tick() {
        this.ticksUntilStopLookingAround--;
        this.entityDungeonCraft.getControllerLook().a(this.entityDungeonCraft.locX + this.directionX, this.entityDungeonCraft.locY + this.entityDungeonCraft.getHeadHeight(), this.entityDungeonCraft.locZ + this.directionZ, 10.0F, this.entityDungeonCraft.x());
    }
}