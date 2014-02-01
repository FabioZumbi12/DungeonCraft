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

package de.keyle.dungeoncraft.util;

import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.vector.Vector;
import de.keyle.knbt.*;
import net.minecraft.server.v1_7_R1.*;
import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftItem;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.Map;

import static org.bukkit.World.Environment;

public class BukkitUtil {
    public static String getPlayerLanguage(Player player) {
        if (!(player instanceof CraftPlayer)) {
            return "en_US";
        }
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        try {
            Field field = entityPlayer.getClass().getDeclaredField("locale");
            field.setAccessible(true);

            return (String) field.get(entityPlayer);
        } catch (Exception e) {
            return "en_US";
        }
    }

    public static String getCommandSenderLanguage(CommandSender sender) {
        String lang = "en";
        if (sender instanceof Player) {
            lang = getPlayerLanguage((Player) sender);
        }
        return lang;
    }

    public static void sendMessageRaw(Player player, String message) {
        if (player instanceof CraftPlayer) {
            ((CraftPlayer) player).getHandle().playerConnection.sendPacket(new PacketPlayOutChat(ChatSerializer.a(message)));
        }
    }

    public static void setPlayerEnvironment(Player player, Environment skyType) {
        if (player instanceof CraftPlayer) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutRespawn(skyType.getId(), EnumDifficulty.NORMAL, WorldType.NORMAL, entityPlayer.playerInteractManager.getGameMode()));
        }
        //ToDo refresh chunks, entities and fly mode to allow environment switching on the fly
    }

    public static void setPlayerGameState(Player player, int code, float value) {
        if (player instanceof CraftPlayer) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutGameStateChange(code, value));
        }
    }

    public static void setPlayerTime(Player player, int time, boolean lock) {
        if (player instanceof CraftPlayer) {
            player.setPlayerTime(Math.abs(time), !lock);
        }
    }

    // https://github.com/SirCmpwn/Craft.Net/blob/master/source/Craft.Net.Common/SoundEffect.cs
    public static void playSoundEffect(Player player, String soundName, Vector pos, float volume, byte pitch) {
        if (player instanceof CraftPlayer) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutNamedSoundEffect(soundName, pos.getX(), pos.getY(), pos.getZ(), volume, pitch));
        }
    }

    // https://gist.github.com/riking/5759002
    public static void playParticleEffect(Player player, Vector pos, String effectName, float offsetX, float offsetY, float offsetZ, float speed, int count) {
        if (player instanceof CraftPlayer) {
            EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
            entityPlayer.playerConnection.sendPacket(new PacketPlayOutWorldParticles(effectName, (float) pos.getX(), (float) pos.getY(), (float) pos.getZ(), offsetX, offsetY, offsetZ, speed, count));
        }
    }

    /**
     * @param location   the {@link Location} around which players must be to see the effect
     * @param effectName list of effects: https://gist.github.com/riking/5759002
     * @param offsetX    the amount to be randomly offset by in the X axis
     * @param offsetY    the amount to be randomly offset by in the Y axis
     * @param offsetZ    the amount to be randomly offset by in the Z axis
     * @param speed      the speed of the particles
     * @param count      the number of particles
     * @param radius     the radius around the location
     */
    public static void playParticleEffect(Location location, String effectName, float offsetX, float offsetY, float offsetZ, float speed, int count, int radius) {
        Validate.notNull(location, "Location cannot be null");
        Validate.notNull(effectName, "Effect cannot be null");
        Validate.notNull(location.getWorld(), "World cannot be null");

        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(effectName, (float) location.getX(), (float) location.getY(), (float) location.getZ(), offsetX, offsetY, offsetZ, speed, count);

        for (Player player : location.getWorld().getPlayers()) {
            if ((int) player.getLocation().distance(location) <= radius) {
                ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
            }
        }
    }

    @SuppressWarnings("unchecked")
    public static boolean registerEntity(Class<? extends EntityDungeonCraft> dungeonCraftEntityClass, String entityTypeName, int entityTypeId) {
        try {
            Field EntityTypes_d = EntityTypes.class.getDeclaredField("d");
            Field EntityTypes_f = EntityTypes.class.getDeclaredField("f");
            EntityTypes_d.setAccessible(true);
            EntityTypes_f.setAccessible(true);

            Map<Class, String> d = (Map) EntityTypes_d.get(EntityTypes_d);
            Map<Class, Integer> f = (Map) EntityTypes_f.get(EntityTypes_f);

            Iterator cIterator = d.keySet().iterator();
            while (cIterator.hasNext()) {
                Class clazz = (Class) cIterator.next();
                if (clazz.getCanonicalName().equals(dungeonCraftEntityClass.getCanonicalName())) {
                    cIterator.remove();
                }
            }

            Iterator eIterator = f.keySet().iterator();
            while (eIterator.hasNext()) {
                Class clazz = (Class) eIterator.next();
                if (clazz.getCanonicalName().equals(dungeonCraftEntityClass.getCanonicalName())) {
                    eIterator.remove();
                }
            }

            d.put(dungeonCraftEntityClass, entityTypeName);
            f.put(dungeonCraftEntityClass, entityTypeId);

            return true;
        } catch (Exception e) {
            DebugLogger.severe("error while registering " + dungeonCraftEntityClass.getCanonicalName());
            DebugLogger.severe(e.getMessage());
            return false;
        }
    }

    public static void setTileEntity(Location location, String data) {
        Validate.notNull(data, "Data can not be null");
        NBTBase localNBTBase = MojangsonParser.a(data);
        Validate.isTrue(localNBTBase instanceof NBTTagCompound, "Data has to be a valid tag");
        if (localNBTBase instanceof NBTTagCompound) {
            setTileEntity(location, (NBTTagCompound) localNBTBase);
        }
    }

    public static void setTileEntity(Location location, NBTTagCompound data) {
        Validate.notNull(location, "Location can not be null");
        Validate.notNull(data, "Data can not be null");
        World world = ((CraftWorld) location.getWorld()).getHandle();
        TileEntity tileEntity = world.getTileEntity(location.getBlockX(), location.getBlockY(), location.getBlockZ());
        if (tileEntity != null) {
            data.setInt("x", location.getBlockX());
            data.setInt("y", location.getBlockY());
            data.setInt("z", location.getBlockZ());

            tileEntity.a(data);
        }
    }

    public static Item getItem(String itemString) {
        Item localItem = (Item) Item.REGISTRY.a(itemString);
        if (localItem == null) {
            try {
                localItem = Item.d(Integer.parseInt(itemString));
            } catch (NumberFormatException ignored) {
            }
        }
        return localItem;
    }

    public static org.bukkit.inventory.ItemStack getItemStackFromString(String itemName, int quantity) {
        return new ItemStack(CraftItemStack.asBukkitCopy(new net.minecraft.server.v1_7_R1.ItemStack(BukkitUtil.getItem(itemName))).getType(),quantity);
    }

    public static boolean isRealPlayer(Player player) {
        return player instanceof CraftPlayer;
    }

    public static void dropItem(String player, ItemStack itemStack) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        org.bukkit.World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,itemStack);
    }

    public static void makeItemUndespawnable(org.bukkit.entity.Item item) {
        EntityItem nmsItem = (EntityItem) ((CraftItem)item).getHandle();
        nmsItem.age = Integer.MIN_VALUE;
    }

    @SuppressWarnings("unchecked")
    public static NBTBase compoundToVanillaCompound(TagBase tag) {
        switch (TagType.getTypeById(tag.getTagTypeId())) {
            case Int:
                return new NBTTagInt(((TagInt) tag).getIntData());
            case Short:
                return new NBTTagShort(((TagShort) tag).getShortData());
            case String:
                return new NBTTagString(((TagString) tag).getStringData());
            case Byte:
                return new NBTTagByte(((TagByte) tag).getByteData());
            case Byte_Array:
                return new NBTTagByteArray(((TagByteArray) tag).getByteArrayData());
            case Double:
                return new NBTTagDouble(((TagDouble) tag).getDoubleData());
            case Float:
                return new NBTTagFloat(((TagFloat) tag).getFloatData());
            case Int_Array:
                return new NBTTagIntArray(((TagIntArray) tag).getIntArrayData());
            case Long:
                return new NBTTagLong(((TagLong) tag).getLongData());
            case List:
                TagList TagList = (TagList) tag;
                NBTTagList tagList = new NBTTagList();
                for (TagBase tagInList : TagList.getReadOnlyList()) {
                    tagList.add(compoundToVanillaCompound(tagInList));
                }
                return tagList;
            case Compound:
                TagCompound TagCompound = (TagCompound) tag;
                NBTTagCompound tagCompound = new NBTTagCompound();
                for (String name : TagCompound.getCompoundData().keySet()) {
                    tagCompound.set(name, compoundToVanillaCompound(TagCompound.getCompoundData().get(name)));
                }
                return tagCompound;
            case End:
                return null;
        }
        return null;
    }
}