import com.exist.ecc.model.Cell;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class CellTest {

    private Cell cell;

    @BeforeEach
    public void setUp() {
        cell = new Cell("key1", "value1");
    }

    @Test
    public void testConstructor() {
        assertEquals("key1", cell.getKey(), "Key should be initialized correctly");
        assertEquals("value1", cell.getValue(), "Value should be initialized correctly");
    }

    @Test
    public void testSetKey() {
        cell.setKey("key2");

        assertEquals("key2", cell.getKey(), "Key should be updated correctly");
    }

    @Test
    public void testSetValue() {
        cell.setValue("value2");

        assertEquals("value2", cell.getValue(), "Value should be updated correctly");
    }

    @Test
    public void testToString() {
        assertEquals("key1 value1", cell.toString(), "toString should return key and value concatenated with a space");

        cell.setKey("key2");
        cell.setValue("value2");
        assertEquals("key2 value2", cell.toString(), "toString should reflect updated key and value");
    }

    @Test
    public void testEmptyKeyAndValue() {
        Cell emptyCell = new Cell("", "");

        assertEquals("", emptyCell.getKey(), "Key should be empty");
        assertEquals("", emptyCell.getValue(), "Value should be empty");
        assertEquals(" ", emptyCell.toString(), "toString should return an empty key and value separated by a space");
    }
}
