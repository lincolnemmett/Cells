 package com.zeshanaslam.cells.listeners;
 
 import com.zeshanaslam.cells.Main;
 import com.zeshanaslam.cells.config.configdata.cells.Cell;
 import com.zeshanaslam.cells.config.configdata.cells.SafeBlock;
 import com.zeshanaslam.cells.utils.CellHelpers;
 import org.bukkit.Location;
 import org.bukkit.block.Block;
 import org.bukkit.entity.Player;
 import org.bukkit.event.EventHandler;
 import org.bukkit.event.Listener;
 import org.bukkit.event.block.BlockBreakEvent;
 import org.bukkit.event.block.BlockPlaceEvent;
 
 public class BlockListener
   implements Listener {
   private Main main;
   private CellHelpers cellHelpers;
   
   public BlockListener(Main plugin) {
     this.main = plugin;
     this.cellHelpers = new CellHelpers(this.main);
   }
   
   @EventHandler
   public void onBreak(BlockBreakEvent event) {
     Player player = event.getPlayer();
     if (event.isCancelled()) {
       return;
     }
     Block block = event.getBlock();
     Location blockLocation = block.getLocation();
     Cell cell = this.cellHelpers.getCellAtLocation(blockLocation);
     
     if (cell == null || cell.tenant == null) {
       return;
     }
     
     SafeBlock safeBlock = new SafeBlock(blockLocation.getWorld().getName(), blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ(), blockLocation.getPitch(), blockLocation.getYaw(), block.getBlockData().getAsString());
     
     if (!cell.placedBlocks.contains(safeBlock)) {
       if (cell.brokenBlocks.contains(safeBlock)) {
         cell.brokenBlocks.remove(safeBlock);
       } else {
         cell.brokenBlocks.add(safeBlock);
       } 
     } else {
       cell.placedBlocks.remove(safeBlock);
     } 
     
     this.cellHelpers.createOrUpdateCell(cell);
   }
   
   @EventHandler
   public void onPlace(BlockPlaceEvent event) {
     if (event.isCancelled()) {
       return;
     }
     Block block = event.getBlock();
     Location blockLocation = block.getLocation();
     Cell cell = this.cellHelpers.getCellAtLocation(blockLocation);
     
     if (cell == null || cell.tenant == null) {
       return;
     }
     
     SafeBlock safeBlock = new SafeBlock(blockLocation.getWorld().getName(), blockLocation.getBlockX(), blockLocation.getBlockY(), blockLocation.getBlockZ(), blockLocation.getPitch(), blockLocation.getYaw(), block.getBlockData().getAsString());
     
     if (!cell.brokenBlocks.contains(safeBlock)) {
       if (cell.placedBlocks.contains(safeBlock)) {
         cell.placedBlocks.remove(safeBlock);
       } else {
         cell.placedBlocks.add(safeBlock);
       } 
     } else {
       cell.brokenBlocks.remove(safeBlock);
     } 
     
     this.cellHelpers.createOrUpdateCell(cell);
   }
 }


