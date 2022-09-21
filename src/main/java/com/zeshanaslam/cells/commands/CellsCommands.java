package com.zeshanaslam.cells.commands;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.IncompleteRegionException;
import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.config.ConfigStore;
import com.zeshanaslam.cells.config.configdata.SafeLocation;
import com.zeshanaslam.cells.config.configdata.cells.Cell;
import com.zeshanaslam.cells.config.configdata.groups.Group;
import com.zeshanaslam.cells.config.configdata.signs.CellSign;
import com.zeshanaslam.cells.utils.CellHelpers;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

import app.ashcon.intake.Command;
import app.ashcon.intake.bukkit.parametric.annotation.Sender;
import app.ashcon.intake.parametric.annotation.Default;
import app.ashcon.intake.parametric.annotation.Range;


public class CellsCommands {
    private final Main main;
    private final CellHelpers cellHelpers;

    public CellsCommands(Main main) {
        this.main = main;
        this.cellHelpers = new CellHelpers(main);
    }


    @Command(aliases = {"create"}, usage = "[name] [price] [group] [auction name] <rent days>", desc = "Creates cell", perms = {"cells.command.create"})
    public void create(@Sender Player sender, String name, @Range(min = 0.0D) double price, String group, String auctionHouse, @Default({"-1"}) int rentDays) {
        if (!this.main.configStore.groupData.groups.containsKey(group)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.GroupNotFound));

