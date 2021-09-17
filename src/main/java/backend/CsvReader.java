package backend;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReader {

    public static ArrayList<List<String>> read() throws IOException, CsvValidationException {

        ArrayList<List<String>>csvValues = new ArrayList<List<String>>();
        FileReader fr = new FileReader("full_crimes.csv");
        CSVReader csvR = new CSVReader(fr);

        String[] nextRecord;
        csvR.readNext();
        while ((nextRecord = csvR.readNext()) != null) {
            List<String>  next = (List<String>) Arrays.asList(nextRecord);
            csvValues.add(next);
        }
    return csvValues;
    }
}
