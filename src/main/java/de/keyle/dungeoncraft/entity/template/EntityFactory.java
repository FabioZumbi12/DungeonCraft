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

package de.keyle.dungeoncraft.entity.template;

import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;

public class EntityFactory {
    public static EntityDungeonCraft createEntityByTemplate(EntityTemplate template) {
        net.minecraft.server.v1_7_R1.World mcWorld = ((CraftWorld) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME)).getHandle();
        EntityDungeonCraft entity = template.getType().getNewEntityInstance(mcWorld);

        entity.getBukkitEntity().setMaxHealth(template.getMaxHealth());

        for (EntityTemplateComonent comonent : template.getComponents()) {
            comonent.applyComponent(entity);
        }

        return entity;
    }
}