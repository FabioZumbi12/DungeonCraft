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

package de.keyle.dungeoncraft.entity.util;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import net.minecraft.server.v1_7_R2.*;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_7_R2.entity.CraftPlayer;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.Field;
import java.util.HashSet;

public class HealthBar {
    public static final int ENTITY_ID = Integer.MAX_VALUE;
    private static HashSet<String> hasHealthBar = new HashSet<String>();

    private static Field FIELD_PPOSEL_a = getField(PacketPlayOutSpawnEntityLiving.class, "a");
    private static Field FIELD_PPOSEL_b = getField(PacketPlayOutSpawnEntityLiving.class, "b");
    private static Field FIELD_PPOSEL_c = getField(PacketPlayOutSpawnEntityLiving.class, "c");
    private static Field FIELD_PPOSEL_d = getField(PacketPlayOutSpawnEntityLiving.class, "d");
    private static Field FIELD_PPOSEL_e = getField(PacketPlayOutSpawnEntityLiving.class, "e");
    private static Field FIELD_PPOSEL_f = getField(PacketPlayOutSpawnEntityLiving.class, "f");
    private static Field FIELD_PPOSEL_g = getField(PacketPlayOutSpawnEntityLiving.class, "g");
    private static Field FIELD_PPOSEL_h = getField(PacketPlayOutSpawnEntityLiving.class, "h");
    private static Field FIELD_PPOSEL_i = getField(PacketPlayOutSpawnEntityLiving.class, "i");
    private static Field FIELD_PPOSEL_j = getField(PacketPlayOutSpawnEntityLiving.class, "j");
    private static Field FIELD_PPOSEL_k = getField(PacketPlayOutSpawnEntityLiving.class, "k");
    private static Field FIELD_PPOSEL_l = getField(PacketPlayOutSpawnEntityLiving.class, "l");
    private static Field FIELD_PPOED_a = getField(PacketPlayOutEntityDestroy.class, "a");
    private static Field FIELD_PPOEM_a = getField(PacketPlayOutEntityMetadata.class, "a");
    private static Field FIELD_PPOEM_b = getField(PacketPlayOutEntityMetadata.class, "b");
    private static Field FIELD_PPICC_a = getField(PacketPlayInClientCommand.class, "a");

    static {
        FIELD_PPOSEL_a.setAccessible(true);
        FIELD_PPOSEL_b.setAccessible(true);
        FIELD_PPOSEL_c.setAccessible(true);
        FIELD_PPOSEL_d.setAccessible(true);
        FIELD_PPOSEL_e.setAccessible(true);
        FIELD_PPOSEL_f.setAccessible(true);
        FIELD_PPOSEL_g.setAccessible(true);
        FIELD_PPOSEL_h.setAccessible(true);
        FIELD_PPOSEL_i.setAccessible(true);
        FIELD_PPOSEL_j.setAccessible(true);
        FIELD_PPOSEL_k.setAccessible(true);
        FIELD_PPOSEL_l.setAccessible(true);
        FIELD_PPOED_a.setAccessible(true);
        FIELD_PPOEM_a.setAccessible(true);
        FIELD_PPOEM_b.setAccessible(true);
        FIELD_PPOEM_b.setAccessible(true);
        FIELD_PPICC_a.setAccessible(true);
    }

