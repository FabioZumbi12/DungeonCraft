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

package de.keyle.dungeoncraft.entity.ai;

import java.util.*;

public class AIGoalSelector {
    private Map<String, AIGoal> AIGoalMap = new HashMap<String, AIGoal>();
    private List<AIGoal> AIGoalList = new LinkedList<AIGoal>();
    private List<AIGoal> activeAIGoalList = new LinkedList<AIGoal>();

    public void addGoal(String name, AIGoal aiGoal) {
        if (AIGoalMap.containsKey(name)) {
            return;
        }
        AIGoalMap.put(name, aiGoal);
        AIGoalList.add(aiGoal);
    }

    public void addGoal(String name, int pos, AIGoal aiGoal) {
        if (AIGoalMap.containsKey(name)) {
            return;
        }
        AIGoalMap.put(name, aiGoal);
        AIGoalList.add(pos, aiGoal);
    }

    public void replaceGoal(String name, AIGoal aiGoal) {
        if (AIGoalMap.containsKey(name)) {
            AIGoal oldGoal = AIGoalMap.get(name);
            if (activeAIGoalList.contains(oldGoal)) {
                activeAIGoalList.remove(oldGoal);
                oldGoal.finish();
            }
            int index = AIGoalList.indexOf(oldGoal);
            AIGoalList.add(index, aiGoal);
            AIGoalList.remove(oldGoal);
            AIGoalMap.put(name, aiGoal);
        } else {
            addGoal(name, aiGoal);
        }
    }

    public void removeGoal(String name) {
        if (AIGoalMap.containsKey(name)) {
            AIGoal goal = AIGoalMap.get(name);
            AIGoalList.remove(goal);
            AIGoalMap.remove(name);
            if (activeAIGoalList.contains(goal)) {
                goal.finish();
            }
            activeAIGoalList.remove(goal);
        }
    }

    public boolean hasGoal(String name) {
        return AIGoalMap.containsKey(name);
    }

    public AIGoal getGoal(String name) {
        return AIGoalMap.get(name);
    }

    public void clearGoals() {
        AIGoalList.clear();
        AIGoalMap.clear();
        for (AIGoal goal : activeAIGoalList) {
            goal.finish();
        }
        activeAIGoalList.clear();
    }

    public void tick() {
        // add goals
        ListIterator iterator = AIGoalList.listIterator();
        while (iterator.hasNext()) {
            AIGoal goal = (AIGoal) iterator.next();
            if (!activeAIGoalList.contains(goal)) {
                if (goal.shouldStart()) {
                    goal.start();
                    activeAIGoalList.add(goal);
                }
            }
        }

        // remove goals
        iterator = activeAIGoalList.listIterator();
        while (iterator.hasNext()) {
            AIGoal goal = (AIGoal) iterator.next();
            if (goal.shouldFinish()) {
                goal.finish();
                iterator.remove();
            }
        }

        // tick goals
        iterator = activeAIGoalList.listIterator();
        while (iterator.hasNext()) {
            AIGoal goal = (AIGoal) iterator.next();
            goal.tick();
        }
    }
}