import com.exist.ecc.model.Board;
import com.exist.ecc.model.Cell;
import com.exist.ecc.util.Utils;
import com.exist.ecc.service.BoardServiceImpl;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;

import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;


public class BoardServiceImplTest {

    @Spy
    @InjectMocks
    private BoardServiceImpl boardService;

    @Mock
    private Board board;

    @Mock
    private Cell cell;

    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    private final int DEFAULT_NUMBER_OF_ROWS = 2;
    private final int DEFAULT_NUMBER_OF_COLS = 2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        List<List<Cell>> array = new ArrayList<>();
        List<Cell> row;
        int cellNumber = 0;

        for (int rowNumber = 0; rowNumber < DEFAULT_NUMBER_OF_ROWS; rowNumber++) {
            row = new ArrayList<>();
            for (int colNumber = 0; colNumber < DEFAULT_NUMBER_OF_COLS; colNumber++) {
                cell = mock(Cell.class);
                cellNumber++;
                when(cell.getKey()).thenReturn("key" + cellNumber);
                when(cell.getValue()).thenReturn("value" + cellNumber);
                when(cell.toString()).thenReturn("key" + cellNumber 
                    + " value" + cellNumber);
                row.add(cell);
            }
            array.add(row);
        }

        when(board.getArray()).thenReturn(array);

        doAnswer(invocation -> {
            List<List<Cell>> newArray = invocation.getArgument(0);

            when(board.getArray()).thenReturn(newArray);

            return null;

        }).when(board).setArray(any(List.class));

        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @AfterEach
    private void assertBoardDoesntContainInvalidValues() {
        board.getArray().forEach(row -> {
            row.forEach(cell -> {
                String cellString = cell.getKey() + cell.getValue();
                assertTrue(!cellString.contains("[")
                        && !cellString.contains("]")
                        && !cellString.contains(" "), 
                    "Cell should not contain invalid characters.");
            });
        });
    }

    @Nested
    class TestSearch {
        @ParameterizedTest
        @ValueSource(strings = {"key1", "k", "1", "value2", "v", "2", "key4", "value4", "4"})
        public void testFound(String stringToSearch) {
            assertTrue(boardService.search(board, stringToSearch));
            assertTrue(outputStreamCaptor.toString().contains("Value found"));
            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            assertBoardNotSavedOrLoaded();
        }

        @ParameterizedTest
        @ValueSource(strings = {"keyx", "x", "key 1", "b", "value 2"})
        public void testNotFound(String stringToSearch) {
            assertFalse(boardService.search(board, stringToSearch), 
                "The search should not find the string");
            assertTrue(outputStreamCaptor.toString()
                .contains("Total occurences: 0"), 
                "Should not print found message.");
            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            assertBoardNotSavedOrLoaded();
        }
    }

    @Test
    public void testPrint() {
        boardService.print(board);
        int cellNumber = 0;
        String expectedOutput = "";

        for (int rowNumber = 0; rowNumber < DEFAULT_NUMBER_OF_ROWS; rowNumber++) {
            for (int colNumber = 0; colNumber < DEFAULT_NUMBER_OF_COLS; colNumber++) {
                cellNumber++;
                expectedOutput += String.format("[key%d value%d] ", cellNumber, 
                    cellNumber);
            }
            expectedOutput += System.lineSeparator();
        }

        assertEquals(expectedOutput.trim(), outputStreamCaptor.toString().trim(),
            "Should print the correct board contents.");
        assertBoardValuesUnchangedWithDefaultRowsAndColumns();
        assertBoardNotSavedOrLoaded();
    }

    @Nested
    class TestContainsKey {
        @ParameterizedTest
        @ValueSource(strings = {"key1", "key2", "key3", "key4"})
        public void testKeyFound(String key) {
            assertTrue(boardService.containsKey(board, key));
            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            assertBoardNotSavedOrLoaded();
        }

        @ParameterizedTest
        @ValueSource(strings = {"x", "k", "e", "y", "1", "key"})
        public void testKeyNotFound(String key) {
            assertFalse(boardService.containsKey(board, key));
            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            assertBoardNotSavedOrLoaded();
        }
    }

