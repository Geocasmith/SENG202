package backend;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class csvReader {

    public ArrayList<List<String>> read() throws IOException, CsvValidationException {

        ArrayList<List<String>>csvValues = new ArrayList<List<String>>();
        FileReader fr = new FileReader("seng202_2021_crimes_one_year_prior_to_present_5k.csv");
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
