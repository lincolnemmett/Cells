package com.zeshanaslam.cells.config.configdata.cells;

import com.zeshanaslam.cells.Main;

import java.util.HashMap;


public class CellData {
    public HashMap<Integer, Cell> cells;
    private final CellDataHelpers cellDataHelpers;

    public CellData() {
        this.cellDataHelpers = new CellDataHelpers();

        this.cells = new HashMap<>();
        for (Cell cell : this.cellDataHelpers.getAllCells()) {
            this.cells.put(cell.id, cell);
        }

        System.out.println("Loaded " + this.cells.size() + " cells!");
    }

    public void save() {
        for (Cell cell : this.cells.values()) {
            this.cellDataHelpers.createOrUpdateCell(cell);
        }

        System.out.println("Saved " + this.cells.size() + " cells!");
    }
}


