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

package de.keyle.dungeoncraft.dungeon;

import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftChunkProvider;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.RegionLoader;
import de.keyle.dungeoncraft.dungeon.scripting.TriggerLoader;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import de.keyle.dungeoncraft.util.vector.Vector;
import de.keyle.knbt.TagCompound;
import net.minecraft.server.v1_7_R3.Entity;
import net.minecraft.server.v1_7_R3.EntityLiving;
import net.minecraft.server.v1_7_R3.EntityTypes;
import net.minecraft.server.v1_7_R3.NBTTagCompound;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonLoader extends Thread {
    private volatile boolean finished = false;
    private final Dungeon dungeon;
    private final List<Entity> entities = new ArrayList<Entity>();

    protected DungeonLoader(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public Dungeon getDungeon() {
        return dungeon;
    }

    public void run() {
        dungeon.setLoading();

        DungeonBase dungeonBase = dungeon.dungeonBase;
        Schematic schematic;

        dungeon.getDungeonLogger().info("Loading Schematic...");
        while (true) {
            schematic = dungeonBase.getSchematic();
            if (schematic != null) {
                break;
            }
        }
        dungeon.lockSchematic();
        dungeon.getDungeonLogger().info("Schematics loaded");

        dungeon.getDungeonLogger().info("Generating Chunks...");

        int xCount = (int) Math.ceil(schematic.getWidth() / 16.);
        int zCount = (int) Math.ceil(schematic.getLenght() / 16.);

        final AtomicInteger callbackCount = new AtomicInteger(xCount * zCount);
        final AtomicInteger generationCount = new AtomicInteger(0);
        final int maxThreads = Math.max(1, Runtime.getRuntime().availableProcessors() - 1);
        for (int x = 0; x < xCount; x++) {
            for (int z = 0; z < zCount; z++) {
                while (true) {
                    if (generationCount.get() < maxThreads) {
                        break;
                    }
                }
                generationCount.incrementAndGet();
                DungeonCraftChunkProvider.chunkloader.generateChunkAt(dungeon.position.getChunkX() + x, dungeon.position.getChunkZ() + z, new Runnable() {
                    @Override
                    public void run() {
                        synchronized (callbackCount) {
                            callbackCount.decrementAndGet();
                            generationCount.decrementAndGet();
                        }
                    }
                });
            }
        }
        dungeon.getDungeonLogger().info("All Chunk generators started");
        while (true) {
            if (callbackCount.get() <= 0) {
                break;
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ignored) {
            }
        }
        dungeon.getDungeonLogger().info("Chunk Generation DONE");

        dungeon.getDungeonLogger().info("Load Paintings");
        loadEntities(schematic);
        dungeon.getDungeonLogger().info("Much Color");

        dungeon.getDungeonLogger().info("Loading Regions...");
        new RegionLoader(dungeon);
        dungeon.getDungeonLogger().info("Region Loading DONE");

        dungeon.getDungeonLogger().info("Loading Triggers...");
        new TriggerLoader(dungeon);
        dungeon.getDungeonLogger().info("Trigger Loading DONE");

        dungeon.setReady();
        finished = true;
    }

    public void spawnEntities() {
        net.minecraft.server.v1_7_R3.World world = ((CraftWorld) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME)).getHandle();
        for (Entity entity : this.entities) {
            world.addEntity(entity);
        }
    }

    public void loadEntities(Schematic schematic) {
        net.minecraft.server.v1_7_R3.World world = ((CraftWorld) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME)).getHandle();
        Map<Vector, TagCompound> schematicEntities = schematic.getEntities();
        for (Vector pos : schematicEntities.keySet()) {
            NBTTagCompound entityCompound = (NBTTagCompound) BukkitUtil.compoundToVanillaCompound(schematicEntities.get(pos));
            Entity newEntity = EntityTypes.a(entityCompound, world); // create new Entity from NBT tag
            if (newEntity != null && !(newEntity instanceof EntityLiving)) {
                newEntity.locX = pos.getX() + (dungeon.position.getChunkX() * 16);
                newEntity.locY = pos.getY();
                newEntity.locZ = pos.getZ() + (dungeon.position.getChunkZ() * 16);
                this.entities.add(newEntity);
            }
        }
    }

    public boolean isFinished() {
        return finished;
    }
}