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

package de.keyle.dungeoncraft.entity.types;

import de.keyle.dungeoncraft.entity.types.bat.EntityDungeonCraftBat;
import de.keyle.dungeoncraft.entity.types.blaze.EntityDungeonCraftBlaze;
import de.keyle.dungeoncraft.entity.types.cavespider.EntityDungeonCraftCaveSpider;
import de.keyle.dungeoncraft.entity.types.chicken.EntityDungeonCraftChicken;
import de.keyle.dungeoncraft.entity.types.cow.EntityDungeonCraftCow;
import de.keyle.dungeoncraft.entity.types.creeper.EntityDungeonCraftCreeper;
import de.keyle.dungeoncraft.entity.types.enderman.EntityDungeonCraftEnderman;
import de.keyle.dungeoncraft.entity.types.ghast.EntityDungeonCraftGhast;
import de.keyle.dungeoncraft.entity.types.giant.EntityDungeonCraftGiant;
import de.keyle.dungeoncraft.entity.types.horse.EntityDungeonCraftHorse;
import de.keyle.dungeoncraft.entity.types.irongolem.EntityDungeonCraftIronGolem;
import de.keyle.dungeoncraft.entity.types.magmacube.EntityDungeonCraftMagmaCube;
import de.keyle.dungeoncraft.entity.types.mooshroom.EntityDungeonCraftMooshroom;
import de.keyle.dungeoncraft.entity.types.ocelot.EntityDungeonCraftOcelot;
import de.keyle.dungeoncraft.entity.types.pig.EntityDungeonCraftPig;
import de.keyle.dungeoncraft.entity.types.pigzombie.EntityDungeonCraftPigZombie;
import de.keyle.dungeoncraft.entity.types.sheep.EntityDungeonCraftSheep;
import de.keyle.dungeoncraft.entity.types.silverfish.EntityDungeonCraftSilverfish;
import de.keyle.dungeoncraft.entity.types.skeleton.EntityDungeonCraftSkeleton;
import de.keyle.dungeoncraft.entity.types.slime.EntityDungeonCraftSlime;
import de.keyle.dungeoncraft.entity.types.snowman.EntityDungeonCraftSnowman;
import de.keyle.dungeoncraft.entity.types.spider.EntityDungeonCraftSpider;
import de.keyle.dungeoncraft.entity.types.squid.EntityDungeonCraftSquid;
import de.keyle.dungeoncraft.entity.types.villager.EntityDungeonCraftVillager;
import de.keyle.dungeoncraft.entity.types.witch.EntityDungeonCraftWitch;
import de.keyle.dungeoncraft.entity.types.wither.EntityDungeonCraftWither;
import de.keyle.dungeoncraft.entity.types.wolf.EntityDungeonCraftWolf;
import de.keyle.dungeoncraft.entity.types.zombie.EntityDungeonCraftZombie;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import net.minecraft.server.v1_7_R1.EntityCreature;
import net.minecraft.server.v1_7_R1.World;
import org.bukkit.ChatColor;

import java.lang.reflect.Constructor;

