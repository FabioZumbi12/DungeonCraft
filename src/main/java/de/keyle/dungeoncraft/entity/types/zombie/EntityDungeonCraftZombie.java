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

package de.keyle.dungeoncraft.entity.types.zombie;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R2.ItemStack;
import net.minecraft.server.v1_7_R2.PacketPlayOutEntityEquipment;
import net.minecraft.server.v1_7_R2.World;
import net.minecraft.server.v1_7_R2.WorldServer;

@EntityInfo(width = 0.6F, height = 1.9F)
public class EntityDungeonCraftZombie extends EntityDungeonCraft {
    public EntityDungeonCraftZombie(World world) {
        super(world);
    }

    /**
     * Returns the sound that is played when the Entity dies
     */
    @Override
    protected String getDeathSound() {
        return "mob.zombie.death";
    }

    /**
     * Returns the sound that is played when the Entity get hurt
     */
    @Override
    protected String getHurtSound() {
        return "mob.zombie.hurt";
    }

    /**
     * Returns the default sound of the Entity
     */
    protected String getLivingSound() {
        return "mob.zombie.say";
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        getDataWatcher().a(12, new Byte((byte) 0));     // is baby
        getDataWatcher().a(13, new Byte((byte) 0));     // is villager
        getDataWatcher().a(14, Byte.valueOf((byte) 0)); // N/A
    }

    public void setBaby(boolean flag) {
        getDataWatcher().watch(12, (byte) (flag ? 1 : 0));
    }

    public void setVillager(boolean flag) {
        getDataWatcher().watch(13, (byte) (flag ? 1 : 0));
    }

    public void playStepSound() {
        makeSound("mob.zombie.step", 0.15F, 1.0F);
    }

    public void setEntityEquipment(int slot, ItemStack itemStack) {
        ((WorldServer) this.world).getTracker().a(this, new PacketPlayOutEntityEquipment(getId(), slot, itemStack));
    }
}