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
import de.keyle.dungeoncraft.entity.types.enderman.EntityDungeonCraftEnderman;
import de.keyle.dungeoncraft.entity.types.pigzombie.EntityDungeonCraftPigZombie;
import de.keyle.dungeoncraft.entity.types.skeleton.EntityDungeonCraftSkeleton;
import de.keyle.dungeoncraft.entity.types.zombie.EntityDungeonCraftZombie;
import de.keyle.dungeoncraft.entity.util.EquipmentSlot;
import de.keyle.dungeoncraft.util.ParsedItem;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class EquipmentWeaponComponent extends EntityTemplateComponent {
    ParsedItem weapon;

    public EquipmentWeaponComponent(@Parameter(type = Parameter.Type.JsonObject, name = "weapon") JSONObject weapon) {
        this.weapon = ParsedItem.parsedItem(weapon);
        if (this.weapon.isEmpty()) {
            this.weapon = null;
        }
    }

    public EquipmentWeaponComponent(ItemStack itemStack) {
        this.weapon = new ParsedItem(itemStack);
        if (this.weapon.isEmpty()) {
            this.weapon = null;
        }
    }

    public boolean hasWeapon() {
        return weapon != null;
    }

    @Override
    public void applyComponent(EntityDungeonCraft entity) {
        if (hasWeapon()) {
            if (entity instanceof EntityDungeonCraftZombie) {
                final EntityDungeonCraftZombie zombie = (EntityDungeonCraftZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        zombie.setEntityEquipment(EquipmentSlot.Weapon.getSlotId(), weapon.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftPigZombie) {
                final EntityDungeonCraftPigZombie pigZombie = (EntityDungeonCraftPigZombie) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        pigZombie.setEntityEquipment(EquipmentSlot.Weapon.getSlotId(), weapon.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftSkeleton) {
                final EntityDungeonCraftSkeleton skeleton = (EntityDungeonCraftSkeleton) entity;
                Bukkit.getScheduler().runTaskLater(DungeonCraftPlugin.getPlugin(), new Runnable() {
                    public void run() {
                        skeleton.setEntityEquipment(EquipmentSlot.Weapon.getSlotId(), weapon.getMinecraftItem());
                    }
                }, 5L);
            } else if (entity instanceof EntityDungeonCraftEnderman) {
                EntityDungeonCraftEnderman enderman = (EntityDungeonCraftEnderman) entity;
                enderman.setBlock(weapon.getBukkitItem().getTypeId(), weapon.getBukkitItem().getData().getData());
            }
        }
    }
}