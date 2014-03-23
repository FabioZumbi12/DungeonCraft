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

package de.keyle.dungeoncraft.entity.types.bat;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R2.World;


@EntityInfo(width = 0.5F, height = 0.9F)
public class EntityDungeonCraftBat extends EntityDungeonCraft {
    public EntityDungeonCraftBat(World world) {
        super(world);
        this.height = 1F;
    }

    /**
     * Returns the sound that is played when the MyEntityPet dies
     */
    @Override
    protected String getDeathSound() {
        return "mob.bat.death";
    }

    /**
     * Returns the sound that is played when the Entity get hurt
     */
    @Override
    protected String getHurtSound() {
        return "mob.bat.hurt";
    }

    @Override
    protected String getLivingSound() {
        return "mob.bat.idle";
    }

    public float getSoundSpeed() {
        return super.getSoundSpeed() * 0.95F;
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(16, new Byte((byte) 0)); // hanging
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }
    }

    protected void b(float f) {
    }
}