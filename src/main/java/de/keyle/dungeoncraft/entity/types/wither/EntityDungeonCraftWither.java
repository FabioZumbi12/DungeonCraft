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

package de.keyle.dungeoncraft.entity.types.wither;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R2.World;

@EntityInfo(width = 0.9F, height = 4.0F)
public class EntityDungeonCraftWither extends EntityDungeonCraft {
    public EntityDungeonCraftWither(World world) {
        super(world);
    }

    @Override
    protected String getDeathSound() {
        return "mob.wither.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.wither.hurt";
    }

    protected String getLivingSound() {
        return "mob.wither.idle";
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(17, new Integer(0));  // target entityID
        this.datawatcher.a(18, new Integer(0));  // N/A
        this.datawatcher.a(19, new Integer(0));  // N/A
        this.datawatcher.a(20, new Integer(0));  // blue (1/0)
    }
}