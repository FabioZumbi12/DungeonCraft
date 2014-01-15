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

package de.keyle.dungeoncraft.entity.ai.movement;

import de.keyle.dungeoncraft.api.entity.ai.AIGoal;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;

public class Float extends AIGoal {
    private EntityDungeonCraft entityDungeonCraft;

    private int lavaCounter = 10;
    private boolean inLava = false;

    public Float(EntityDungeonCraft entityDungeonCraft) {
        this.entityDungeonCraft = entityDungeonCraft;
        entityDungeonCraft.getNavigation().e(true);
    }

    @Override
    public boolean shouldStart() {
        return entityDungeonCraft.world.containsLiquid(entityDungeonCraft.boundingBox);
    }

    @Override
    public void finish() {
        inLava = false;
    }

    @Override
    public void tick() {
        entityDungeonCraft.motY += 0.05D;

        if (!inLava && entityDungeonCraft.world.e(entityDungeonCraft.boundingBox)) // e -> is in Fire/Lava
        {
            inLava = true;
        }
    }
}