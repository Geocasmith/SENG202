package backend;
import com.google.gson.Gson;
import backend.database.Database;
import backend.readerwriter.csvReader;
import com.opencsv.exceptions.CsvValidationException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args ) throws SQLException, IOException, CsvValidationException {



        MyObject myObject = new MyObject("chair", 3);
        Gson gson = new Gson();
        String jsonString = gson.toJson(myObject);

        System.out.println("myObject = " + myObject);
        System.out.println("myObject GSONOBJECT = " + jsonString);
        csvReader cs = new csvReader();

        Database d = new Database();
        d.connectDatabase();
        d.insertRows(cs.read());

    }
}
