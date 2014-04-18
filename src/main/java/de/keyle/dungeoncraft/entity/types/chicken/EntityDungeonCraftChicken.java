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

package de.keyle.dungeoncraft.entity.types.chicken;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R3.Items;
import net.minecraft.server.v1_7_R3.World;

@EntityInfo(width = 0.3F, height = 0.7F)
public class EntityDungeonCraftChicken extends EntityDungeonCraft {
    private int nextEggTimer;

    public EntityDungeonCraftChicken(World world) {
        super(world);
        nextEggTimer = (random.nextInt(6000) + 6000);
    }

    @Override
    protected String getDeathSound() {
        return "mob.chicken.hurt";
    }

    @Override
    protected String getHurtSound() {
        return "mob.chicken.hurt";
    }

    protected String getLivingSound() {
        return "mob.chicken.say";
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(12, new Integer(0)); // age
    }

    public void setBaby(boolean flag) {
        if (flag) {
            this.datawatcher.watch(12, Integer.valueOf(Integer.MIN_VALUE));
        } else {
            this.datawatcher.watch(12, new Integer(0));
        }
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (!this.onGround && this.motY < 0.0D) {
            this.motY *= 0.6D;
        }

        if (--nextEggTimer <= 0) {
            world.makeSound(this, "mob.chicken.plop", 1.0F, (random.nextFloat() - random.nextFloat()) * 0.2F + 1.0F);
            a(Items.EGG, 1);
            nextEggTimer = random.nextInt(6000) + 6000;
        }
    }

    public void playStepSound() {
        makeSound("mob.chicken.step", 0.15F, 1.0F);
    }

    protected void b(float f) {
    }
}