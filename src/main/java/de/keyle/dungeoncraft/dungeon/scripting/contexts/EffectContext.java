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

package de.keyle.dungeoncraft.dungeon.scripting.contexts;

import de.keyle.dungeoncraft.dungeon.Dungeon;
import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.BukkitUtil;
import de.keyle.dungeoncraft.util.vector.Vector;

@SuppressWarnings("unused")
public class EffectContext {
    protected final Dungeon dungeon;

    public EffectContext(Dungeon dungeon) {
        this.dungeon = dungeon;
    }

    public void playSoundEffect(String soundName, Vector pos, float volume, byte pitch) {
        playSoundEffect(soundName, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), volume, pitch);
    }

    public void playSoundEffect(String soundName, int x, int y, int z, float volume, byte pitch) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        for (DungeonCraftPlayer player : dungeon.getPlayerList()) {
            if (player.isOnline()) {
                BukkitUtil.playSoundEffect(player.getPlayer(), soundName, new Vector(x, y, z), volume, pitch);
            }
        }
    }

    public void playParticleEffect(String particelName, Vector pos, float speed, int count) {
        playParticleEffect(particelName, pos.getBlockX(), pos.getBlockY(), pos.getBlockZ(), speed, count);
    }

    public void playParticleEffect(String particelName, int x, int y, int z, float speed, int count) {
        x += dungeon.getPosition().getBlockX();
        z += dungeon.getPosition().getBlockZ();
        for (DungeonCraftPlayer player : dungeon.getPlayerList()) {
            if (player.isOnline()) {
                BukkitUtil.playParticleEffect(player.getPlayer(), new Vector(x, y, z), particelName, 0, 0, 0, speed, count);
            }
        }
    }
}