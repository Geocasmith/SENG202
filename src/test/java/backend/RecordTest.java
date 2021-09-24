package backend;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class RecordTest {
    Record testRec1, testRec2;

    @BeforeEach
    /**
     * Creates a record with no location data (x coordinates, y coordinates, latitude, longitude),
     * and one with all fields filled in.
     */
    public void createTestRecords() {
        List<String> data = Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "", "", "", "");
        testRec1 = new Record(data); // no location data
        data = Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "0", "1", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062");
        testRec2 = new Record(data);
    }

    @Test
    /**
     * Check that the getlocation method correctly returns the location.
     */
    public void locationTest() {
        assertEquals(testRec2.getLocation(), "(41.748486365, -87.602675062)");
    }

    @Test
    /**
     * Checks that getLocation returns null properly.
     */
    public void nullLocationTest() {
        assertNull(testRec1.getLocation());
    }

    @Test
    /**
     * Checks that values are passed in correctly and that the toString() method works with the "labels" option.
     */
    public void toStringLabelsTest() {
        String desiredString2 = "Case Number: JE163990, Date: 11/23/2020 03:05:00 PM, Block: 073XX S SOUTH SHORE DR, IUCR: 820, Primary Description: THEFT, Secondary Description: $500 AND UNDER, Location Description: APARTMENT, Arrest: Y, Domestic: N, Beat: 334, Ward: 7, FBICD: 6, X Coordinate: -1, Y Coordinate: -1, Latitude: null, Longitude: null";
        assertEquals(desiredString2, testRec1.toString("labels"));
        String desiredString3 = "Case Number: JE163990, Date: 11/23/2020 03:05:00 PM, Block: 073XX S SOUTH SHORE DR, IUCR: 820, Primary Description: THEFT, Secondary Description: $500 AND UNDER, Location Description: APARTMENT, Arrest: N, Domestic: Y, Beat: 334, Ward: 7, FBICD: 6, X Coordinate: 1183633, Y Coordinate: 1851786, Latitude: 41.748486365, Longitude: -87.602675062";
        assertEquals(desiredString3, testRec2.toString("labels"));
    }

    @Test
    /**
     * Checks that toString() can work with bad or no format string, ie. it defaults to the behaviour of toString().
     */
    public void toStringBadFormatTest() {
        String desiredString2 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, Y, N, 334, 7, 6, -1, -1, null, null";
        assertEquals(desiredString2, testRec1.toString(""));
        String desiredString3 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, N, Y, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString3, testRec2.toString("1475hv0917"));
    }

    @Test
    /**
     * Checks that values are passed in correctly and that the toString() method works with no options.
     */
    public void toStringNoLabelsTest() {
        String desiredString2 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, Y, N, 334, 7, 6, -1, -1, null, null";
        assertEquals(desiredString2, testRec1.toString());
        String desiredString3 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, N, Y, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString3, testRec2.toString());
    }

    @Test
    /**
     * Checks that the correct list is created when asked for.
     */
    public void toListTest() {
        List<String> desiredList1 = Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "N", "334", "7", "6", "-1", "-1", "null", "null");
        assertEquals(desiredList1, testRec1.toList());
        List<String> desiredList2 = Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "Y", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062");
        assertEquals(desiredList2, testRec2.toList());
    }
}
