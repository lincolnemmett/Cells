package com.zeshanaslam.cells;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.zeshanaslam.cells.commands.CellsCommands;
import com.zeshanaslam.cells.config.ConfigStore;
import com.zeshanaslam.cells.listeners.BlockListener;
import com.zeshanaslam.cells.listeners.CellListener;
import com.zeshanaslam.cells.listeners.SignListener;

import net.milkbowl.vault.economy.Economy;

import org.bukkit.Bukkit;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

import app.ashcon.intake.bukkit.BukkitIntake;
import app.ashcon.intake.bukkit.graph.BasicBukkitCommandGraph;
import app.ashcon.intake.fluent.DispatcherNode;

public class Main extends JavaPlugin {
    public WorldGuardPlugin worldGuardPlugin;
    public WorldEditPlugin worldEditPlugin;
    public Economy economy;
    public ConfigStore configStore;
    public RentHandler rentHandler;
    public AuctionHouseHandler auctionHouseHandler;

    public void onEnable() {
        super.onEnable();

        saveDefaultConfig();


        File cellsDirectory = new File("plugins/Cells/cells/");
        if (!cellsDirectory.exists()) {
            cellsDirectory.mkdir();
        }


        this.configStore = new ConfigStore(this);


        if (Bukkit.getServer().getPluginManager().isPluginEnabled("WorldGuard")) {
            this.worldGuardPlugin = (WorldGuardPlugin) getServer().getPluginManager().getPlugin("WorldGuard");
        }


        if (Bukkit.getServer().getPluginManager().isPluginEnabled("WorldEdit")) {
            this.worldEditPlugin = (WorldEditPlugin) getServer().getPluginManager().getPlugin("WorldEdit");
        }


        BasicBukkitCommandGraph basicBukkitCommandGraph = new BasicBukkitCommandGraph();
        DispatcherNode dispatcherNode = basicBukkitCommandGraph.getRootDispatcherNode().registerNode("cells");
        dispatcherNode.registerCommands(new CellsCommands(this));

        BukkitIntake bukkitIntake = new BukkitIntake(this, basicBukkitCommandGraph);
        bukkitIntake.register();


        RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
        if (rsp != null) {
            this.economy = rsp.getProvider();
        }


        this.rentHandler = new RentHandler(this);


        this.auctionHouseHandler = new AuctionHouseHandler(this);


        getServer().getPluginManager().registerEvents(new SignListener(this), this);
        getServer().getPluginManager().registerEvents(new BlockListener(this), this);
        getServer().getPluginManager().registerEvents(new CellListener(this), this);
    }


    public void onDisable() {
        super.onDisable();

        this.configStore.save(this);
    }
}


