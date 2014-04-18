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

package de.keyle.dungeoncraft.util;

import de.keyle.dungeoncraft.party.DungeonCraftPlayer;
import de.keyle.dungeoncraft.util.locale.Locales;
import de.keyle.dungeoncraft.util.logger.DebugLogger;
import de.keyle.dungeoncraft.util.vector.Vector;
import org.apache.commons.lang.Validate;
import org.apache.commons.lang.WordUtils;

import java.text.MessageFormat;
import java.util.concurrent.TimeUnit;

public class Util {
    public static String cutString(String string, int length) {
        if (string.length() > length) {
            return string.substring(0, length);
        }
        return string;
    }

    public static String formatText(String text, Object... values) {
        return MessageFormat.format(text, values);
    }

    public static String capitalizeName(String name) {
        Validate.notNull(name, "Name can't be null");

        name = name.replace("_", " ");
        name = WordUtils.capitalizeFully(name);
        name = name.replace(" ", "");
        return name;
    }

    public static boolean isInsideCuboid(Vector point, Vector pointMin, Vector pointMax) {
        double px = point.getX();
        double py = point.getY();
        double pz = point.getZ();

        return (px >= pointMin.getX() && px <= pointMax.getX() && py >= pointMin.getY() && py <= pointMax.getY() && pz >= pointMin.getZ() && pz <= pointMax.getZ());
    }

    public static boolean isDouble(String number) {
        try {
            Double.parseDouble(number);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    public static boolean isFloat(String number) {
        try {
            Float.parseFloat(number);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    public static boolean isByte(String number) {
        try {
            Byte.parseByte(number);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    public static boolean isInt(String number) {
        try {
            Integer.parseInt(number);
            return true;
        } catch (NumberFormatException nFE) {
            return false;
        }
    }

    public static String getDurationBreakdown(long millis, DungeonCraftPlayer dungeonCraftPlayer) {
        if (millis < 0) {
            throw new IllegalArgumentException("Duration must be greater than zero!");
        }

        long days = TimeUnit.MILLISECONDS.toDays(millis);
        millis -= TimeUnit.DAYS.toMillis(days);
        long hours = TimeUnit.MILLISECONDS.toHours(millis);
        millis -= TimeUnit.HOURS.toMillis(hours);
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);

        return (Util.formatText(Locales.getString("Terms.Common.Days", dungeonCraftPlayer), days)
                + Util.formatText(Locales.getString("Terms.Common.Hours", dungeonCraftPlayer), hours)
                + Util.formatText(Locales.getString("Terms.Common.Minutes", dungeonCraftPlayer), minutes)
                + Util.formatText(Locales.getString("Terms.Common.Seconds", dungeonCraftPlayer), seconds));
    }

    public static boolean findClassInStackTrace(StackTraceElement[] stackTrace, String className) {
        return findClassInStackTrace(stackTrace, className, 0, stackTrace.length - 1, false);
    }

    public static boolean findClassInStackTrace(StackTraceElement[] stackTrace, String className, int element) {
        return findClassInStackTrace(stackTrace, className, element, element, false);
    }

    public static boolean findClassInStackTrace(StackTraceElement[] stackTrace, String className, int from, int to, boolean debug) {
        Validate.isTrue(to >= from, "\"to\" has to be >= \"from\".");
        Validate.isTrue(from >= 0, "\"from\" has to be >= 0.");
        to = Math.min(stackTrace.length - 1, to);
        if (debug) {
            DebugLogger.info("=====================================================================================================================================");
        }
        for (int i = from; i <= to; i++) {
            DebugLogger.info("  " + i + ": " + stackTrace[i].getClassName());
            if (stackTrace[i].getClassName().equals(className)) {
                if (debug) {
                    DebugLogger.info("=====================================================================================================================================");
                }
                return true;
            }
        }
        if (debug) {
            DebugLogger.info("=====================================================================================================================================");
        }
        return false;
    }
}