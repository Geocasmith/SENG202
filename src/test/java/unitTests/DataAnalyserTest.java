package unitTests;

import backend.DataAnalyser;
import backend.Database;
import backend.Record;
import backend.TypeFrequencyPair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.time.Duration;
import java.util.List;

public class DataAnalyserTest {
    Record testRecord1, testRecord2, testRecord3, testRecord4, testRecord5;
    ArrayList<String> data;
    DataAnalyser dataAnalyser = new DataAnalyser();

    @BeforeEach
    public void init() {
        data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRecord1 = new Record(data);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2020 03:25:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1155154", "1896404", "41.87154041", "-87.705838807"));
        testRecord2 = new Record(data);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2021 03:25:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord3 = new Record(data);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/23/2020 05:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord4 = new Record(data);
        data = new ArrayList<>(Arrays.asList("JE1639090", "11/26/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "", "", "", ""));
        testRecord5 = new Record(data);
    }

    @Test
    public void calculateRecordTimeDifferenceTest() {
        Duration difference1 = Duration.ofMinutes(20);
        assertEquals(difference1, dataAnalyser.calculateTimeDifference(testRecord1, testRecord2));
        assertEquals(difference1, dataAnalyser.calculateTimeDifference(testRecord2, testRecord1));

        Duration difference2 = Duration.ofMinutes(0);
        assertEquals(difference2, dataAnalyser.calculateTimeDifference(testRecord2, testRecord2));

        Duration difference3 = Duration.ofDays(365);
        assertEquals(difference3, dataAnalyser.calculateTimeDifference(testRecord2, testRecord3));
    }

    @Test
    public void calculateObjectTimeDifferenceTest() {
        Duration difference1 = Duration.ofMinutes(20);
        assertEquals(difference1, dataAnalyser.calculateTimeDifference(testRecord1.getDateAsObject(), testRecord2.getDateAsObject()));
        assertEquals(difference1, dataAnalyser.calculateTimeDifference(testRecord2.getDateAsObject(), testRecord1.getDateAsObject()));

        Duration difference2 = Duration.ofMinutes(0);
        assertEquals(difference2, dataAnalyser.calculateTimeDifference(testRecord2.getDateAsObject(), testRecord2.getDateAsObject()));

        Duration difference3 = Duration.ofDays(365);
        assertEquals(difference3, dataAnalyser.calculateTimeDifference(testRecord2.getDateAsObject(), testRecord3.getDateAsObject()));
    }

    @Test
    public void getTimeDifferenceStringTest() {
        assertEquals("20 minutes", dataAnalyser.getTimeDifferenceString(testRecord1, testRecord2));
        assertEquals("2 hours", dataAnalyser.getTimeDifferenceString(testRecord1, testRecord4));
        assertEquals("3 days", dataAnalyser.getTimeDifferenceString(testRecord1, testRecord5));
        assertEquals("12 months", dataAnalyser.getTimeDifferenceString(testRecord2, testRecord3));
    }

    @Test
    public void calculateLocationDifferenceMetersTest() {
        assertEquals(16135, dataAnalyser.calculateLocationDifferenceMeters(testRecord1, testRecord2));
        assertEquals(0, dataAnalyser.calculateLocationDifferenceMeters(testRecord2, testRecord2));
        assertEquals(-1, dataAnalyser.calculateLocationDifferenceMeters(testRecord2, testRecord3));
    }

    @Test
    public void getLocationDifferenceStringTest() {
        assertEquals("0 meters", dataAnalyser.getLocationDifferenceString(testRecord1, testRecord1));
        assertEquals("16 kilometers", dataAnalyser.getLocationDifferenceString(testRecord1, testRecord2));
    }

    @Test
    void getTypeFrequencyTest() throws SQLException {
        Database db = new Database();
        DataAnalyser dataAnalyser = new DataAnalyser(db.getAll());
        List<TypeFrequencyPair> res;
        List<Object> testColumn = new ArrayList<>(Arrays.asList("WEAPONS VIOLATION", "WEAPONS VIOLATION", "WEAPONS VIOLATION", "NARCOTICS",
                "CRIMINAL SEXUAL ASSAULT", "PUBLIC INDECENCY", "NARCOTICS", "NARCOTICS", "NARCOTICS"));
        res = dataAnalyser.getTypeFrequencyDescending(testColumn);
        assertEquals(3, res.get(0).getFrequency());

    }

    @Test
    void getTypeFrequencyTest2() throws SQLException {
        Database db = new Database();
        DataAnalyser dataAnalyser = new DataAnalyser(db.getAll());
        List<TypeFrequencyPair> res;
        List<Object> testColumn = new ArrayList<>(Arrays.asList("WEAPONS VIOLATION", "WEAPONS VIOLATION", "WEAPONS VIOLATION", "WEAPONS VIOLATION",
                "WEAPONS VIOLATION", "WEAPONS VIOLATION", "WEAPONS VIOLATION", "WEAPONS VIOLATION", "WEAPONS VIOLATION"));
        res = dataAnalyser.getTypeFrequencyDescending(testColumn);
        assertEquals(9, res.get(0).getFrequency());

    }

    @Test
    void getTypeFrequencyTest3() throws SQLException {
        Database db = new Database();
        DataAnalyser dataAnalyser = new DataAnalyser(db.getAll());
        List<TypeFrequencyPair> res;
        List<Object> testColumn = new ArrayList<>(List.of("Theft"));
        res = dataAnalyser.getTypeFrequencyDescending(testColumn);

        assertEquals(1, res.get(0).getFrequency());
    }
}
