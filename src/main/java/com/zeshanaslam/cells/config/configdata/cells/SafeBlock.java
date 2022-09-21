package com.zeshanaslam.cells.config.configdata.cells;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.data.BlockData;

import java.util.Objects;

public class SafeBlock {
    public String world;
    public double x;
    public double y;
    public double z;
    public float pitch;
    public float yaw;
    public String blockData;

    public SafeBlock(String world, double x, double y, double z, float pitch, float yaw, String blockData) {
        this.world = world;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
        this.blockData = blockData;
    }


    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SafeBlock safeBlock = (SafeBlock) o;
        return (Double.compare(safeBlock.x, this.x) == 0 &&
                Double.compare(safeBlock.y, this.y) == 0 &&
                Double.compare(safeBlock.z, this.z) == 0 &&
                Float.compare(safeBlock.pitch, this.pitch) == 0 &&
                Float.compare(safeBlock.yaw, this.yaw) == 0 &&
                Objects.equals(this.world, safeBlock.world) &&
                Objects.equals(this.blockData, safeBlock.blockData));
    }


    public int hashCode() {
        return Objects.hash(this.world, this.x, this.y, this.z, this.pitch, this.yaw, this.blockData);
    }

    public BlockData getBlockData() {
        return Bukkit.getServer().createBlockData(this.blockData);
    }

    public Location getLocation() {
        Location location = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
        location.setPitch(this.pitch);
        location.setYaw(this.yaw);

        return location;
    }
}


