package com.zeshanaslam.cells.config.configdata.auctionhouse;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.utils.FileHandler;

import java.util.HashMap;
import java.util.List;


public class AuctionHouseData {
    private final Gson gson;
    public HashMap<String, AuctionHouse> auctionHouses;
    public double lowest;
    public double highest;
    public int perChestMin;
    public int perChestMax;
    public int chestTime;

    public AuctionHouseData(Main main) {
        this.gson = new Gson();
        this.auctionHouses = new HashMap<>();

        this.lowest = main.getConfig().getDouble("AuctionHouse.Lowest");
        this.highest = main.getConfig().getDouble("AuctionHouse.Highest");
        this.perChestMin = main.getConfig().getInt("AuctionHouse.PerChest.Min");
        this.perChestMax = main.getConfig().getInt("AuctionHouse.PerChest.Max");
        this.chestTime = main.getConfig().getInt("AuctionHouse.ChestTime");

        FileHandler auctionConfig = new FileHandler("plugins/Cells/auctions.yml");
        if (auctionConfig.contains("AuctionHouses")) {
            for (String json : auctionConfig.getStringList("AuctionHouses")) {
                AuctionHouse auctionHouse = (AuctionHouse) this.gson.fromJson(json, AuctionHouse.class);

                this.auctionHouses.put(auctionHouse.name, auctionHouse);
            }
        }

        System.out.println("Loaded " + this.auctionHouses.size() + " auction houses!");
    }

    public void save() {
        List<String> json = Lists.newArrayList();
        for (AuctionHouse auctionHouse : this.auctionHouses.values()) {
            json.add(this.gson.toJson(auctionHouse));
        }

        FileHandler auctionConfig = new FileHandler("plugins/Cells/auctions.yml");
        auctionConfig.createNewStringList("AuctionHouses", json);
        auctionConfig.save();

        System.out.println("Saved " + this.auctionHouses.size() + " auction houses!");
    }
}


