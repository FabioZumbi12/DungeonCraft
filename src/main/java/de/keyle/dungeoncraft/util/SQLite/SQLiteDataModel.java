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

import de.keyle.dungeoncraft.util.logger.DungeonCraftLogger;

import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SQLiteDataModel {

    private static Connection connection;

    private static final String PLAYER_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS DungeoncraftPlayers " +
            "(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
            " playername TEXT NOT NULL);";

    private static final String DUNGEON_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS DungeoncraftDungeons " +
            "(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
            " dungeonname TEXT NOT NULL);";

    private static final String COOLDOWN_TABLE_CREATE = "CREATE TABLE IF NOT EXISTS DungeoncraftCooldowns " +
            "(ID INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT ," +
            " FK_PLAYER INTEGER NOT NULL, " +
            " FK_DUNGEON INTEGER NOT NULL, " +
            " cooldown INTEGER NOT NULL, " +
            " FOREIGN KEY( FK_PLAYER ) REFERENCES DungeoncraftPlayers( ID )" +
            " FOREIGN KEY( FK_DUNGEON ) REFERENCES DungeoncraftDungeons( ID ));";

    private static final String GET_ALL_DUNGEONS = "SELECT dungeonname FROM DungeoncraftDungeons;";
    private static final String GET_DUNGEON = "SELECT * FROM DungeoncraftDungeons WHERE dungeonname = ?;";
    private static final String GET_DUNGEON_BY_ID = "SELECT dungeonname FROM DungeoncraftDungeons WHERE ID = ?;";
    private static final String INSERT_DUNGEON = "INSERT INTO DungeoncraftDungeons (dungeonname) VALUES (?);";
    private static final String GET_PLAYER = "SELECT * FROM DungeoncraftPlayers WHERE playername = ?;";
    private static final String INSERT_PLAYER = "INSERT INTO DungeoncraftPlayers (playername) VALUES (?);";
    private static final String INSERT_COOLDOWN = "INSERT INTO DungeoncraftCooldowns (FK_PLAYER, FK_DUNGEON, cooldown) VALUES (?,?,?);";
    private static final String GET_COOLDOWNS = "SELECT * FROM DungeoncraftCooldowns WHERE FK_PLAYER = ?;";
    private static final String DELETE_COOLDOWN = "DELETE FROM DungeoncraftCooldowns WHERE FK_PLAYER = ? AND FK_DUNGEON =?;";

    public static void setupSQLiteDB() {
        connection = SQLiteConnection.getConnection();
        try {
            Statement statement = connection.createStatement();
            statement.executeUpdate(PLAYER_TABLE_CREATE);
            statement.executeUpdate(DUNGEON_TABLE_CREATE);
            statement.executeUpdate(COOLDOWN_TABLE_CREATE);
            statement.close();
        } catch (Exception e) {
            DungeonCraftLogger.write("Error creating tables!");
            e.printStackTrace();
        }
    }

    public static List<String> getAllDungeons() {
        List<String> returnList = new ArrayList<String>();
        connection = SQLiteConnection.getConnection();
        PreparedStatement getAllDungeonsStatement = null;
        ResultSet getAllDungeonsResult = null;
        try {
            getAllDungeonsStatement = connection.prepareStatement(GET_ALL_DUNGEONS);
            getAllDungeonsResult = getAllDungeonsStatement.executeQuery();
            while (getAllDungeonsResult.next()) {
                returnList.add(getAllDungeonsResult.getString("dungeonname"));
            }
        } catch (Exception e) {
            DungeonCraftLogger.write("Error getting all dungeons");
            e.printStackTrace();
        } finally {
            try {
                if (getAllDungeonsResult != null) {
                    getAllDungeonsResult.close();
                }
                if (getAllDungeonsStatement != null) {
                    getAllDungeonsStatement.close();
                }
            } catch (SQLException e) {
                DungeonCraftLogger.write("Error closing statement in getAllDungeons");
                e.printStackTrace();
            }
        }
        return returnList;
    }

    private static boolean containsDungeon(String dungeonName) {
        connection = SQLiteConnection.getConnection();
        PreparedStatement getDungeonStatement = null;
        ResultSet getDungeonResult = null;
        boolean contains = true;
        try {
            getDungeonStatement = connection.prepareStatement(GET_DUNGEON);
            getDungeonStatement.setString(1, dungeonName);
            getDungeonResult = getDungeonStatement.executeQuery();
            contains = getDungeonResult.next();
        } catch (Exception e) {
            DungeonCraftLogger.write("Error getting dungeon " + dungeonName);
            e.printStackTrace();
        } finally {
            try {
                if(getDungeonResult != null) {
                    getDungeonResult.close();
                }
                if(getDungeonStatement != null) {
                    getDungeonStatement.close();
                }
            } catch (SQLException e) {
                DungeonCraftLogger.write("Error closing statements in containsDungeon");
                e.printStackTrace();
            }
        }
        return contains;
    }

    public static void addDungeon(String dungeonName) {
        if (!containsDungeon(dungeonName)) {
            connection = SQLiteConnection.getConnection();
            PreparedStatement insertDungeonStatement = null;
            try {
                insertDungeonStatement = connection.prepareStatement(INSERT_DUNGEON);
                insertDungeonStatement.setString(1, dungeonName);
                insertDungeonStatement.execute();
            } catch (Exception e) {
                DungeonCraftLogger.write("Error inserting dungeon " + dungeonName);
                e.printStackTrace();
            } finally {
                try {
                    if(insertDungeonStatement != null) {
                        insertDungeonStatement.close();
                    }
                } catch (SQLException e) {
                    DungeonCraftLogger.write("Error closing statement in addDungeon");
                    e.printStackTrace();
                }
            }
        }
    }

    private static boolean containsPlayer(String playerName) {
        connection = SQLiteConnection.getConnection();
        boolean contains = true;
        PreparedStatement getPlayerStatement = null;
        ResultSet getPlayerResult = null;
        try {
            getPlayerStatement = connection.prepareStatement(GET_PLAYER);
            getPlayerStatement.setString(1, playerName);
            getPlayerResult = getPlayerStatement.executeQuery();
            contains = getPlayerResult.next();
        } catch (Exception e) {
            DungeonCraftLogger.write("Error getting player " + playerName);
            e.printStackTrace();
        } finally {
            try {
                if(getPlayerResult != null) {
                    getPlayerResult.close();
                }
                if(getPlayerStatement != null) {
                    getPlayerStatement.close();
                }
            } catch (SQLException e) {
                DungeonCraftLogger.write("Error closing statements in containsPlayer");
                e.printStackTrace();
            }
        }
        return contains;
    }

    public static void addPlayer(String playerName) {
        if (!containsPlayer(playerName)) {
            connection = SQLiteConnection.getConnection();
            PreparedStatement insertPlayerStatement = null;
            try {
                insertPlayerStatement = connection.prepareStatement(INSERT_PLAYER);
                insertPlayerStatement.setString(1, playerName);
                insertPlayerStatement.execute();
            } catch (Exception e) {
                DungeonCraftLogger.write("Error inserting player " + playerName);
                e.printStackTrace();
            } finally {
                try {
                    if(insertPlayerStatement != null) {
                        insertPlayerStatement.close();
                    }
                } catch (SQLException e) {
                    DungeonCraftLogger.write("Error closing statement in addPlayer");
                    e.printStackTrace();
                }
            }
        }
    }

    private static int getPlayerID(String playerName) {
        int id = -1;
        if (containsPlayer(playerName)) {
            connection = SQLiteConnection.getConnection();
            PreparedStatement getPlayerStatement = null;
            ResultSet getPlayerResult = null;
            try {
                getPlayerStatement = connection.prepareStatement(GET_PLAYER);
                getPlayerStatement.setString(1, playerName);
                getPlayerResult = getPlayerStatement.executeQuery();
                getPlayerResult.next();
                id = getPlayerResult.getInt("ID");
            } catch (Exception e) {
                DungeonCraftLogger.write("Error getting player ID" + playerName);
                e.printStackTrace();
            } finally {
                try {
                    if(getPlayerResult != null) {
                        getPlayerResult.close();
                    }
                    if(getPlayerStatement != null) {
                        getPlayerStatement.close();
                    }
                } catch (SQLException e) {
                    DungeonCraftLogger.write("Error closing statement in getPlayerID");
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

    private static int getDungeonID(String dungeonName) {
        int id = -1;
        if (containsDungeon(dungeonName)) {
            connection = SQLiteConnection.getConnection();
            PreparedStatement getDungeonStatement = null;
            ResultSet getDungeonResult = null;
            try {
                getDungeonStatement = connection.prepareStatement(GET_DUNGEON);
                getDungeonStatement.setString(1, dungeonName);
                getDungeonResult = getDungeonStatement.executeQuery();
                getDungeonResult.next();
                id = getDungeonResult.getInt("ID");
            } catch (Exception e) {
                DungeonCraftLogger.write("Error getting dungeon ID" + dungeonName);
                e.printStackTrace();
            } finally {
                try {
                    if (getDungeonResult != null) {
                        getDungeonResult.close();
                    }
                    if (getDungeonStatement != null) {
                        getDungeonStatement.close();
                    }
                } catch (SQLException e) {
                    DungeonCraftLogger.write("Error closing statements in getDungeonID");
                    e.printStackTrace();
                }
            }
        }
        return id;
    }

    public static void addCooldown(String playerName, String dungeonName, long cooldown) {
        int playerID = getPlayerID(playerName);
        int dungeonID = getDungeonID(dungeonName);
        PreparedStatement insertCooldownStatement = null;
        if (playerID != -1 && dungeonID != -1) {
            connection = SQLiteConnection.getConnection();
            try {
                insertCooldownStatement = connection.prepareStatement(INSERT_COOLDOWN);
                insertCooldownStatement.setInt(1, playerID);
                insertCooldownStatement.setInt(2, dungeonID);
                insertCooldownStatement.setLong(3, cooldown);
                insertCooldownStatement.execute();
            } catch (Exception e) {
                DungeonCraftLogger.write("Error inserting cooldown");
                e.printStackTrace();
            } finally {
                try {
                    if(insertCooldownStatement != null) {
                        insertCooldownStatement.close();
                    }
                } catch (SQLException e) {
                    DungeonCraftLogger.write("Error closing statement in addCooldown");
                    e.printStackTrace();
                }
            }
        }
    }

    private static String getDungeonByID(int id) {
        connection = SQLiteConnection.getConnection();
        String dungeonName = null;
        PreparedStatement getDungeonStatement = null;
        ResultSet getDungeonResult = null;
        try {
            getDungeonStatement = connection.prepareStatement(GET_DUNGEON_BY_ID);
            getDungeonStatement.setInt(1, id);
            getDungeonResult = getDungeonStatement.executeQuery();
            getDungeonResult.next();
            dungeonName = getDungeonResult.getString("dungeonname");
        } catch (Exception e) {
            DungeonCraftLogger.write("Error getting dungeon with ID " + id);
            e.printStackTrace();
        } finally {
            try {
                if(getDungeonResult != null) {
                    getDungeonResult.close();
                }
                if(getDungeonStatement != null) {
                    getDungeonStatement.close();
                }
            } catch (SQLException e) {
                DungeonCraftLogger.write("Error closing statement in getDungeonByID");
                e.printStackTrace();
            }
        }
        return dungeonName;
    }

    public static Map<String, Long> getCooldowns(String playerName) {
        int playerID = getPlayerID(playerName);
        Map<String, Long> returnMap = new HashMap<String, Long>();
        PreparedStatement getCooldownsStatement = null;
        ResultSet getCooldownsResult = null;
        if (playerID != -1) {
            connection = SQLiteConnection.getConnection();
            try {
                getCooldownsStatement = connection.prepareStatement(GET_COOLDOWNS);
                getCooldownsStatement.setInt(1, playerID);
                getCooldownsResult = getCooldownsStatement.executeQuery();
                while (getCooldownsResult.next()) {
                    returnMap.put(getDungeonByID(getCooldownsResult.getInt("FK_DUNGEON")), getCooldownsResult.getLong("cooldown"));
                }
            } catch (Exception e) {
                DungeonCraftLogger.write("Error getting cooldown");
                e.printStackTrace();
            } finally {
                try {
                    if(getCooldownsResult != null) {
                        getCooldownsResult.close();
                    }
                    if(getCooldownsStatement != null) {
                        getCooldownsStatement.close();
                    }
                } catch (SQLException e) {
                    DungeonCraftLogger.write("Error closing statements in getCooldowns");
                    e.printStackTrace();
                }
            }
        }
        return returnMap;
    }

    public static void deleteCooldown(String playerName, String dungeonName) {
        int playerID = getPlayerID(playerName);
        int dungeonID = getDungeonID(dungeonName);
        connection = SQLiteConnection.getConnection();
        PreparedStatement deleteCooldownStatement = null;
        try {
            deleteCooldownStatement = connection.prepareStatement(DELETE_COOLDOWN);
            deleteCooldownStatement.setInt(1, playerID);
            deleteCooldownStatement.setInt(2, dungeonID);
            deleteCooldownStatement.executeUpdate();
        } catch (Exception e) {
            DungeonCraftLogger.write("Error deleting cooldown for dungeon " + dungeonName + "from player " + playerName);
            e.printStackTrace();
        } finally {
            try {
                if(deleteCooldownStatement != null) {
                    deleteCooldownStatement.close();
                }
            } catch (SQLException e) {
                DungeonCraftLogger.write("Error closing statements in deleteCooldown");
                e.printStackTrace();
            }
        }
    }
}