    @Nested
    class TestEdit {
        @Test
        public void testValidKey() {
            Utils.SCANNER = new Scanner(
                new ByteArrayInputStream("newKey1 newValue1\n".getBytes())
            );
            boardService.edit(board, "key1");

            Cell editedCell = board.getArray().get(0).get(0);
            verify(editedCell).setKey("newKey1");
            verify(editedCell).setValue("newValue1");

            editedCell = board.getArray().get(1).get(1);
            String cellKey = editedCell.getKey();
            Utils.SCANNER = new Scanner(
                new ByteArrayInputStream((cellKey + " newValue4\n").getBytes())
            );
            boardService.edit(board, cellKey);

            verify(editedCell).setKey(cellKey);
            verify(editedCell).setValue("newValue4");

            assertEquals(DEFAULT_NUMBER_OF_ROWS, board.getArray().size(),
                "The board should have " + DEFAULT_NUMBER_OF_ROWS 
                + " rows after editing");
            assertEquals(DEFAULT_NUMBER_OF_COLS, board.getArray().get(0).size(), 
                "Each row should have " + DEFAULT_NUMBER_OF_COLS 
                + " columns after editing");
            assertBoardNotSavedOrLoaded();
        }

        @ParameterizedTest
        @ValueSource(strings = {"x", "k", "e", "y", "1", "key"})
        public void testInvalidKey(String key) {
            Utils.SCANNER = new Scanner(
                new ByteArrayInputStream("newKey newValue\n".getBytes())
            );
            boardService.edit(board, key);

            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            assertBoardNotSavedOrLoaded();
        }
    }

    @ParameterizedTest
    @CsvSource({
        "2, 2",
        "10, 3",
        "4, 5",
        "101, 202"
    })
    public void testReset(int rows, int cols) {
        boardService.reset(board, rows, cols);
        
        assertEquals(rows, board.getArray().size(), 
            "The board should have " + rows + " rows after reset");
        assertEquals(cols, board.getArray().get(0).size(), 
            "Each row should have " + cols + " columns after reset");
        verify(board).setArray(any(List.class));
        assertBoardNotSavedOrLoaded();
    }

    @Test
    public void testAddRow() {
        int addedRows;
        for (addedRows = 0; addedRows < 5; addedRows++) {
            boardService.addRow(board);
            assertEquals(DEFAULT_NUMBER_OF_ROWS + addedRows + 1, 
                board.getArray().size(), "The board should have " 
                + (DEFAULT_NUMBER_OF_ROWS + addedRows + 1) 
                + " rows after adding a row");
        }

        assertBoardValuesUnchanged();
        assertBoardNotSavedOrLoaded();
        assertEquals(DEFAULT_NUMBER_OF_COLS, board.getArray()
            .get(addedRows + DEFAULT_NUMBER_OF_ROWS - 1).size(), 
            "Each row should have " + DEFAULT_NUMBER_OF_COLS 
            + " columns after adding");
    }

    @Nested
    class TestSort {
        @Test
        public void testUnsortedRow() {
            List<Cell> row = new ArrayList<>();
            row.add(new Cell("b", "valueB"));
            row.add(new Cell("a", "valueA"));
            board.getArray().add(row);

            boardService.sortRow(board, DEFAULT_NUMBER_OF_ROWS);
            assertEquals("a", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(0).getKey(), 
                "The row should be sorted by key and value");
            assertEquals("valueA", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(0).getValue(), 
                "The row should be sorted by key and value");
            assertEquals("b", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(1).getKey(), 
                "The row should be sorted by key and value");
            assertEquals("valueB", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(1).getValue(), 
                "The row should be sorted by key and value");
            
            assertBoardValuesUnchanged();
            assertBoardNotSavedOrLoaded();
            assertEquals(DEFAULT_NUMBER_OF_ROWS + 1, board.getArray().size(), 
                "The board should have " + DEFAULT_NUMBER_OF_ROWS 
                + " rows after sorting");
            assertEquals(DEFAULT_NUMBER_OF_COLS, board.getArray().get(0).size(), 
                "Each row should have " + DEFAULT_NUMBER_OF_COLS 
                + " columns after sorting");
        }

        @Test
        public void testSortedRow() {
            List<Cell> row = new ArrayList<>();
            row.add(new Cell("a", "valueA"));
            row.add(new Cell("b", "valueB"));
            board.getArray().add(row);

            boardService.sortRow(board, DEFAULT_NUMBER_OF_ROWS);
            assertEquals("a", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(0).getKey(), 
                "The row should be sorted by key and value");
            assertEquals("valueA", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(0).getValue(), 
                "The row should be sorted by key and value");
            assertEquals("b", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(1).getKey(), 
                "The row should be sorted by key and value");
            assertEquals("valueB", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(1).getValue(), 
                "The row should be sorted by key and value");

            assertBoardValuesUnchanged();
            assertBoardNotSavedOrLoaded();
            assertEquals(DEFAULT_NUMBER_OF_ROWS + 1, 
                board.getArray().size(), 
                "The board should have " 
                + (DEFAULT_NUMBER_OF_ROWS + 1) + " rows after sorting");
            assertEquals(DEFAULT_NUMBER_OF_COLS, 
                board.getArray().get(0).size(), 
                "Each row should have " + DEFAULT_NUMBER_OF_COLS 
                + " columns after sorting");
        }

