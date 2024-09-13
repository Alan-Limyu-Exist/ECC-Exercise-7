import com.exist.ecc.model.Board;
import com.exist.ecc.model.Cell;
import com.exist.ecc.util.Utils;
import com.exist.ecc.service.BoardServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
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

public class BoardServiceImplTest {

    private BoardServiceImpl boardService;
    private Board board;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        boardService = new BoardServiceImpl();
        board = new Board();
        List<List<Cell>> array = new ArrayList<>();
        List<Cell> row = new ArrayList<>();
        row.add(new Cell("key1", "value1"));
        row.add(new Cell("key2", "value2"));
        array.add(row);

        row = new ArrayList<>();
        row.add(new Cell("key3", "value3"));
        row.add(new Cell("key4", "value4"));
        array.add(row);

        board.setArray(array);
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Nested
    class TestSearch {
        @ParameterizedTest
        @ValueSource(strings = {"key1", "k", "1", "value2", "v", "2", "key4", "value4", "4"})
        public void testFound(String stringToSearch) {
            assertTrue(boardService.search(board, stringToSearch));
            assertTrue(outputStreamCaptor.toString().contains("Value found"));
            assertBoardValuesUnchanged();
        }

        @ParameterizedTest
        @ValueSource(strings = {"key5", "x", "key 1", "value5", "value 2"})
        public void testNotFound(String stringToSearch) {
            assertFalse(boardService.search(board, stringToSearch), "The search should not find the string");
            assertTrue(outputStreamCaptor.toString().contains("Total occurences: 0"), "Should not print found message.");
            assertBoardValuesUnchanged();
        }
    }

    @Test
    public void testPrint() {
        boardService.print(board);
        assertTrue(outputStreamCaptor.toString().contains("[key1 value1] [key2 value2]")
            && outputStreamCaptor.toString().contains("[key3 value3] [key4 value4]"),
            "Should print the correct board contents.");
        assertBoardValuesUnchanged();
    }

    @Nested
    class TestContainsKey {
        @ParameterizedTest
        @ValueSource(strings = {"key1", "key2", "key3", "key4"})
        public void testKeyFound(String key) {
            assertTrue(boardService.containsKey(board, key));
            assertBoardValuesUnchanged();
        }

        @ParameterizedTest
        @ValueSource(strings = {"x", "k", "e", "y", "1", "key"})
        public void testKeyNotFound(String key) {
            assertFalse(boardService.containsKey(board, key));
            assertBoardValuesUnchanged();
        }
    }

    @Nested
    class TestEdit {
        @Test
        public void testValidKey() {
            Utils.SCANNER = new Scanner(new ByteArrayInputStream("newKey1 newValue1\n".getBytes()));
            boardService.edit(board, "key1");

            Cell editedCell = board.getArray().get(0).get(0);
            assertEquals("newKey1", editedCell.getKey(), "The key should be updated");
            assertEquals("newValue1", editedCell.getValue(), "The value should be updated");

            Utils.SCANNER = new Scanner(new ByteArrayInputStream("newKey4 newValue4\n".getBytes()));
            boardService.edit(board, "key4");

            editedCell = board.getArray().get(1).get(1);
            assertEquals("newKey4", editedCell.getKey(), "The key should be updated");
            assertEquals("newValue4", editedCell.getValue(), "The value should be updated");

            editedCell = board.getArray().get(1).get(0);
            assertEquals("key3", editedCell.getKey(), "The key should not be updated");
            assertEquals("value3", editedCell.getValue(), "The value should not be updated");

            editedCell = board.getArray().get(0).get(1);
            assertEquals("key2", editedCell.getKey(), "The key should not be updated");
            assertEquals("value2", editedCell.getValue(), "The value should not be updated");

            assertEquals(2, board.getArray().size(), "The board should have 2 rows after editing");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after editing");
        }

        @ParameterizedTest
        @ValueSource(strings = {"x", "k", "e", "y", "1", "key"})
        public void testInvalidKey(String key) {
            Utils.SCANNER = new Scanner(new ByteArrayInputStream("newKey newValue\n".getBytes()));
            boardService.edit(board, key);

            assertBoardValuesUnchanged();
            assertEquals(2, board.getArray().size(), "The board should have 2 rows after editing");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after editing");
        }
    }

    @Test
    public void testReset() {
        boardService.reset(board, 2, 2);
        assertEquals(2, board.getArray().size(), "The board should have 2 rows after reset");
        assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after reset");

        boardService.reset(board, 10, 3);
        assertEquals(10, board.getArray().size(), "The board should have 10 rows after reset");
        assertEquals(3, board.getArray().get(0).size(), "Each row should have 3 columns after reset");

        boardService.reset(board, 4, 5);
        assertEquals(4, board.getArray().size(), "The board should have 4 rows after reset");
        assertEquals(5, board.getArray().get(0).size(), "Each row should have 5 columns after reset");

        boardService.reset(board, 101, 202);
        assertEquals(101, board.getArray().size(), "The board should have 101 rows after reset");
        assertEquals(202, board.getArray().get(0).size(), "Each row should have 202 columns after reset");
    }

