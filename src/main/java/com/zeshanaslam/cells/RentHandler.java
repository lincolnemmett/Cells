package com.zeshanaslam.cells;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.zeshanaslam.cells.config.configdata.cells.Cell;
import com.zeshanaslam.cells.config.configdata.cells.SafeBlock;
import com.zeshanaslam.cells.config.configdata.signs.BuySign;
import com.zeshanaslam.cells.utils.CellHelpers;
import com.zeshanaslam.cells.utils.WorldUtils;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.block.data.BlockData;
import org.bukkit.inventory.BlockInventoryHolder;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Iterator;
import java.util.List;


public class RentHandler {
    private Main main;
    private CellHelpers cellHelpers;

    public RentHandler(Main main) {
        this.main = main;
        this.cellHelpers = new CellHelpers(main);
    }


    public void onComplete(Cell cell) {
        cell.tenant = null;
        this.cellHelpers.createOrUpdateCell(cell);


        resetCell(cell);

        System.out.println("Completed: " + cell.name);
    }


    public void startAuctionHouse(Cell cell) {
        List<ItemStack> items = Lists.newArrayList();


        World world = Bukkit.getWorld(cell.world);
        RegionManager regionManager = (new WorldUtils(this.main)).getRegionManager(world);
        ProtectedRegion region = regionManager.getRegion("cellsplugin" + cell.id);
        if (region == null) {
            System.err.println("Cells error! Region no longer exists for cell. Manual deletion?");

            return;
        }
        BlockVector3 max = region.getMaximumPoint();
        BlockVector3 min = region.getMinimumPoint();

        for (int x = min.getBlockX(); x <= max.getBlockX(); x++) {
            for (int y = min.getBlockY(); y <= max.getBlockY(); y++) {
                for (int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
                    Block block = world.getBlockAt(x, y, z);
                    BlockState blockState = block.getState();

                    if (blockState instanceof BlockInventoryHolder blockInventoryHolder) {
                        Inventory inventory = blockInventoryHolder.getInventory();

                        for (int i = 0; i < inventory.getSize(); i++) {
                            ItemStack itemStack = inventory.getItem(i);
                            if (itemStack != null) {
                                items.add(itemStack.clone());

                                inventory.setItem(i, null);
                            }
                        }
                    }
                }
            }
        }

        this.main.auctionHouseHandler.auctionCell(cell, items);

        region.getMembers().removePlayer(cell.tenant);


        cell.tenant = null;
        this.cellHelpers.createOrUpdateCell(cell);


        resetCell(cell);
    }

    public void resetCell(Cell cell) {
        for (SafeBlock safeBlock : cell.placedBlocks) {
            Location location = safeBlock.getLocation();

            if (!location.getChunk().isLoaded()) {
                location.getChunk().load(true);
            }
            location.getWorld().getBlockAt(location).setType(Material.AIR);
        }

        Iterator<SafeBlock> safeBlockIterator = cell.brokenBlocks.iterator();
        while (safeBlockIterator.hasNext()) {
            SafeBlock safeBlock = safeBlockIterator.next();

            Location location = safeBlock.getLocation();

            if (!location.getChunk().isLoaded()) {
                location.getChunk().load(true);
            }
            Block block = location.getWorld().getBlockAt(location);
            BlockData blockData = safeBlock.getBlockData();
            if (!blockData.getMaterial().isBlock()) {
                continue;
            }
            block.setType(blockData.getMaterial());
            block.setBlockData(blockData, true);
            safeBlockIterator.remove();
        }

        for (SafeBlock safeBlock : cell.brokenBlocks) {
            Location location = safeBlock.getLocation();

            if (!location.getChunk().isLoaded()) {
                location.getChunk().load(true);
            }
            Block block = location.getWorld().getBlockAt(location);
            BlockData blockData = safeBlock.getBlockData();

            block.setType(blockData.getMaterial());
            block.setBlockData(blockData, true);
        }

        cell.brokenBlocks = Lists.newArrayList();
        cell.placedBlocks = Lists.newArrayList();
        this.cellHelpers.createOrUpdateCell(cell);
    }

    public void onUnrent(Cell cell) {
        onComplete(cell);
    }

    public void stopRentChest(BuySign buySign) {
        this.main.configStore.buySignData.signs.remove(buySign.location.getLocation());


        buySign.location.getLocation().getBlock().setType(Material.AIR);

        Block chest = buySign.chest.getLocation().getBlock();
        BlockState chestState = chest.getState();
        if (chestState instanceof Chest) {
            ((Chest) chestState).getBlockInventory().clear();
        }

        chest.setType(Material.AIR);
    }
}


