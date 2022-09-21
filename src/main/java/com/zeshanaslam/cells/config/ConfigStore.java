package com.zeshanaslam.cells.config;

import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.config.configdata.RegionFlags;
import com.zeshanaslam.cells.config.configdata.auctionhouse.AuctionHouseData;
import com.zeshanaslam.cells.config.configdata.cells.CellData;
import com.zeshanaslam.cells.config.configdata.groups.GroupData;
import com.zeshanaslam.cells.config.configdata.signs.BuySignData;
import com.zeshanaslam.cells.config.configdata.signs.SignData;

import org.bukkit.ChatColor;

import java.util.HashMap;


public class ConfigStore {
    public static final String path = "plugins/Cells/";
    public HashMap<Messages, String> messages = new HashMap<>();
    public GroupData groupData;
    public CellData cellData;
    public SignData signData;
    public BuySignData buySignData;
    public AuctionHouseData auctionHouseData;
    public RegionFlags regionFlags;
    public int cellCounter;
    public int defaultRentDays;
    public ConfigStore(Main main) {
        for (String key : main.getConfig().getConfigurationSection("Messages").getKeys(false)) {
            this.messages.put(Messages.valueOf(key), ChatColor.translateAlternateColorCodes('&', main.getConfig().getString("Messages." + key)));
        }

        this.groupData = new GroupData(main);
        this.cellData = new CellData();
        this.signData = new SignData(main);
        this.buySignData = new BuySignData(main);
        this.auctionHouseData = new AuctionHouseData(main);
        this.regionFlags = new RegionFlags(main);
        this.cellCounter = main.getConfig().getInt("CellCounter");
        this.defaultRentDays = main.getConfig().getInt("DefaultRentDays");
    }

    public void save(Main main) {
        this.cellData.save();
        this.signData.save();
        this.buySignData.save();
        this.auctionHouseData.save();

        main.getConfig().set("CellCounter", this.cellCounter);
        main.saveConfig();
    }

    public boolean isNumeric(String strNum) {
        return strNum.matches("-?\\d+(\\.\\d+)?");
    }

    public enum Messages {
        CellCreated,
        GroupNotFound,
        UnableToCreateRegion,
        CellNotFound,
        SetHome,
        RentDays,
        SetPrice,
        HomeNotSet,
        MustBeLookingAtSign,
        AddedSign,
        CannotRentInGroup,
        CannotRentAnyMore,
        NotEnoughMoney,
        RentedCell,
        RemovedSIgn,
        NotCellSign,
        AlreadyCellSign,
        UnRentMoreCells,
        UnRented,
        CellDeleted,
        ResetCells,
        HomeMoreCells,
        Reloaded,
        GroupAlreadyExists,
        GroupAdded,
        GroupRemoved,
        InvalidMaterial,
        CreatedAuctionHouse,
        AuctionHouseAlreadyExists,
        AuctionHouseNotFound,
        BoughtChest,
        Renewed
    }
}


