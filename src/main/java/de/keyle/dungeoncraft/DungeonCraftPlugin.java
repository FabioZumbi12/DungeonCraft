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

package de.keyle.dungeoncraft;

import de.keyle.command.framework.CommandFramework;
import de.keyle.dungeoncraft.commands.*;
import de.keyle.dungeoncraft.dungeon.DungeonBaseLoader;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntranceRegistry;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
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
import de.keyle.dungeoncraft.listeners.DungeonListener;
import de.keyle.dungeoncraft.listeners.EntityListener;
import de.keyle.dungeoncraft.listeners.PlayerListener;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.Configuration;
import de.keyle.dungeoncraft.util.DungeonCraftVersion;
import de.keyle.dungeoncraft.util.locale.Locales;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Arrays;

public class DungeonCraftPlugin extends JavaPlugin {
    private static DungeonCraftPlugin plugin;
    CommandFramework framework;

    public void onDisable() {
        DungeonCraftLogger.setConsole(null);
        Bukkit.getServer().getScheduler().cancelTasks(plugin);
        DebugLogger.info("DungeonCraft disabled!");

        for (DungeonCraftPlayer player : DungeonCraftPlayer.getAllPlayers()) {
            if (player.getDungeon() != null) {
                player.getDungeon().teleportOut(player);
            }
        }

        Bukkit.getServer().unloadWorld(DungeonCraftWorld.WORLD_NAME, true);
    }

    public void onEnable() {
        plugin = this;

        new File(getPlugin().getDataFolder(), "dungeons").mkdirs();
        new File(getPlugin().getDataFolder(), "logs").mkdirs();
        new File(getPlugin().getDataFolder().getAbsolutePath() + File.separator + "logs", "dungeon").mkdirs();

        DungeonCraftVersion.reset();
        DungeonCraftLogger.setConsole(getServer().getConsoleSender());
        Configuration.config = this.getConfig();
        Configuration.setDefault();
        Configuration.loadConfiguration();
        DebugLogger.setup();

        DebugLogger.info("----------- loading DungeonCraft ... -----------");
        DebugLogger.info("DungeonCraft " + DungeonCraftVersion.getVersion() + " build: " + DungeonCraftVersion.getBuild());
        DebugLogger.info("Bukkit " + getServer().getVersion());
        DebugLogger.info("Java: " + System.getProperty("java.version") + " (VM: " + System.getProperty("java.vm.version") + ") by " + System.getProperty("java.vendor"));
        DebugLogger.info("Plugins: " + Arrays.toString(getServer().getPluginManager().getPlugins()));

        new Locales();
        new DungeonBaseLoader();
        DungeonEntranceRegistry.loadEntrances();

        EntityListener entityListener = new EntityListener();
        getServer().getPluginManager().registerEvents(entityListener, this);
        PlayerListener playerListener = new PlayerListener();
        getServer().getPluginManager().registerEvents(playerListener, this);
        DungeonListener dungeonListener = new DungeonListener();
        getServer().getPluginManager().registerEvents(dungeonListener, this);

        framework = new CommandFramework(this) {
            @Override
            public void printMessage(String message) {
                DungeonCraftLogger.write(message);
            }
        };
        framework.registerCommands(new PartyCreateCommand());
        framework.registerCommands(new PartyInviteCommand());
        framework.registerCommands(new PartyJoinCommand());
        framework.registerCommands(new PartyLeaveCommand());
        framework.registerCommands(new PartyKickCommand());
        framework.registerCommands(new PartyLeaderCommand());
        framework.registerCommands(new PartyOptionCommand());
        framework.registerCommands(new CreateDungeonEntranceCommand());

        framework.registerCommands(new TestCommand());
        framework.registerCommands(new SpawnTemplateEntityCommand());

        BukkitUtil.registerEntity(EntityDungeonCraftCreeper.class, "Creeper", 50);
        BukkitUtil.registerEntity(EntityDungeonCraftSkeleton.class, "Skeleton", 51);
        BukkitUtil.registerEntity(EntityDungeonCraftSpider.class, "Spider", 52);
        BukkitUtil.registerEntity(EntityDungeonCraftGiant.class, "Giant", 53);
        BukkitUtil.registerEntity(EntityDungeonCraftZombie.class, "Zombie", 54);
        BukkitUtil.registerEntity(EntityDungeonCraftSlime.class, "Slime", 55);
        BukkitUtil.registerEntity(EntityDungeonCraftGhast.class, "Ghast", 56);
        BukkitUtil.registerEntity(EntityDungeonCraftPigZombie.class, "PigZombie", 57);
        BukkitUtil.registerEntity(EntityDungeonCraftEnderman.class, "Enderman", 58);
        BukkitUtil.registerEntity(EntityDungeonCraftCaveSpider.class, "CaveSpider", 59);
        BukkitUtil.registerEntity(EntityDungeonCraftSilverfish.class, "Silverfish", 60);
        BukkitUtil.registerEntity(EntityDungeonCraftBlaze.class, "Blaze", 61);
        BukkitUtil.registerEntity(EntityDungeonCraftMagmaCube.class, "LavaSlime", 62);
        BukkitUtil.registerEntity(EntityDungeonCraftWither.class, "WitherBoss", 64);
        BukkitUtil.registerEntity(EntityDungeonCraftBat.class, "Bat", 65);
        BukkitUtil.registerEntity(EntityDungeonCraftWitch.class, "Witch", 66);
        BukkitUtil.registerEntity(EntityDungeonCraftPig.class, "Pig", 90);
        BukkitUtil.registerEntity(EntityDungeonCraftSheep.class, "Sheep", 91);
        BukkitUtil.registerEntity(EntityDungeonCraftCow.class, "Cow", 92);
        BukkitUtil.registerEntity(EntityDungeonCraftChicken.class, "Chicken", 93);
        BukkitUtil.registerEntity(EntityDungeonCraftSquid.class, "Squid", 94);
        BukkitUtil.registerEntity(EntityDungeonCraftWolf.class, "Wolf", 95);
        BukkitUtil.registerEntity(EntityDungeonCraftMooshroom.class, "MushroomCow", 96);
        BukkitUtil.registerEntity(EntityDungeonCraftSnowman.class, "SnowMan", 97);
        BukkitUtil.registerEntity(EntityDungeonCraftOcelot.class, "Ozelot", 98);
        BukkitUtil.registerEntity(EntityDungeonCraftIronGolem.class, "VillagerGolem", 99);
        BukkitUtil.registerEntity(EntityDungeonCraftHorse.class, "EntityHorse", 100);
        BukkitUtil.registerEntity(EntityDungeonCraftVillager.class, "Villager", 120);

        DungeonCraftWorld.createWorld();

        DungeonCraftLogger.write("version " + DungeonCraftVersion.getVersion() + "-b" + DungeonCraftVersion.getBuild() + ChatColor.GREEN + " ENABLED");
    }

    public static DungeonCraftPlugin getPlugin() {
        return plugin;
    }

    public File getFile() {
        return super.getFile();
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        return framework.handleCommand(sender, label, command, args);
    }
}