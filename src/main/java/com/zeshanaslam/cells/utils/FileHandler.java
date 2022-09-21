package com.zeshanaslam.cells.utils;

import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class FileHandler {
    private File file = null;

    private final YamlConfiguration yaml = new YamlConfiguration();

    public FileHandler(String path) {
        this.file = new File(path);
        if (!this.file.exists()) {
            try {
                this.file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        load();
    }

    public String getName() {
        return this.file.getName();
    }


    public static boolean fileExists(String path) {
        File file = new File(path);

        return file.exists();
    }

    private void load() {
        try {
            this.yaml.load(this.file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void save() {
        try {
            this.yaml.save(this.file);
        } catch (IOException e) {
            System.out.println("CR: Error saving: " + this.file.getName());
        }
    }

    public void delete() {
        try {
            this.file.delete();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String getString(String s) {
        return this.yaml.getString(s);
    }

    public Object get(String s) {
        return this.yaml.get(s);
    }

    public void add(String s, Object o) {
        if (!contains(s)) {
            set(s, o);
        }
    }

    public List<String> getStringList(String s) {
        return this.yaml.getStringList(s);
    }

    public void createNewStringList(String s, List<String> list) {
        this.yaml.set(s, list);
    }

    public boolean contains(String s) {
        return this.yaml.contains(s);
    }

    public void set(String s, Object o) {
        this.yaml.set(s, o);
    }
}


