package com.dolphln.npcfindminigame.models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;

import java.util.ArrayList;
import java.util.List;

public class BasicCuboid {

    private final String worldName;
    private int minX;
    private int maxX;
    private int minY;
    private int maxY;
    private int minZ;
    private int maxZ;

    public BasicCuboid(BasicLocation loc1, BasicLocation loc2, String world) {
        this(world, loc1.getX(), loc1.getY(), loc1.getZ(), loc2.getX(), loc2.getY(), loc2.getZ());
    }

    public BasicCuboid(String world, int x1, int y1, int z1, int x2, int y2, int z2) {
        this.worldName = world;

        minX = Math.min(x1, x2);
        minY = Math.min(y1, y2);
        minZ = Math.min(z1, z2);
        maxX = Math.max(x1, x2);
        maxY = Math.max(y1, y2);
        maxZ = Math.max(z1, z2);
    }

    public List<BasicLocation> blockList() {
        final ArrayList<BasicLocation> bL = new ArrayList<>(this.getTotalBlockSize());
        for(int x = this.minX; x <= this.maxX; ++x) {
            for(int y = this.minY; y <= this.maxY; ++y) {
                for(int z = this.minZ; z <= this.maxZ; ++z) {
                    bL.add(new BasicLocation(x, y, z, this.worldName));
                }
            }
        }
        return bL;
    }

    public String getWorldName() {
        return worldName;
    }

    public World getWorld() {
        return Bukkit.getWorld(getWorldName());
    }

    public BasicLocation getCenter() {
        return new BasicLocation((this.maxX - this.minX) / 2 + this.minX, (this.maxY - this.minY) / 2 + this.minY, (this.maxZ - this.minZ) / 2 + this.minZ, getWorldName());
    }

    public Location getPoint1() {
        return new Location(getWorld(), this.minX, this.minY, this.minZ);
    }

    public Location getPoint2() {
        return new Location(getWorld(), this.maxX, this.maxY, this.maxZ);
    }

    public void setPoint1(int x, int y, int z) {
        this.minX = x;
        this.minY = y;
        this.minZ = z;
    }

    public void setPoint2(int x, int y, int z) {
        this.maxX = x;
        this.maxY = y;
        this.maxZ = z;
    }

    public int getTotalBlockSize() {
        return this.getHeight() * this.getXWidth() * this.getZWidth();
    }

    public int getXWidth() {
        return this.maxX - this.minX + 1;
    }

    public int getHeight() {
        return this.maxY - this.minY + 1;
    }

    public int getZWidth() {
        return this.maxZ - this.minZ + 1;
    }

    public boolean contains(Location location) {
        return contains(location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ());
    }

    public boolean contains(BasicLocation location) {
        return contains(location.getWorldName(), location.getX(), location.getY(), location.getZ());
    }

    public boolean contains(String worldName, int x, int y, int z) {
        return this.getWorldName().equals(worldName) &&
                x >= minX && x <= maxX &&
                y >= minY && y <= maxY &&
                z >= minZ && z <= maxZ;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || !(obj instanceof BasicCuboid)) {
            return false;
        }
        final BasicCuboid other = (BasicCuboid) obj;
        return worldName.equals(other.worldName)
                && minX == other.minX
                && minY == other.minY
                && minZ == other.minZ
                && maxX == other.maxX
                && maxY == other.maxY
                && maxZ == other.maxZ;
    }

    @Override
    public String toString() {
        return "Cuboid[world:" + worldName +
                ", minX:" + minX +
                ", minY:" + minY +
                ", minZ:" + minZ +
                ", maxX:" + maxX +
                ", maxY:" + maxY +
                ", maxZ:" + maxZ + "]";
    }

}
