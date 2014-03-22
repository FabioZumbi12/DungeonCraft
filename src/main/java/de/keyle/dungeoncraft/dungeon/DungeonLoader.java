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

import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftChunk;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftChunkProvider;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.dungeon.region.RegionLoader;
import de.keyle.dungeoncraft.dungeon.scripting.TriggerLoader;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import de.keyle.knbt.TagCompound;
import de.keyle.knbt.TagString;
import net.minecraft.server.v1_7_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayDeque;
import java.util.Map;
import java.util.Queue;
import java.util.Vector;
import java.util.concurrent.atomic.AtomicInteger;

public class DungeonLoader extends Thread {
    private static final Queue<DungeonLoader> LOADER_QUEUE = new ArrayDeque<DungeonLoader>();
    private volatile static DungeonLoader runningLoader = null;

    private final Dungeon dungeon;

    public DungeonLoader(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public synchronized boolean isInQueue() {
        return runningLoader != this;
    }

    public void startLoader() {
        synchronized (LOADER_QUEUE) {
            LOADER_QUEUE.add(this);

            if (runningLoader == null && LOADER_QUEUE.size() == 1) {
                runNextLoader();
            }
        }
    }

    @Override
    public void start() {
        runningLoader = this;
        super.start();
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

        dungeon.getDungeonLogger().info("Calculting Light...");
        //Lighting calculation
        for (int x = 0; x < xCount; x++) {
            for (int z = 0; z < zCount; z++) {
                Chunk c = DungeonCraftChunkProvider.chunkloader.getChunkAt(dungeon.position.getChunkX() + x, dungeon.position.getChunkZ() + z);
                if (c instanceof DungeonCraftChunk) {
                    ((DungeonCraftChunk) c).initEmittedLight();
                    ((DungeonCraftChunk) c).generateSkylightMap();
                }
            }
        }
        for (int x = 0; x < xCount; x++) {
            for (int z = 0; z < zCount; z++) {
                Chunk c = DungeonCraftChunkProvider.chunkloader.getChunkAt(dungeon.position.getChunkX() + x, dungeon.position.getChunkZ() + z);
                if (c instanceof DungeonCraftChunk) {
                    ((DungeonCraftChunk) c).updateSkylight(false);
                }
            }
        }
        dungeon.getDungeonLogger().info("Light Calculation DONE");

        dungeon.getDungeonLogger().info("Hanging Paintings");

        spawnEntities(schematic);

        dungeon.getDungeonLogger().info("Ohhhh such paintings");

        dungeon.getDungeonLogger().info("Loading Regions...");
        new RegionLoader(dungeon);
        dungeon.getDungeonLogger().info("Region Loading DONE");

        dungeon.getDungeonLogger().info("Loading Triggers...");
        new TriggerLoader(dungeon);
        dungeon.getDungeonLogger().info("Trigger Loading DONE");

        dungeon.setReady();

        runNextLoader();
    }

    private static void runNextLoader() {
        synchronized (LOADER_QUEUE) {
            if (LOADER_QUEUE.size() > 0) {
                DungeonLoader loader = LOADER_QUEUE.poll();
                runningLoader = loader;
                loader.start();
                return;
            }
        }
        runningLoader = null;
    }

    private void spawnEntities(Schematic schematic) {
        net.minecraft.server.v1_7_R1.World world = ((CraftWorld) Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME)).getHandle();
        Map<Vector<Double>, TagCompound> entities = schematic.getEntities();
        for (Vector<Double> pos : entities.keySet()) {
            Entity entity = null;
            Method METHOD_A = null;
            String methodName = "a";
            TagCompound entityCompound = entities.get(pos);
            String entityType = ((TagString) entityCompound.get("id")).getStringData();
            double x = pos.get(0) + (dungeon.position.getChunkX() * 16);
            double y = pos.get(1);
            double z = pos.get(2) + (dungeon.position.getChunkZ() * 16);
            try {
                NBTTagCompound nbtTagCompound = (NBTTagCompound) BukkitUtil.compoundToVanillaCompound(entityCompound);
                if (entityType.equals("Painting")) {
                    entity = new EntityPainting(world, (int) x, (int) y, (int) z, 0);
                    ((EntityPainting) entity).a(nbtTagCompound);
                } else if (entityType.equals("ItemFrame")) {
                    entity = new EntityItemFrame(world, (int) x, (int) y, (int) z, 0);
                    ((EntityItemFrame) entity).a(nbtTagCompound);
                } else if (entityType.equals("Boat")) {
                    entity = new EntityBoat(world, x, y, z);
                    //a Method empty
                } else if (entityType.equals("MinecartRideable")) {
                    entity = new EntityMinecartRideable(world, x, y, z);
                    METHOD_A = EntityMinecartAbstract.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("MinecartChest")) {
                    entity = new EntityMinecartChest(world, x, y, z);
                    METHOD_A = EntityMinecartContainer.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("MinecartFurnace")) {
                    entity = new EntityMinecartFurnace(world, x, y, z);
                    METHOD_A = EntityMinecartFurnace.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("MinecartTNT")) {
                    entity = new EntityMinecartTNT(world, x, y, z);
                    METHOD_A = EntityMinecartTNT.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("MinecartHopper")) {
                    entity = new EntityMinecartHopper(world, x, y, z);
                    METHOD_A = EntityMinecartHopper.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("MinecartSpawner")) {
                    entity = new EntityMinecartMobSpawner(world, x, y, z);
                    METHOD_A = EntityMinecartMobSpawner.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("MinecartCommandBlock")) {
                    entity = new EntityMinecartCommandBlock(world, x, y, z);
                    METHOD_A = EntityMinecartCommandBlock.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("Item")) {
                    entity = new EntityItem(world, x, y, z);
                    ((EntityItem) entity).a(nbtTagCompound);
                } else if (entityType.equals("XPOrb")) {
                    entity = new EntityExperienceOrb(world, x, y, z, 0);
                    ((EntityExperienceOrb) entity).a(nbtTagCompound);
                } else if (entityType.equals("Arrow")) {
                    entity = new EntityArrow(world, x, y, z);
                    //a method modifies the position of the object based on these coordinates
                    nbtTagCompound.setShort("xTile", (short) (nbtTagCompound.getShort("xTile") + (dungeon.position.getChunkX() * 16)));
                    nbtTagCompound.setShort("zTile", (short) (nbtTagCompound.getShort("zTile") + (dungeon.position.getChunkZ() * 16)));
                    ((EntityArrow) entity).a(nbtTagCompound);
                } else if (entityType.equals("Snowball")) {
                    entity = new EntitySnowball(world, x, y, z);
                    //a method modifies the position of the object based on these coordinates
                    nbtTagCompound.setShort("xTile", (short) (nbtTagCompound.getShort("xTile") + (dungeon.position.getChunkX() * 16)));
                    nbtTagCompound.setShort("zTile", (short) (nbtTagCompound.getShort("zTile") + (dungeon.position.getChunkZ() * 16)));
                    ((EntitySnowball) entity).a(nbtTagCompound);
                } else if (entityType.equals("ThrownEnderpearl")) {
                    entity = new EntityEnderPearl(world);
                    //a method modifies the position of the object based on these coordinates
                    nbtTagCompound.setShort("xTile", (short) (nbtTagCompound.getShort("xTile") + (dungeon.position.getChunkX() * 16)));
                    nbtTagCompound.setShort("zTile", (short) (nbtTagCompound.getShort("zTile") + (dungeon.position.getChunkZ() * 16)));
                    ((EntityEnderPearl) entity).a(nbtTagCompound);
                } else if (entityType.equals("EyeOfEnderSignal")) {
                    entity = new EntityEnderSignal(world, x, y, z);
                    //((EntityEnderSignal) entity).a((NBTTagCompound) BukkitUtil.compoundToVanillaCompound(entityCompound)); --> Empty method
                } else if (entityType.equals("FireworksRocketEntity")) {
                    entity = new EntityFireworks(world, x, y, z, null);
                    ((EntityFireworks) entity).a(nbtTagCompound);
                } else if (entityType.equals("PrimedTnt")) {
                    entity = new EntityTNTPrimed(world, x, y, z, null);
                    METHOD_A = EntityTNTPrimed.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("FallingSand")) {
                    entity = new EntityFallingBlock(world, x, y, z, null);
                    METHOD_A = EntityFallingBlock.class.getDeclaredMethod(methodName, NBTTagCompound.class);
                } else if (entityType.equals("Fireball")) {
                    entity = new EntityLargeFireball(world, null, x, y, z);
                    //a method modifies the position of the object based on these coordinates
                    nbtTagCompound.setShort("xTile", (short) (nbtTagCompound.getShort("xTile") + (dungeon.position.getChunkX() * 16)));
                    nbtTagCompound.setShort("zTile", (short) (nbtTagCompound.getShort("zTile") + (dungeon.position.getChunkZ() * 16)));
                    ((EntityLargeFireball) entity).a(nbtTagCompound);
                } else if (entityType.equals("SmallFireball ")) {
                    entity = new EntitySmallFireball(world, null, x, y, z);
                    //a method modifies the position of the object based on these coordinates
                    nbtTagCompound.setShort("xTile", (short) (nbtTagCompound.getShort("xTile") + (dungeon.position.getChunkX() * 16)));
                    nbtTagCompound.setShort("zTile", (short) (nbtTagCompound.getShort("zTile") + (dungeon.position.getChunkZ() * 16)));
                    ((EntitySmallFireball) entity).a(nbtTagCompound);
                } else if (entityType.equals("EnderCrystal ")) {
                    entity = new EntityEnderCrystal(world);
                    //METHOD_A = EntityEnderCrystal.class.getDeclaredMethod("a", NBTTagCompound.class); --> Empty method
                }
                if (METHOD_A != null) {
                    METHOD_A.setAccessible(true);
                    METHOD_A.invoke(entity, nbtTagCompound);
                }
                if (entity != null) {
                    world.addEntity(entity);
                }
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }
}