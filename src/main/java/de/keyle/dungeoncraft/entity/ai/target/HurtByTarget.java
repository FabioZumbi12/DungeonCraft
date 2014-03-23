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

package de.keyle.dungeoncraft.entity.ai.target;

import de.keyle.dungeoncraft.api.entity.ai.AIGoal;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import net.minecraft.server.v1_7_R2.EntityLiving;

public class HurtByTarget extends AIGoal {
    EntityDungeonCraft entityDungeonCraft;
    EntityLiving target = null;

    public HurtByTarget(EntityDungeonCraft entityDungeonCraft) {
        this.entityDungeonCraft = entityDungeonCraft;
    }

    @Override
    public boolean shouldStart() {
        if (entityDungeonCraft.getLastDamager() == null) {
            return false;
        }
        if (target != entityDungeonCraft.getLastDamager()) {
            target = entityDungeonCraft.getLastDamager();
        }
        if (target == entityDungeonCraft) {
            return false;
        }
        return true;
    }

    @Override
    public boolean shouldFinish() {
        EntityLiving entityliving = entityDungeonCraft.getGoalTarget();

        if (!entityDungeonCraft.canMove()) {
            return true;
        } else if (entityliving == null) {
            return true;
        } else if (!entityliving.isAlive()) {
            return true;
        } else if (entityDungeonCraft.getGoalTarget().world != entityDungeonCraft.world) {
            return true;
        } else if (entityDungeonCraft.e(entityDungeonCraft.getGoalTarget()) > 400) {
            return true;
        }
        return false;
    }

    @Override
    public void start() {
        entityDungeonCraft.setGoalTarget(this.target);
    }

    @Override
    public void finish() {
        entityDungeonCraft.setGoalTarget(null);
    }
}