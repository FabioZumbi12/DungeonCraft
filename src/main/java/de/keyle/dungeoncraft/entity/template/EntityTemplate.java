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

import de.keyle.dungeoncraft.entity.types.EntityType;

import java.util.ArrayList;
import java.util.List;

public class EntityTemplate {
    protected double maxHealth;
    protected float walkSpeed = 0.3F;
    protected final String templateId;
    protected final EntityType type;
    protected List<EntityTemplateComonent> components = new ArrayList<EntityTemplateComonent>();

    public EntityTemplate(String templateId, double maxHealth, EntityType type) {
        this.maxHealth = maxHealth;
        this.templateId = templateId;
        this.type = type;
    }

    public String getTemplateId() {
        return templateId;
    }

    public EntityType getType() {
        return type;
    }

    public double getMaxHealth() {
        return maxHealth;
    }

    public void setMaxHealth(double maxHealth) {
        if (maxHealth <= 0) {
            return;
        }
        this.maxHealth = maxHealth;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public void addComponent(EntityTemplateComonent comonent) {
        components.add(comonent);
    }

    public List<EntityTemplateComonent> getComponents() {
        return components;
    }
}