package com.dolphln.npcfindminigame.files;

import com.dolphln.npcfindminigame.NPCFindMinigame;
import com.dolphln.npcfindminigame.models.BasicCuboid;
import com.dolphln.npcfindminigame.models.BasicLocation;
import org.bukkit.Location;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.logging.Level;


public class DataFile {

    private final NPCFindMinigame plugin;

    private YamlConfiguration dataCFG;
    private File dataFile;

    private BasicCuboid hubCuboid;

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

        BasicLocation pos1 = getLocation("hub.pos1");
        BasicLocation pos2 = getLocation("hub.pos2");
        this.hubCuboid = new BasicCuboid(pos1, pos2, pos1.getWorldName());
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

    public BasicLocation getLocation(String path) {
        int x = this.getData().getInt(path + ".x");
        int y = this.getData().getInt(path + ".y");
        int z = this.getData().getInt(path + ".z");
        String world = this.getData().getString(path + ".world");
        return new BasicLocation(x, y, z, world);
    }

    public BasicCuboid getHubCuboid() {
        return hubCuboid;
    }
}
