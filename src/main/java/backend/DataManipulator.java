package backend;

import backend.database.Database;
import com.opencsv.exceptions.CsvException;
import com.opencsv.exceptions.CsvMalformedLineException;
import com.opencsv.exceptions.CsvValidationException;
import gui.PopupWindow;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * DataManipulator class owns Record, Database, writer and reader classes
 * It includes methods for adding, editing and removing, records
 */

public class DataManipulator {
private ArrayList<Record> currentData;


    DataManipulator(ArrayList<Record> currentData) {
    this.currentData = currentData;
}

    /**
     * This method checks if a record has a unique entry
     * @param line Integer
     * @param data Record
     * @return true if Record case number is unique or false otherwise
     */
    public boolean hasUniqueCaseNumber(int line, Record data)
{
    for (int i = 0; i < currentData.size(); i++) {
        if (Objects.equals(currentData.get(i).getCaseNumber(), data.getCaseNumber())) {
            if (i != line) {
                return false;
            }

        }
    }

    return true;
}

    /**
     *
     * @param line
     * @param data
     * @return true if data is edited or false otherwise
     */
    public boolean editLine(int line, Record data) throws IOException {
        try {
            if (hasUniqueCaseNumber(line, data)) {
                currentData.set(line, data);
                return true;

        }

    }catch(Exception e){
            //Display error message?
        }
    return false;
}

    /**
     * Adds a record into CurrentData object
     * @param line
     * @param data
     * @return true if the record has been added or false otherwise
     */
    public boolean addLine(int line, Record data) {
    if (hasUniqueCaseNumber(line, data)) {
        currentData.add(data);
        return true;
    }

    return false;
    }

    /**
     * Removes a record from list of records in currentData object
     * @param line
     */
    public void deleteLine(int line) {
        currentData.remove(line);
    }

    /**
     * Takes column number and extracts corresponding column values from currentData object
     * @param col int
     * @return extractedCol
     */
    public static ArrayList<Object> extractCol(ArrayList<Record> currentData, int col)
    {
        int colSize = currentData.size();
        ArrayList<Object> extractedCol = new ArrayList<>();
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
     * Calls extractCol DataManipulator class method to extract column values from CurrentData record object
     * @param col1 represents column number of the first column to be extracted
     * @param col2 represents column number of the second column to be extracted
     * @return dataToGraph Array<ArrayList<Object>> type that contain list of extracted column values
     */
    public ArrayList<ArrayList<Object>> getCurrentDataToGraph(int col1, int col2)
    {
        ArrayList<ArrayList<Object> > dataToGraph = new ArrayList<ArrayList<Object>>();
        //dataToGraph.add(extractCol(col1));
        //dataToGraph.add(extractCol(col2));
        return dataToGraph;

    }

    /**
     * Gets all record objects from the database
     * @return
     * @throws SQLException
     */

    public static ArrayList<Record> getAllRecords() throws SQLException {
        Database d = new Database();
        ArrayList<Record> records = d.getAll();
        d.closeConnection();
        return records;
    }

    /**
     * Calls extractCol Database class method to extract column values from Database Crime Table
     * @param col1 represents column number of the first column to be extracted
     * @param col2 represents column number of the second column to be extracted
     * @return dataToGraph Array<ArrayList<Object>> type that contain list of extracted column values
     */

    public ArrayList<ArrayList<Object>> dataBaseDataToGraph(int col1, int col2) throws SQLException {
        ArrayList<ArrayList<Object> > dataToGraph = new ArrayList<ArrayList<Object>>();
        Database db = new Database();
        db.connectDatabase();
        dataToGraph.add(db.extractCol(db.getColName(col1)));
        dataToGraph.add(db.extractCol(db.getColName(col2)));
        db.closeConnection();
        return dataToGraph;

    }

    /**
     * Saves the current data to CSV file
     * @param fileName name of the Csv file
     * @return
     * @throws IOException
     */

    public boolean saveToCsv(String fileName) throws IOException {
        try
        {
            CsvWriter.write("./Files/"+fileName, currentData);
            return true;
        }catch (Exception e)
        {
            //Display Error Message?
        }
        return false;
    }

    /**
     * Reads and returns valid and invalid rows from CSV
     * @param filepath
     * @return
     * @throws IOException
     * @throws CsvValidationException
     */

    public static ArrayList<ArrayList<List<String>>> getRowsfromCsv(String filepath) throws IOException,  CsvValidationException {
        try
        {
            ArrayList<ArrayList<List<String>>> result = new ArrayList<ArrayList<List<String>>>();
            ArrayList<List<String>> csvValues = new  ArrayList<List<String>>();
            ArrayList<List<String>> validRows = new ArrayList<List<String>>();
            ArrayList<List<String>> invalidRows = new ArrayList<List<String>>();
            csvValues = CsvReader.read(filepath);
            for (List<String> rec : csvValues) {
                if (InputValidator.isValidRecord(rec, false)){
                    validRows.add(rec);
                }
                else {
                    invalidRows.add(rec);
                }
            }
            result.add(validRows);
            result.add(invalidRows);

            return result;
        } catch (CsvException e) {
            PopupWindow.displayPopup("Error", e.getMessage());
        } catch (IOException exception) {
            PopupWindow.displayPopup("Error", exception.getMessage());
        }
       return null;
    }

}
