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

package de.keyle.dungeoncraft.entity.types.slime;

import de.keyle.dungeoncraft.entity.ai.attack.MeleeAttack;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R1.PathEntity;
import net.minecraft.server.v1_7_R1.World;

@EntityInfo(width = 0.6F, height = 0.6F)
public class EntityDungeonCraftSlime extends EntityDungeonCraft {
    int jumpDelay;
    int size = 1;
    PathEntity lastPathEntity = null;

    public EntityDungeonCraftSlime(World world) {
        super(world);
        this.jumpDelay = (this.random.nextInt(20) + 10);
    }

    @Override
    protected String getDeathSound() {
        return "mob.slime." + (size > 1 ? "big" : "small");

    }

    @Override
    protected String getHurtSound() {
        return getDeathSound();
    }

    protected String getLivingSound() {
        return null;
    }

    public void setSize(int value) {
        value = Math.max(1, value);
        this.datawatcher.watch(16, new Byte((byte) value));
        EntityInfo es = EntityDungeonCraftSlime.class.getAnnotation(EntityInfo.class);
        if (es != null) {
            this.a(es.height() * value, es.width() * value);
        }
        if (petPathfinderSelector != null && petPathfinderSelector.hasGoal("MeleeAttack")) {
            petPathfinderSelector.replaceGoal("MeleeAttack", new MeleeAttack(this, 0.1F, 3 + (size * 0.6), 20));
        }
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(16, new Byte((byte) 1)); //size
    }

    public void onLivingUpdate() {
        super.onLivingUpdate();

        if (this.onGround && jumpDelay-- <= 0 && lastPathEntity != getNavigation().e()) {
            getControllerJump().a();
            jumpDelay = (this.random.nextInt(20) + 10);
            lastPathEntity = getNavigation().e();
            makeSound(getDeathSound(), bf(), ((this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F) / 0.8F);
        }
    }

    public void setPathfinder() {
        super.setPathfinder();
        petPathfinderSelector.replaceGoal("MeleeAttack", new MeleeAttack(this, 0.1F, 2 + size, 20));
    }
}