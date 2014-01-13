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

import de.keyle.dungeoncraft.entity.ai.AIGoal;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.ocelot.EntityDungeonCraftOcelot;
import de.keyle.dungeoncraft.entity.types.wolf.EntityDungeonCraftWolf;

public class Sit extends AIGoal {
    private EntityDungeonCraft entityDungeonCraft;
    private boolean sitting = false;

    public Sit(EntityDungeonCraft entityDungeonCraft) {
        this.entityDungeonCraft = entityDungeonCraft;
    }

    @Override
    public boolean shouldStart() {
        if (!(this.entityDungeonCraft instanceof EntityDungeonCraftOcelot) && !(this.entityDungeonCraft instanceof EntityDungeonCraftWolf)) {
            return false;
        } else if (this.entityDungeonCraft.M()) { // -> isInWater()
            return false;
        } else if (!this.entityDungeonCraft.onGround) {
            return false;
        }
        return this.sitting;
    }

    @Override
    public void start() {
        this.entityDungeonCraft.petNavigation.stop();
        if (this.entityDungeonCraft instanceof EntityDungeonCraftOcelot) {
            ((EntityDungeonCraftOcelot) this.entityDungeonCraft).applySitting(true);
        }
        if (this.entityDungeonCraft instanceof EntityDungeonCraftWolf) {
            ((EntityDungeonCraftWolf) this.entityDungeonCraft).applySitting(true);
        }
        entityDungeonCraft.setGoalTarget(null);
    }

    @Override
    public void finish() {
        if (this.entityDungeonCraft instanceof EntityDungeonCraftOcelot) {
            ((EntityDungeonCraftOcelot) this.entityDungeonCraft).applySitting(false);
        }
        if (this.entityDungeonCraft instanceof EntityDungeonCraftWolf) {
            ((EntityDungeonCraftWolf) this.entityDungeonCraft).applySitting(false);
        }
    }

    public void setSitting(boolean sitting) {
        this.sitting = sitting;
    }

    public boolean isSitting() {
        return this.sitting;
    }

    public void toogleSitting() {
        this.sitting = !this.sitting;
    }
}