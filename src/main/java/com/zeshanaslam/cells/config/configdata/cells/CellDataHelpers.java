package com.zeshanaslam.cells.config.configdata.cells;

import com.google.gson.Gson;
import com.zeshanaslam.cells.Main;
import com.zeshanaslam.cells.utils.FileHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;


public class CellDataHelpers {
    private Gson gson;
    private final String path;

    public CellDataHelpers() {
        this.gson = new Gson();
        this.path = "plugins/Cells/cells/";
    }

    public void createOrUpdateCell(Cell cell) {
        FileHandler fileHandler = new FileHandler(this.path + cell.id + ".yml");
        fileHandler.set("data", this.gson.toJson(cell));
        fileHandler.save();
    }

    public void delete(Cell cell) {
        FileHandler fileHandler = new FileHandler(this.path + cell.id + ".yml");
        fileHandler.delete();
    }

    public Cell getCell(int id) {
        if (!FileHandler.fileExists(this.path + id + ".yml")) {
            System.err.println("Cell not found: " + this.path + id + ".yml");
            return null;
        }

        FileHandler fileHandler = new FileHandler(this.path + id + ".yml");
        return (Cell) this.gson.fromJson(fileHandler.getString("data"), Cell.class);
    }

    public List<Cell> getAllCells() {
        List<Cell> cells = new ArrayList<>();
        try (Stream<Path> walk = Files.walk(Paths.get(this.path, new String[0]), new java.nio.file.FileVisitOption[0])) {


            List<String> result = (List<String>) walk.map(Path::toString).filter(f -> f.contains(".yml")).collect(Collectors.toList());

            for (String file : result) {
                file = file.replace("plugins\\Cells\\cells\\", "").replace("plugins/Cells/cells/", "").replace(".yml", "");
                int id = Integer.parseInt(file);

                cells.add(getCell(id));
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        return cells;
    }
}


