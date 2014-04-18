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

package de.keyle.dungeoncraft.api.entity.components;

import de.keyle.dungeoncraft.entity.types.EntityDungeonCraft;

public abstract class EntityTemplateComponent implements Cloneable {
    EntityDungeonCraft owner;

    public boolean canTick() {
        return true;
    }

    public final void tick() {
        if (canTick()) {
            onTick();
        }
    }

    public void onTick() {
    }

    public boolean attachTo(EntityDungeonCraft owner) {
        EntityTemplateComponent newComponent = clone();
        newComponent.owner = owner;
        owner.components.add(newComponent);
        newComponent.onAttached();
        return true;
    }

    public abstract void onAttached();

    public EntityDungeonCraft getOwner() {
        if (owner == null) {
            throw new IllegalStateException("Trying to access the owner of this component before it was attached");
        }
        return owner;
    }

    public abstract EntityTemplateComponent clone();
}