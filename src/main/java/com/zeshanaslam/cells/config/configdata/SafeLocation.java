 package com.zeshanaslam.cells.config.configdata;
 
 import java.util.UUID;
 import org.bukkit.Bukkit;
 import org.bukkit.Location;
 
 public class SafeLocation
 {
   public UUID world;
   public double x;
   public double y;
   public double z;
   public float pitch;
   public float yaw;
   
   public SafeLocation() {}
   
   public SafeLocation(UUID world, double x, double y, double z, float pitch, float yaw) {
     this.world = world;
     this.x = x;
     this.y = y;
     this.z = z;
     this.pitch = pitch;
     this.yaw = yaw;
   }
   
   public Location getLocation() {
     Location location = new Location(Bukkit.getWorld(this.world), this.x, this.y, this.z);
     location.setPitch(this.pitch);
     location.setYaw(this.yaw);
     
     return location;
   }
   
   public SafeLocation fromLocation(Location location) {
     this.world = location.getWorld().getUID();
     this.x = location.getBlockX();
     this.y = location.getBlockY();
     this.z = location.getBlockZ();
     this.pitch = location.getPitch();
     this.yaw = location.getYaw();
     
     return this;
   }
 }


