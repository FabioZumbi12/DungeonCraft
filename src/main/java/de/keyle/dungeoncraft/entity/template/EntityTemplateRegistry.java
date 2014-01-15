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

import de.keyle.dungeoncraft.entity.template.basic.*;
import de.keyle.dungeoncraft.util.logger.DebugLogger;

import java.util.HashMap;
import java.util.Map;

public class EntityTemplateRegistry {
    Map<String, EntityTemplate> registeredTemplates = new HashMap<String, EntityTemplate>();

    public EntityTemplateRegistry() {
        registerTemplate(new BatTemplate());
        registerTemplate(new BlazeTemplate());
        registerTemplate(new CaveSpiderTemplate());
        registerTemplate(new ChickenTemplate());
        registerTemplate(new CowTemplate());
        registerTemplate(new CreeperTemplate());
        registerTemplate(new EndermanTemplate());
        registerTemplate(new GhastTemplate());
        registerTemplate(new GiantTemplate());
        registerTemplate(new HorseTemplate());
        registerTemplate(new IronGolemTemplate());
        registerTemplate(new MagmaCubeTemplate());
        registerTemplate(new MooshroomTemplate());
        registerTemplate(new OcelotTemplate());
        registerTemplate(new PigTemplate());
        registerTemplate(new PigZombieTemplate());
        registerTemplate(new SheepTemplate());
        registerTemplate(new SilverfishTemplate());
        registerTemplate(new SkeletonTemplate());
        registerTemplate(new SlimeTemplate());
        registerTemplate(new SnowmanTemplate());
        registerTemplate(new SpiderTemplate());
        registerTemplate(new SquidTemplate());
        registerTemplate(new VillagerTemplate());
        registerTemplate(new WitchTemplate());
        registerTemplate(new WitherTemplate());
        registerTemplate(new WolfTemplate());
        registerTemplate(new ZombieTemplate());
    }

    public void registerTemplate(EntityTemplate template) {
        if (!registeredTemplates.containsKey(template.getTemplateId())) {
            registeredTemplates.put(template.getTemplateId(), template);
            return;
        }
        DebugLogger.warning("Entity Template with id \"" + template.getTemplateId() + "\" is already registered!");
    }

    public EntityTemplate getTemplate(String templateId) {
        return registeredTemplates.get(templateId);
    }
}