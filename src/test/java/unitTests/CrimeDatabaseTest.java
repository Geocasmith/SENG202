package unitTests;

import com.opencsv.exceptions.CsvValidationException;
import data.CrimeDatabase;
import data.Record;
import importExport.CsvReader;
import org.junit.jupiter.api.Test;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * This class contains tests for Database class
 * @author
 */
@NotThreadSafe
class CrimeDatabaseTest {

    @Test
    /**
     * Creates new table and checks if it is valid
     */
    void createTable() throws SQLException, IOException, CsvValidationException, ParseException {
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/createTableTest.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/createTableTest.db");

        try{
            db.deleteTable("CRIMES");
            db.createTable();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }


        assert(db.checkValidDB());
    }
    @Test
    void deleteTable() throws SQLException, IOException, CsvValidationException, ParseException {
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/deleteTableTest.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/deleteTableTest.db");

        db.insertRows(CsvReader.read("./test/Files/smallTest.csv"));

        db.deleteTable("CRIMES");
        assertEquals("0",db.getNumRows());
    }

    @Test
    void insertRows() throws SQLException, IOException, CsvValidationException, ParseException {
        //Initialises empty table
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/test.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/test.db");
        db.deleteTable("CRIMES");

        //Inserts rows from csv
        db.insertRows(CsvReader.read("src/test/Files/tenRowsTest.csv"));

        //Checks if number of rows correct
        assertEquals("9",db.getNumRows());
        //checks if last row exists
        assertEquals("JE266604",db.searchDB("ID","JE266604").get(0).getCaseNumber());
    }
    @Test
    void replaceRows() throws SQLException, IOException, CsvValidationException, ParseException {
        //Initialises empty table
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/test.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/test.db");
        db.deleteTable("CRIMES");

        //Inserts 5000 rows
        db.insertRows(CsvReader.read("src/test/Files/smallTest.csv"));

        //Checks if the rows were imported correctly
        assertEquals("4997",db.getNumRows());

        //Replaces with 10 rows
        db.replaceRows(CsvReader.read("src/test/Files/tenRowsTest.csv"));

        //checks if last row exists
        assertEquals("JE266604",db.searchDB("ID","JE266604").get(0).getCaseNumber());
        //Checks if number of rows correct
        assertEquals("9",db.getNumRows());
    }
    @Test
    void manualAdd() throws SQLException, IOException, ParseException {
        //Initialises empty table
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/manualTest.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/manualTest.db");
        db.deleteTable("CRIMES");

        //Creates new record
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        Record testRecord1 = new Record(data);

        //Adds record to table
        db.manualAdd(testRecord1);

        //Checks if row exists in table
        assertEquals("JE163990",db.searchDB("ID","JE163990").get(0).getCaseNumber());
    }
    @Test
    void manualUpdate() throws SQLException, IOException, ParseException {
        //Initialises empty table
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/manualTest.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/manualTest.db");
        db.deleteTable("CRIMES");

        //Creates new record
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        Record testRecord1 = new Record(data);

        //Adds record to table
        db.manualAdd(testRecord1);

        //Create new record
        ArrayList<String> data2 = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "999", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        Record testRecord2 = new Record(data2);

        //Updates record (changes IUCR to 999)
        db.manualUpdate(testRecord2);
        //Checks if row exists in table
        assertEquals("999",db.searchDB("ID","JE163990").get(0).getIucr());
    }

