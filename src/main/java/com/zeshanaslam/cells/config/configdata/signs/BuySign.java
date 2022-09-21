 package com.zeshanaslam.cells.config.configdata.signs;
 
 import com.zeshanaslam.cells.config.configdata.SafeLocation;
 import java.util.UUID;
 
 
 public class BuySign
 {
   public int cellId;
   public SafeLocation location;
   public SafeLocation chest;
   public long bought;
   public UUID owner;
   public int price;
   public UUID cellOwner;
   
   public BuySign(int cellId, SafeLocation location, SafeLocation chest, long bought, UUID owner, int price, UUID cellOwner) {
     this.cellId = cellId;
     this.location = location;
     this.chest = chest;
     this.bought = bought;
     this.owner = owner;
     this.price = price;
     this.cellOwner = cellOwner;
   }
 }


