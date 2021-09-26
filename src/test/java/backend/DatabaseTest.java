package backend;

import org.junit.jupiter.api.Test;

import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

class DatabaseTest {

    @Test
    void extractCol() throws SQLException {
        Database db = new Database();
        db.connectDatabase();
        ArrayList<Object> extractedCol;
        extractedCol = db.extractCol(2);
        for (int i = 0; i < extractedCol.size(); i++) {
            continue;
        }

    }
    @Test
    void filterEmpty() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }

    @Test
    void filterTwoDates() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null, new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/18/2020 08:00:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }
    @Test
    void filterStartDate() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record> str = d.getFilter(null, new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }
    @Test
    void filterEndDate() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null, null,new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/17/2020 11:40:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }

    /**
     * Tests one crime type being passed in
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterCrimeType1() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        crimeTypes.add("THEFT");
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }
    /**
     * Tests multiple crime types being passed in
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterCrimeTypeMultiple() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        crimeTypes.add("THEFT");
        crimeTypes.add("MOTOR VEHICLE THEFT");
        crimeTypes.add("CRIMINAL TRESPASS");
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }
    /**
     * Tests one location description types being passed in
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterLocationDescriptionMultiple() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        locationDescriptions.add("NURSING / RETIREMENT HOME");
        locationDescriptions.add("OTHER (SPECIFY)");
        locationDescriptions.add("SCHOOL - PUBLIC BUILDING");
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,null);
    }
    /**
     * Tests valid ward input
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterValidWard() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String ward = "25";
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,ward,null,null,null,0,null,null);
    }
    /**
     * Tests invalid ward input. Throws SQLexception
     * @throws SQLException
     * @throws ParseException
     */
    @Test()
    void filterInvalidWard() {
        try {
            Database d = new Database();
            d.connectDatabase();
            ArrayList<String> crimeTypes = new ArrayList<String>();
            ArrayList<String> locationDescriptions = new ArrayList<String>();
            String ward = "INVALID";
            ArrayList<Record> str = d.getFilter(null, null, null, crimeTypes, locationDescriptions, ward, null, null, null, 0,null, null);
        } catch (SQLException e){
            System.out.println(e);
        }
    }
    /**
     * Tests valid ward input
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterValidBeat() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String beat = "124";
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,beat,null,null,0,null,null);

        for (Record r:str){
            assert beat.equals(Integer.toString(r.getBeat()));
        }
    }
    /**
     * Tests invalid beat input. Throws SQLexception
     * @throws SQLException
     * @throws ParseException
     */
    @Test()
    void filterInvalidBeat() {
        try {
            Database d = new Database();
            d.connectDatabase();
            ArrayList<String> crimeTypes = new ArrayList<String>();
            ArrayList<String> locationDescriptions = new ArrayList<String>();
            String beat = "INVALID";
            ArrayList<Record> str = d.getFilter(null, null, null, crimeTypes, locationDescriptions, null, beat, null, null, 0,null, null);
        } catch (SQLException e){
            System.out.println(e);
        }
    }
    /**
     * Tests valid arrest input
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterArrestY() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String arrest = "Y";
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,arrest,null);
    }

    /**
     * Tests valid arrest input N
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterArrestN() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String arrest = "N";
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,arrest,null);
    }

    /**
     * Tests valid domestic input Y
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterDomesticY() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String domestic = "Y";
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,domestic);
    }

    /**
     * Tests valid domestic input N
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterDomesticN() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String domestic = "N";
        ArrayList<Record>str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,null,null,0,null,domestic);
    }

    /**
     * Tests radius
     * @throws SQLException
     * @throws ParseException
     */
    @Test
    void filterRadius() throws SQLException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record> str = d.getFilter(null, null,null,crimeTypes,locationDescriptions,null,null,"41.7057609558105","-87.5328750610352",100,null,null);
    }
}