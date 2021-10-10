package unitTests;

import importExport.CsvReader;
import importExport.CsvWriter;
import data.Record;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for CsvWriter
 * @author Sofonias Tekele Tesfaye
 */
class CsvWriterTest {

    /**
     * Gets the CSV reader to read a fle and then write it to a file.
     * @throws IOException
     * @throws CsvValidationException
     */
    @Test
    public void writeTest() throws IOException, CsvValidationException {
        CsvReader reader = new CsvReader();
        List<List<String>> readData;
        readData = reader.read("./Files/seng202_2021_crimes_one_year_prior_to_present_5k.csv");
        ArrayList<Record> recordsList = new ArrayList<>();
        for (int i = 0; i < readData.size(); i++) {
            Record record = new Record(readData.get(i));
            recordsList.add(record);
        }
        CsvWriter testWrite = new CsvWriter();
        testWrite.write("./Files/Test.csv", recordsList);
    }
}