package backend;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

/**
 * Hello world!
 *
 */
public class App 
{

    public static void main( String[] args ) throws SQLException, IOException, CsvValidationException, ParseException {

        //Sets up csvReader and database
        CsvReader cs = new CsvReader();
        Database d = new Database();
        d.connectDatabase();
        d.insertRows(cs.read("./Files/seng202_2021_crimes_one_year_prior_to_present_5k.csv"));

        //Examples of using the search function with Casenumber and a string for casenumber
        ArrayList<Record> str = d.getDateRange("06/15/2021 09:10:00 AM","06/15/2021 09:43:00 AM");


        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        str = d.getFilter(new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/05/2020 12:00:00 PM"),new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse("07/18/2020 08:00:00 AM"),crimeTypes,locationDescriptions,null,null,null,null,null,null);
        //Example of using the search function to find a crime at a latitude
        ArrayList<Record> lat = d.searchDB("XCOORDINATE",1176416);

        ArrayList<Record> all = d.getAll();

        //Test using the manual add function
        Record test = all.get(0);
        test.setCaseNumber("JE69");
        d.manualAdd(test);

        //d.manualDelete("JE69");
        test.setBeat(4500);
        d.manualUpdate(test);
//
//        //Prints out values
//        System.out.println("For input terms ID and 41.JE267466 you get the objects");
        int count = 1;
        for (Record r:str){
            System.out.println(count+":"+r.toString());
            count ++;
        }
//        System.out.println();
//
//        System.out.println("For input terms LATITUDE and 41.8916778564453 you get the objects");
//        System.out.println(lat.size());
//        for (Record r:lat){
//            System.out.println(r.toString());
//        }
//
//        for (Record r:all){
//            System.out.println(r.toString());
//        }
    }

}
