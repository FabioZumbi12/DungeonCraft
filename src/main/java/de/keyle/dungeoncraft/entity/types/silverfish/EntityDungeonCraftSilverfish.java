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

package de.keyle.dungeoncraft.entity.types.silverfish;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R1.World;

@EntityInfo(width = 0.3F, height = 0.7F)
public class EntityDungeonCraftSilverfish extends EntityDungeonCraft {
    public EntityDungeonCraftSilverfish(World world) {
        super(world);
    }

    @Override
    protected String getDeathSound() {
        return "mob.silverfish.kill";
    }

    @Override
    protected String getHurtSound() {
        return "mob.silverfish.hit";
    }

    protected String getLivingSound() {
        return "mob.silverfish.say";
    }

    @Override
    public void playStepSound() {
        makeSound("mob.silverfish.step", 1.0F, 1.0F);
    }
}