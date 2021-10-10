package data;

import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvValidationException;
import importExport.CsvReader;
import importExport.CsvWriter;
import gui.PopupWindow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * DataManipulator class owns Record, Database, writer and reader classes
 * It includes methods for adding, editing and removing, records
 * @author Sofonias Tekele Tesfaye
 * Date 09/10/2021
 */

public class DataManipulator {
    private final List<Record> currentData;
    public DataManipulator(List<Record> currentData) {
    this.currentData = currentData;
}

    /**
     * This method checks if a record has a unique entry
     * @param line interger value representing the line number
     * @param data list of record objects
     * @return true if Record case number is unique or false otherwise
     */
    public boolean hasUniqueCaseNumber(int line, Record data)
    {
        for (int i = 0; i < currentData.size(); i++) {
            if (Objects.equals(currentData.get(i).getCaseNumber(), data.getCaseNumber()) && i != line) {
                return false;
            }
        }
        return true;
    }


    /**
     * Adds a record into CurrentData object
     * @param line usually an integer value representing the line number
     * @param data  usually list of record objects
     * @return true if the record has been added or false otherwise
     */
    public boolean addLine(int line, Record data)
    {
        if (hasUniqueCaseNumber(line, data)) {
            currentData.add(data);
            return true;
        }
        return false;
    }



    /**
     * Takes column number and extracts corresponding column values from currentData object
     * @param col int an integer value representing the column number
     * @return extractedCol Array list of objects taken out from a column in the database
     */
    public static List<Object> extractCol(List<Record> currentData, int col)
    {
        List<Object> extractedCol = new ArrayList<>();
        switch (col) {
            case 0:
                for (Record record : currentData) {
                    extractedCol.add(record.getCaseNumber());
                }
                break;
            case 1:
                for (Record record : currentData) {
                    extractedCol.add(record.getDate());
                }
                break;
            case 2:
                for (Record record : currentData) {
                    extractedCol.add(record.getBlock());
                }
                break;
            case 3:
                for (Record record : currentData) {
                    extractedCol.add(record.getIucr());
                }
                break;
            case 4:
                for (Record record : currentData) {
                    extractedCol.add(record.getPrimaryDescription());
                }
                break;
            case 5:
                for (Record record : currentData) {
                    extractedCol.add(record.getSecondaryDescription());
                }
                break;
            case 6:
                for (Record record : currentData) {
                    extractedCol.add(record.getLocationDescription());
                }
                break;
            case 7:
                for (Record record : currentData) {
                    extractedCol.add(record.getArrest());
                }
                break;
            case 8:
                for (Record record : currentData) {
                    extractedCol.add(record.getDomestic());
                }
                break;
            case 9:
                for (Record record : currentData) {
                    extractedCol.add(record.getBeat());
                }
                break;
            case 10:
                for (Record record : currentData) {
                    extractedCol.add(record.getWard());
                }
                break;
            case 11:
                for (Record record : currentData) {
                    extractedCol.add(record.getFbicd());
                }
                break;
            case 12:
                for (Record record : currentData) {
                    extractedCol.add(record.getXcoord());
                }
                break;
            case 13:
                for (Record record : currentData) {
                    extractedCol.add(record.getYcoord());
                }
                break;
            case 14:
                for (Record record : currentData) {
                    extractedCol.add(record.getLatitude());
                }
                break;
            case 15:
                for (Record record : currentData) {
                    extractedCol.add(record.getLongitude());
                }
                break;
            case 16:
                for (Record record : currentData) {
                    extractedCol.add(record.getLocation());
                }
                break;
            default:
                break;
        }
        return extractedCol;
    }

    /**
     * Gets all record objects from the database
     * @return list of Record objects extracted from the database
     */
    public static List<Record> getAllRecords() throws SQLException {
        CrimeDatabase db = new CrimeDatabase();
        List<Record> currentRecords = db.getAll();
        db.disconnectDatabase();
        return currentRecords;
    }

    /**
     * Calls extractCol Database class method to extract column values from Database Crime Table
     * @param col1 represents column number of the first column to be extracted
     * @param col2 represents column number of the second column to be extracted
     * @return dataToGraph Array<ArrayList<Object>> type that contain list of extracted column values
     */

    public List<List<Object>> dataBaseDataToGraph(String col1, String col2) throws SQLException {
        List<List<Object> > dataToGraph = new ArrayList<>();
        CrimeDatabase db = new CrimeDatabase();
        db.connectDatabase();
        dataToGraph.add(db.extractCol(col1));
        dataToGraph.add(db.extractCol(col2));
        db.disconnectDatabase();
        return dataToGraph;
    }

    /**
     * Saves the current data to CSV file
     * @param fileName String value specifying the name of the Csv file
     * @return boolean value (true if the file has been saved or false otherwise)
     * @throws IOException
     */

    public boolean saveToCsv(String fileName) throws IOException {
        try
        {
            CsvWriter.write("./Files/"+fileName, currentData);
            return true;
        }catch (Exception e)
        {
            PopupWindow.displayPopup("Error", e.getMessage());
        }
        return false;
    }

    /**
     * Reads and returns valid and invalid rows from csv in double dimensional array list object
     * @param filepath contains the string representation of the file path
     * @return An ArrayList containing two objects, the first is a list containing a list of valid records and a list of
     *         invalid records, and the second is a boolean stating whether the file is valid
     * @throws IOException
     * @throws CsvValidationException
     */

    public static List<Object> getRowsfromCsv(String filepath) throws IOException,  CsvValidationException {
        try {
            List<List<List<String>>> result = new ArrayList<>();
            List<List<String>> csvValues;
            List<List<String>> validRows = new ArrayList<>();
            List<List<String>> invalidRows = new ArrayList<>();
            csvValues = CsvReader.read(filepath);

            // Loop through all values read
            for (List<String> rec : csvValues) {
                // Check if row is valid
                if (InputValidator.isValidRecord(rec, false)){
                    validRows.add(rec);
                }
                else {
                    invalidRows.add(rec);
                }
            }
            result.add(validRows);
            result.add(invalidRows);

            return new ArrayList<>(Arrays.asList(result, true));
        } catch (CsvException | IOException | ArrayIndexOutOfBoundsException e) {
            return new ArrayList<>(Arrays.asList(new ArrayList<ArrayList<List<String>>>(), false));
        }
    }
}
