package backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.time.Duration;

public class DataAnalyserTest {
    Record testRecord1, testRecord2, testRecord3, testRecord4;
    ArrayList<String> data;
    DataAnalyser dataAnalyser = new DataAnalyser();

    @BeforeEach
    private void init() {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRecord1 = new Record(data);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2020 03:25:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1155154", "1896404", "41.87154041", "-87.705838807"));
        testRecord2 = new Record(data);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2021 03:25:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord3 = new Record(data);
    }

    @Test
    public void calculateTimeDifferenceTest() {
        Duration difference1 = Duration.ofMinutes(20);
        assertEquals(difference1, dataAnalyser.calculateTimeDifference(testRecord1, testRecord2));
        assertEquals(difference1, dataAnalyser.calculateTimeDifference(testRecord2, testRecord1));

        Duration difference2 = Duration.ofMinutes(0);
        assertEquals(difference2, dataAnalyser.calculateTimeDifference(testRecord2, testRecord2));

        Duration difference3 = Duration.ofDays(365);
        assertEquals(difference3, dataAnalyser.calculateTimeDifference(testRecord2, testRecord3));
    }

    @Test
    public void calculateLocationDifferenceMetersTest() {
        assertEquals(16135, dataAnalyser.calculateLocationDifferenceMeters(testRecord1, testRecord2));
        assertEquals(0, dataAnalyser.calculateLocationDifferenceMeters(testRecord2, testRecord2));
        assertEquals(-1, dataAnalyser.calculateLocationDifferenceMeters(testRecord2, testRecord3));
    }

}