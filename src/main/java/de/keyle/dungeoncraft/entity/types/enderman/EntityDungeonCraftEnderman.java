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

package de.keyle.dungeoncraft.entity.types.enderman;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import net.minecraft.server.v1_7_R3.World;


@EntityInfo(width = 0.6F, height = 2.9F)
public class EntityDungeonCraftEnderman extends EntityDungeonCraft {
    public EntityDungeonCraftEnderman(World world) {
        super(world);
    }

    @Override
    protected String getDeathSound() {
        return "mob.endermen.death";
    }

    @Override
    protected String getHurtSound() {
        return "mob.endermen.hit";
    }

    @Override
    protected String getLivingSound() {
        return isScreaming() ? "mob.endermen.scream" : "mob.endermen.idle";
    }

    protected void initDatawatcher() {
        super.initDatawatcher();
        this.datawatcher.a(16, new Byte((byte) 0));  // blockID
        this.datawatcher.a(17, new Byte((byte) 0));  // blockData
        this.datawatcher.a(18, new Byte((byte) 0));  // face(angry)
    }

    public boolean isScreaming() {
        return false;
    }

    public void setScreaming(boolean screaming) {
        this.datawatcher.watch(18, (byte) (screaming ? 1 : 0));
    }

    public void setBlock(int blockID, int blockData) {
        this.datawatcher.watch(16, (byte) (blockID & 0xFF));

        this.datawatcher.watch(17, (byte) (blockData & 0xFF));
    }
}