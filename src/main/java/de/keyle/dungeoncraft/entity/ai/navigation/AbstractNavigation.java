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
import net.minecraft.server.v1_7_R3.EntityLiving;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;

public abstract class AbstractNavigation {
    protected EntityDungeonCraft entityDungeonCraft;
    NavigationParameters parameters;

    public abstract void stop();

    public abstract boolean navigateTo(double x, double y, double z);

    public abstract void applyNavigationParameters();

    public AbstractNavigation(EntityDungeonCraft entityDungeonCraft) {
        this.entityDungeonCraft = entityDungeonCraft;
        parameters = new NavigationParameters(entityDungeonCraft.getWalkSpeed());
    }

    public AbstractNavigation(EntityDungeonCraft entityDungeonCraft, NavigationParameters parameters) {
        this.entityDungeonCraft = entityDungeonCraft;
        this.parameters = parameters;
    }

    public boolean navigateTo(Location loc) {
        return navigateTo(loc.getX(), loc.getY(), loc.getZ());
    }

    public boolean navigateTo(LivingEntity entity) {
        return navigateTo(entity.getLocation());
    }

    public boolean navigateTo(EntityLiving entity) {
        return navigateTo((LivingEntity) entity.getBukkitEntity());
    }

    public NavigationParameters getParameters() {
        return parameters;
    }

    public abstract void tick();
}