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

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.api.entity.components.EntityTemplateComponent;
import de.keyle.dungeoncraft.api.entity.components.Parameter;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.pigzombie.EntityDungeonCraftPigZombie;
import de.keyle.dungeoncraft.entity.types.skeleton.EntityDungeonCraftSkeleton;
import de.keyle.dungeoncraft.entity.types.zombie.EntityDungeonCraftZombie;
import de.keyle.dungeoncraft.entity.util.EquipmentSlot;
import de.keyle.dungeoncraft.util.ParsedItem;
import org.bukkit.Bukkit;
import org.json.simple.JSONObject;

public class EquipmentArmorComponent extends EntityTemplateComponent {
    ParsedItem helmet;
    ParsedItem chestplate;
    ParsedItem leggins;
    ParsedItem boots;

    public EquipmentArmorComponent(@Parameter(type = Parameter.Type.JsonObject, name = "helmet") JSONObject helmet,
                                   @Parameter(type = Parameter.Type.JsonObject, name = "chestplate") JSONObject chestplate,
                                   @Parameter(type = Parameter.Type.JsonObject, name = "leggins") JSONObject leggins,
                                   @Parameter(type = Parameter.Type.JsonObject, name = "boots") JSONObject boots) {
        this.helmet = ParsedItem.parsedItem(helmet);
        if (this.helmet.isEmpty()) {
            this.helmet = null;
        }
        this.chestplate = ParsedItem.parsedItem(chestplate);
        if (this.chestplate.isEmpty()) {
            this.chestplate = null;
        }
        this.leggins = ParsedItem.parsedItem(leggins);
        if (this.leggins.isEmpty()) {
            this.leggins = null;
        }
        this.boots = ParsedItem.parsedItem(boots);
        if (this.boots.isEmpty()) {
            this.boots = null;
        }
    }

    public boolean hasHelmet() {
        return helmet != null;
    }

    public boolean hasChestplate() {
        return chestplate != null;
    }

    public boolean hasLeggins() {
        return leggins != null;
    }

    public boolean hasBoots() {
        return boots != null;
    }

    @Override
    public void applyComponent(EntityDungeonCraft entity) {
        if (hasHelmet()) {
            if (entity instanceof EntityDungeonCraftZombie) {
                final EntityDungeonCraftZombie zombie = (EntityDungeonCraftZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        zombie.setEntityEquipment(EquipmentSlot.Helmet.getSlotId(), helmet.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftPigZombie) {
                final EntityDungeonCraftPigZombie pigZombie = (EntityDungeonCraftPigZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        pigZombie.setEntityEquipment(EquipmentSlot.Helmet.getSlotId(), helmet.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftSkeleton) {
                final EntityDungeonCraftSkeleton skeleton = (EntityDungeonCraftSkeleton) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        skeleton.setEntityEquipment(EquipmentSlot.Helmet.getSlotId(), helmet.getMinecraftItem());
                    }
                }, 5L);
            }
        }
        if (hasChestplate()) {
            if (entity instanceof EntityDungeonCraftZombie) {
                final EntityDungeonCraftZombie zombie = (EntityDungeonCraftZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        zombie.setEntityEquipment(EquipmentSlot.Chestplate.getSlotId(), chestplate.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftPigZombie) {
                final EntityDungeonCraftPigZombie pigZombie = (EntityDungeonCraftPigZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        pigZombie.setEntityEquipment(EquipmentSlot.Chestplate.getSlotId(), chestplate.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftSkeleton) {
                final EntityDungeonCraftSkeleton skeleton = (EntityDungeonCraftSkeleton) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        skeleton.setEntityEquipment(EquipmentSlot.Chestplate.getSlotId(), chestplate.getMinecraftItem());
                    }
                }, 5L);
            }
        }
        if (hasLeggins()) {
            if (entity instanceof EntityDungeonCraftZombie) {
                final EntityDungeonCraftZombie zombie = (EntityDungeonCraftZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        zombie.setEntityEquipment(EquipmentSlot.Leggins.getSlotId(), leggins.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftPigZombie) {
                final EntityDungeonCraftPigZombie pigZombie = (EntityDungeonCraftPigZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        pigZombie.setEntityEquipment(EquipmentSlot.Leggins.getSlotId(), leggins.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftSkeleton) {
                final EntityDungeonCraftSkeleton skeleton = (EntityDungeonCraftSkeleton) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        skeleton.setEntityEquipment(EquipmentSlot.Leggins.getSlotId(), leggins.getMinecraftItem());
                    }
                }, 5L);
            }
        }
        if (hasBoots()) {
            if (entity instanceof EntityDungeonCraftZombie) {
                final EntityDungeonCraftZombie zombie = (EntityDungeonCraftZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        zombie.setEntityEquipment(EquipmentSlot.Boots.getSlotId(), boots.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftPigZombie) {
                final EntityDungeonCraftPigZombie pigZombie = (EntityDungeonCraftPigZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        pigZombie.setEntityEquipment(EquipmentSlot.Boots.getSlotId(), boots.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftSkeleton) {
                final EntityDungeonCraftSkeleton skeleton = (EntityDungeonCraftSkeleton) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        skeleton.setEntityEquipment(EquipmentSlot.Boots.getSlotId(), boots.getMinecraftItem());
                    }
                }, 5L);
            }
        }
    }
}