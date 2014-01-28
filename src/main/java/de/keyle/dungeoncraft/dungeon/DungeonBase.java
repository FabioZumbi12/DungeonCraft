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

import com.google.common.io.PatternFilenameFilter;
import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.entity.template.EntityTemplateLoader;
import de.keyle.dungeoncraft.entity.template.EntityTemplateRegistry;
import de.keyle.dungeoncraft.util.Colorizer;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.config.ConfigurationYaml;
import de.keyle.dungeoncraft.util.locale.Locales;
import de.keyle.dungeoncraft.util.locale.ResourceBundle;
import de.keyle.dungeoncraft.util.schematic.ISchematicReveiver;
import de.keyle.dungeoncraft.util.schematic.Schematic;
import de.keyle.dungeoncraft.util.schematic.SchematicLoader;
import de.keyle.dungeoncraft.util.vector.OrientationVector;
import org.apache.commons.lang.Validate;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.*;
import java.util.regex.Pattern;

import static org.bukkit.World.Environment;

public class DungeonBase implements ISchematicReveiver {
    int maxPlayerCount = 0;
    int minPlayerCount = 1;
    String name;
    OrientationVector spawn;
    int timeLimit = 0;
    int startTime = 1000;
    boolean timeLock = false;
    long playerLockoutTime = 60000;
    Environment environment = Environment.NORMAL;
    boolean weather = false;
    Set<String> allowedCommands;
    Map<String, ResourceBundle> locale = new HashMap<String, ResourceBundle>();

    boolean isLoading = false;
    ConfigurationSection customConfigOptions;
    EntityTemplateRegistry entityTemplateRegistry;

    WeakReference<Schematic> schematic = null;

    public DungeonBase(String name) {
        this.name = name;
        entityTemplateRegistry = new EntityTemplateRegistry();
        allowedCommands = new HashSet<String>();
        load();
    }

    public EntityTemplateRegistry getEntityTemplateRegistry() {
        return entityTemplateRegistry;
    }

    public boolean hasTimeLimit() {
        return timeLimit > 0;
    }

    public int getTimeLimit() {
        return timeLimit;
    }

    public int getMaxPlayerCount() {
        return maxPlayerCount;
    }

    public int getMinPlayerCount() {
        return minPlayerCount;
    }

    public String getName() {
        return name;
    }

    public long getPlayerLockoutTime() {
        return playerLockoutTime;
    }

    public boolean hasCustomConfigOptions() {
        return customConfigOptions != null;
    }

    public ConfigurationSection getCustomConfigOptions() {
        return customConfigOptions;
    }

    public Set<String> getAllowedCommands() {
        return Collections.unmodifiableSet(allowedCommands);
    }

    public OrientationVector getSpawn() {
        return spawn;
    }

    public Environment getEnvironment() {
        return environment;
    }

    public int getStartTime() {
        return startTime;
    }

    public boolean isTimeLocked() {
        return timeLock;
    }

    public boolean getWeather() {
        return weather;
    }

    public String getTranslation(String key, String langcode) {
        langcode = Util.cutString(langcode, 2).toLowerCase();
        ResourceBundle bundle = locale.get(langcode);
        if (bundle == null) {
            String globalTranslation = Locales.getString(key, langcode);
            if (!globalTranslation.equals(key)) {
                return globalTranslation;
            }
            bundle = locale.get("en");
        }
        if (bundle == null) {
            String globalTranslation = Locales.getString(key, langcode);
            if (!globalTranslation.equals(key)) {
                return globalTranslation;
            }
            return key;
        }
        if (bundle.containsKey(key)) {
            return Colorizer.setColors(bundle.getString(key));
        }
        return key;
    }

    public boolean isSchematicLoaded() {
        return schematic != null && schematic.get() != null;
    }

    public void loadSchematic() {
        if (!isLoading) {
            SchematicLoader schematicLoader = new SchematicLoader(this);
            schematicLoader.start();
            isLoading = true;
        }
    }