        @Test
        public void testRowWithSingleElement() {
            List<Cell> row = new ArrayList<>();
            row.add(new Cell("a", "valueA"));
            board.getArray().add(row);

            boardService.sortRow(board, DEFAULT_NUMBER_OF_ROWS);
            assertEquals("a", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(0).getKey(), 
                "The single element's key should still be 'a'.");
            assertEquals("valueA", 
                board.getArray().get(DEFAULT_NUMBER_OF_ROWS).get(0).getValue(), 
                "The single element should have the value 'valueA'.");

            assertBoardValuesUnchanged();
            assertBoardNotSavedOrLoaded();
            assertEquals(DEFAULT_NUMBER_OF_ROWS + 1, board.getArray().size(), 
                "The board should have " + DEFAULT_NUMBER_OF_ROWS 
                + " rows after sorting");
            assertEquals(DEFAULT_NUMBER_OF_COLS, board.getArray().get(0).size(), 
                "Each row should have " + DEFAULT_NUMBER_OF_COLS 
                + " columns after sorting");
        }
    }

    @Test
    public void testSave() throws Exception {
        File testFile = new File("test_save.txt");
        try {
            boardService.save(board, testFile.getName());
            assertTrue(testFile.exists(), "The save file should be created");

            String expectedContent = "";
            int cellNumber = 0;
            boolean firstCellPrinted = false;

            for (int rowNumber = 0; rowNumber < DEFAULT_NUMBER_OF_ROWS; rowNumber++) {
                firstCellPrinted = false;
                for (int colNumber = 0; colNumber < DEFAULT_NUMBER_OF_COLS; colNumber++) {
                    cellNumber++;
                    if (!firstCellPrinted) {
                        expectedContent += "key" + cellNumber + " value" + cellNumber;
                        firstCellPrinted = true;
                    } else {
                        expectedContent += "[]" + "key" + cellNumber + " value" + cellNumber;
                    }
                }
                expectedContent += "\n";
            }

            StringBuilder fileContent = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n");
                }
            }

            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            verify(boardService, never()).load(any(Object.class), any(String.class));
            assertEquals(expectedContent.trim(), fileContent.toString().trim(), 
                "The file content should match the expected format");
        } finally {
            testFile.delete();
        }
    }


    @Test
    public void testLoad() throws Exception {
        File testFile = new File("pre_existing_board.txt");

        try (FileWriter writer = new FileWriter(testFile)) {
            int cellNumber = 0;

            for (int rowNumber = 0; rowNumber < DEFAULT_NUMBER_OF_ROWS; rowNumber++) {
                for (int colNumber = 0; colNumber < DEFAULT_NUMBER_OF_COLS; colNumber++) {
                    cellNumber++;
                    writer.write("key" + cellNumber + " value" + cellNumber + "[]");
                }
                writer.write("\n");
            }
        }

        try {
            boardService.load(board, testFile.getName());

            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
            verify(boardService, never()).save(any(Object.class), any(String.class));
        } finally {
            testFile.delete();
        }
    }

    @Test
    public void testSaveAndLoadBoard() throws Exception {
        File testFile = new File("test_board.txt");
        try {
            boardService.save(board, testFile.getName());
            assertTrue(testFile.exists(), "The save file should be created");

            boardService.load(board, testFile.getName());

            assertBoardValuesUnchangedWithDefaultRowsAndColumns();
        } finally {
            testFile.delete();
        }
    }

    private void assertBoardNotSavedOrLoaded() {
        verify(boardService, never()).save(any(Object.class), any(String.class));
        verify(boardService, never()).load(any(Object.class), any(String.class));
    }

    private void assertBoardValuesUnchanged() {
        int cellNumber = 0;

        for (int rowNumber = 0; rowNumber < DEFAULT_NUMBER_OF_ROWS; rowNumber++) {
            for (int colNumber = 0; colNumber < DEFAULT_NUMBER_OF_COLS; colNumber++) {
                cellNumber++;
                Cell cell = board.getArray().get(rowNumber).get(colNumber);
                assertEquals("key" + cellNumber + " value" + cellNumber, 
                    cell.toString(), "The key and value should not change");
            }
        }
    }

    private void assertBoardValuesUnchangedWithDefaultRowsAndColumns() {
        assertBoardValuesUnchanged();
        assertEquals(DEFAULT_NUMBER_OF_ROWS, board.getArray().size(), 
            "The board should have " + DEFAULT_NUMBER_OF_ROWS 
            + " rows after the operation");
        assertEquals(DEFAULT_NUMBER_OF_COLS, board.getArray().get(0).size(), 
            "Each row should have " + DEFAULT_NUMBER_OF_COLS 
            + " columns after the operation");
    }
}
