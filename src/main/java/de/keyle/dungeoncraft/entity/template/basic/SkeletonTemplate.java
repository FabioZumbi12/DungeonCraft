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

package de.keyle.dungeoncraft.entity.template.basic;

import de.keyle.dungeoncraft.entity.ai.attack.ranged.Projectile;
import de.keyle.dungeoncraft.entity.template.BasicTemplate;
import de.keyle.dungeoncraft.entity.template.components.MeeleDamageComponent;
import de.keyle.dungeoncraft.entity.template.components.RangedDamageComponent;
import de.keyle.dungeoncraft.entity.types.EntityType;

public class SkeletonTemplate extends BasicTemplate {
    public SkeletonTemplate() {
        super("skeleton", 20, EntityType.Skeleton);
        addBasicComponent(new MeeleDamageComponent(2));
        addBasicComponent(new RangedDamageComponent(3, Projectile.Projectile.Arrow));
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }
}