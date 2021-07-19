package com.dolphln.npcfindminigame.database;

import com.dolphln.npcfindminigame.NPCFindMinigame;

import com.dolphln.npcfindminigame.database.models.PlayerDatabaseResult;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.configuration.ConfigurationSection;

import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetProvider;
import java.sql.*;
import java.util.ArrayList;
import java.util.UUID;

public class Database {

    private final NPCFindMinigame plugin;

    private HikariDataSource hikari;

    private DatabaseCache databaseCache;

    public Database(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.initialize();
        this.createTables();

        this.databaseCache = new DatabaseCache(plugin);
    }

    public void initialize() {

        ConfigurationSection databaseSection = plugin.getConfigFile().getConfig().getConfigurationSection("mysql");
        if (databaseSection == null) {
            throw new IllegalStateException("MySQL not configured correctly. Cannot continue.");
        }

        hikari = new HikariDataSource();
        hikari.setMaximumPoolSize(databaseSection.getInt("pool-size"));

        hikari.setDataSourceClassName("com.mysql.cj.jdbc.MysqlDataSource");

        hikari.addDataSourceProperty("serverName", databaseSection.getString("host"));
        hikari.addDataSourceProperty("port", databaseSection.getInt("port"));
        hikari.addDataSourceProperty("databaseName", databaseSection.getString("database"));

        hikari.addDataSourceProperty("user", databaseSection.getString("username"));
        hikari.addDataSourceProperty("password", databaseSection.getString("password"));

        hikari.validate();
    }

    private void execute(String query, Object... parameters) throws SQLException {
        Connection connection = hikari.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            statement.execute();
            connection.close();
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

    private ResultSet executeQuery(String query, Object... parameters) throws SQLException {
        Connection connection = hikari.getConnection();
        try {
            PreparedStatement statement = connection.prepareStatement(query);
            if (parameters != null) {
                for (int i = 0; i < parameters.length; i++) {
                    statement.setObject(i + 1, parameters[i]);
                }
            }

            CachedRowSet resultCached = RowSetProvider.newFactory().createCachedRowSet();
            ResultSet resultSet = statement.executeQuery();

            resultCached.populate(resultSet);
            resultSet.close();
            connection.close();
            return resultCached;
        } catch (SQLException e) {
            connection.close();
            throw e;
        }
    }

    private void createTables() {
        for (String query : Query.createTables) {
            try {
                execute(query);
            } catch (SQLException e) {
                if (e.getErrorCode() != 1050) {
                    e.printStackTrace();
                }
            }
        }
    }

    public PlayerDatabaseResult getPlayer(UUID uuid) {
        PlayerDatabaseResult player = this.databaseCache.getPlayerCache(uuid);
        if (player != null) return player;
        try {
            ResultSet rs = executeQuery(Query.getPlayer, uuid.toString());
            rs.next();
            player = new PlayerDatabaseResult(uuid, rs.getString("playerName"), rs.getInt("wins"));
            this.databaseCache.addPlayerCache(player);
            return player;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public PlayerDatabaseResult addPlayer(UUID uuid, String playerName) {
        try {
            execute(Query.addPlayer, uuid.toString(), playerName);
        } catch (SQLException e) {
            if (e.getErrorCode() == 1062 /* User is already on the database */) {
                try {
                    execute(Query.updatePlayerName, playerName, uuid.toString());
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }

        return getPlayer(uuid);
    }

    public void addWin(UUID uuid) {
        try {
            execute(Query.updateWins, uuid.toString());
            this.databaseCache.removePlayerCache(uuid);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PlayerDatabaseResult> getTopWins() {
        return this.databaseCache.getTopPlayersCache();
    }

    public ArrayList<PlayerDatabaseResult> getTopWins(int limit) {
        try {
            ResultSet rs = executeQuery(Query.getTopWins, limit);

            ArrayList<PlayerDatabaseResult> topPlayers = new ArrayList<>();
            while (rs.next()) {
                topPlayers.add(new PlayerDatabaseResult(UUID.fromString(rs.getString("uuid")), rs.getString("playerName"), rs.getInt("wins")));
            }
            return topPlayers;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return new ArrayList<>();
    }

}
