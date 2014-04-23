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

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import de.keyle.dungeoncraft.api.util.Scheduler;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.Util;
import de.keyle.dungeoncraft.util.locale.Locales;

import java.util.ArrayDeque;
import java.util.Queue;

public class DungeonLoaderQueue implements Scheduler {
    private final Queue<DungeonLoader> loaderQueue = new ArrayDeque<DungeonLoader>();
    private final BiMap<Dungeon, DungeonLoader> dungeonToLoaderMap = HashBiMap.create();
    private final BiMap<DungeonLoader, Dungeon> loaderToDungeonMap = dungeonToLoaderMap.inverse();
    private volatile DungeonLoader runningLoader = null;

    public DungeonLoader loadDungeon(Dungeon dungeon) {
        if (dungeonToLoaderMap.containsKey(dungeon)) {
            return dungeonToLoaderMap.get(dungeon);
        }
        DungeonLoader loader = new DungeonLoader(dungeon);
        dungeonToLoaderMap.put(dungeon, loader);
        loaderQueue.add(loader);
        return loader;
    }

    public boolean isOnQueue(Dungeon dungeon) {
        return dungeonToLoaderMap.containsKey(dungeon);
    }

    public DungeonLoader getLoader(Dungeon dungeon) {
        return dungeonToLoaderMap.get(dungeon);
    }

    @Override
    public void schedule() {
        if (runningLoader == null) {
            if (loaderQueue.size() > 0) {
                DungeonLoader newLoader = loaderQueue.poll();
                loaderToDungeonMap.remove(newLoader);
                runningLoader = newLoader;
                newLoader.start();

                // notify party leaders about their position int the queue
                int place = 0;
                for (DungeonLoader loader : loaderQueue) {
                    Dungeon dungeon = loader.getDungeon();
                    DungeonCraftPlayer player = dungeon.getPlayerParty().getPartyLeader();
                    place++;
                    if (player.isOnline()) {
                        player.sendMessage(Util.formatText(Locales.getString("Message.Dungeon.Loader.Queue.Update", player), place));
                    }
                }
            }
        } else {
            if (runningLoader.isFinished()) {
                runningLoader = null;
            }
        }
    }
}