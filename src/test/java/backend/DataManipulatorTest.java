package backend;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tests for Data Manipulator class
 * @author Sofonias Tekele Tesfaye
 */

public class DataManipulatorTest {
    Record testRecord1;
    ArrayList<String> data;
    DataManipulator dataManipulatorInstance;
    ArrayList<Record> recordsList = new ArrayList<>();
    ArrayList<ArrayList<Object> > dataToGraphTest = new ArrayList<ArrayList<Object>>();
    ArrayList<Object> dataCol1 = new ArrayList<>();
    ArrayList<Object> dataCol2 = new ArrayList<>();;

    @Test
    public void addLineTest()
    {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);
        dataManipulatorInstance = new DataManipulator(recordsList);
        assertFalse(dataManipulatorInstance.addLine(1,testRecord1));
        data = new ArrayList<>(Arrays.asList("JE1639901", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        assertTrue(dataManipulatorInstance.addLine(2,testRecord1));
    }


    @Test
    public void getDataToGraphTest()
    {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        recordsList.add(testRecord1);
        dataManipulatorInstance = new DataManipulator(recordsList);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord1 = new Record(data);
        dataManipulatorInstance.addLine(1, testRecord1);
        dataCol1.add("JE163990");
        dataCol1.add("JE1639090");
        dataCol2.add("820");
        dataCol2.add("820");
        dataToGraphTest.add(dataCol1);
        dataToGraphTest.add(dataCol2);
    }
}