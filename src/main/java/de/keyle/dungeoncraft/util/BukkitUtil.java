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

import net.minecraft.server.v1_7_R1.*;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_7_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;

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
}