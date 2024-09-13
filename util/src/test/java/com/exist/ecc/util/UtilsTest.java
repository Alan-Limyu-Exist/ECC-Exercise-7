import com.exist.ecc.util.Utils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

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

    @Nested
    class TestGetUnsignedIntInput {
        @ParameterizedTest
        @ValueSource(strings = {"0", "1", "5", "100", "1000", "123456"})
        public void testValidInput(String input) throws java.io.IOException{
            String invalidMessage = "Error message for invalid input";

            resetScanner(new ByteArrayInputStream(input.getBytes()));

            assertEquals(Integer.parseInt(input), Utils.getUnsignedIntInput(invalidMessage), "The input should be valid");
            assertEquals("", outputStreamCaptor.toString().trim(), "No error message should be printed");
        }

        @ParameterizedTest
        @ValueSource(strings = {"-1", "-10", "-124", "-1000", "-123456"})
        public void testInvalidInput(String input) {
            String invalidMessage = "Error message for invalid input";

            resetScanner(new ByteArrayInputStream(input.getBytes()));

            assertEquals(-1, Utils.getUnsignedIntInput(invalidMessage), "The input should be invalid");
            assertEquals(invalidMessage, outputStreamCaptor.toString().trim(), "Error message should be printed");
        }
    }

    @ParameterizedTest
    @ValueSource(strings = {"Test Input", "1", "0", "-1", " "})
    public void testGetStringInput(String input) {
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
