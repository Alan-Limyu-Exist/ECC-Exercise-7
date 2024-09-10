package com.exist.ecc.service;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.SetUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.Random;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

import com.exist.ecc.model.Board;
import com.exist.ecc.model.Cell;
import com.exist.ecc.service.BoardService;
import com.exist.ecc.util.CellComparator;
import com.exist.ecc.util.Loadable;
import com.exist.ecc.util.Saveable;
import com.exist.ecc.util.Utils;


public class BoardServiceImpl implements BoardService, Loadable, Saveable  {
    private static final Set<Character> INVALID_CHARACTERS =
        SetUtils.hashSet('[', ']');

    public BoardServiceImpl() { }

    public boolean search(Board board) {
        boolean found = false;
        List<List<Cell>> array = board.getArray();
        int rows = array.size();
        int totalOccurences = 0;

        String stringToSearch = Utils.getStringInput(
            "Enter character to search: ");

        for (int currentRow = 0; currentRow < rows; currentRow++) {
            int currentCol = 0;

            for (Cell cell : array.get(currentRow)) {
                int index = 0;

                while ((index = cell.toString().indexOf(
                        stringToSearch, index)) != -1) {
                    if (!found) {
                        System.out.printf("Value found at: [%d, %d, %d]", currentRow, currentCol, index);
                        found = true;
                    } else {
                        System.out.printf(", [%d, %d, %d]", currentRow, currentCol, index);
                    }

                    totalOccurences++;
                    index++;
                }

                currentCol++;
            }
        }

        System.out.println("\nTotal occurences: " + totalOccurences);
        return found;
    }

    public void print(Board board) {
        int rows = board.getArray().size();

        for (int currentRow = 0; currentRow < rows; currentRow++) {
            System.out.println();

            for (Cell cell : board.getArray().get(currentRow)) {
                System.out.printf("[%s] ", cell.toString());
            }
        }

        System.out.println();
    }

    public boolean containsKey(Board board, String key) {
        return CollectionUtils.exists(board.getArray(), row -> row.stream()
            .anyMatch(cell -> cell.getKey().equals(key)));
    }

    public void edit(Board board) {
        String keyToEdit;

        keyToEdit = Utils.getStringInput("Enter cell key to edit: ");

        boolean invalidInput;
        String[] keyAndValue;

        if (!containsKey(board, keyToEdit)) {
            System.out.println("Key does not exist");
            return;
        }

        for (List<Cell> row : board.getArray()) {

            for (Cell cell : row) {

                if (!cell.getKey().equals(keyToEdit)) {
                    continue;
                }

                do {
                    invalidInput = false;
                    System.out.println("Format \"<KEY> <VALUE>\" Ex: sdf jr;dfoi");
                    System.out.println("Whitespaces (' ') and square brackets ('[', ']') are not allowed");
                    
                    keyAndValue = Utils.getStringInput("Enter new value of cell: ").split(" ");

                    if (keyAndValue.length != 2) {
                        System.out.println("Invalid value, please try again");
                        invalidInput = true;
                        continue;
                    }
                    
                    if (containsKey(board, keyAndValue[0]) && !cell.getKey().equals(keyAndValue[0])) {
                        System.out.println("Key already exists, please try again");
                        invalidInput = true;
                        continue;
                    }

                    for (char currentChar : INVALID_CHARACTERS) {
                        if (keyAndValue[0].contains(String.valueOf(currentChar)) 
                        || keyAndValue[1].contains(String.valueOf(currentChar))) {
                            System.out.println("Invalid value, please try again");
                            invalidInput = true;
                            break;
                        }
                    }

                } while (invalidInput);

                cell.setKey(keyAndValue[0]);
                cell.setValue(keyAndValue[1]);
            }
        }
    }

    public void reset(Board board) {
        int rows;
        int cols;

        System.out.print("Enter number of rows: ");
        rows = Utils.getUnsignedIntInput("Invalid row value, please input a number greater than 0");

        if (rows == -1) {
            return;
        }

        System.out.print("Enter number of columns: ");
        cols = Utils.getUnsignedIntInput("Invalid column value, please input a number greater than 0");

        if (cols == -1) {
            return;
        }

        board.setArray(new ArrayList<>());

        for (int currentRow = 0; currentRow < rows; currentRow++) {
            board.getArray().add(new ArrayList<Cell>());

            for (int currentCol = 0; currentCol < cols; currentCol++) {
                board.getArray().get(currentRow).add(generateCell(board));
            }

        }
    }

