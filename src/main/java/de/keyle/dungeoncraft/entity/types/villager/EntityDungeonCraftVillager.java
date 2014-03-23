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

package de.keyle.dungeoncraft.entity.types.villager;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R2.World;

@EntityInfo(width = 0.6F, height = 1.9F)
public class EntityDungeonCraftVillager extends EntityDungeonCraft {
    public EntityDungeonCraftVillager(World world) {
        super(world);
    }

    protected String getDeathSound() {
        return "mob.villager.defaultdeath";
    }

    protected String getHurtSound() {
        return "mob.villager.defaulthurt";
    }

    protected String getLivingSound() {
        return "mob.villager.default";
    }

    public void setProfession(int value) {
        this.datawatcher.watch(16, value);
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(12, new Integer(0)); // age
        this.datawatcher.a(16, new Integer(0)); // profession
    }

    public void setBaby(boolean flag) {
        if (flag) {
            this.datawatcher.watch(12, Integer.valueOf(Integer.MIN_VALUE));
        } else {
            this.datawatcher.watch(12, new Integer(0));
        }
    }
}