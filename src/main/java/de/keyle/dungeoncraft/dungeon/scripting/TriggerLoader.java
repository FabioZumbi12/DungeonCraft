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
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import sun.org.mozilla.javascript.internal.Context;
import sun.org.mozilla.javascript.internal.Function;
import sun.org.mozilla.javascript.internal.NativeFunction;

import javax.script.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.lang.reflect.InvocationTargetException;

public class TriggerLoader {
    private TriggerScript triggerScript = null;

    private final Dungeon dungeon;

    public TriggerLoader(Dungeon d) {
        dungeon = d;
        initScriptEngines();
    }

    private void initScriptEngines() {
        if (triggerScript == null) {
            ScriptEngineManager manager = new ScriptEngineManager();
            for (File f : dungeon.getDungeonBase().getTriggerFiles()) {
                ScriptEngine scriptEngine = manager.getEngineByName("js"); // JavaScript
                if (scriptEngine != null) {
                    Context.enter();
                    try {
                        final String fileName = f.getName().substring(0, f.getName().lastIndexOf("."));
                        final ScriptContext scriptContext = new SimpleScriptContext();

                        scriptContext.setAttribute("Trigger", new TriggerContext() {
                            @SuppressWarnings("unused")
                            public void registerTrigger(int id, Function function) {
                                NativeFunction f = (NativeFunction) function;
                                DebugLogger.info("register Trigger (d: " + dungeon.getDungeonName() + "): " + fileName + "_" + TriggerRegistry.getEventClassById(id).getName());
                                Trigger t = new Trigger(fileName + "_" + TriggerRegistry.getEventClassById(id).getName(), f);
                                dungeon.getTriggerRegistry().registerTrigger(id, t);
                            }

                            @Override
                            public void enableTrigger(int id) {
                                this.enableTrigger(id, fileName);
                            }

                            @Override
                            public void disableTrigger(int id) {
                                this.disableTrigger(id, fileName);
                            }
                        }, ScriptContext.ENGINE_SCOPE);
                        scriptEngine.setContext(scriptContext);

                        scriptEngine.eval(new FileReader(f));
                        if (scriptEngine instanceof Invocable) {
                            Invocable inv = (Invocable) scriptEngine;
                            triggerScript = inv.getInterface(TriggerScript.class);

                            try {
                                triggerScript.init();
                            } catch (InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        }
                    } catch (ScriptException e) {
                        e.printStackTrace();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } finally {
                        Context.exit();
                    }
                }
            }
        }
    }

    public abstract class TriggerContext {
        public abstract void registerTrigger(int id, Function function);

        public abstract void enableTrigger(int id);

        public void enableTrigger(int id, String filename) {
            for (Trigger t : dungeon.getTriggerRegistry().getTriggers(id)) {
                if (t.getName().equals(filename + "_" + TriggerRegistry.getEventClassById(id).getName())) {
                    t.setActive(true);
                }
            }
        }

        public abstract void disableTrigger(int id);

        public void disableTrigger(int id, String filename) {
            for (Trigger t : dungeon.getTriggerRegistry().getTriggers(id)) {
                if (t.getName().equals(filename + "_" + TriggerRegistry.getEventClassById(id).getName())) {
                    t.setActive(false);
                }
            }
        }
    }

    interface TriggerScript {
        public abstract void init() throws InvocationTargetException;
    }
}