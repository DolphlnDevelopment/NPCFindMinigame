package com.dolphln.npcfindminigame.files;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.ArrayList;
import java.util.logging.Level;

public class ConfigFile {

    private final NPCFindMinigame plugin;

    private YamlConfiguration defaultcfg;
    private File configFile;

    private ArrayList<ConfigNPC> NPCs;

    public ConfigFile(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.NPCs = new ArrayList<>();

        setup();
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        configFile = new File(plugin.getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try {
                plugin.saveResource("config.yml", true);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create config.yml file.");
            }
        }

        defaultcfg = YamlConfiguration.loadConfiguration(configFile);

        plugin.getLogger().log(Level.FINE, "File config.yml loaded correctly.");

        this.loadNPCs();
    }

    public YamlConfiguration getConfig() {
        return defaultcfg;
    }

    public File getFile() {
        return configFile;
    }

    public void reloadConfig() {
        if (configFile.exists()) {
            defaultcfg = YamlConfiguration.loadConfiguration(configFile);
        }
    }

    private void loadNPCs() {
        this.NPCs.clear();
        for (String key : this.getConfig().getConfigurationSection("npcs").getKeys(false)) {
            String name = this.getConfig().getString("npcs." + key + ".name");
            String texture = this.getConfig().getString("npcs." + key + ".texture");
            String signature = this.getConfig().getString("npcs." + key + ".signature");
            ConfigNPC configNPC = new ConfigNPC(name, texture, signature);

            this.NPCs.add(configNPC);
        }
    }

    public ConfigNPC getRandomNPC() {
        int index = (int)(Math.random() * this.NPCs.size());
        return this.NPCs.get(index);
    }
}
