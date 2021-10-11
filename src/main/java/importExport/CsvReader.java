package importExport;


import com.opencsv.CSVReader;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import gui.PopupWindow;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * This class contains a methods to read data from a CSV File
 * @author George Carr-Smith
 * Date 09/10/2021
 */
public class CsvReader {

    /**
     * Reads rows from the CSV file to a List of Strings and returns a List all the rows in the CSV file
     * @param path                      A String representing the path to the CSV file
     * @return                          List of Lists of Strings which containing all the rows in the CSV File
     * @throws CsvValidationException   If an exception occurs while reading the CSV file
     * @throws IOException              If an exception occurs while reading the file
     */
    public static List<List<String>> read(String path) throws CsvValidationException, IOException {
        try {

            //Gets the CSV file and opens a CSV Reader
            ArrayList<List<String>> csvValues = new ArrayList<>();
            FileReader fr = new FileReader(path);
            try (CSVReader csvR = new CSVReader(fr)) {

                //Reads each row from the CSV file
                String[] nextRecord;
                csvR.readNext();
                while ((nextRecord = csvR.readNext()) != null) {

                    //Converts each row to a list of Strings
                    List<String> next = Arrays.asList(nextRecord);
                    csvValues.add(next);
                }
            }
            return csvValues;
        } catch (CsvException | IOException e) {

            //Pops up an error window if a CSV read exception has occured
            PopupWindow.displayPopup("Error", "There was an error reading the given CSV. Please try again.");
        }

        return null;
    }

    /**
     * Reads rows from the CSV file to a List of Strings and returns a List all the rows in the CSV file but does
     * not create a pop up window (unit tests do not allow for pop up windows in threads which are not the main one)
     * @param path                      A String representing the path to the CSV file
     * @return                          List of Lists of Strings which containing all the rows in the CSV File
     * @throws CsvValidationException   If an exception occurs while reading the CSV file
     * @throws IOException              If an exception occurs while reading the file
     */
    public static List<List<String>> readTest(String path) throws CsvValidationException, IOException {
        try {

            //Gets the CSV file and opens a CSV Reader
            ArrayList<List<String>> csvValues = new ArrayList<>();
            FileReader fr = new FileReader(path);
            try (CSVReader csvR = new CSVReader(fr)) {

                //Reads each row from the CSV file
                String[] nextRecord;
                csvR.readNext();
                while ((nextRecord = csvR.readNext()) != null) {

                    //Converts each row to a list of Strings
                    List<String> next = Arrays.asList(nextRecord);
                    csvValues.add(next);
                }
            }
            return csvValues;
        } catch (CsvException | IOException e) {

        }

        return null;
    }

}
