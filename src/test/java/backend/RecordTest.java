package backend;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class RecordTest {
    Record testRec1, testRec2, testRec3;

    @BeforeEach
    public void createTestRecords() {
        List<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "TrUE", "asdf", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRec1 = new Record(data); // null domestic flag
        data = Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "", "", "", "");
        testRec2 = new Record(data); // no location data
        data = Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "0", "1", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062");
        testRec3 = new Record(data);
    }

    @Test
    /**
     * Check that the getlocation method correctly returns the location.
     */
    public void locationTest() {
        assertEquals(testRec1.getLocation(), "(41.748486365, -87.602675062)");
    }

    @Test
    public void nullLocationTest() {
        assertNull(testRec2.getLocation());
    }

    @Test
    /**
     * Checks that values are passed in correctly and that the toString() method works with the "labels" option.
     */
    public void toStringLabelsTest() {
        String desiredString1 = "Case Number: JE163990, Date: 11/23/2020 03:05:00 PM, Block: 073XX S SOUTH SHORE DR, IUCR: 820, Primary Description: THEFT, Secondary Description: $500 AND UNDER, Location Description: APARTMENT, Arrest: Y, Domestic: null, Beat: 334, Ward: 7, FBICD: 6, X Coordinate: 1183633, Y Coordinate: 1851786, Latitude: 41.748486365, Longitude: -87.602675062";
        assertEquals(desiredString1, testRec1.toString("labels"));
        String desiredString2 = "Case Number: JE163990, Date: 11/23/2020 03:05:00 PM, Block: 073XX S SOUTH SHORE DR, IUCR: 820, Primary Description: THEFT, Secondary Description: $500 AND UNDER, Location Description: APARTMENT, Arrest: Y, Domestic: N, Beat: 334, Ward: 7, FBICD: 6, X Coordinate: -1, Y Coordinate: -1, Latitude: null, Longitude: null";
        assertEquals(desiredString2, testRec2.toString("labels"));
        String desiredString3 = "Case Number: JE163990, Date: 11/23/2020 03:05:00 PM, Block: 073XX S SOUTH SHORE DR, IUCR: 820, Primary Description: THEFT, Secondary Description: $500 AND UNDER, Location Description: APARTMENT, Arrest: N, Domestic: Y, Beat: 334, Ward: 7, FBICD: 6, X Coordinate: 1183633, Y Coordinate: 1851786, Latitude: 41.748486365, Longitude: -87.602675062";
        assertEquals(desiredString3, testRec3.toString("labels"));
    }

    @Test
    /**
     * Checks that toString() can work with bad or no format string, ie. it defaults to the behaviour of toString().
     */
    public void toStringBadFormatTest() {
        String desiredString1 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, Y, null, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString1, testRec1.toString("asdf"));
        String desiredString2 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, Y, N, 334, 7, 6, -1, -1, null, null";
        assertEquals(desiredString2, testRec2.toString(""));
        String desiredString3 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, N, Y, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString3, testRec3.toString("1475hv0917"));
    }

    @Test
    /**
     * Checks that values are passed in correctly and that the toString() method works with no options.
     */
    public void toStringNoLabelsTest() {
        String desiredString1 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, Y, null, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString1, testRec1.toString());
        String desiredString2 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, Y, N, 334, 7, 6, -1, -1, null, null";
        assertEquals(desiredString2, testRec2.toString());
        String desiredString3 = "JE163990, 11/23/2020 03:05:00 PM, 073XX S SOUTH SHORE DR, 820, THEFT, $500 AND UNDER, APARTMENT, N, Y, 334, 7, 6, 1183633, 1851786, 41.748486365, -87.602675062";
        assertEquals(desiredString3, testRec3.toString());
    }
}