    public void addRow(Board board) {
        int newRowIndex = board.getArray().size();
        int cols = board.getArray().get(0).size();
        board.getArray().add(new ArrayList<Cell>());

        for (int currentCol = 0; currentCol < cols; currentCol++) {
            board.getArray().get(newRowIndex).add(generateCell(board));
        }
    }

    private Cell generateCell(Board board) {
        String key;
        String value;

        do {
            key = generateRandomCharacters();
        } while (containsKey(board, key));

        value = generateRandomCharacters();

        return new Cell(key, value);
    }

    private String generateRandomCharacters() {
        String value = "";
        Random random = new Random();

        for (int currentChar = 0; currentChar < random.nextInt(MAX_CHARACTERS) + 1; currentChar++) {
            char temp;

            do {
                temp = (char) (random.nextInt(94) + 33);
            } while (INVALID_CHARACTERS.contains(temp));

            value += temp;
        }

        return value;
    }

    public void sortRow(Board board) {
        int rows = board.getArray().size();
        int row = 0;

        System.out.print("Enter row to sort: ");
        row = Utils.getUnsignedIntInput("Invalid row, please input a number from 0 - " + (rows - 1));

        if (row >= rows) {
            System.out.println("Invalid row, please input a number from 0 - " + (rows - 1));
            return;
        }

        Collections.sort(board.getArray().get(row), new CellComparator());
    }

    @Override
    public <T> boolean load(T obj, String fileName) {
        if (obj instanceof Board board) {
            try (BufferedReader bufferedReader = new BufferedReader(getResource(fileName))) {
                board.setArray(new ArrayList<List<Cell>>());
                board.getArray().add(new ArrayList<Cell>());
                int row = 0;
                String line;

                while ((line = bufferedReader.readLine()) != null) {
                    line = line.trim();
                    if (line.equals("")) {
                        break;
                    }

                    if (row > 0) {
                        board.getArray().add(new ArrayList<Cell>());
                    }

                    String[] cells = line.split("\\[\\]");

                    for (String cell : cells) {
                        String[] keyAndValue = cell.trim().split("\\s+");
                        board.getArray().get(row).add(new Cell(keyAndValue[0], keyAndValue[1]));
                    }

                    row++;
                }

                System.out.println("Successfully loaded " + fileName);

            } catch (IOException | NullPointerException e) {
                System.out.println("No save file loaded. Continuing will create " + Utils.DEFAULT_FILE_NAME);
                return false;
            }
            
            return true;
        } else {
            System.out.println("Incorrect type. Cannot load object!");
            return false;
        }
    }

    private BufferedReader getResource(String fileName) throws IOException {
        InputStream inputStream = getClass().getClassLoader().getResourceAsStream(fileName);
        if (inputStream != null) {
            System.out.println("Loading resource from JAR: " + fileName);
            return new BufferedReader(new InputStreamReader(inputStream));
        }

        File file = new File(fileName);
        if (file.exists()) {
            System.out.println("Loading resource from file system: " + fileName);
            return new BufferedReader(new FileReader(file));
        }

        System.out.println("Resource not found: " + fileName);
        return null;
    }


    @Override
    public <T> void save(T obj, String fileName) {
        if (obj instanceof Board board) {
            try (BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(fileName))) {
                int rows = board.getArray().size();
                boolean firstCellPrinted = false;
                
                for (int currentRow = 0; currentRow < rows; currentRow++) {
                    firstCellPrinted = false;

                    for (Cell cell : board.getArray().get(currentRow)) {

                        if (!firstCellPrinted) {
                            bufferedWriter.write(cell.toString());
                            firstCellPrinted = true;
                        } else {
                            bufferedWriter.write("[]" + cell.toString());
                        }
                    }

                    bufferedWriter.newLine();
                }

            } catch (IOException e) {
                System.out.println("Could not save file. An error occurred.");
                e.printStackTrace();
            }
        } else {
            System.out.println("Incorrect type. Cannot save object!");
        }
    }
}
