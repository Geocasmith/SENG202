package seng202group10;

import com.google.gson.Gson;

/**
 * Hello world!
 *
 */
public class App 
{
    public static void main( String[] args )
    {
        System.out.println( "Hello SENG202 Team 10" );
        System.out.println( "Daniel test commit" );

        MyObject myObject = new MyObject("chair", 3);
        Gson gson = new Gson();
        String jsonString = gson.toJson(myObject);

        System.out.println("myObject = " + myObject);
        System.out.println("myObject GSONOBJECT = " + jsonString);
    }
}
