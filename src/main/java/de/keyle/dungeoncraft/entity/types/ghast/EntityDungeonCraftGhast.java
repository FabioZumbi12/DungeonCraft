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

package de.keyle.dungeoncraft.entity.types.ghast;

import de.keyle.dungeoncraft.entity.ai.attack.MeleeAttack;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R2.World;

@EntityInfo(width = 4.F, height = 4.F)
public class EntityDungeonCraftGhast extends EntityDungeonCraft {
    public EntityDungeonCraftGhast(World world) {
        super(world);
        this.height = 3.5F;
    }

    @Override
    protected String getDeathSound() {
        return "mob.ghast.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.ghast.scream";
    }

    protected String getLivingSound() {
        return "mob.ghast.moan";
    }

    public void setPathfinder() {
        super.setPathfinder();
        petPathfinderSelector.replaceGoal("MeleeAttack", new MeleeAttack(this, 0.1F, 5.5, 20));
    }
}