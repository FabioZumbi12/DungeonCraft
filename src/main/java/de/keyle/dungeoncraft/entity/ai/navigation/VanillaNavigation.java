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

package de.keyle.dungeoncraft.entity.ai.navigation;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import net.minecraft.server.v1_7_R2.EntityLiving;
import net.minecraft.server.v1_7_R2.GenericAttributes;
import net.minecraft.server.v1_7_R2.Navigation;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftLivingEntity;
import org.bukkit.entity.LivingEntity;

public class VanillaNavigation extends AbstractNavigation {
    Navigation nav;

    public VanillaNavigation(EntityDungeonCraft entityDungeonCraft) {
        super(entityDungeonCraft);
        nav = entityDungeonCraft.getNavigation();
    }

    public VanillaNavigation(EntityDungeonCraft entityDungeonCraft, NavigationParameters parameters) {
        super(entityDungeonCraft, parameters);
        nav = entityDungeonCraft.getNavigation();
    }

    @Override
    public void stop() {
        nav.h();
    }

    @Override
    public boolean navigateTo(double x, double y, double z) {
        applyNavigationParameters();
        if (this.nav.a(x, y, z, 1.D)) {
            applyNavigationParameters();
            return true;
        }
        return false;
    }

    @Override
    public boolean navigateTo(LivingEntity entity) {
        return navigateTo(((CraftLivingEntity) entity).getHandle());
    }

    @Override
    public boolean navigateTo(EntityLiving entity) {
        if (this.nav.a(entity, 1.D)) {
            applyNavigationParameters();
            return true;
        }
        return false;
    }

    @Override
    public void tick() {
        nav.f();
    }

    public void applyNavigationParameters() {
        this.nav.a(parameters.avoidWater());
        this.entityDungeonCraft.getAttributeInstance(GenericAttributes.d).setValue(parameters.speed() + parameters.speedModifier());
    }
}