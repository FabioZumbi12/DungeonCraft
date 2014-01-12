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
import de.keyle.dungeoncraft.commands.CreateGroupCommand;
import de.keyle.dungeoncraft.commands.InviteToGroupCommand;
import de.keyle.dungeoncraft.commands.JoinGroupCommand;
import de.keyle.dungeoncraft.commands.TestCommand;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.listeners.WorldListener;
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

        Bukkit.getServer().unloadWorld(DungeonCraftWorld.WORLD_NAME, true);
    }

    public void onEnable() {
        plugin = this;

        new File(getPlugin().getDataFolder().getAbsolutePath(), "dungeons").mkdirs();
        new File(getPlugin().getDataFolder().getAbsolutePath(), "logs").mkdirs();

        DungeonCraftVersion.reset();
        DungeonCraftLogger.setConsole(getServer().getConsoleSender());
        Configuration.config = this.getConfig();
        Configuration.setDefault();
        Configuration.loadConfiguration();
        DebugLogger.setup();

        DebugLogger.info("----------- loading DungeonCraft ... -----------");
        DebugLogger.info("MyPet " + DungeonCraftVersion.getVersion() + " build: " + DungeonCraftVersion.getBuild());
        DebugLogger.info("Bukkit " + getServer().getVersion());
        DebugLogger.info("Java: " + System.getProperty("java.version") + " (VM: " + System.getProperty("java.vm.version") + ") by " + System.getProperty("java.vendor"));
        DebugLogger.info("Plugins: " + Arrays.toString(getServer().getPluginManager().getPlugins()));

        new Locales();

        WorldListener worldListener = new WorldListener();
        getServer().getPluginManager().registerEvents(worldListener, this);

        framework = new CommandFramework(this) {
            @Override
            public void printMessage(String message) {
                DungeonCraftLogger.write(message);
            }
        };
        framework.registerCommands(new CreateGroupCommand());
        framework.registerCommands(new InviteToGroupCommand());
        framework.registerCommands(new JoinGroupCommand());

        framework.registerCommands(new TestCommand());

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