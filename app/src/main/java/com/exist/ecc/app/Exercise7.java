package com.exist.ecc.app;

import com.exist.ecc.model.Board;
import com.exist.ecc.service.BoardService;
import com.exist.ecc.service.BoardServiceImpl;
import com.exist.ecc.util.Utils;


public class Exercise7 {
    private static final String DEFAULT_BOARD = "DefaultBoard.txt";

    public static void main(String[] args) {
        Board board = new Board();
        String fileName = Utils.DEFAULT_FILE_NAME;
        BoardService boardService = new BoardServiceImpl();

        boolean exit = false;

        if (args.length == 1) {
            fileName = args[0];
        } else if (args.length > 1) {
            System.out.println("Too many arguments! Exiting...");
            return;
        }

        if (!boardService.load(board, fileName)) {
            boardService.load(board, DEFAULT_BOARD);
        }

        while (!exit) {
            int option;

            printMenu();
            option = Utils.getUnsignedIntInput("Invalid choice! Please try again\n");
            if (option == -1) {
                continue;
            }

            switch (option) {
                case 1 -> {
                    String stringToSearch = Utils.getStringInput(
                        "Enter character to search: ");
                    if (!boardService.search(board, stringToSearch)) {
                        System.out.println("Value not found!");
                    }
                }
                case 2 -> {
                    String keyToEdit = Utils.getStringInput("Enter cell key to edit: ");
                    boardService.edit(board, keyToEdit);
                }
                case 3 -> {
                    boardService.print(board);
                }
                case 4 -> {
                    System.out.print("Enter number of rows: ");
                    int rows = Utils.getUnsignedIntInput("Invalid row value, please input a number greater than 0");

                    if (rows == -1) {
                        continue;
                    }

                    System.out.print("Enter number of columns: ");
                    int cols = Utils.getUnsignedIntInput("Invalid column value, please input a number greater than 0");

                    if (cols == -1) {
                        continue;
                    }

                    boardService.reset(board, rows, cols);
                }
                case 5 -> {
                    boardService.addRow(board);
                }
                case 6 -> {
                    int rows = board.getArray().size();

                    System.out.print("Enter row to sort: ");
                    int row = Utils.getUnsignedIntInput("Invalid row, please input a number from 0 - " + (rows - 1));

                    if (row >= rows) {
                        System.out.println("Invalid row, please input a number from 0 - " + (rows - 1));
                        return;
                    }

                    boardService.sortRow(board, row);
                }
                case 7 -> {
                    System.out.println("Exiting...");
                    exit = true;
                }
                default  -> {
                    System.out.println("Invalid choice! Please try again");
                }
            }

            System.out.println();
            boardService.save(board, fileName);
        }

        Utils.SCANNER.close();
    }

    private static void printMenu() {
        System.out.println("Choose your option:");
        System.out.println("[1] Search");
        System.out.println("[2] Edit");
        System.out.println("[3] Print");
        System.out.println("[4] Reset");
        System.out.println("[5] Add new row");
        System.out.println("[6] Sort row");
        System.out.println("[7] Exit");
        System.out.print("Enter choice: ");
    }
}
