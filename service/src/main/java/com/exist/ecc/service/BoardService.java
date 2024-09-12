package com.exist.ecc.service;

import com.exist.ecc.model.Board;


public interface BoardService {
    int MAX_CHARACTERS = 10;

    boolean search(Board board, String keyToSearch);

    void print(Board board);

    boolean containsKey(Board board, String key);

    void edit(Board board, String keyToEdit);

    void reset(Board board, int rows, int cols);

    void addRow(Board board);

    void sortRow(Board board, int row);

    <T> boolean load(T obj, String fileName);

    <T> void save(T obj, String fileName);
}
