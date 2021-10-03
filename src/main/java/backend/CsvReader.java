package backend;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import gui.PopupWindow;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CsvReader {

    public static ArrayList<List<String>> read(String path) throws CsvValidationException, IOException {
        try {
            ArrayList<List<String>> csvValues = new ArrayList<>();
            FileReader fr = new FileReader(path);
            CSVReader csvR = new CSVReader(fr);

            String[] nextRecord;
            csvR.readNext();
            while ((nextRecord = csvR.readNext()) != null) {
                List<String> next = Arrays.asList(nextRecord);
                csvValues.add(next);
            }
            return csvValues;
        } catch (CsvException | IOException e) {
            //PopupWindow.displayPopup("Error", e.getMessage());
        }

        return null;
    }

}
