package com.dolphln.npcfindminigame.database;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import com.dolphln.npcfindminigame.database.models.PlayerDatabaseResult;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.UUID;

public class DatabaseCache {

    private final NPCFindMinigame plugin;

    private HashMap<UUID, PlayerDatabaseResult> databasePlayersCache;
    private ArrayList<PlayerDatabaseResult> topPlayersCache;

    public DatabaseCache(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.databasePlayersCache = new HashMap<>();
        this.topPlayersCache = new ArrayList<>();

        new BukkitRunnable() {
            @Override
            public void run() {
                topPlayersCache = plugin.getDatabase().getTopWins(10);
            }
        }.runTaskTimerAsynchronously(plugin, 15*20L, 30*20L);
    }

    public void addPlayerCache(PlayerDatabaseResult playerDatabaseResult) {
        databasePlayersCache.put(playerDatabaseResult.uuid(), playerDatabaseResult);
    }

    public void removePlayerCache(UUID uuid) {
        databasePlayersCache.remove(uuid);
    }

    public PlayerDatabaseResult getPlayerCache(UUID uuid) {
        return databasePlayersCache.get(uuid);
    }

    public ArrayList<PlayerDatabaseResult> getTopPlayersCache() {
        return (ArrayList<PlayerDatabaseResult>) topPlayersCache.clone();
    }
}
