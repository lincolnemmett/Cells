package com.zeshanaslam.cells.config.configdata.signs;

import com.google.gson.Gson;
import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.config.configdata.cells.Cell;
import com.zeshanaslam.cells.utils.FileHandler;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.plugin.Plugin;

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
import java.util.TimeZone;

public class SignData {
    private final Gson gson;
    private final String path;
    public HashMap<Location, CellSign> signs;
    private final List<String> cellSignUnclaimed;
    private final List<String> cellSignClaimed;
    private final DateFormat cellFormat;
    private final Main main;

    public SignData(Main main) {
        this.main = main;
        this.signs = new HashMap<>();
        this.gson = new Gson();
        this.path = "plugins/Cells/signs.yml";

        FileHandler fileHandler = new FileHandler(this.path);
        if (fileHandler.contains("Signs")) {
            List<String> signData = fileHandler.getStringList("Signs");
            for (String json : signData) {
                CellSign sign = this.gson.fromJson(json, CellSign.class);

                this.signs.put(sign.location.getLocation(), sign);
            }
        }

        System.out.println("Loaded " + this.signs.size() + " signs!");

        this.cellSignUnclaimed = main.getConfig().getStringList("Signs.Cell.Unclaimed");
        this.cellSignClaimed = main.getConfig().getStringList("Signs.Cell.Claimed");
        this.cellFormat = new SimpleDateFormat(main.getConfig().getString("Signs.Cell.Format"));


        signUpdate(main);
    }

    public void save() {
        FileHandler fileHandler = new FileHandler(this.path);

        List<String> json = new ArrayList<>();
        for (CellSign sign : this.signs.values()) {
            json.add(this.gson.toJson(sign));
        }

        fileHandler.createNewStringList("Signs", json);
        fileHandler.save();

        System.out.println("Saved " + json.size() + " signs!");
    }

    public List<String> getCellSign(Cell cell) {
        List<String> sign = new ArrayList<>();

        LocalDateTime localDateTime = LocalDateTime.now();


        OfflinePlayer offlinePlayer = null;
        String formattedTimeLeft = null;
        if (cell.tenant != null) {
            offlinePlayer = Bukkit.getOfflinePlayer(cell.tenant);

            LocalDateTime triggerTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(cell.rentTimestamp), TimeZone.getDefault().toZoneId()).plusDays(cell.rentDays);

            Duration duration = getDuration(localDateTime, triggerTime);
            formattedTimeLeft = getTimeLeft(duration, this.cellFormat);


            if (duration.isZero() || duration.isNegative()) {
                this.main.rentHandler.onComplete(cell);
            }
        }

        for (String line : (cell.tenant == null) ? this.cellSignUnclaimed : this.cellSignClaimed) {
            LocalDateTime later = localDateTime.plusDays(cell.rentDays);


            line = ChatColor.translateAlternateColorCodes('&', line).replace("%time%", getTimeLeft(getDuration(localDateTime, later), this.cellFormat)).replace("%price%", String.valueOf(cell.price)).replace("%rentcost%", String.valueOf(getRenewAmount(cell)));

            if (cell.tenant != null) {
                line = line.replace("%celltimeleft%", formattedTimeLeft).replace("%owner%", offlinePlayer.getName());
            }

            sign.add(line);
        }

        return sign;
    }

    public double getRenewAmount(Cell cell) {
        LocalDateTime rentTime = LocalDateTime.ofInstant(Instant.ofEpochMilli(cell.rentTimestamp), TimeZone.getDefault().toZoneId());

        Duration difference = getDuration(LocalDateTime.now(), rentTime.plusDays(cell.rentDays));
        Duration difference1 = getDuration(rentTime, rentTime.plusDays(cell.rentDays));

        long minutes = difference1.toMinutes() - difference.toMinutes();
        double amount = cell.price / 24.0D / 60.0D;

        return Math.round(minutes * amount * 100.0D) / 100.0D;
    }

    private Duration getDuration(LocalDateTime current, LocalDateTime later) {
        return Duration.between(current, later);
    }

    private String getTimeLeft(Duration duration, DateFormat format) {
        return format.format(duration.toMillis());
    }

    private void signUpdate(Main main) {
        main.getServer().getScheduler().scheduleSyncRepeatingTask((Plugin) main, () -> {
            Iterator<Location> locationIterator = this.signs.keySet().iterator();
            while (locationIterator.hasNext()) {
                Location location = locationIterator.next();
                CellSign cellSign = this.signs.get(location);
                Cell cell = main.configStore.cellData.cells.get(cellSign.cellId);
                if (cell == null) {
                    locationIterator.remove();
                    continue;
                }
                Block block = location.getBlock();
                if (block.getState() instanceof Sign sign) {
                    List<String> signData = main.configStore.signData.getCellSign(cell);
                    String[] signDataArray = signData.toArray(new String[0]);
                    if (Arrays.equals(signDataArray, sign.getLines())) continue;
                    for (int i = 0; i < signData.size(); i++) sign.setLine(i, signData.get(i));
                    sign.update();
                } else {
                    locationIterator.remove();
                }
            }
        }, 0L, 20L);
    }
}


