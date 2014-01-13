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

package de.keyle.dungeoncraft.entity.types.wolf;

import de.keyle.dungeoncraft.entity.ai.movement.Sit;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R1.MathHelper;
import net.minecraft.server.v1_7_R1.World;

@EntityInfo(width = 0.6F, height = 0.8F)
public class EntityDungeonCraftWolf extends EntityDungeonCraft {
    protected boolean shaking;
    protected boolean isWet;
    protected float shakeCounter;
    private Sit sitPathfinder;

    public EntityDungeonCraftWolf(World world) {
        super(world);
    }

    public void applySitting(boolean sitting) {
        int i = this.datawatcher.getByte(16);
        if (sitting) {
            this.datawatcher.watch(16, (byte) (i | 0x1));
        } else {
            this.datawatcher.watch(16, (byte) (i & 0xFFFFFFFE));
        }
    }

    public boolean canMove() {
        return !sitPathfinder.isSitting();
    }

    public void setCollarColor(byte color) {
        this.datawatcher.watch(20, color);
    }

    @Override
    protected String getDeathSound() {
        return "mob.wolf.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.wolf.hurt";
    }

    protected String getLivingSound() {
        return this.random.nextInt(5) == 0 ? (getHealth() * 100 / getMaxHealth() <= 25 ? "mob.wolf.whine" : "mob.wolf.panting") : "mob.wolf.bark";
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(12, new Integer(0));         // age
        this.datawatcher.a(16, new Byte((byte) 0));     // tamed/angry/sitting
        this.datawatcher.a(17, "");                     // wolf owner name
        this.datawatcher.a(18, new Float(getHealth())); // tail height
        this.datawatcher.a(19, new Byte((byte) 0));     // N/A
        this.datawatcher.a(20, new Byte((byte) 14));    // collar color
    }

    public void setAngry(boolean flag) {
        byte b0 = this.datawatcher.getByte(16);
        if (flag) {
            this.datawatcher.watch(16, (byte) (b0 | 0x2));
        } else {
            this.datawatcher.watch(16, (byte) (b0 & 0xFFFFFFFD));
        }
    }

    public void setBaby(boolean flag) {
        if (flag) {
            this.datawatcher.watch(12, Integer.valueOf(Integer.MIN_VALUE));
        } else {
            this.datawatcher.watch(12, new Integer(0));
        }
    }

    public void setTamed(boolean flag) {
        int i = this.datawatcher.getByte(16);
        if (flag) {
            this.datawatcher.watch(16, (byte) (i | 0x4));
        } else {
            this.datawatcher.watch(16, (byte) (i & 0xFFFFFFFB));
        }
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        if (this.isWet && !this.shaking && !bQ() && this.onGround) // bQ() -> has pathentity
        {
            this.shaking = true;
            this.shakeCounter = 0.0F;
            this.world.broadcastEntityEffect(this, (byte) 8);
        }

        if (L()) // -> is in water
        {
            this.isWet = true;
            this.shaking = false;
            this.shakeCounter = 0.0F;
        } else if ((this.isWet || this.shaking) && this.shaking) {
            if (this.shakeCounter == 0.0F) {
                makeSound("mob.wolf.shake", bf(), (this.random.nextFloat() - this.random.nextFloat()) * 0.2F + 1.0F);
            }

            this.shakeCounter += 0.05F;
            if (this.shakeCounter - 0.05F >= 2.0F) {
                this.isWet = false;
                this.shaking = false;
                this.shakeCounter = 0.0F;
            }

            if (this.shakeCounter > 0.4F) {
                float locY = (float) this.boundingBox.b;
                int i = (int) (MathHelper.sin((this.shakeCounter - 0.4F) * 3.141593F) * 7.0F);

                for (int j = 0; j < i; j++) {
                    float offsetX = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;
                    float offsetZ = (this.random.nextFloat() * 2.0F - 1.0F) * this.width * 0.5F;

                    this.world.addParticle("splash", this.locX + offsetX, locY + 0.8F, this.locZ + offsetZ, this.motX, this.motY, this.motZ);
                }
            }
        }

        float tailHeight = 25.F * getHealth() / getMaxHealth();
        if (this.datawatcher.getFloat(18) != tailHeight) {
            this.datawatcher.watch(18, tailHeight); // update tail height
        }
    }

    @Override
    public void playStepSound() {
        makeSound("mob.wolf.step", 0.15F, 1.0F);
    }

    public void setHealth(float i) {
        super.setHealth(i);
        this.datawatcher.watch(18, Float.valueOf(i));
    }

    public void setPathfinder() {
        super.setPathfinder();
        petPathfinderSelector.addGoal("Sit", 2, sitPathfinder);
    }
}