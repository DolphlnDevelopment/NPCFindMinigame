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

    private final ArrayList<ConfigNPC> NPCs;
    private final ArrayList<Material> whitelistedMaterials;

    public ConfigFile(NPCFindMinigame plugin) {
        this.plugin = plugin;

        this.NPCs = new ArrayList<>();
        this.whitelistedMaterials = new ArrayList<>();

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
        this.loadMaterials();
    }

    public YamlConfiguration getConfig() {
        return defaultcfg;
    }

    public File getFile() {
        return configFile;
    }

    public void reloadConfig() {
        setup();
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

    private void loadMaterials() {
        this.whitelistedMaterials.clear();
        for (String rawMat : this.getConfig().getStringList("random_settings.whitelisted_blocks")) {
            Material mat = Material.getMaterial(rawMat);
            if (mat == null) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.translateAlternateColorCodes('&', "&cThe material &e" + rawMat + " &cis invalid. Please, check it and reload the config."));
            } else {
                this.whitelistedMaterials.add(mat);
            }
        }
    }

    public boolean isMatValid(Material mat) {
        return this.whitelistedMaterials.contains(mat);
    }

    public ConfigNPC getRandomNPC() {
        int index = (int) (Math.random() * this.NPCs.size());
        return this.NPCs.get(index);
    }
}
