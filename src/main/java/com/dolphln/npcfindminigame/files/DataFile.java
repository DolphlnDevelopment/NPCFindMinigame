package com.dolphln.npcfindminigame.files;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.logging.Level;


public class DataFile {

    private final NPCFindMinigame plugin;

    private YamlConfiguration dataCFG;
    private File dataFile;

    public DataFile(NPCFindMinigame plugin) {
        this.plugin = plugin;

        setup();
    }

    public void setup() {
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdir();
        }

        dataFile = new File(plugin.getDataFolder(), "data.yml");

        if (!dataFile.exists()) {
            try {
                plugin.saveResource("data.yml", true);
            } catch (Exception e) {
                plugin.getLogger().log(Level.SEVERE, "Could not create data.yml file.");
            }
        }

        dataCFG = YamlConfiguration.loadConfiguration(dataFile);

        plugin.getLogger().log(Level.FINE, "File data.yml loaded correctly.");
    }

    public YamlConfiguration getData() {
        return dataCFG;
    }

    public File getFile() {
        return dataFile;
    }

    public void save() {
        try {
            this.dataCFG.save(dataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        setup();
    }

    /*public void newId() {
        this.getData().set("id", this.getData().getInt("id")+1);
        this.save();
    }

    public int getId() {
        return this.getData().getInt("id");
    }*/

    public void setLocation(String path, Location loc) {
        this.getData().set(path + ".x", loc.getBlockX());
        this.getData().set(path + ".y", loc.getBlockY());
        this.getData().set(path + ".z", loc.getBlockZ());
        this.getData().set(path + ".world", loc.getWorld().getName());
    }

    public Location getLocation(String path) {
        int x = this.getData().getInt(path + ".x");
        int y = this.getData().getInt(path + ".y");
        int z = this.getData().getInt(path + ".z");
        String world = this.getData().getString(path + ".world");
        return new Location(Bukkit.getWorld(world), x, y, z);
    }
}