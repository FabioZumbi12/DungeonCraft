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
import de.keyle.dungeoncraft.entity.types.EntityType;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EntityTemplate {
    protected double maxHealth = 10;
    protected float walkSpeed = 0.3F;
    protected String displayName = "";
    protected final String templateId;
    protected final EntityType type;
    protected int exp = 0;
    protected int lootIterations = 0;
    protected int maxDrops = 0;
    protected Map<Float,ItemStack> lootTable;
    protected List<EntityTemplateComponent> components = new ArrayList<EntityTemplateComponent>();

    public EntityTemplate(String templateId, EntityType type) {
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

    public int getExp() {
        return exp;
    }

    public void setExp(int exp) {
        this.exp = exp;
    }

    public float getWalkSpeed() {
        return walkSpeed;
    }

    public void setWalkSpeed(float walkSpeed) {
        this.walkSpeed = walkSpeed;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public void addComponent(EntityTemplateComponent comonent) {
        components.add(comonent);
    }

    public List<EntityTemplateComponent> getComponents() {
        return components;
    }

    public int getLootIterations() {
        return lootIterations;
    }

    public void setLootIterations(int lootIterations) {
        this.lootIterations = lootIterations;
    }

    public int getMaxDrops() {
        return maxDrops;
    }

    public void setMaxDrops(int maxDrops) {
        this.maxDrops = maxDrops;
    }

    public Map<Float, ItemStack> getLootTable() {
        return lootTable;
    }

    public void setLootTable(Map<Float, ItemStack> lootTable) {
        this.lootTable = lootTable;
    }
}