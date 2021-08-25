package backend;
import backend.readerwriter.CsvWriter;
import backend.readerwriter.csvReader;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Test class for CsvWriter
 */
class CsvWriterTest {
    /**
     *
     * @throws IOException
     * @throws CsvValidationException
     */
    @Test
    public void writeTest() throws IOException, CsvValidationException {
        csvReader reader = new csvReader();
        ArrayList<List<String>> readData;
        readData = reader.read();
        ArrayList<Record> recordsList = new ArrayList<>();
        for (int i = 0; i < readData.size(); i++) {
            Record record = new Record(readData.get(i));
            recordsList.add(record);
        }
        CsvWriter testWrite = new CsvWriter();
        testWrite.write("Test.csv", recordsList);


    }

}