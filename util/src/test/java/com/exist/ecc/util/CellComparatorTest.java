import com.exist.ecc.model.Cell;
import com.exist.ecc.util.CellComparator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CellComparatorTest {

    private CellComparator cellComparator;

    @BeforeEach
    public void setUp() {
        cellComparator = new CellComparator();
    }

    @Test
    public void testCompareEqualCells() {
        Cell cell1 = new Cell("AAA", "AAA");
        Cell cell2 = new Cell("AAA", "AAA");

        assertEquals(0, cellComparator.compare(cell1, cell2), "Cells with equal keys and values should be equal");
    }

    @Test
    public void testCompareCellKeyValueOrder() {
        Cell cell1 = new Cell("AAA", "AAA");
        Cell cell2 = new Cell("BBB", "BBB");

        assertEquals(-1, cellComparator.compare(cell1, cell2), "Cell with smaller key should come first");

        cell1 = new Cell("BBB", "BBB");
        cell2 = new Cell("AAA", "SAA");

        assertEquals(1, cellComparator.compare(cell1, cell2), "Cell with smaller key should come first");
    }

    @Test
    public void testCompareCellKeyOrder() {
        Cell cell1 = new Cell("BBB", "AAA");
        Cell cell2 = new Cell("AAA", "AAA");

        assertEquals(1, cellComparator.compare(cell1, cell2), "Cell with smaller value should come first if keys are equal");

        cell1 = new Cell("AAA", "AAA");
        cell2 = new Cell("BBB", "AAA");

        assertEquals(-1, cellComparator.compare(cell1, cell2), "Cell with smaller value should come first");
    }

    @Test
    public void testCompareCellValueOrder() {
        Cell cell1 = new Cell("AAA", "BBB");
        Cell cell2 = new Cell("AAA", "AAA");

        assertEquals(1, cellComparator.compare(cell1, cell2), "Cell with smaller value should come first if keys are equal");

        cell1 = new Cell("AAA", "AAA");
        cell2 = new Cell("AAA", "BBB");

        assertEquals(-1, cellComparator.compare(cell1, cell2), "Cell with smaller value should come first");
    }
}
