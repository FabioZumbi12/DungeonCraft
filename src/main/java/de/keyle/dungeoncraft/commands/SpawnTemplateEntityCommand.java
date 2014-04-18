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

package de.keyle.dungeoncraft.commands;

import de.keyle.command.framework.Command;
import de.keyle.command.framework.CommandArgs;
import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.DungeonFieldManager;
import de.keyle.dungeoncraft.dungeon.DungeonManager;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.entity.template.EntityFactory;
import de.keyle.dungeoncraft.entity.template.EntityTemplate;
import de.keyle.dungeoncraft.entity.template.EntityTemplateRegistry;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;

public class SpawnTemplateEntityCommand {
    @Command(name = "dcste")
    public void onCommand(CommandArgs args) {
        if (args.getSender() instanceof CraftPlayer && args.getArgs().size() >= 1) {
            CraftPlayer p = (CraftPlayer) args.getSender();

            Location pLocation = p.getLocation();

            if (pLocation.getWorld().getName().equals(DungeonCraftWorld.WORLD_NAME)) {
                Dungeon d = DungeonManager.getDungeonAt(DungeonFieldManager.getDungeonFieldForChunk(pLocation.getChunk().getX(), pLocation.getChunk().getZ()));
                if (d != null) {
                    EntityTemplateRegistry r = d.getDungeonBase().getEntityTemplateRegistry();
                    EntityTemplate et = r.getTemplate(args.getArgs().get(0));
                    if (et != null) {
                        EntityDungeonCraft entity = EntityFactory.createEntityByTemplate(et);
                        net.minecraft.server.v1_7_R3.World mcWorld = ((CraftWorld) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME)).getHandle();
                        entity.setPosition(pLocation.getX(), pLocation.getY(), pLocation.getZ());
                        mcWorld.addEntity(entity);
                    }
                }
            }
        }
    }
}