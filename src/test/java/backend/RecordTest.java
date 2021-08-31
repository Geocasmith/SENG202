package backend;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

public class RecordTest {
    Record testRecord;

    @BeforeEach
    public void createTestRecord() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRecord = new Record(data);
    }

    @Test
    public void locationTest() {
        assertEquals(testRecord.getLocation(), "(41.748486365, -87.602675062)");
    }

    @Test
    public void nullLocationTest() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord = new Record(data);
        assertNull(testRecord.getLocation());
    }

    @Test
    /**
     * Checks that values are passed in correctly and that the toString() method works with the "labels" option.
     */
    public void toStringLabelsTest() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRecord = new Record(data);
        String desiredString = "Case Number: JE163990, Date: 11/23/2020 03:05:00 PM, Block: 073XX S SOUTH SHORE DR, IUCR: 820, Primary Description: THEFT, Secondary Description: $500 AND UNDER, Location Description: APARTMENT, Arrest: N, Domestic: N, Beat: 334, Ward: 7, FBICD: 6, X Coordinate: 1183633, Y Coordinate: 1851786, Latitude: 41.748486365, Longitude: -87.602675062";
        assertEquals(desiredString, testRecord.toString("labels"));
    }

    @Test
    /**
     * Checks that values are passed in correctly and that the toString() method works with no options.
     */
    public void toStringNoLabelsTest() {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRecord = new Record(data);
        String desiredString = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, N, N, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString, testRecord.toString());
    }
}
