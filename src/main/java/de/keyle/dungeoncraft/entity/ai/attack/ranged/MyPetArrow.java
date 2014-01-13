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

package de.keyle.dungeoncraft.entity.ai.attack.ranged;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import net.minecraft.server.v1_7_R1.EntityArrow;
import net.minecraft.server.v1_7_R1.EntityLiving;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import net.minecraft.server.v1_7_R1.World;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftArrow;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftEntity;

public class MyPetArrow extends EntityArrow implements MyPetProjectile {
    public MyPetArrow(World world, EntityDungeonCraft entityMyPet, EntityLiving target, float v, int i) {
        super(world, entityMyPet, target, v, i);
    }

    @Override
    public EntityDungeonCraft getShooter() {
        return (EntityDungeonCraft) this.shooter;
    }

    @Override
    public CraftEntity getBukkitEntity() {
        if (this.bukkitEntity == null) {
            this.bukkitEntity = new CraftArrow(this.world.getServer(), this);
        }
        return this.bukkitEntity;
    }

    @Override
    public void a(NBTTagCompound nbtTagCompound) {
    }

    @Override
    public void b(NBTTagCompound nbtTagCompound) {
    }

    public void h() {
        try {
            super.h();
            if (this.isInGround()) {
                die();
            }
        } catch (Exception e) {
            e.printStackTrace();
            DebugLogger.printThrowable(e);
        }
    }
}