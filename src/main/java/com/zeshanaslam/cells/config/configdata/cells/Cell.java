package com.zeshanaslam.cells.config.configdata.cells;

import com.zeshanaslam.cells.config.configdata.SafeLocation;
import com.zeshanaslam.cells.config.configdata.groups.Group;

import java.util.List;
import java.util.UUID;


public class Cell {
    public int id;
    public String name;
    public double price;
    public Group group;
    public SafeLocation home;
    public int rentDays;
    public UUID tenant;
    public long rentTimestamp;
    public List<SafeBlock> placedBlocks;
    public List<SafeBlock> brokenBlocks;
    public String auctionHouse;
    public UUID world;
    public UUID tempOwner;

    public Cell(int id, String name, double price, Group group, SafeLocation home, int rentDays, UUID tenant, long rentTimestamp, List<SafeBlock> placedBlocks, List<SafeBlock> brokenBlocks, String auctionHouse, UUID world) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.group = group;
        this.home = home;
        this.rentDays = rentDays;
        this.tenant = tenant;
        this.rentTimestamp = rentTimestamp;
        this.placedBlocks = placedBlocks;
        this.brokenBlocks = brokenBlocks;
        this.auctionHouse = auctionHouse;
        this.world = world;
    }
}


