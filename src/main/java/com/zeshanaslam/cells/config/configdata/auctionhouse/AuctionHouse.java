package com.zeshanaslam.cells.config.configdata.auctionhouse;

import java.util.UUID;

public class AuctionHouse {
    public String name;
    public String placedOn;
    public String frontOf;
    public UUID world;

    public AuctionHouse(String name, String placedOn, String frontOf, UUID world) {
        this.name = name;
        this.placedOn = placedOn;
        this.frontOf = frontOf;
        this.world = world;
    }
}


