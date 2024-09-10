package com.exist.ecc.model;

import java.util.List;


public class Board {
    private List<List<Cell>> array;

    public List<List<Cell>> getArray() {
        return array;
    }

    public void setArray(List<List<Cell>> array) {
        this.array = array;
    }
}
