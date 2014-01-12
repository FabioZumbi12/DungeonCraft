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

package de.keyle.dungeoncraft.dungeon.generator;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_7_R1.scoreboard.CraftScoreboard;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.generator.ChunkGenerator;

import java.io.File;
import java.util.Random;

public class DungeonCraftWorld {
    public static final String WORLD_NAME = "dungeon_craft_world";

    public static World createWorld() {
        File worldsFolder = new File(DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath());
        MinecraftServer console = MinecraftServer.getServer();
        long seed = new Random().nextLong();

        if (Bukkit.getWorld(WORLD_NAME) != null) {
            Bukkit.getServer().unloadWorld(Bukkit.getWorld(WORLD_NAME), true);
        }

        if ((worldsFolder.exists()) && (!worldsFolder.isDirectory())) {
            throw new IllegalArgumentException("File exists with the name '" + WORLD_NAME + "' and isn't a folder");
        }

        int dimension = 10 + console.worlds.size();
        boolean used = false;
        do {
            for (WorldServer server : console.worlds) {
                used = server.dimension == dimension;
                if (used) {
                    dimension++;
                    break;
                }
            }
        }
        while (used);

        ChunkGenerator generator = console.server.getGenerator(WORLD_NAME);
        ServerNBTManager nbtManager = new ServerNBTManager(worldsFolder, "world", true);
        WorldSettings worldSettings = new WorldSettings(seed, EnumGamemode.ADVENTURE, false, false, WorldType.NORMAL);

        WorldServer internal = new DungeonCraftWorldServer(console, nbtManager, WORLD_NAME, dimension, worldSettings, console.methodProfiler, World.Environment.NORMAL, generator);

        internal.scoreboard = ((CraftScoreboard) Bukkit.getScoreboardManager().getMainScoreboard()).getHandle();
        //internal.tracker = new EntityTracker(internal); //doppelte zuweisung?!?! (WorldServer.java)
        internal.addIWorldAccess(new WorldManager(console, internal));
        internal.difficulty = EnumDifficulty.NORMAL;
        internal.setSpawnFlags(true, true);
        internal.keepSpawnInMemory = false;
        internal.savingDisabled = false;

        console.worlds.add(internal);

        Bukkit.getPluginManager().callEvent(new WorldLoadEvent(internal.getWorld()));
        return internal.getWorld();
    }
}