    public static void sendPacket(Player player, Packet packet) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        entityPlayer.playerConnection.sendPacket(packet);
    }

    public static Field getField(Class<?> cl, String field_name) {
        try {
            return cl.getDeclaredField(field_name);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static PacketPlayOutSpawnEntityLiving getMobPacket(String text, Location loc) {
        PacketPlayOutSpawnEntityLiving mobPacket = new PacketPlayOutSpawnEntityLiving();
        try {
            FIELD_PPOSEL_a.set(mobPacket, ENTITY_ID);
            FIELD_PPOSEL_b.set(mobPacket, (byte) EntityType.WITHER.getTypeId());
            FIELD_PPOSEL_c.set(mobPacket, (int) Math.floor(loc.getBlockX() * 32.0D));
            FIELD_PPOSEL_d.set(mobPacket, (int) Math.floor(loc.getBlockY() * 32.0D));
            FIELD_PPOSEL_e.set(mobPacket, (int) Math.floor(loc.getBlockZ() * 32.0D));
            FIELD_PPOSEL_f.set(mobPacket, (byte) 0);
            FIELD_PPOSEL_g.set(mobPacket, (byte) 0);
            FIELD_PPOSEL_h.set(mobPacket, (byte) 0);
            FIELD_PPOSEL_i.set(mobPacket, (byte) 0);
            FIELD_PPOSEL_j.set(mobPacket, (byte) 0);
            FIELD_PPOSEL_k.set(mobPacket, (byte) 0);
        } catch (IllegalArgumentException e1) {
            e1.printStackTrace();
        } catch (IllegalAccessException e1) {
            e1.printStackTrace();
        }
        DataWatcher watcher = getWatcher(text, 300);
        try {
            FIELD_PPOSEL_l.set(mobPacket, watcher);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return mobPacket;
    }

    public static PacketPlayOutEntityDestroy getDestroyEntityPacket() {
        PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy();
        try {
            FIELD_PPOED_a.set(packet, new int[]{ENTITY_ID});
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return packet;
    }

    public static PacketPlayOutEntityMetadata getMetadataPacket(DataWatcher watcher) {
        PacketPlayOutEntityMetadata metaPacket = new PacketPlayOutEntityMetadata();
        try {
            FIELD_PPOEM_a.set(metaPacket, ENTITY_ID);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        try {
            FIELD_PPOEM_b.set(metaPacket, watcher.c());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return metaPacket;
    }

    public static PacketPlayInClientCommand getRespawnPacket() {
        PacketPlayInClientCommand packet = new PacketPlayInClientCommand();
        try {
            FIELD_PPICC_a.set(packet, 1);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return packet;
    }

    public static DataWatcher getWatcher(String text, int health) {
        DataWatcher watcher = new DataWatcher(null);
        watcher.a(0, Byte.valueOf((byte) 0x20));
        watcher.a(6, Float.valueOf(health));
        watcher.a(10, text);
        watcher.a(11, Byte.valueOf((byte) 1));
        return watcher;
    }

    public static void displayTextBar(String text, final Player player) {
        PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(text, player.getLocation());
        sendPacket(player, mobPacket);
        hasHealthBar.add(player.getName());
        new BukkitRunnable() {
            @Override
            public void run() {
                PacketPlayOutEntityDestroy destroyEntityPacket = getDestroyEntityPacket();
                sendPacket(player, destroyEntityPacket);
                hasHealthBar.remove(player.getName());
            }
        }.runTaskLater(DungeonCraftPlugin.getPlugin(), 2000L);
    }

    public static void displayHealthBar(final String text, final Player player, final int healthPercent) {
        PacketPlayOutSpawnEntityLiving mobPacket = getMobPacket(text, player.getLocation());
        sendPacket(player, mobPacket);
        hasHealthBar.add(player.getName());
        new BukkitRunnable() {
            int health = (int) (300. * healthPercent / 100.);

            @Override
            public void run() {
                if (healthPercent > 0) {
                    DataWatcher watcher = getWatcher(text, health);
                    PacketPlayOutEntityMetadata metaPacket = getMetadataPacket(watcher);
                    sendPacket(player, metaPacket);
                } else {
                    PacketPlayOutEntityDestroy destroyEntityPacket = getDestroyEntityPacket();
                    sendPacket(player, destroyEntityPacket);
                    hasHealthBar.remove(player.getName());
                }
            }
        }.runTaskLater(DungeonCraftPlugin.getPlugin(), 20L);
    }
}