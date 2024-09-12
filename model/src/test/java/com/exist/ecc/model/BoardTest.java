import com.exist.ecc.model.Board;
import com.exist.ecc.model.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class BoardTest {

    private Board board;
    private List<List<Cell>> cells;

    @BeforeEach
    public void setUp() {
        board = new Board();

        cells = new ArrayList<>();
        List<Cell> row0 = new ArrayList<>();
        row0.add(new Cell("row0key1", "row0value1"));
        row0.add(new Cell("row0key2", "row0value2"));

        List<Cell> row1 = new ArrayList<>();
        row1.add(new Cell("row1key1", "row1value1"));
        row1.add(new Cell("row1key2", "row1value2"));
        cells.add(row0);
        cells.add(row1);
    }

    @Test
    public void testSetArray() {
        board.setArray(cells);

        assertEquals(cells, board.getArray(), "The array should be set correctly");
    }

    @Test
    public void testGetArray() {
        board.setArray(cells);

        List<List<Cell>> result = board.getArray();

        assertNotNull(result, "The array should not be null");
        assertEquals(2, result.size(), "The array should contain 2 rows");
        assertEquals(2, result.get(0).size(), "The first row should contain 2 cells");
        assertEquals("row0key1", result.get(0).get(0).getKey(), "row0 cell0 should have the correct key");
        assertEquals("row0value1", result.get(0).get(0).getValue(), "row0 cell0 should have the correct value");
        assertEquals("row0key2", result.get(0).get(1).getKey(), "row0 cell1 should have the correct key");
        assertEquals("row0value2", result.get(0).get(1).getValue(), "row0 cell1 should have the correct value");
        assertEquals("row1key1", result.get(1).get(0).getKey(), "row1 cell0 should have the correct key");
        assertEquals("row1value1", result.get(1).get(0).getValue(), "row1 cell0 should have the correct value");
        assertEquals("row1key2", result.get(1).get(1).getKey(), "row1 cell1 should have the correct key");
        assertEquals("row1value2", result.get(1).get(1).getValue(), "row1 cell1 should have the correct value");
    }

    @Test
    public void testGetArrayWithEmptyList() {
        board.setArray(new ArrayList<>());

        List<List<Cell>> result = board.getArray();

        assertNotNull(result, "The array should not be null even if it's empty");
        assertEquals(0, result.size(), "The array should be empty");
    }
}
