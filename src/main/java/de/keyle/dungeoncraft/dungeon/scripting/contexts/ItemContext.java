/*
 * This file is part of DungeonCraft
 *
 * Copyright (C) 2011-2013 Keyle & xXLupoXx
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

package de.keyle.dungeoncraft.dungeon.scripting.contexts;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.generator.DungeonCraftWorld;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.BukkitUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.inventory.ItemStack;

@SuppressWarnings("unused")
public class ItemContext {
    protected final Dungeon dungeon;

    public ItemContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void giveItemToPlayer(String player, int itemID , int quantity) {
        ItemStack newItem = new ItemStack(itemID,quantity);
        DungeonCraftPlayer.getPlayer(player).getPlayer().getInventory().addItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().updateInventory();
    }

    public void giveItemToPlayer(String player, String itemName, int quantity) {
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        DungeonCraftPlayer.getPlayer(player).getPlayer().getInventory().addItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().updateInventory();
    }

    public void dropItem(String player, int itemID , int quantity) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        ItemStack newItem = new ItemStack(itemID,quantity);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,newItem);
    }

    public void dropItem(String player, String itemName, int quantity) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,newItem);
    }

    public void dropItem(int x, int y, int z, int itemID , int quantity) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        ItemStack newItem = new ItemStack(itemID,quantity);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(new Location(world,x,y,z),newItem);
    }

    public void dropItem(int x, int y, int z, String itemName, int quantity) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(new Location(world,x,y,z),newItem);
    }

    public void giveDungeonCraftItemToPlayer(String player, int itemID , int quantity) {
        ItemStack newItem = new ItemStack(itemID,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().getInventory().addItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().updateInventory();
    }

    public void giveDungeonCraftItemToPlayer(String player, String itemName, int quantity) {
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().getInventory().addItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().updateInventory();
    }

    public void giveDungeonCraftItemToPlayerUndroppable(String player, int itemID , int quantity) {
        ItemStack newItem = new ItemStack(itemID,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        Dungeon.markAsUndroppableItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().getInventory().addItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().updateInventory();
    }

    public void giveDungeonCraftItemToPlayerUndroppable(String player, String itemName, int quantity) {
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        Dungeon.markAsUndroppableItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().getInventory().addItem(newItem);
        DungeonCraftPlayer.getPlayer(player).getPlayer().updateInventory();
    }

    public void dropDungeonCraftItem(String player, int itemID , int quantity) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        ItemStack newItem = new ItemStack(itemID,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,newItem);
    }

    public void dropDungeonCraftItem(String player, String itemName, int quantity) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,newItem);
    }

    public void dropDungeonCraftItem(int x, int y, int z, int itemID , int quantity) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        ItemStack newItem = new ItemStack(itemID,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(new Location(world,x,y,z),newItem);
    }

    public void dropDungeonCraftItem(int x, int y, int z, String itemName, int quantity) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(new Location(world,x,y,z),newItem);
    }

    public void dropDungeonCraftItemUndroppable(String player, int itemID , int quantity) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        ItemStack newItem = new ItemStack(itemID,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        Dungeon.markAsUndroppableItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,newItem);
    }

    public void dropDungeonCraftItemUndroppable(String player, String itemName, int quantity) {
        Location playerLocation = DungeonCraftPlayer.getPlayer(player).getPlayer().getLocation();
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        Dungeon.markAsUndroppableItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(playerLocation,newItem);
    }

    public void dropDungeonCraftItemUndroppable(int x, int y, int z, int itemID , int quantity) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        ItemStack newItem = new ItemStack(itemID,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        Dungeon.markAsUndroppableItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(new Location(world,x,y,z),newItem);
    }

    public void dropDungeonCraftItemUndroppable(int x, int y, int z, String itemName, int quantity) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        ItemStack newItem = BukkitUtil.getItemStackFromString(itemName,quantity);
        Dungeon.markAsDungeonCraftItem(newItem);
        Dungeon.markAsUndroppableItem(newItem);
        World world = Bukkit.getWorld(DungeonCraftWorld.WORLD_NAME);

        world.dropItem(new Location(world,x,y,z),newItem);
    }
}