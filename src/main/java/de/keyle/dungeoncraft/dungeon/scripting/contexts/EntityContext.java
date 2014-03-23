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

package de.keyle.dungeoncraft.dungeon.scripting.contexts;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.entity.template.EntityFactory;
import de.keyle.dungeoncraft.entity.template.EntityTemplate;
import de.keyle.dungeoncraft.entity.template.EntityTemplateRegistry;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R2.CraftWorld;
import org.bukkit.entity.LivingEntity;

@SuppressWarnings("unused")
public class EntityContext {
    protected final Dungeon dungeon;

    public EntityContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void createEntity(String templateId, OrientationVector vector) {
        createEntity(templateId, vector.getX(), vector.getY(), vector.getZ());
    }

    public void createEntity(String templateId, double posX, double posY, double posZ) {
        EntityTemplateRegistry r = dungeon.getDungeonBase().getEntityTemplateRegistry();
        EntityTemplate et = r.getTemplate(templateId);
        if (et != null) {
            EntityDungeonCraft entity = EntityFactory.createEntityByTemplate(et);
            net.minecraft.server.v1_7_R2.World mcWorld = ((CraftWorld) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME)).getHandle();
            entity.setPosition(dungeon.getPosition().getBlockX() + posX, posY, dungeon.getPosition().getBlockZ() + posZ);
            mcWorld.addEntity(entity);
        }
    }

    public void killEntity(LivingEntity entity) {
        entity.setHealth(0);
    }

    public void removeEntity(LivingEntity entity) {
        entity.remove();
    }
}