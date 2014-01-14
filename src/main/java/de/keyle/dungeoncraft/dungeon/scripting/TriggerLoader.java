/*
 * This file is part of MyPet
 *
 * Copyright (C) 2011-2014 Keyle
 * MyPet is licensed under the GNU Lesser General Public License.
 *
 * MyPet is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * MyPet is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package de.keyle.dungeoncraft.dungeon.scripting;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.dungeon.scripting.contexts.*;
import org.mozilla.javascript.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

public class TriggerLoader {
    private final Dungeon dungeon;

    public TriggerLoader(Dungeon d) {
        dungeon = d;
        initScriptEngines();
    }

    private void initScriptEngines() {

        DungeonContext dungeonContext = new DungeonContext(dungeon);
        EntityContext entityContext = new EntityContext(dungeon);
        LoggerContext loggerContext = new LoggerContext(dungeon);
        VariablesContext variablesContext = new VariablesContext();

        for (File f : dungeon.getDungeonBase().getTriggerFiles()) {
            Context cx = Context.enter();
            try {
                String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));
                ScriptableObject scriptable = new ImporterTopLevel(cx);
                Scriptable scope = cx.initStandardObjects(scriptable);
                ScriptableObject.putConstProperty(scope, "Dungeon", dungeonContext);
                ScriptableObject.putConstProperty(scope, "Entity", entityContext);
                ScriptableObject.putConstProperty(scope, "Trigger", new TriggerContext(dungeon, fileName));
                ScriptableObject.putConstProperty(scope, "Logger", loggerContext);
                ScriptableObject.putConstProperty(scope, "Variables", variablesContext);

                cx.evaluateReader(scope, new FileReader(f), fileName, 0, null);

                Function initFunction = (Function) scope.get("init", scope);
                initFunction.call(cx, scope, scope, null);

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                Context.exit();
            }
        }
    }
}