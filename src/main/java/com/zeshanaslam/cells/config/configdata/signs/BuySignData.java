package com.zeshanaslam.cells.config.configdata.signs;

import com.google.gson.Gson;
import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.utils.FileHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.TimeZone;

public class BuySignData {
    private Main main;
    private final Gson gson;
    private final String path;
    public HashMap<Location, BuySign> signs;
    private List<String> ahSignUnclaimed;
    private List<String> ahSignClaimed;
    private SimpleDateFormat ahFormat;

    public BuySignData(Main main) {
        this.main = main;
        this.signs = new HashMap<>();
        this.gson = new Gson();
        this.path = "plugins/Cells/buysigns.yml";

        FileHandler fileHandler = new FileHandler(this.path);
        if (fileHandler.contains("Signs")) {
            List<String> signData = fileHandler.getStringList("Signs");
            for (String json : signData) {
                BuySign sign = (BuySign) this.gson.fromJson(json, BuySign.class);

                this.signs.put(sign.location.getLocation(), sign);
            }
        }

        System.out.println("Loaded " + this.signs.size() + " buy signs!");

        this.ahSignUnclaimed = main.getConfig().getStringList("Signs.Auction.Unclaimed");
        this.ahSignClaimed = main.getConfig().getStringList("Signs.Auction.Claimed");
        this.ahFormat = new SimpleDateFormat(Objects.requireNonNull(main.getConfig().getString("Signs.Auction.Format")));

        signUpdate(main);
    }

    public void save() {
        FileHandler fileHandler = new FileHandler(this.path);

        List<String> json = new ArrayList<>();
        for (BuySign sign : this.signs.values()) {
            json.add(this.gson.toJson(sign));
        }

        fileHandler.createNewStringList("Signs", json);
        fileHandler.save();

        System.out.println("Saved " + json.size() + " buy signs!");
    }

    public List<String> getBuySign(BuySign buySign) {
        List<String> sign = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.now();


        OfflinePlayer offlinePlayer = null;
        String formattedTimeLeft = null;
        if (buySign.owner != null) {
            offlinePlayer = Bukkit.getOfflinePlayer(buySign.owner);

            LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(buySign.bought), TimeZone.getDefault().toZoneId()).plusSeconds(this.main.configStore.auctionHouseData.chestTime);

            Duration duration = getDuration(localDateTime, triggerTime);
            formattedTimeLeft = getTimeLeft(duration, this.ahFormat);


            if (duration.isZero() || duration.isNegative()) {
                this.main.rentHandler.stopRentChest(buySign);
            }
        }

        OfflinePlayer originalOwner = Bukkit.getOfflinePlayer(buySign.cellOwner);

        for (String line : (buySign.owner == null) ? this.ahSignUnclaimed : this.ahSignClaimed) {
            LocalDateTime later = localDateTime.plusSeconds(this.main.configStore.auctionHouseData.chestTime);


            line = ChatColor.translateAlternateColorCodes('&', line).replace("%time%", getTimeLeft(getDuration(localDateTime, later), this.ahFormat)).replace("%price%", String.valueOf(buySign.price)).replace("%cellowner%", originalOwner.getName());

            if (buySign.owner != null) {
                line = line.replace("%auctiontimeleft%", formattedTimeLeft).replace("%owner%", offlinePlayer.getName());
            }

            sign.add(line);
        }

        return sign;
    }

    private Duration getDuration(LocalDateTime current, LocalDateTime later) {
        return Duration.between(current, later);
    }

    private String getTimeLeft(Duration duration, DateFormat format) {
        return format.format(duration.toMillis());
    }

    private void signUpdate(Main main) {
        main.getServer().getScheduler().scheduleSyncRepeatingTask(main, () -> {
            Iterator<Location> locationIterator = this.signs.keySet().iterator();
            while (locationIterator.hasNext()) {
                Location location = locationIterator.next();
                BuySign buySign = this.signs.get(location);
                Block block = location.getBlock();
                if (!(block.getState() instanceof Sign sign)) {
                    locationIterator.remove();
                    continue;
                }
                List<String> signData = main.configStore.buySignData.getBuySign(buySign);
                String[] signDataArray = signData.toArray(new String[0]);
                if (Arrays.equals(signDataArray, sign.getLines())) continue;
                for (int i = 0; i < signData.size(); i++) sign.setLine(i, signData.get(i));
                sign.update();
            }
        }, 0L, 20L);
    }
}


