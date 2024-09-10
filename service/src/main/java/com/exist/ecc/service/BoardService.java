package com.exist.ecc.service;

import com.exist.ecc.model.Board;


public interface BoardService {
    int MAX_CHARACTERS = 10;

    boolean search(Board board);

    void print(Board board);

    boolean containsKey(Board board, String key);

    void edit(Board board);

    void reset(Board board);

    void addRow(Board board);

    void sortRow(Board board);

    <T> boolean load(T obj, String fileName);

    <T> void save(T obj, String fileName);
}
