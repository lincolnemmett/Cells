package com.zeshanaslam.cells.config.configdata.groups;

import com.zeshanaslam.cells.Main;

import java.util.HashMap;


public class GroupData {
    public HashMap<String, Group> groups = new HashMap<>();

    public GroupData(Main main) {
        for (String key : main.getConfig().getConfigurationSection("Groups").getKeys(false)) {
            String permission = main.getConfig().getString("Groups." + key + ".Permission");

            Group group = new Group(key, permission);
            this.groups.put(key, group);
        }
    }
}


