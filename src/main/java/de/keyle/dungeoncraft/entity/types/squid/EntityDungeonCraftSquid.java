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

package de.keyle.dungeoncraft.entity.types.squid;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.EntityInfo;
import de.keyle.dungeoncraft.util.BukkitUtil;
import net.minecraft.server.v1_7_R3.World;

@EntityInfo(width = 0.95F, height = 0.95F)
public class EntityDungeonCraftSquid extends EntityDungeonCraft {
    public EntityDungeonCraftSquid(World world) {
        super(world);
    }

    @Override
    protected String getDeathSound() {
        return null;
    }

    @Override
    protected String getHurtSound() {
        return null;
    }

    protected String getLivingSound() {
        return null;
    }

    @Override
    public void onLivingUpdate() {
        super.onLivingUpdate();
        BukkitUtil.playParticleEffect(getBukkitEntity().getLocation().add(0, 0.7, 0), "splash", 0.2F, 0.2F, 0.2F, 0.5F, 10, 20);
    }
}