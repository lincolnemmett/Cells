package com.zeshanaslam.cells.listeners;

import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.config.ConfigStore;
import com.zeshanaslam.cells.config.configdata.cells.Cell;
import com.zeshanaslam.cells.config.configdata.signs.BuySign;
import com.zeshanaslam.cells.config.configdata.signs.CellSign;
import com.zeshanaslam.cells.utils.CellHelpers;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Block;
import org.bukkit.block.data.type.Chest;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;


public class SignListener
        implements Listener {
    private final Main main;
    private final CellHelpers cellHelpers;

    public SignListener(Main plugin) {
        this.main = plugin;
        this.cellHelpers = new CellHelpers(this.main);
    }

    @EventHandler
    public void onSign(PlayerInteractEvent event) {
        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK)) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();

            if (this.main.configStore.signData.signs.containsKey(block.getLocation())) {
                CellSign cellSign = this.main.configStore.signData.signs.get(block.getLocation());
                if (!this.main.configStore.cellData.cells.containsKey(cellSign.cellId)) {
                    System.err.println("Sign exists for cell that is not found. Deleted manually?");

                    return;
                }
                Cell cell = this.main.configStore.cellData.cells.get(cellSign.cellId);
                if (!this.cellHelpers.rentCell(player, cell)) {
                    if (cell.tenant != null && cell.tenant.equals(player.getUniqueId())) {
                        double renew = this.main.configStore.signData.getRenewAmount(cell);

                        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                        if (!this.main.economy.has(offlinePlayer, renew)) {
                            player.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.NotEnoughMoney));

                            return;
                        }
                        this.main.economy.withdrawPlayer(offlinePlayer, renew);

                        cell.rentTimestamp = System.currentTimeMillis();
                        this.cellHelpers.createOrUpdateCell(cell);
                        player.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.Renewed));
                    }
                }

                return;
            }
            if (block.getType() == Material.CHEST) {
                Chest chest = (Chest) block.getBlockData();

                Block blockSign = block.getRelative(chest.getFacing());
                if (this.main.configStore.buySignData.signs.containsKey(blockSign.getLocation())) {
                    BuySign buySign = this.main.configStore.buySignData.signs.get(blockSign.getLocation());
                    if (buySign.owner != null && player.getUniqueId() == buySign.owner) {
                        return;
                    }
                    event.setCancelled(true);
                }

                return;
            }
            if (block.getType() == Material.OAK_WALL_SIGN &&
                this.main.configStore.buySignData.signs.containsKey(block.getLocation())) {
                BuySign buySign = this.main.configStore.buySignData.signs.get(block.getLocation());
                if (buySign.owner != null) {
                    return;
                }
                OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(player.getUniqueId());
                if (!this.main.economy.has(offlinePlayer, buySign.price)) {
                    player.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.NotEnoughMoney));

                    return;
                }
                this.main.economy.withdrawPlayer(offlinePlayer, buySign.price);

                buySign.bought = System.currentTimeMillis();
                buySign.owner = player.getUniqueId();
                this.main.configStore.buySignData.signs.put(block.getLocation(), buySign);

                player.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.BoughtChest));
            }
        }
    }
}