    @Test
    public void testAddRow() {
        boardService.addRow(board);
        assertEquals(3, board.getArray().size(), "The board should have 3 rows after adding a row");

        boardService.addRow(board);
        assertEquals(4, board.getArray().size(), "The board should have 4 rows after adding a row");

        boardService.addRow(board);
        assertEquals(5, board.getArray().size(), "The board should have 5 rows after adding a row");

        assertBoardValuesUnchanged();
        assertEquals(5, board.getArray().size(), "The board should have 5 rows after adding");
        assertEquals(2, board.getArray().get(4).size(), "Each row should have 3 columns after adding");
    }

    @Nested
    class TestSort {
        @Test
        public void testUnsortedRow() {
            List<Cell> row = new ArrayList<>();
            row.add(new Cell("b", "valueB"));
            row.add(new Cell("a", "valueA"));
            board.getArray().add(row);

            boardService.sortRow(board, 2);
            assertEquals("a", board.getArray().get(2).get(0).getKey(), "The row should be sorted by key and value");
            assertEquals("valueA", board.getArray().get(2).get(0).getValue(), "The row should be sorted by key and value");
            assertEquals("b", board.getArray().get(2).get(1).getKey(), "The row should be sorted by key and value");
            assertEquals("valueB", board.getArray().get(2).get(1).getValue(), "The row should be sorted by key and value");
            
            assertBoardValuesUnchanged();
            assertEquals(3, board.getArray().size(), "The board should have 3 rows after sorting");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after sorting");
        }

        @Test
        public void testSortedRow() {
            List<Cell> row = new ArrayList<>();
            row.add(new Cell("a", "valueA"));
            row.add(new Cell("b", "valueB"));
            board.getArray().add(row);

            boardService.sortRow(board, 0);
            assertEquals("a", board.getArray().get(2).get(0).getKey(), "The row should be sorted by key and value");
            assertEquals("valueA", board.getArray().get(2).get(0).getValue(), "The row should be sorted by key and value");
            assertEquals("b", board.getArray().get(2).get(1).getKey(), "The row should be sorted by key and value");
            assertEquals("valueB", board.getArray().get(2).get(1).getValue(), "The row should be sorted by key and value");

            assertBoardValuesUnchanged();
            assertEquals(3, board.getArray().size(), "The board should have 3 rows after sorting");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after sorting");
        }

        @Test
        public void testRowWithSingleElement() {
            List<Cell> row = new ArrayList<>();
            row.add(new Cell("a", "valueA"));
            board.getArray().add(row);

            boardService.sortRow(board, 2);
            assertEquals("a", board.getArray().get(2).get(0).getKey(), "The single element's key should still be 'a'.");
            assertEquals("valueA", board.getArray().get(2).get(0).getValue(), "The single element should have the value 'valueA'.");

            assertBoardValuesUnchanged();
            assertEquals(3, board.getArray().size(), "The board should have 3 rows after sorting");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after sorting");
        }
    }

    @Test
    public void testSave() throws Exception {
        File testFile = new File("test_save.txt");
        try {
            boardService.save(board, testFile.getName());
            assertTrue(testFile.exists(), "The save file should be created");

            String expectedContent = "key1 value1[]key2 value2\nkey3 value3[]key4 value4\n";
            StringBuilder fileContent = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(testFile))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    fileContent.append(line).append("\n"); // Include new line character
                }
            }

            assertBoardValuesUnchanged();
            assertEquals(expectedContent, fileContent.toString(), "The file content should match the expected format");
        } finally {
            testFile.delete();
        }
    }


    @Test
    public void testLoad() throws Exception {
        File testFile = new File("pre_existing_board.txt");

        try (FileWriter writer = new FileWriter(testFile)) {
            writer.write("key1 value1[]key2 value2\n");
            writer.write("key3 value3[]key4 value4\n");
        }

        try {
            boardService.load(board, testFile.getName());

            assertBoardValuesUnchanged();
            assertEquals(2, board.getArray().size(), "The board should have 2 rows after loading");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after loading");
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

            assertBoardValuesUnchanged();
            assertEquals(2, board.getArray().size(), "The board should have 2 rows after loading");
            assertEquals(2, board.getArray().get(0).size(), "Each row should have 2 columns after loading");
        } finally {
            testFile.delete();
        }
    }

    private void assertBoardValuesUnchanged() {
        assertEquals("key1", board.getArray().get(0).get(0).getKey(), "The key should not change");
        assertEquals("value1", board.getArray().get(0).get(0).getValue(), "The value should not change");
        assertEquals("key2", board.getArray().get(0).get(1).getKey(), "The key should not change");
        assertEquals("value2", board.getArray().get(0).get(1).getValue(), "The value should not change");
        assertEquals("key3", board.getArray().get(1).get(0).getKey(), "The key should not change");
        assertEquals("value3", board.getArray().get(1).get(0).getValue(), "The value should not change");
        assertEquals("key4", board.getArray().get(1).get(1).getKey(), "The key should not change");
        assertEquals("value4", board.getArray().get(1).get(1).getValue(), "The value should not change");
    }
}
