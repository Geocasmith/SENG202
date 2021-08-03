package seng202.group10;

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

        MyObject myObject = new MyObject("chair", 3);
        Gson gson = new Gson();
        String jsonString = gson.toJson(myObject);

        System.out.println("myObject = " + myObject);
        System.out.println("myObject stringfield = " + jsonString);
    }
}

/**
 * Jonathan test edit 2
 */
