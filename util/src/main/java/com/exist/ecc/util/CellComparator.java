package com.exist.ecc.util;

import java.util.Comparator;

import com.exist.ecc.model.Cell;


public class CellComparator implements Comparator<Cell> {
    @Override
    public int compare(Cell a, Cell b) {
        String cellA = a.getKey() + a.getValue();
        String cellB = b.getKey() + b.getValue();
        return cellA.compareTo(cellB);
    }
}