    @Test
    void manualDelete() throws SQLException, IOException, ParseException {
        //Initialises empty table
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/manualDeleteTest.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/manualDeleteTest.db");
        db.deleteTable("CRIMES");

        //Creates and new record
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        Record testRecord1 = new Record(data);
        db.manualAdd(testRecord1);

        //Delete record
        db.manualDelete("JE163990");

        //Checks if row deleted (no rows exist)
        assertEquals("0",db.getNumRows());
    }
    @Test
    void searchDB() throws SQLException, IOException, ParseException {
        //Initialises empty table
        CrimeDatabase db = new CrimeDatabase();
        File file = new File("src/test/Files/searchDBTest.db");
        file.createNewFile();
        db.setDatabasePath("src/test/Files/searchDBTest.db");
        db.deleteTable("CRIMES");

        //Creates and new record
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        Record testRecord1 = new Record(data);
        db.manualAdd(testRecord1);

        //Tests manual search for Strings (sees if the searched record is correct)
        Record r = db.searchDB("ID","JE163990").get(0);
        assertEquals("JE163990",r.getCaseNumber());
    }
//    @Test
//    void getAll() throws SQLException, IOException, ParseException, CsvValidationException {
//        //Initialises empty table
//        CrimeDatabase db = new CrimeDatabase();
//        File file = new File("src/test/Files/getAllTest.db");
//        file.createNewFile();
//        db.setDatabasePath("src/test/Files/getAllTest.db");
//        db.deleteTable("CRIMES");
//
//        //Inserts rows from csv
//        db.insertRows(CsvReader.read("src/test/Files/tenRowsTest.csv"));
//
//        //Gets all rows
//        List<Record> records = db.getAll();
//        assertEquals(records.size(),Integer.parseInt(db.getNumRows()));
//    }
    @Test
    void extractCol() throws SQLException {
        CrimeDatabase db = new CrimeDatabase();
        db.connectDatabase();
        List<Object> extractedCol;
        extractedCol = db.extractCol("ADDRESS");
        for (int i = 0; i < extractedCol.size(); i++) {
            continue;
        }
        db.disconnectDatabase();

    }
    @Test
    void filterEmpty() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record> str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }

    @Test
    void filterTwoDates() throws SQLException, ParseException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record> str = d.getFilter(null, new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/18/2020 08:00:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }
    @Test
    void filterStartDate() throws SQLException, ParseException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record> str = d.getFilter(null, new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }
    @Test
    void filterEndDate() throws SQLException, ParseException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record> str = d.getFilter(null, null,new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/17/2020 11:40:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }

    /**
     * Tests one crime type being passed in
     * @throws SQLException
     */
    @Test
    void filterCrimeType1() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        crimeTypes.add("THEFT");
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }
    /**
     * Tests multiple crime types being passed in
     * @throws SQLException
     */
    @Test
    void filterCrimeTypeMultiple() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        crimeTypes.add("THEFT");
        crimeTypes.add("MOTOR VEHICLE THEFT");
        crimeTypes.add("CRIMINAL TRESPASS");
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }
    /**
     * Tests one location description types being passed in
     * @throws SQLException
     */
    @Test
    void filterLocationDescriptionMultiple() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        locationDescriptions.add("NURSING / RETIREMENT HOME");
        locationDescriptions.add("OTHER (SPECIFY)");
        locationDescriptions.add("SCHOOL - PUBLIC BUILDING");
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
        d.disconnectDatabase();
    }
    /**
     * Tests valid ward input
     * @throws SQLException
     */
    @Test
    void filterValidWard() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String ward = "25";
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,ward,null,null,null,0,null,null);
        d.disconnectDatabase();
    }
    /**
     * Tests invalid ward input. Throws SQLexception
     */
    @Test()
    void filterInvalidWard() {
        try {
            CrimeDatabase d = new CrimeDatabase();
            d.connectDatabase();
            ArrayList<String> crimeTypes = new ArrayList<String>();
            ArrayList<String> locationDescriptions = new ArrayList<String>();
            String ward = "INVALID";
            List<Record> str = d.getFilter(null, null, null, crimeTypes, locationDescriptions, ward, null, null, null, 0,null, null);
            d.disconnectDatabase();
        } catch (SQLException e){
            System.out.println(e);
        }
    }
    /**
     * Tests valid ward input
     * @throws SQLException
     */
    @Test
    void filterValidBeat() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String beat = "124";
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,beat,null,null,0,null,null);

        for (Record r:str){
            assert beat.equals(Integer.toString(r.getBeat()));
        }
        d.disconnectDatabase();
    }
    /**
     * Tests invalid beat input.
     */
    @Test()
    void filterInvalidBeat() {
        try {
            CrimeDatabase d = new CrimeDatabase();
            d.connectDatabase();
            ArrayList<String> crimeTypes = new ArrayList<String>();
            ArrayList<String> locationDescriptions = new ArrayList<String>();
            String beat = "INVALID";
            List<Record> str = d.getFilter(null, null, null, crimeTypes, locationDescriptions, null, beat, null, null, 0,null, null);
            d.disconnectDatabase();
        } catch (SQLException e){
            System.out.println(e);
        }
    }
    /**
     * Tests valid arrest input
     * @throws SQLException
     */
    @Test
    void filterArrestY() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String arrest = "Y";
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,arrest,null);
        d.disconnectDatabase();
    }

    /**
     * Tests valid arrest input N
     * @throws SQLException
     */
    @Test
    void filterArrestN() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String arrest = "N";
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,arrest,null);
        d.disconnectDatabase();
    }

    /**
     * Tests valid domestic input Y
     * @throws SQLException
     */
    @Test
    void filterDomesticY() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String domestic = "Y";
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,domestic);
        d.disconnectDatabase();
    }

    /**
     * Tests valid domestic input N
     * @throws SQLException
     */
    @Test
    void filterDomesticN() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String domestic = "N";
        List<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,domestic);
        d.disconnectDatabase();
    }

    /**
     * Tests radius
     * @throws SQLException
     */
    @Test
    void filterRadius() throws SQLException {
        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        List<Record> str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,"41.7057609558105","-87.5328750610352",100,null,null);
        d.disconnectDatabase();
    }
}