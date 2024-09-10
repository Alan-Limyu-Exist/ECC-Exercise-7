package com.exist.ecc.app;

import com.exist.ecc.model.Board;
import com.exist.ecc.service.BoardService;
import com.exist.ecc.service.BoardServiceImpl;
import com.exist.ecc.util.Utils;


public class Exercise5 {
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
                case 1:
                    if (!boardService.search(board)) {
                        System.out.println("Value not found!");
                    }
                    break;
                case 2:
                    boardService.edit(board);
                    break;
                case 3:
                    boardService.print(board);
                    break;
                case 4:
                    boardService.reset(board);
                    break;
                case 5:
                    boardService.addRow(board);
                    break;
                case 6:
                    boardService.sortRow(board);
                    break;
                case 7:
                    System.out.println("Exiting...");
                    exit = true;
                    break;
                default:
                    System.out.println("Invalid choice! Please try again");
                    break;
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
