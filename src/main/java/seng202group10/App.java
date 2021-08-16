package seng202group10;
import com.google.gson.Gson;
import seng202group10.Database.Database;

import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException {
        System.out.println( "Hello SENG202 Team 10" );
        System.out.println( "Daniel test commit" );
        System.out.println( "Sofonias test commit" );
        System.out.println( "Sofonias test commit" );


        MyObject myObject = new MyObject("chair", 3);
        Gson gson = new Gson();
        String jsonString = gson.toJson(myObject);

        System.out.println("myObject = " + myObject);
        System.out.println("myObject GSONOBJECT = " + jsonString);
        Database d = new Database();
        d.createDatabase();
        d.connectDatabase();
        d.initialiseTable();
        d.addRow();
    }
}