public enum EntityType {
    Bat(org.bukkit.entity.EntityType.BAT, "Bat", EntityDungeonCraftBat.class),
    Blaze(org.bukkit.entity.EntityType.BLAZE, "Blaze", EntityDungeonCraftBlaze.class),
    CaveSpider(org.bukkit.entity.EntityType.CAVE_SPIDER, "CaveSpider", EntityDungeonCraftCaveSpider.class),
    Chicken(org.bukkit.entity.EntityType.CHICKEN, "Chicken", EntityDungeonCraftChicken.class),
    Cow(org.bukkit.entity.EntityType.COW, "Cow", EntityDungeonCraftCow.class),
    Creeper(org.bukkit.entity.EntityType.CREEPER, "Creeper", EntityDungeonCraftCreeper.class),
    Enderman(org.bukkit.entity.EntityType.ENDERMAN, "Enderman", EntityDungeonCraftEnderman.class),
    Ghast(org.bukkit.entity.EntityType.GHAST, "Ghast", EntityDungeonCraftGhast.class),
    Giant(org.bukkit.entity.EntityType.GIANT, "Giant", EntityDungeonCraftGiant.class),
    Horse(org.bukkit.entity.EntityType.HORSE, "Horse", EntityDungeonCraftHorse.class),
    IronGolem(org.bukkit.entity.EntityType.IRON_GOLEM, "IronGolem", EntityDungeonCraftIronGolem.class),
    MagmaCube(org.bukkit.entity.EntityType.MAGMA_CUBE, "MagmaCube", EntityDungeonCraftMagmaCube.class),
    Mooshroom(org.bukkit.entity.EntityType.MUSHROOM_COW, "Mooshroom", EntityDungeonCraftMooshroom.class),
    Ocelot(org.bukkit.entity.EntityType.OCELOT, "Ocelot", EntityDungeonCraftOcelot.class),
    Pig(org.bukkit.entity.EntityType.PIG, "Pig", EntityDungeonCraftPig.class),
    PigZombie(org.bukkit.entity.EntityType.PIG_ZOMBIE, "PigZombie", EntityDungeonCraftPigZombie.class),
    Sheep(org.bukkit.entity.EntityType.SHEEP, "Sheep", EntityDungeonCraftSheep.class),
    Silverfish(org.bukkit.entity.EntityType.SILVERFISH, "Silverfish", EntityDungeonCraftSilverfish.class),
    Skeleton(org.bukkit.entity.EntityType.SKELETON, "Skeleton", EntityDungeonCraftSkeleton.class),
    Slime(org.bukkit.entity.EntityType.SLIME, "Slime", EntityDungeonCraftSlime.class),
    Snowman(org.bukkit.entity.EntityType.SNOWMAN, "Snowman", EntityDungeonCraftSnowman.class),
    Spider(org.bukkit.entity.EntityType.SPIDER, "Spider", EntityDungeonCraftSpider.class),
    Squid(org.bukkit.entity.EntityType.SQUID, "Squid", EntityDungeonCraftSquid.class),
    Witch(org.bukkit.entity.EntityType.WITCH, "Witch", EntityDungeonCraftWitch.class),
    Wither(org.bukkit.entity.EntityType.WITHER, "Wither", EntityDungeonCraftWither.class),
    Wolf(org.bukkit.entity.EntityType.WOLF, "Wolf", EntityDungeonCraftWolf.class),
    Villager(org.bukkit.entity.EntityType.VILLAGER, "Villager", EntityDungeonCraftVillager.class),
    Zombie(org.bukkit.entity.EntityType.ZOMBIE, "Zombie", EntityDungeonCraftZombie.class);

    private org.bukkit.entity.EntityType bukkitType;
    private String name;
    private Class<? extends EntityDungeonCraft> entityClass;

    private EntityType(org.bukkit.entity.EntityType bukkitType, String typeName, Class<? extends EntityDungeonCraft> entityClass) {
        this.bukkitType = bukkitType;
        this.name = typeName;
        this.entityClass = entityClass;
    }

    public Class<? extends EntityDungeonCraft> getEntityClass() {
        return entityClass;
    }

    public org.bukkit.entity.EntityType getEntityType() {
        return bukkitType;
    }

    public static EntityType getMyPetTypeByEntityClass(Class<? extends EntityCreature> entityClass) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.entityClass == entityClass) {
                return entityType;
            }
        }
        return null;
    }

    public static EntityType getMyPetTypeByEntityType(org.bukkit.entity.EntityType type) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.bukkitType == type) {
                return entityType;
            }
        }
        return null;
    }

    public static EntityType getMyPetTypeByName(String name) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.name.equalsIgnoreCase(name)) {
                return entityType;
            }
        }
        return null;
    }

    public EntityDungeonCraft getNewEntityInstance(World world) {
        EntityDungeonCraft petEntity = null;

        try {
            Constructor<?> ctor = entityClass.getConstructor(World.class);
            Object obj = ctor.newInstance(world);
            if (obj instanceof EntityDungeonCraft) {
                petEntity = (EntityDungeonCraft) obj;
            }
        } catch (Exception e) {
            DungeonCraftLogger.write(ChatColor.RED + entityClass.getName() + " is no valid MyPet(Entity)!");
            DebugLogger.warning(entityClass.getName() + " is no valid MyPet(Entity)!");
            e.printStackTrace();
        }
        return petEntity;
    }

    public String getTypeName() {
        return name;
    }

    public static boolean hasCorrespondingEntityType(org.bukkit.entity.EntityType type) {
        for (EntityType entityType : EntityType.values()) {
            if (entityType.bukkitType == type) {
                return true;
            }
        }
        return false;
    }
}