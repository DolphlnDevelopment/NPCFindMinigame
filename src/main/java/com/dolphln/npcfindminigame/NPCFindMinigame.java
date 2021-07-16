package com.dolphln.npcfindminigame;

import co.aikar.commands.PaperCommandManager;
import com.dolphln.npcfindminigame.commands.NPCMinigameCommand;
import com.dolphln.npcfindminigame.core.NPCManager;
import com.dolphln.npcfindminigame.files.ConfigFile;
import com.dolphln.npcfindminigame.files.DataFile;
import org.bukkit.plugin.java.JavaPlugin;

public final class NPCFindMinigame extends JavaPlugin {

    private static NPCFindMinigame instance;
    private PaperCommandManager commandManager;

    private ConfigFile configFile;
    private DataFile dataFile;

    private NPCManager npcManager;

    @Override
    public void onEnable() {
        instance = this;

        this.configFile = new ConfigFile(this);
        this.dataFile = new DataFile(this);

        this.npcManager = new NPCManager(this);

        this.commandManager.registerCommand(new NPCMinigameCommand(this));
    }

    @Override
    public void onDisable() {
        this.dataFile.save();
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

    public NPCManager getNpcManager() {
        return npcManager;
    }
}
