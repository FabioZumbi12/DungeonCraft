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
import net.minecraft.server.v1_7_R1.Entity;

public class LookAtPlayer extends AIGoal {
    private EntityDungeonCraft entityDungeonCraft;
    protected Entity targetPlayer;
    private float range;
    private int ticksUntilStopLooking;
    private float lookAtPlayerChance;

    public LookAtPlayer(EntityDungeonCraft entityDungeonCraft, float range) {
        this.entityDungeonCraft = entityDungeonCraft;
        this.range = range;
        this.lookAtPlayerChance = 0.02F;
    }

    public LookAtPlayer(EntityDungeonCraft entityDungeonCraft, float range, float lookAtPlayerChance) {
        this.entityDungeonCraft = entityDungeonCraft;
        this.range = range;
        this.lookAtPlayerChance = lookAtPlayerChance;
    }

    @Override
    public boolean shouldStart() {
        if (this.entityDungeonCraft.getRandom().nextFloat() >= this.lookAtPlayerChance) {
            return false;
        }
        if (this.entityDungeonCraft.getGoalTarget() != null && this.entityDungeonCraft.getGoalTarget().isAlive()) {
            return false;
        }
        if (this.entityDungeonCraft.passenger != null) {
            return false;
        }
        this.targetPlayer = this.entityDungeonCraft.world.findNearbyPlayer(this.entityDungeonCraft, this.range);
        return this.targetPlayer != null;
    }

    @Override
    public boolean shouldFinish() {
        if (!this.targetPlayer.isAlive()) {
            return true;
        }
        if (this.entityDungeonCraft.e(this.targetPlayer) > this.range * this.range) {
            return true;
        }
        if (this.entityDungeonCraft.passenger != null) {
            return true;
        }
        return this.ticksUntilStopLooking <= 0;
    }

    @Override
    public void start() {
        this.ticksUntilStopLooking = (40 + this.entityDungeonCraft.getRandom().nextInt(40));
    }

    @Override
    public void finish() {
        this.targetPlayer = null;
    }

    @Override
    public void tick() {
        this.entityDungeonCraft.getControllerLook().a(this.targetPlayer.locX, this.targetPlayer.locY + this.targetPlayer.getHeadHeight(), this.targetPlayer.locZ, 10.0F, this.entityDungeonCraft.x());
        this.ticksUntilStopLooking -= 1;
    }
}