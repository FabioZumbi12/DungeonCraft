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

import de.keyle.dungeoncraft.util.logger.DebugLogger;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;

import java.util.HashMap;
import java.util.Map;

public class PluginSupportManager implements Listener {

    private static PluginManager pluginManager = null;
    private static Map<String, Plugin> pluginInstances = new HashMap<String, Plugin>();
    private static Map<String, String> pluginNames = new HashMap<String, String>();
    private static Map<String, Boolean> pluginFound = new HashMap<String, Boolean>();


    public static <T extends Plugin> T getPluginInstance(Class<T> clazz) {
        if (pluginManager == null) {
            pluginManager = Bukkit.getServer().getPluginManager();
        }
        if (pluginInstances.containsKey(clazz.getName())) {
            return clazz.cast(pluginInstances.get(clazz.getName()));
        }
        for (Plugin p : pluginManager.getPlugins()) {
            if (clazz.isInstance(p)) {
                T plugin = clazz.cast(p);
                pluginInstances.put(clazz.getName(), plugin);
                pluginFound.put(clazz.getName(), true);
                pluginNames.put(plugin.getName(), clazz.getName());
                DebugLogger.info("Plugin " + plugin.getName() + " (" + clazz.getName() + ") found!");
                return plugin;
            }
        }
        pluginFound.put(clazz.getName(), false);
        return null;
    }

    public static boolean isPluginUsable(String pluginName) {
        if (pluginManager == null) {
            pluginManager = Bukkit.getServer().getPluginManager();
        }
        if (pluginFound.containsKey(pluginName)) {
            return pluginFound.get(pluginName);
        }
        if (!pluginNames.containsKey(pluginName)) {
            Plugin plugin = pluginManager.getPlugin(pluginName);
            if (plugin != null) {
                return getPluginInstance(plugin.getClass()) != null;
            } else {
                pluginFound.put(pluginName, false);
            }
            return false;
        } else {
            return true;
        }
    }

    public static void reset() {
        pluginFound.clear();
        pluginInstances.clear();
        pluginNames.clear();
        pluginManager = null;
    }

    @EventHandler
    public void onPluginDisable(final PluginDisableEvent event) {
        String pluginName = event.getPlugin().getName();
        String className = event.getPlugin().getClass().getName();

        pluginNames.remove(pluginName);
        pluginFound.remove(className);
        pluginInstances.remove(className);
    }
}
