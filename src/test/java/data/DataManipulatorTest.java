package data;
import static org.junit.jupiter.api.Assertions.*;

import com.opencsv.exceptions.CsvValidationException;
import importExport.CsvReader;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Tests for Data Manipulator class
 * @author Sofonias Tekele Tesfaye
 */
public class DataManipulatorTest {
    Record testRecord1;
    ArrayList<String> data;
    DataManipulator dataManipulatorInstance;
    ArrayList<Record> recordsList = new ArrayList<>();
    ArrayList<ArrayList<Object> > dataToGraphTest = new ArrayList<ArrayList<Object>>();
    ArrayList<Object> dataCol1 = new ArrayList<>();
    ArrayList<Object> dataCol2 = new ArrayList<>();;

    @Test
    public void addLineTest() {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);
        dataManipulatorInstance = new DataManipulator(recordsList);
        assertFalse(dataManipulatorInstance.addLine(1,testRecord1));
        data = new ArrayList<>(Arrays.asList("JE1639901", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        assertTrue(dataManipulatorInstance.addLine(2,testRecord1));
    }

    @Test
    public void getDataToGraphTest()
    {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);
        dataManipulatorInstance = new DataManipulator(recordsList);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        dataManipulatorInstance.addLine(1, testRecord1);
        dataCol1.add("JE163990");
        dataCol1.add("JE1639090");
        dataCol2.add("820");
        dataCol2.add("820");
        dataToGraphTest.add(dataCol1);
        dataToGraphTest.add(dataCol2);
    }

    @Test
    void hasUniqueCaseNumber() {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);
        dataManipulatorInstance = new DataManipulator(recordsList);
        assertFalse(dataManipulatorInstance.hasUniqueCaseNumber(1, testRecord1));
        assertTrue(dataManipulatorInstance.hasUniqueCaseNumber(0, testRecord1));

    }

    @Test
    void extractColTest() {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);
        data = new ArrayList<>(Arrays.asList("JD442622","11/26/2020 04:45:00 PM","020XX S MICHIGAN AVE","0620","BURGLARY","UNLAWFUL ENTRY","APARTMENT","N","N","132","3","05","1177528","1890620","41.855190551","-87.623871195"));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);

        assertEquals(Arrays.asList("JE163990", "JD442622"), DataManipulator.extractCol(recordsList, 0));
        assertEquals(Arrays.asList("11/23/2020 03:05:00 PM", "11/26/2020 04:45:00 PM"), DataManipulator.extractCol(recordsList, 1));
        assertEquals(Arrays.asList("073XX S SOUTH SHORE DR", "020XX S MICHIGAN AVE"), DataManipulator.extractCol(recordsList, 2));
        assertEquals(Arrays.asList("820", "0620"), DataManipulator.extractCol(recordsList, 3));
        assertEquals(Arrays.asList("THEFT", "BURGLARY"), DataManipulator.extractCol(recordsList, 4));
        assertEquals(Arrays.asList("$500 AND UNDER", "UNLAWFUL ENTRY"), DataManipulator.extractCol(recordsList, 5));
        assertEquals(Arrays.asList("APARTMENT", "APARTMENT"), DataManipulator.extractCol(recordsList, 6));
        assertEquals(Arrays.asList("N", "N"), DataManipulator.extractCol(recordsList, 7));
        assertEquals(Arrays.asList("N", "N"), DataManipulator.extractCol(recordsList, 8));
        assertEquals(Arrays.asList(334, 132), DataManipulator.extractCol(recordsList, 9));
        assertEquals(Arrays.asList(7, 3), DataManipulator.extractCol(recordsList, 10));
        assertEquals(Arrays.asList("6", "05"), DataManipulator.extractCol(recordsList, 11));
        assertEquals(Arrays.asList(-1, 1177528), DataManipulator.extractCol(recordsList, 12));
        assertEquals(Arrays.asList(-1, 1890620), DataManipulator.extractCol(recordsList, 13));
        assertEquals(Arrays.asList(null, 41.855190551), DataManipulator.extractCol(recordsList, 14));
        assertEquals(Arrays.asList(null, -87.623871195), DataManipulator.extractCol(recordsList, 15));
        assertEquals(Arrays.asList(null, "(41.855190551, -87.623871195)"), DataManipulator.extractCol(recordsList, 16));
    }


    @Test
    void getAllRecords() throws SQLException, IOException, CsvValidationException, ParseException {
        CrimeDatabase db = new CrimeDatabase();
        db.connectDatabase();
        db.insertRows(CsvReader.readTest("src/test/resources/csvFiles/tenRowsTest.csv"));

        assertEquals(db.getAll().get(4).getBlock(), DataManipulator.getAllRecords().get(4).getBlock());
        db.disconnectDatabase();

    }



    @Test
    void saveToCsv() {


    }

    @Test
    void getRowsfromCsv() throws IOException, CsvValidationException {
        List<Object> rows = new ArrayList<>();
        rows = DataManipulator.getRowsfromCsv("src/test/resources/csvFiles/tenRowsTest.csv");
        assertTrue((Boolean) rows.get(1));
    }
}