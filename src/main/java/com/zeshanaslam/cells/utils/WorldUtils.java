package com.zeshanaslam.cells.utils;

import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.zeshanaslam.cells.Main;

import org.bukkit.World;
import org.bukkit.entity.Player;

public class WorldUtils {
    public Main main;

    public WorldUtils(Main main) {
        this.main = main;
    }

    public Region getRegionSelection(Player player) throws IncompleteRegionException {
        com.sk89q.worldedit.world.World world = this.main.worldEditPlugin.getSession(player).getSelectionWorld();
        if (world == null) {
            return null;
        }

        return this.main.worldEditPlugin.getSession(player).getSelection(world);
    }

    public RegionManager getRegionManager(Player player) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        return container.get(this.main.worldEditPlugin.getSession(player).getSelectionWorld());
    }

    public RegionManager getRegionManager(World world) {
        RegionContainer container = WorldGuard.getInstance().getPlatform().getRegionContainer();

        return container.get(BukkitAdapter.adapt(world));
    }
}


