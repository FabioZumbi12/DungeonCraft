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

package de.keyle.dungeoncraft.util.SQLite;

import de.keyle.dungeoncraft.DungeonCraftPlugin;
import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;
import org.sqlite.SQLiteConfig;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class SQLiteConnection {

    private static Connection connection = null;

    public static Connection getConnection() {
        if (connection != null) {
            return connection;
        } else {
            SQLiteConfig config = new SQLiteConfig();
            config.enforceForeignKeys(true);
            String driver = "org.sqlite.JDBC";
            String datafolder = DungeonCraftPlugin.getPlugin().getDataFolder().getAbsolutePath();
            String url = "jdbc:sqlite:" + datafolder + File.separator + "dungeoncraft.db";
            try {
                Class.forName(driver);
            } catch (ClassNotFoundException e) {
                DungeonCraftLogger.write("No SQLite driver found!");
                return null;
            }
            try {
                connection = DriverManager.getConnection(url, config.toProperties());
                return connection;
            } catch (SQLException e) {
                DungeonCraftLogger.write("Can't get a connection");
                connection = null;
            }
            return null;
        }
    }
}
