import com.exist.ecc.util.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;

import java.util.Scanner;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class UtilsTest {
    private final PrintStream originalOut = System.out;
    private final InputStream originalIn = System.in;
    private final ByteArrayOutputStream outputStreamCaptor = new ByteArrayOutputStream();

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outputStreamCaptor));
    }

    @Test
    public void testValidGetUnsignedInput() throws java.io.IOException{
        String validHighestInput = String.valueOf(Integer.MAX_VALUE);
        String validLowestInput = 0 + "\n";
        String invalidMessage = "Error message for invalid input";

        resetScanner(new ByteArrayInputStream(validHighestInput.getBytes()));

        assertEquals(Integer.MAX_VALUE, Utils.getUnsignedIntInput(invalidMessage), "The input should be valid");
        assertEquals("", outputStreamCaptor.toString().trim(), "No error message should be printed");

        resetScanner(new ByteArrayInputStream(validLowestInput.getBytes()));

        assertEquals(0, Utils.getUnsignedIntInput(invalidMessage), "The input should be valid");
        assertEquals("", outputStreamCaptor.toString().trim(), "No error message should be printed");
    }

    @Test
    public void testInvalidGetUnsignedInput() {
        String invalidHighestInput = -1 + "\n";
        String invalidLowestInput = Integer.MIN_VALUE + "\n";
        String invalidMessage = "Error message for invalid input";

        resetScanner(new ByteArrayInputStream(invalidHighestInput.getBytes()));

        assertEquals(-1, Utils.getUnsignedIntInput(invalidMessage), "The input should be invalid");
        assertEquals(invalidMessage, outputStreamCaptor.toString().trim(), "Error message should be printed");

        resetScanner(new ByteArrayInputStream(invalidLowestInput.getBytes()));

        assertEquals(-1, Utils.getUnsignedIntInput(invalidMessage), "The input should be invalid");
        assertEquals(invalidMessage, outputStreamCaptor.toString().trim(), "Error message should be printed");
    }

    @Test
    public void testGetStringInput() {
        String input = "Test Input";
        String message = "Enter a string: ";

        resetScanner(new ByteArrayInputStream(input.getBytes()));

        String returnedInput = Utils.getStringInput(message);
        String outputtedMessage = outputStreamCaptor.toString();

        assertEquals(message, outputtedMessage, "The message prompt should be printed");
        assertEquals(message + input, outputtedMessage + returnedInput, "The input should match the expected string");
    }

    @AfterEach
    public void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void resetScanner(ByteArrayInputStream inputStream) {
        outputStreamCaptor.reset();
        Utils.SCANNER = new Scanner(inputStream);
    }
}
