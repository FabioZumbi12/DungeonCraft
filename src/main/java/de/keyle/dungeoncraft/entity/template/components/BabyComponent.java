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

import de.keyle.dungeoncraft.api.entity.components.EntityTemplateComponent;
import de.keyle.dungeoncraft.api.entity.components.Parameter;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.entity.types.chicken.EntityDungeonCraftChicken;
import de.keyle.dungeoncraft.entity.types.cow.EntityDungeonCraftCow;
import de.keyle.dungeoncraft.entity.types.horse.EntityDungeonCraftHorse;
import de.keyle.dungeoncraft.entity.types.mooshroom.EntityDungeonCraftMooshroom;
import de.keyle.dungeoncraft.entity.types.ocelot.EntityDungeonCraftOcelot;
import de.keyle.dungeoncraft.entity.types.pig.EntityDungeonCraftPig;
import de.keyle.dungeoncraft.entity.types.pigzombie.EntityDungeonCraftPigZombie;
import de.keyle.dungeoncraft.entity.types.sheep.EntityDungeonCraftSheep;
import de.keyle.dungeoncraft.entity.types.villager.EntityDungeonCraftVillager;
import de.keyle.dungeoncraft.entity.types.wolf.EntityDungeonCraftWolf;
import de.keyle.dungeoncraft.entity.types.zombie.EntityDungeonCraftZombie;

public class BabyComponent extends EntityTemplateComponent {
    boolean baby = false;

    public BabyComponent(@Parameter(type = Parameter.Type.Boolean, name = "baby") boolean baby) {
        this.baby = baby;
    }

    public boolean isBaby() {
        return baby;
    }

    @Override
    public void applyComponent(EntityDungeonCraft entity) {
        if (entity instanceof EntityDungeonCraftChicken) {
            ((EntityDungeonCraftChicken) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftCow) {
            ((EntityDungeonCraftCow) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftHorse) {
            ((EntityDungeonCraftHorse) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftMooshroom) {
            ((EntityDungeonCraftMooshroom) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftOcelot) {
            ((EntityDungeonCraftOcelot) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftPig) {
            ((EntityDungeonCraftPig) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftPigZombie) {
            ((EntityDungeonCraftPigZombie) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftSheep) {
            ((EntityDungeonCraftSheep) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftVillager) {
            ((EntityDungeonCraftVillager) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftWolf) {
            ((EntityDungeonCraftWolf) entity).setBaby(baby);
        } else if (entity instanceof EntityDungeonCraftZombie) {
            ((EntityDungeonCraftZombie) entity).setBaby(baby);
        }
    }
}