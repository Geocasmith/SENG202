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
    void filterTwoDates() throws SQLException, ParseException {
        Database d = new Database();
        d.connectDatabase();
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        ArrayList<Record>str = d.getFilter(new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/18/2020 08:00:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,null,null);

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
        ArrayList<Record>str = d.getFilter(new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),null,crimeTypes,locationDescriptions,null,null,null,null,null,null);

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
        ArrayList<Record>str = d.getFilter(null,null,crimeTypes,locationDescriptions,null,null,null,null,null,null);

        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
    }
}