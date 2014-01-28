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

import net.minecraft.server.v1_7_R1.Item;
import net.minecraft.server.v1_7_R1.MojangsonParser;
import net.minecraft.server.v1_7_R1.NBTBase;
import net.minecraft.server.v1_7_R1.NBTTagCompound;
import org.bukkit.craftbukkit.v1_7_R1.inventory.CraftItemStack;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONObject;

public class ParsedItem {
    net.minecraft.server.v1_7_R1.ItemStack item = null;

    public ParsedItem(net.minecraft.server.v1_7_R1.ItemStack item) {
        this.item = item;
    }

    public ParsedItem() {
    }

    public ParsedItem(ItemStack item) {
        this.item = CraftItemStack.asNMSCopy(item);
    }

    public net.minecraft.server.v1_7_R1.ItemStack getMinecraftItem() {
        return item;
    }

    public ItemStack getBukkitItem() {
        return CraftItemStack.asBukkitCopy(item);
    }

    public boolean isEmpty() {
        return item == null;
    }

    public String toString() {
        return "ConfigItem{item: " + item + "}";
    }

    public static ParsedItem parseItem(String data) {
        NBTBase nbtBase = null;
        if (data.contains("{")) {
            String tagString = data.substring(data.indexOf("{"));
            data = data.substring(0, data.indexOf("{"));
            try {
                nbtBase = MojangsonParser.a(tagString);
            } catch (Exception ignored) {
            }
        }

        String[] splitData = data.split("\\s+");

        int itemId = 1;
        int itemDamage = 0;

        if (splitData.length == 0) {
            return new ParsedItem();
        }
        if (splitData.length >= 1) {
            if (Util.isInt(splitData[0])) {
                itemId = Integer.parseInt(splitData[0]);
            }
        }
        if (itemId != 0) {

            net.minecraft.server.v1_7_R1.ItemStack is = new net.minecraft.server.v1_7_R1.ItemStack(Item.d(itemId), 1, itemDamage);
            if (nbtBase != null && nbtBase instanceof NBTTagCompound) {
                is.setTag((NBTTagCompound) nbtBase);
            }

            return new ParsedItem(is);
        }
        return new ParsedItem();
    }

    public static ParsedItem parsedItem(JSONObject jsonData) {
        if (jsonData.containsKey("material")) {
            String material = jsonData.get("material").toString();
            Item item = BukkitUtil.getItem(material);
            byte data = 0;
            int amount = 1;
            if (item != null) {
                if (jsonData.containsKey("data") && Util.isByte(jsonData.get("data").toString())) {
                    data = Byte.parseByte(jsonData.get("data").toString());
                }
                if (jsonData.containsKey("count") && Util.isInt(jsonData.get("count").toString())) {
                    amount = Integer.parseInt(jsonData.get("count").toString());
                }
                net.minecraft.server.v1_7_R1.ItemStack itemStack = new net.minecraft.server.v1_7_R1.ItemStack(item, amount, data);
                if (jsonData.containsKey("tag")) {
                    String tagString = jsonData.get("tag").toString();
                    NBTBase localNBTBase = MojangsonParser.a(tagString);
                    if ((localNBTBase instanceof NBTTagCompound)) {
                        itemStack.setTag((NBTTagCompound) localNBTBase);
                    }
                }
                return new ParsedItem(itemStack);
            }
        }
        return new ParsedItem();
    }
}