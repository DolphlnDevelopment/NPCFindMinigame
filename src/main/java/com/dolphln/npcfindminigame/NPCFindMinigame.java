package com.dolphln.npcfindminigame;

import co.aikar.commands.PaperCommandManager;
import com.dolphln.npcfindminigame.commands.NPCMinigameCommand;
import com.dolphln.npcfindminigame.core.NPCManager;
import com.dolphln.npcfindminigame.database.Database;
import com.dolphln.npcfindminigame.files.ConfigFile;
import com.dolphln.npcfindminigame.files.DataFile;
import com.dolphln.npcfindminigame.listener.JoinLeaveListener;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public final class NPCFindMinigame extends JavaPlugin {

    private static NPCFindMinigame instance;
    private PaperCommandManager commandManager;

    private ConfigFile configFile;
    private DataFile dataFile;

    private Database database;

    private NPCManager npcManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new ConfigFile(this);
        this.dataFile = new DataFile(this);

        this.database = new Database(this);

        this.npcManager = new NPCManager(this);

        this.commandManager = new PaperCommandManager(this);
        this.commandManager.registerCommand(new NPCMinigameCommand(this));

        Bukkit.getPluginManager().registerEvents(new JoinLeaveListener(this), this);
    }

    @Override
    public void onDisable() {
        this.dataFile.save();
    }

    public void runCommands(List<String> commands, Player player) {
        commands.forEach(cmd -> Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd.replace("%player%", player.getDisplayName())));
    }

    public static NPCFindMinigame getInstance() {
        return instance;
    }

    public ConfigFile getConfigFile() {
        return configFile;
    }

    public DataFile getDataFile() {
        return dataFile;
    }

    public Database getDatabase() {
        return database;
    }

    public NPCManager getNpcManager() {
        return npcManager;
    }
}
