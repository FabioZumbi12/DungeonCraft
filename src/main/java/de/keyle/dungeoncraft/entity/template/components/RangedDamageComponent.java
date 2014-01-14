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

package de.keyle.dungeoncraft.entity.template.components;

import de.keyle.dungeoncraft.entity.ai.attack.RangedAttack;
import de.keyle.dungeoncraft.entity.ai.target.HurtByTarget;
import de.keyle.dungeoncraft.entity.template.EntityTemplateComonent;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;

import static de.keyle.dungeoncraft.entity.ai.attack.ranged.MyPetProjectile.Projectile;

public class RangedDamageComponent extends EntityTemplateComonent {
    double damage = 0;
    private final Projectile projectile;

    public RangedDamageComponent(double damage, Projectile projectile) {
        this.damage = damage;
        this.projectile = projectile;
    }

    public double getDamage() {
        return damage;
    }

    public Projectile getProjectile() {
        return projectile;
    }

    @Override
    public void applyComponent(EntityDungeonCraft entity) {
        entity.petPathfinderSelector.addGoal("RangedAttack", new RangedAttack(entity, -0.1F, 12.0F, projectile));
        entity.petTargetSelector.addGoal("HurtByTarget", new HurtByTarget(entity));
    }
}