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

package de.keyle.dungeoncraft.dungeon.scripting;

import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.NativeFunction;

import java.util.ArrayList;
import java.util.List;

public class Trigger {
    private boolean isActive = true;
    private String name;
    private NativeFunction function;
    private List<String> functionParameters = new ArrayList<String>();

    public Trigger(String name, NativeFunction function) {
        this.name = name;
        this.function = function;
        for (int i = 0; i < function.getDebuggableView().getParamCount(); i++) {
            functionParameters.add(function.getDebuggableView().getParamOrVarName(i));
        }
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean isActive) {
        this.isActive = isActive;
    }

    public String getName() {
        return name;
    }

    public boolean execute(Object... parameters) {
        if (!isActive) {
            return false;
        }
        Context ctx = Context.enter();
        try {
            Object returnValue = function.call(ctx, function.getParentScope(), null, parameters);
            if (returnValue != null) {
                try {
                    return Boolean.class.cast(returnValue);
                } catch (ClassCastException e) {
                    return false;
                }
            } else {
                return false;
            }
        } finally {
            Context.exit();
        }
    }

    public List<String> getFunctionParameterNames() {
        return functionParameters;
    }
}