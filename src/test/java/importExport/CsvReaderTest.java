package importExport;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test class for CsvReader
 * @author George Carr-Smith
 */
class CsvReaderTest {

    @Test
    /**
     * Tests if the CSV reader reads every line
     */
    public void readTest() throws IOException, CsvValidationException {
        CsvReader reader = new CsvReader();
        List<List<String>> readData;
        readData = reader.read("src/test/resources/csvFiles/tenRowsTest.csv");

        //Checks if every line is read
        assertEquals(readData.size(),9);
    }
}