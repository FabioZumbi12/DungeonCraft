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
    public void onAttached() {
        if (getOwner() instanceof EntityDungeonCraftChicken) {
            ((EntityDungeonCraftChicken) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftCow) {
            ((EntityDungeonCraftCow) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftHorse) {
            ((EntityDungeonCraftHorse) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftMooshroom) {
            ((EntityDungeonCraftMooshroom) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftOcelot) {
            ((EntityDungeonCraftOcelot) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftPig) {
            ((EntityDungeonCraftPig) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftPigZombie) {
            ((EntityDungeonCraftPigZombie) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftSheep) {
            ((EntityDungeonCraftSheep) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftVillager) {
            ((EntityDungeonCraftVillager) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftWolf) {
            ((EntityDungeonCraftWolf) getOwner()).setBaby(baby);
        } else if (getOwner() instanceof EntityDungeonCraftZombie) {
            ((EntityDungeonCraftZombie) getOwner()).setBaby(baby);
        }
    }

    @Override
    public EntityTemplateComponent clone() {
        return new BabyComponent(this.baby);
    }
}