            return;
        }
        if (!auctionHouse.equalsIgnoreCase("none") && !this.main.configStore.auctionHouseData.auctionHouses.containsKey(auctionHouse)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.AuctionHouseNotFound));

            return;
        }
        Group cellGroup = this.main.configStore.groupData.groups.get(group);

        Cell cell = new Cell(this.cellHelpers.getNextId(), name, price, cellGroup, null, (rentDays == -1) ? this.main.configStore.defaultRentDays : rentDays, null, 0L, Lists.newArrayList(), Lists.newArrayList(), auctionHouse, sender.getWorld().getUID());

        if (!this.cellHelpers.createRegion(sender, cell)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.UnableToCreateRegion));

            return;
        }
        this.cellHelpers.createOrUpdateCell(cell);
        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellCreated)
                                                         .replace("%id%", String.valueOf(cell.id)));
    }


    @Command(aliases = {"delete"}, usage = "[cell id]", desc = "Deletes cell", perms = {"cells.command.delete"})
    public void delete(@Sender Player sender, int cellId) {
        Cell cell = this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }

        this.cellHelpers.deleteCell(cell, sender.getLocation().getWorld().getName());
        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellDeleted));
    }


    @Command(aliases = {"sethome"}, usage = "[cell id]", desc = "Sets cell home", perms = {"cells.command.sethome"})
    public void setHome(@Sender Player sender, int cellId) {
        Cell cell = this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }
        Location loc = sender.getLocation();
        cell.home = new SafeLocation(loc.getWorld().getUID(), loc.getBlockX(), loc.getBlockY(), loc.getBlockZ(), loc.getPitch(), loc.getYaw());
        this.cellHelpers.createOrUpdateCell(cell);
        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.SetHome));
    }


    @Command(aliases = {"setdays"}, usage = "[days] [cellid/group/all]", desc = "Sets cell home", perms = {"cells.command.setdays"})
    public void setDays(CommandSender sender, int days, String type) {
        List<Cell> cells = this.cellHelpers.getCellsByType(sender, type);
        if (cells.isEmpty()) {
            return;
        }

        for (Cell cell : cells) {
            cell.rentDays = days;
            this.cellHelpers.createOrUpdateCell(cell);
        }

        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.RentDays)
                                                         .replace("%amount%", String.valueOf(cells.size())));
    }


    @Command(aliases = {"setcost"}, usage = "[cost] [cellid/group/all]", desc = "Sets cell cost", perms = {"cells.command.setcost"})
    public void setCost(CommandSender sender, int cost, String type) {
        List<Cell> cells = this.cellHelpers.getCellsByType(sender, type);
        if (cells.isEmpty()) {
            return;
        }

        for (Cell cell : cells) {
            cell.price = cost;
            this.cellHelpers.createOrUpdateCell(cell);
        }

        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.SetPrice)
                                                         .replace("%amount%", String.valueOf(cells.size())));
    }


    @Command(aliases = {"home"}, usage = "[cell id]", desc = "Teleports to cell home", perms = {"cells.command.home"})
    public void home(@Sender Player sender, @Default({"-1"}) int cellId) {
        List<Cell> cells = this.cellHelpers.getPlayerCells(sender);
        if (cells.isEmpty()) {
            sender.sendMessage(ChatColor.RED + "You do not own a cell!");

            return;
        }
        if (cells.size() > 1 && cellId == -1) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.HomeMoreCells));

            return;
        }
        Cell cell = (cellId == -1) ? cells.get(0) : this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }
        if (cell.home == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.HomeNotSet));

            return;
        }
        sender.teleport(cell.home.getLocation().clone().add(0.5D, 0.0D, 0.5D));
    }


    @Command(aliases = {"addsign"}, usage = "[cell id]", desc = "Adds cell sign", perms = {"cells.command.addsign"})
    public void addSign(@Sender Player sender, int cellId) {
        Cell cell = this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }
        Block block = sender.getTargetBlock(null, 10);
        BlockState blockState = block.getState();
        if (!(blockState instanceof org.bukkit.block.Sign)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.MustBeLookingAtSign));

            return;
        }
        Location location = block.getLocation();

        if (this.main.configStore.signData.signs.containsKey(location)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.AlreadyCellSign));

            return;
        }
        CellSign cellSign = new CellSign(cell.id, new SafeLocation(location.getWorld().getUID(), location.getBlockX(), location.getBlockY(), location.getBlockZ(), location.getPitch(), location.getYaw()));
        this.main.configStore.signData.signs.put(location, cellSign);
        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.AddedSign));
    }


    @Command(aliases = {"removesign"}, usage = "[cell id]", desc = "Removes cell sign player is looking at.", perms = {"cells.command.removesign"})
    public void removeSign(@Sender Player sender) {
        Block block = sender.getTargetBlock(null, 10);
        BlockState blockState = block.getState();
        if (!(blockState instanceof org.bukkit.block.Sign)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.MustBeLookingAtSign));

            return;
        }
        Location location = block.getLocation();
        if (!this.main.configStore.signData.signs.containsKey(location)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.NotCellSign));

            return;
        }
        this.main.configStore.signData.signs.remove(location);
        block.setType(Material.AIR);
        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.RemovedSIgn));
    }


    @Command(aliases = {"unrent"}, desc = "Un-rent cell", perms = {"cells.command.unrent"})
    public void unRent(@Sender Player sender, @Default({"-1"}) int cellId) {
        List<Cell> cells = this.cellHelpers.getPlayerCells(sender);

        if (cells.size() > 1 && cellId == -1) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.UnRentMoreCells));

            return;
        }
        Cell cell = (cellId == -1) ? cells.get(0) : this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }
        this.main.rentHandler.onUnrent(cell);
        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.UnRented));
    }


    @Command(aliases = {"rent"}, usage = "[cell id]", desc = "Rents cell", perms = {"cells.command.rent"})
    public void rent(@Sender Player sender, int cellId) {
        Cell cell = this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }
        this.cellHelpers.rentCell(sender, cell);
    }


    @Command(aliases = {"reset"}, usage = "[cell id/all]", desc = "Resets cell(s)", perms = {"cells.command.reset"})
    public void reset(@Sender Player sender, String type) {
        List<Cell> cells = Lists.newArrayList();

        if (this.main.configStore.isNumeric(type)) {
            Cell cell = this.cellHelpers.getCell(Integer.parseInt(type));
            if (cell == null) {
                sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

                return;
            }
            cells.add(cell);
        } else {
            cells = new ArrayList<>(this.main.configStore.cellData.cells.values());
        }

        for (Cell cell : cells) {
            this.main.rentHandler.resetCell(cell);
        }

        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.ResetCells));
    }


    @Command(aliases = {"groupadd"}, usage = "[group name] [permission]", desc = "Adds group", perms = {"cells.command.groupadd"})
    public void groupAdd(@Sender Player sender, String groupName, String permission) {
        if (this.main.configStore.groupData.groups.containsKey(groupName)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.GroupAlreadyExists));

            return;
        }
        this.main.configStore.groupData.groups.put(groupName, new Group(groupName, permission));
        this.main.getConfig().set("Groups." + groupName + ".Permission", permission);
        this.main.saveConfig();

        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.GroupAdded));
    }


    @Command(aliases = {"groupremove"}, usage = "[group name]", desc = "Adds group", perms = {"cells.command.groupadd"})
    public void groupRemove(@Sender Player sender, String groupName) {
        if (!this.main.configStore.groupData.groups.containsKey(groupName)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.GroupNotFound));

            return;
        }
        for (Cell cell : this.cellHelpers.getCellsByGroup(groupName)) {
            if (cell.tenant != null) {
                this.main.rentHandler.onUnrent(cell);
            }
            this.cellHelpers.deleteCell(cell, sender.getLocation().getWorld().getName());
        }

        this.main.configStore.groupData.groups.remove(groupName);
        this.main.getConfig().set("Groups." + groupName, null);
        this.main.getConfig().set("Groups." + groupName + ".Permission", null);
        this.main.saveConfig();

        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.GroupRemoved));
    }


    @Command(aliases = {"reload"}, desc = "Reloads cell", perms = {"cells.command.reload"})
    public void reload(@Sender Player sender) {
        this.main.reloadConfig();
        this.main.configStore.save(this.main);
        this.main.configStore = new ConfigStore(this.main);

        sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.Reloaded));
    }


    @Command(aliases = {"save"}, desc = "Saves cells plugin", perms = {"cells.command.save"})
    public void save(@Sender Player sender) {
        this.main.configStore.save(this.main);

        sender.sendMessage(ChatColor.GREEN + "Data has been saved!");
    }


    @Command(aliases = {"ahcreate", "ahc"}, usage = "[name] [placedOnIn]", desc = "Testing", perms = {"cells.command.ahcreate"})
    public void auctionHouseCreate(@Sender Player sender, String name, String placedOnIn, String frontOfIn) {
        if (this.main.configStore.auctionHouseData.auctionHouses.containsKey(name)) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.AuctionHouseAlreadyExists));

            return;
        }
        Material placedOn = Material.matchMaterial(placedOnIn.toUpperCase());
        Material frontOf = Material.matchMaterial(frontOfIn.toUpperCase());

        if (placedOn == null || frontOf == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.InvalidMaterial));

            return;
        }
        try {
            if (this.main.auctionHouseHandler.create(sender, name, placedOn, frontOf)) {
                sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CreatedAuctionHouse));
            } else {
                sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.UnableToCreateRegion));
            }
        } catch (IncompleteRegionException e) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.UnableToCreateRegion));
            e.printStackTrace();
        }
    }


    @Command(aliases = {"evict"}, desc = "Evicts cell", perms = {"cells.command.evict"})
    public void evict(CommandSender sender, int cellId) {
        Cell cell = this.cellHelpers.getCell(cellId);
        if (cell == null) {
            sender.sendMessage(this.main.configStore.messages.get(ConfigStore.Messages.CellNotFound));

            return;
        }
        this.main.rentHandler.startAuctionHouse(cell);
    }


    @Command(aliases = {"evictall"}, desc = "Evicts cell", perms = {"cells.command.evictall"})
    public void evictall(CommandSender sender, String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        if (player == null) {
            sender.sendMessage(ChatColor.RED + "Player not found!");

            return;
        }
        List<Cell> cells = this.cellHelpers.getPlayerCells(player);
        for (Cell cell : cells) {
            this.main.rentHandler.startAuctionHouse(cell);
        }

        sender.sendMessage("Evicted " + player.getName() + " from " + cells.size() + " cells!");
    }
}


