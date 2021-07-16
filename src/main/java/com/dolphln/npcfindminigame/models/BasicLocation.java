package com.dolphln.npcfindminigame.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;

public class BasicLocation {

    private final int x;
    private final int y;
    private final int z;
    private final String worldName;

    public BasicLocation(int x, int y, int z, String worldName) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.worldName = worldName;
    }

    public BasicLocation(Location loc) {
        this.x = loc.getBlockX();
        this.y = loc.getBlockY();
        this.z = loc.getBlockZ();
        this.worldName = loc.getWorld().getName();
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getZ() {
        return z;
    }

    public String getWorldName() {
        return worldName;
    }

    public Location getLocation() {
        return new Location(Bukkit.getWorld(this.worldName), this.x, this.y, this.z);
    }
}
