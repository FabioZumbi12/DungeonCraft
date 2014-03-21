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

package de.keyle.dungeoncraft.entity.template;

import de.keyle.dungeoncraft.api.entity.components.EntityTemplateComponent;
import de.keyle.dungeoncraft.api.entity.components.Parameter;
import de.keyle.dungeoncraft.dungeon.DungeonBase;
import de.keyle.dungeoncraft.entity.types.EntityType;
import de.keyle.dungeoncraft.util.ParsedItem;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.config.ConfigurationJson;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import net.minecraft.util.org.apache.commons.lang3.math.NumberUtils;
import org.bukkit.inventory.ItemStack;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityTemplateLoader {
    private final DungeonBase dungeonBase;

    public EntityTemplateLoader(DungeonBase d) {
        dungeonBase = d;
        loadEntityTemplates();
    }

    private void loadEntityTemplates() {
        if (dungeonBase.getEntityTemplateFile().exists()) {
            ConfigurationJson jsonConfig = new ConfigurationJson(dungeonBase.getEntityTemplateFile());
            jsonConfig.load();

            JSONObject root = jsonConfig.getJSONObject();

            if (root.containsKey("templates")) {
                JSONArray templates = (JSONArray) root.get("templates");

                for (Object templateObject : templates) {
                    if (templateObject instanceof JSONObject) {
                        JSONObject template = (JSONObject) templateObject;
                        if (!template.containsKey("template-id") || !template.containsKey("entity-type")) {
                            DebugLogger.warning("\"template-id\" node is missing in a template!", "DC]b:[" + dungeonBase.getName());
                            continue;
                        }
                        String templateId = (String) template.get("template-id");
                        String entityTypeName = (String) template.get("entity-type");
                        EntityType entityType = EntityType.getEntityTypeByName(entityTypeName);
                        if (entityType == null) {
                            DebugLogger.warning("\"entity-type\" node is missing in template with id: " + templateId, "DC]b:[" + dungeonBase.getName());
                            continue;
                        }
                        EntityTemplate newEntityTemplate = new EntityTemplate(templateId, entityType);

                        if (template.containsKey("max-health") && Util.isDouble(template.get("max-health").toString())) {
                            double maxHealth = Double.parseDouble(template.get("max-health").toString());
                            newEntityTemplate.setMaxHealth(maxHealth);
                        }
                        if (template.containsKey("walk-speed") && Util.isDouble(template.get("walk-speed").toString())) {
                            float walkSpeed = Float.parseFloat(template.get("walk-speed").toString());
                            newEntityTemplate.setWalkSpeed(walkSpeed);
                        }
                        if (template.containsKey("display-name")) {
                            String displayName = template.get("display-name").toString();
                            newEntityTemplate.setDisplayName(displayName);
                        }
                        if (template.containsKey("exp")) {
                            String exp = template.get("exp").toString();
                            newEntityTemplate.setExp(Integer.parseInt(exp));
                        }
                        if (template.containsKey("loot-iterations")) {
                            int lootIterations = Integer.parseInt(template.get("loot-iterations").toString());
                            newEntityTemplate.setLootIterations(lootIterations);
                        }

                        if (template.containsKey("max-drops")) {
                            int maxDrops = Integer.parseInt(template.get("max-drops").toString());
                            newEntityTemplate.setMaxDrops(maxDrops);
                        }

                        if (template.containsKey("loot-table")) {
                            Object lootObjcets = template.get("loot-table");
                            if (lootObjcets instanceof JSONArray) {
                                JSONArray lootArray = (JSONArray) lootObjcets;
                                Map<Float, ItemStack> newLootTable = new HashMap<Float, ItemStack>();
                                for (Object lootObject : lootArray) {
                                    if (lootObject instanceof JSONObject) {
                                        newLootTable.put(Float.parseFloat(((JSONObject) lootObject).get("dropchance").toString()), ParsedItem.parsedItem((JSONObject) lootObject).getBukkitItem());
                                    }
                                }
                                newEntityTemplate.setLootTable(newLootTable);
                            }
                        }

                        if (template.containsKey("components")) {
                            Object componentsObject = template.get("components");
                            if (componentsObject instanceof JSONArray) {
                                JSONArray componentsArray = (JSONArray) componentsObject;
                                for (Object componentObject : componentsArray) {
                                    if (componentObject instanceof JSONObject) {
                                        JSONObject component = (JSONObject) componentObject;
                                        if (!component.containsKey("class")) {
                                            DebugLogger.warning("Component \"class\" node is missing in template with id: " + templateId, "DC]b:[" + dungeonBase.getName());
                                            continue;
                                        }
                                        int parameterCount = 0;
                                        JSONObject parameter = null;
                                        if (component.containsKey("parameter")) {
                                            Object parameterObject = component.get("parameter");
                                            if (parameterObject instanceof JSONObject) {
                                                parameter = (JSONObject) parameterObject;
                                                parameterCount = parameter.size();
                                            }
                                        }
                                        if (parameter == null) {
                                            parameter = new JSONObject();
                                        }
                                        String className = (String) component.get("class");
                                        try {
                                            Class componentClass = Class.forName(className);
                                            Constructor[] constructors = componentClass.getConstructors();

                                            cLoop:
                                            for (Constructor c : constructors) {
                                                if (c.getParameterTypes().length != parameterCount) {
                                                    continue;
                                                }
                                                List<Object> constructorValues = new ArrayList<Object>();

                                                aLoop:
                                                for (int i = 0; i < c.getParameterAnnotations().length; i++) {
                                                    Annotation[] parameterAnnotations = c.getParameterAnnotations()[i];
                                                    for (Annotation a : parameterAnnotations) {
                                                        if (a.annotationType().equals(Parameter.class)) {
                                                            Parameter p = (Parameter) a;
                                                            if (p.type() == Parameter.Type.Boolean && parameter.containsKey(p.name())) {
                                                                if (c.getParameterTypes()[i].equals(boolean.class) || c.getParameterTypes()[i].equals(Boolean.class)) {
                                                                    constructorValues.add(Boolean.parseBoolean(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                }
                                                            } else if (p.type() == Parameter.Type.String && parameter.containsKey(p.name())) {
                                                                if (c.getParameterTypes()[i].equals(String.class)) {
                                                                    constructorValues.add(parameter.get(p.name()).toString());
                                                                    continue aLoop;
                                                                }
                                                            } else if (p.type() == Parameter.Type.Number && parameter.containsKey(p.name())) {
                                                                if (c.getParameterTypes()[i].equals(double.class) || c.getParameterTypes()[i].equals(Double.class)) {
                                                                    if (!NumberUtils.isNumber(parameter.get(p.name()).toString())) {
                                                                        continue cLoop;
                                                                    }
                                                                    constructorValues.add(Double.parseDouble(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                } else if (c.getParameterTypes()[i].equals(int.class) || c.getParameterTypes()[i].equals(Integer.class)) {
                                                                    if (!NumberUtils.isNumber(parameter.get(p.name()).toString())) {
                                                                        continue cLoop;
                                                                    }
                                                                    constructorValues.add(Integer.parseInt(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                } else if (c.getParameterTypes()[i].equals(float.class) || c.getParameterTypes()[i].equals(Float.class)) {
                                                                    if (!NumberUtils.isNumber(parameter.get(p.name()).toString())) {
                                                                        continue cLoop;
                                                                    }
                                                                    constructorValues.add(Float.parseFloat(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                } else if (c.getParameterTypes()[i].equals(byte.class) || c.getParameterTypes()[i].equals(Byte.class)) {
                                                                    if (!NumberUtils.isNumber(parameter.get(p.name()).toString())) {
                                                                        continue cLoop;
                                                                    }
                                                                    constructorValues.add(Byte.parseByte(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                } else if (c.getParameterTypes()[i].equals(short.class) || c.getParameterTypes()[i].equals(Short.class)) {
                                                                    if (!NumberUtils.isNumber(parameter.get(p.name()).toString())) {
                                                                        continue cLoop;
                                                                    }
                                                                    constructorValues.add(Short.parseShort(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                } else if (c.getParameterTypes()[i].equals(long.class) || c.getParameterTypes()[i].equals(Long.class)) {
                                                                    if (!NumberUtils.isNumber(parameter.get(p.name()).toString())) {
                                                                        continue cLoop;
                                                                    }
                                                                    constructorValues.add(Long.parseLong(parameter.get(p.name()).toString()));
                                                                    continue aLoop;
                                                                }
                                                            } else if (p.type() == Parameter.Type.JsonObject && parameter.containsKey(p.name())) {
                                                                if (c.getParameterTypes()[i].equals(JSONObject.class)) {
                                                                    constructorValues.add(parameter.get(p.name()));
                                                                    continue aLoop;
                                                                }
                                                            } else if (p.type() == Parameter.Type.JsonArray && parameter.containsKey(p.name())) {
                                                                if (c.getParameterTypes()[i].equals(JSONArray.class)) {
                                                                    constructorValues.add(parameter.get(p.name()));
                                                                    continue aLoop;
                                                                }
                                                            }
                                                            continue cLoop;
                                                        }
                                                    }
                                                    continue cLoop;
                                                }
                                                Object componentInstance;
                                                try {
                                                    c.setAccessible(true);
                                                    componentInstance = c.newInstance(constructorValues.toArray(new Object[constructorValues.size()]));
                                                } catch (Exception e) {
                                                    continue;
                                                }
                                                if (componentInstance instanceof EntityTemplateComponent) {
                                                    newEntityTemplate.addComponent((EntityTemplateComponent) componentInstance);
                                                }
                                                break;
                                            }
                                        } catch (ClassNotFoundException e) {
                                            DebugLogger.warning("Can not find class: " + className, "DC]b:[" + dungeonBase.getName());
                                        }
                                    }
                                }
                            }
                        }
                        dungeonBase.getEntityTemplateRegistry().registerTemplate(newEntityTemplate);
                    }
                }
            } else {
                DebugLogger.warning("Root node (\"templates\") is missing \"entity-templates.json\"!", "DC]b:[" + dungeonBase.getName());
            }
        }
    }
}