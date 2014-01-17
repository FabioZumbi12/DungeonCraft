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

package de.keyle.dungeoncraft.commands;

import de.keyle.command.framework.Command;
import de.keyle.command.framework.CommandArgs;
import de.keyle.dungeoncraft.dungeon.DungeonBase;
import de.keyle.dungeoncraft.dungeon.DungeonBaseRegistry;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntrance;
import de.keyle.dungeoncraft.dungeon.entrance.DungeonEntranceRegistry;
import de.keyle.dungeoncraft.util.vector.Region;
import de.keyle.dungeoncraft.util.vector.Vector;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.List;

public class CreateDungeonEntranceCommand {
    @Command(name = "dccreateentrance", aliases = {"dccde"}, permission = "dungeoncraft.admin.createentrance")
    public void onCommand(CommandArgs args) {
        if (args.getArgs().size() > 0) {
            List<String> arguments = args.getArgs();
            Region region = null;
            String worldName = null;
            DungeonBase base = null;
            String dungeonName = null;
            Location exitLocation = null;

            if (arguments.size() > 0) {
                int minX = Integer.MIN_VALUE;
                int minY = Integer.MIN_VALUE;
                int minZ = Integer.MIN_VALUE;
                int maxX = Integer.MIN_VALUE;
                int maxY = Integer.MIN_VALUE;
                int maxZ = Integer.MIN_VALUE;
                int exitX = Integer.MIN_VALUE;
                int exitY = Integer.MIN_VALUE;
                int exitZ = Integer.MIN_VALUE;
                float exitYaw = Float.NaN;
                float exitPitch = Float.NaN;
                String exitWorld = null;
                String world = null;
                String baseName = null;
                String name = null;

                for (String arg : arguments) {
                    if (arg.startsWith("x=")) {
                        String x = arg.substring(2);
                        if (NumberUtils.isNumber(x)) {
                            minX = Integer.parseInt(x);
                        } else {
                            args.getSender().sendMessage(x + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("y=")) {
                        String y = arg.substring(2);
                        if (NumberUtils.isNumber(y)) {
                            minY = Integer.parseInt(y);
                        } else {
                            args.getSender().sendMessage(y + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("z=")) {
                        String z = arg.substring(2);
                        if (NumberUtils.isNumber(z)) {
                            minZ = Integer.parseInt(z);
                        } else {
                            args.getSender().sendMessage(z + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("dx=")) {
                        String x = arg.substring(3);
                        if (NumberUtils.isNumber(x)) {
                            maxX = Integer.parseInt(x);
                        } else {
                            args.getSender().sendMessage(x + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("dy=")) {
                        String y = arg.substring(3);
                        if (NumberUtils.isNumber(y)) {
                            maxY = Integer.parseInt(y);
                        } else {
                            args.getSender().sendMessage(y + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("dz=")) {
                        String z = arg.substring(3);
                        if (NumberUtils.isNumber(z)) {
                            maxZ = Integer.parseInt(z);
                        } else {
                            args.getSender().sendMessage(z + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("ex=")) {
                        String x = arg.substring(3);
                        if (NumberUtils.isNumber(x)) {
                            exitX = Integer.parseInt(x);
                        } else {
                            args.getSender().sendMessage(x + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("ey=")) {
                        String y = arg.substring(3);
                        if (NumberUtils.isNumber(y)) {
                            exitY = Integer.parseInt(y);
                        } else {
                            args.getSender().sendMessage(y + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("ez=")) {
                        String z = arg.substring(3);
                        if (NumberUtils.isNumber(z)) {
                            exitZ = Integer.parseInt(z);
                        } else {
                            args.getSender().sendMessage(z + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("eyaw=")) {
                        String yaw = arg.substring(5);
                        if (NumberUtils.isNumber(yaw)) {
                            exitYaw = Float.parseFloat(yaw);
                        } else {
                            args.getSender().sendMessage(yaw + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("epitch=")) {
                        String pitch = arg.substring(7);
                        if (NumberUtils.isNumber(pitch)) {
                            exitPitch = Float.parseFloat(pitch);
                        } else {
                            args.getSender().sendMessage(pitch + " is not a number!");
                            return;
                        }
                    } else if (arg.startsWith("w=")) {
                        String w = arg.substring(2);
                        if (Bukkit.getWorld(w) != null) {
                            world = w;
                        } else {
                            args.getSender().sendMessage(w + " world not found!");
                            return;
                        }
                    } else if (arg.startsWith("ew=")) {
                        String w = arg.substring(3);
                        if (Bukkit.getWorld(w) != null) {
                            exitWorld = w;
                        } else {
                            args.getSender().sendMessage(w + " world not found!");
                            return;
                        }
                    } else if (arg.startsWith("b=")) {
                        String b = arg.substring(2);
                        if (DungeonBaseRegistry.hasDungeonBase(b)) {
                            baseName = b;
                        } else {
                            args.getSender().sendMessage(b + " DungeonBase not found!");
                            return;
                        }
                    } else if (arg.startsWith("n=")) {
                        name = arg.substring(2);
                    }
                }
                if (world == null && args.getSender() instanceof Player) {
                    world = ((Player) args.getSender()).getWorld().getName();
                }
                if (exitWorld == null && args.getSender() instanceof Player) {
                    exitWorld = ((Player) args.getSender()).getWorld().getName();
                }

                if (minX != Integer.MIN_VALUE &&
                        minY != Integer.MIN_VALUE &&
                        minZ != Integer.MIN_VALUE &&
                        maxX != Integer.MIN_VALUE &&
                        maxY != Integer.MIN_VALUE &&
                        maxZ != Integer.MIN_VALUE &&
                        exitX != Integer.MIN_VALUE &&
                        exitY != Integer.MIN_VALUE &&
                        exitZ != Integer.MIN_VALUE &&
                        exitWorld != null &&
                        world != null &&
                        name != null &&
                        baseName != null) {
                    Vector min = new Vector(minX, minY, minZ);
                    Vector max = new Vector(maxX, maxY, maxZ);
                    region = new Region(min, max);
                    worldName = world;
                    base = DungeonBaseRegistry.getDungeonBase(baseName);
                    dungeonName = name;
                    exitLocation = new Location(Bukkit.getWorld(exitWorld), exitX, exitY + 0.5D, exitZ);
                    if (exitYaw != Float.NaN) {
                        exitLocation.setYaw(exitYaw);
                    }
                    if (exitPitch != Float.NaN) {
                        exitLocation.setPitch(exitPitch);
                    }
                } else {
                    args.getSender().sendMessage("Parameters missing!");
                }
            }

            if (region != null && worldName != null && base != null && dungeonName != null && exitLocation != null) {
                args.getSender().sendMessage("Dungeon entrance created");
                DungeonEntrance entrance = new DungeonEntrance(dungeonName, worldName, region, base, exitLocation);
                DungeonEntranceRegistry.registerEntrance(entrance);
                DungeonEntranceRegistry.saveEntrances();
            }
        }
    }
}