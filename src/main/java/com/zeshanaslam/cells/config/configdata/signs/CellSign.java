 package com.zeshanaslam.cells.config.configdata.signs;
 
 import com.zeshanaslam.cells.config.configdata.SafeLocation;
 
 public class CellSign
 {
   public int cellId;
   public SafeLocation location;
   
   public CellSign(int cellId, SafeLocation location) {
     this.cellId = cellId;
     this.location = location;
   }
 }


