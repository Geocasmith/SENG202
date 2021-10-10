package unitTests;

import data.CrimeDatabase;
import data.Record;
import org.junit.jupiter.api.Test;

import javax.annotation.concurrent.NotThreadSafe;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * This class contains tests for Database class
 * @author
 */
@NotThreadSafe
class CrimeDatabaseTest {

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