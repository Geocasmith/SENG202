package backend.database;

import backend.Record;
import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class DatabaseTest {

    @Test
    void extractCol() throws SQLException, CsvValidationException, IOException {
        Database db = new Database();
        db.connectDatabase();
        ArrayList<Object> extractedCol = new ArrayList<>();
        extractedCol = db.extractCol(2);
        for (int i = 0; i < extractedCol.size(); i++) {
            System.out.println(extractedCol.get(i));
        }

    }
    @Test
    void filterEmpty() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }

    @Test
    void filterTwoDates() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/18/2020 08:00:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }
    @Test
    void filterStartDate() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }
    @Test
    void filterEndDate() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null,new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/17/2020 11:40:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }

    /**
     * Tests one crime type being passed in
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterCrimeType1() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        crimeTypes.add("THEFT");
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }
    /**
     * Tests multiple crime types being passed in
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterCrimeTypeMultiple() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        crimeTypes.add("THEFT");
        crimeTypes.add("MOTOR VEHICLE THEFT");
        crimeTypes.add("CRIMINAL TRESPASS");
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }
    /**
     * Tests one location description types being passed in
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterLocationDescriptionMultiple() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        locationDescriptions.add("NURSING / RETIREMENT HOME");
        locationDescriptions.add("OTHER (SPECIFY)");
        locationDescriptions.add("SCHOOL - PUBLIC BUILDING");
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){

            assert locationDescriptions.contains(r.getLocationDescription());

        }
    }
    /**
     * Tests valid ward input
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterValidWard() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String ward = "25";
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,ward,null,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
            assert ward.equals(Integer.toString(r.getWard()));

        }
    }
    /**
     * Tests invalid ward input. Throws SQLexception
     * @throws SQLException
     * @throws ParseException
     */
    @Test()
    void filterInvalidWard() throws SQLException, ParseException {
        try {
            Database d = new Database();
            d.connectDatabase();
            ArrayList<String> crimeTypes = new ArrayList<String>();
            ArrayList<String> locationDescriptions = new ArrayList<String>();
            String ward = "INVALID";
            ArrayList<Record> str = d.getFilter(null, null, crimeTypes, locationDescriptions, ward, null, null, null, 0,null, null);

            int count = 1;
            for (Record r : str) {
                System.out.println(count + ":" + r.toString());
                count++;


            }
        }catch (SQLException e){
            System.out.println(e);
            //assert e.equals("org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (no such column: INVALID)");
        }
    }
    /**
     * Tests valid ward input
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterValidBeat() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String beat = "124";
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,beat,null,null,0,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
            assert beat.equals(Integer.toString(r.getBeat()));

        }
    }
    /**
     * Tests invalid beat input. Throws SQLexception
     * @throws SQLException
     * @throws ParseException
     */
    @Test()
    void filterInvalidBeat() throws SQLException, ParseException {
        try {
            Database d = new Database();
            d.connectDatabase();
            ArrayList<String> crimeTypes = new ArrayList<String>();
            ArrayList<String> locationDescriptions = new ArrayList<String>();
            String beat = "INVALID";
            ArrayList<Record> str = d.getFilter(null, null, crimeTypes, locationDescriptions, null, beat, null, null, 0,null, null);

            int count = 1;
            for (Record r : str) {
                System.out.println(count + ":" + r.toString());
                count++;


            }
        }catch (SQLException e){
            System.out.println(e);
            //assert e.equals("org.sqlite.SQLiteException: [SQLITE_ERROR] SQL error or missing database (no such column: INVALID)");
        }
    }
    /**
     * Tests valid arrest input
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterArrestY() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String arrest = "Y";
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,arrest,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;


        }
    }/**
     * Tests valid arrest input N
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterArrestN() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String arrest = "N";
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,arrest,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;


        }
    }

    /**
     * Tests valid domestic input Y
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterDomesticY() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String domestic = "Y";
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,domestic);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;

        }
    }
    /**
     * Tests valid domestic input N
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterDomesticN() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String domestic = "N";
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,domestic);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;

        }
    }
    /**
     * Tests radius
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterRadius() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,"41.7057609558105","-87.5328750610352",100,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;

        }
    }
}