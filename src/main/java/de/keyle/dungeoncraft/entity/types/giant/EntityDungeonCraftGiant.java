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

package de.keyle.dungeoncraft.entity.types.giant;

import de.keyle.dungeoncraft.entity.ai.attack.MeleeAttack;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R1.World;

@EntityInfo(width = 5.5f, height = 5.5F)
public class EntityDungeonCraftGiant extends EntityDungeonCraft {
    public EntityDungeonCraftGiant(World world) {
        super(world);
    }

    @Override
    protected String getDeathSound() {
        return "mob.zombie.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.zombie.hurt";
    }

    protected String getLivingSound() {
        return "mob.zombie.say";
    }

    public void playStepSound() {
        makeSound("mob.zombie.step", 0.15F, 1.0F);
    }

    public void setPathfinder() {
        super.setPathfinder();
        petPathfinderSelector.replaceGoal("MeleeAttack", new MeleeAttack(this, 0.1F, 8, 20));
    }
}