    public Schematic getSchematic() {
        if (!isSchematicLoaded()) {
            loadSchematic();
            return null;
        }
        return schematic.get();
    }

    @Override
    public File getSchematicFile() {
        return new File(getFolder(), name + ".schematic");
    }

    public File getConfigFile() {
        return new File(getFolder(), "config.yml");
    }

    public File getRegionFile() {
        return new File(getFolder(), "regions.yml");
    }

    public File getEntityTemplateFile() {
        return new File(getFolder(), "entity-templates.json");
    }

    public File getFolder() {
        return new File(DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "dungeons" + File.separator + name);
    }

    public File getLocaleFolder() {
        return new File(DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "dungeons" + File.separator + name + File.separator + "locale");
    }

    public File[] getLocaleFiles() {
        // full langcode support later -> DungeonCraft_[a-z][a-z](_[a-z][a-z])?\.properties
        File[] localeFiles = getLocaleFolder().listFiles(new PatternFilenameFilter(Pattern.compile(getName() + "_[a-z][a-z]\\.properties")));
        if (localeFiles == null) {
            localeFiles = new File[0];
        }
        return localeFiles;
    }

    public File getTriggerFolder() {
        return new File(DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath() + File.separator + "dungeons" + File.separator + name + File.separator + "trigger");
    }

    public File[] getTriggerFiles() {
        File[] triggerFiles = getTriggerFolder().listFiles(new PatternFilenameFilter(Pattern.compile("[.-_a-z0-9]+\\.js", Pattern.CASE_INSENSITIVE)));
        if (triggerFiles == null) {
            triggerFiles = new File[0];
        }
        return triggerFiles;
    }

    public synchronized void setSchematic(Schematic schematic) {
        this.schematic = new WeakReference<Schematic>(schematic);
        isLoading = false;
    }

    public void load() {
        if (getConfigFile().exists()) {
            ConfigurationYaml configurationYaml = new ConfigurationYaml(getConfigFile());
            FileConfiguration config = configurationYaml.getConfig();

            double x = config.getDouble("spawn.location.x", -1D);
            double y = config.getDouble("spawn.location.y", -1D);
            double z = config.getDouble("spawn.location.z", -1D);
            double yaw = config.getDouble("spawn.location.yaw");
            double pitch = config.getDouble("spawn.location.pitch");
            Validate.isTrue(x >= 0 && x < 1600, "The X part of the spawn location has to be between 0 and 1600");
            Validate.isTrue(y >= 0 && y < 256, "The Y part of the spawn location has to be between 0 and 256");
            Validate.isTrue(z >= 0 && z < 1600, "The Z part of the spawn location has to be between 0 and 1600");
            spawn = new OrientationVector(x, y, z, yaw, pitch);

            timeLimit = config.getInt("time.limit", 0);
            startTime = config.getInt("time.start", 1000);
            timeLock = config.getBoolean("time.lock", false);
            playerLockoutTime = config.getLong("time.lockout", 60000L);

            environment = Environment.valueOf(config.getString("world.environment", "NORMAL").toUpperCase());
            weather = config.getBoolean("world.weather", false);

            minPlayerCount = config.getInt("player.count.min", 1);
            maxPlayerCount = config.getInt("player.count.max", 0);

            allowedCommands.clear();
            for (String cmd : config.getStringList("commands.allowed")) {
                allowedCommands.add(cmd.toLowerCase());
            }

            customConfigOptions = config.getConfigurationSection("options");
        }

        for (File f : getLocaleFiles()) {
            String langcode = f.getName().replace(getName() + "_", "").replace(".properties", "").toLowerCase();
            try {
                java.util.ResourceBundle bundle = new PropertyResourceBundle(new FileInputStream(f));
                ResourceBundle newLocale = new ResourceBundle(bundle);
                locale.put(langcode, newLocale);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


        new EntityTemplateLoader(this);
    }
}