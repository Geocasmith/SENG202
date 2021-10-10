package cucumberTests;

import backend.*;
import backend.Record;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.SQLException;
import java.text.ParseException;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.Assert.*;

public class StepDefinitions {

    Database db = new Database();
    DataAnalyser dataAnalyser = new DataAnalyser();
    Duration analysisTimeResult;
    int analysisDistanceResult;
    int columnNumber;

    List<TypeFrequencyPair> crimeFrequencyPairList;
    List<TypeFrequencyPair> blocksFrequencyPairList;
    ArrayList<Record> records;
    Record record1;
    Record record2;
    private static final int crimeTypeColumn = 4;
    private static final int blockColumn = 2;

    @Given("I am connected to a database")
    public void iAmConnectedToADatabase() throws SQLException {
        db.setDatabasePath("src/test/resources/twoRecordTestDatabase.db");
        db.connectDatabase();
    }

    @And("I have a record")
    public void iHaveARecord() {
        record1 = new Record(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "0", "0", "0", "0"));
    }

    @When("I add the record to the database")
    public void iAddARecordToTheDatabase() throws SQLException, ParseException {
        db.manualAdd(record1);
    }

    @And("I have a record in the database")
    public void iHaveARecordInTheDatabase() throws SQLException, ParseException {
        record1 = new Record(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "0", "0", "0", "0"));
        db.manualAdd(record1);
    }

    @Then("the record should be in the database")
    public void theRecordShouldBeInTheDatabase() throws SQLException {
        assertEquals(record1.getCaseNumber(), db.searchDB("ID", record1.getCaseNumber()).get(0).getCaseNumber());
        db.disconnectDatabase();
    }

    @When("I delete a record from the database")
    public void iDeleteARecordFromTheDatabase() throws SQLException {
        db.manualDelete(record1.getCaseNumber());
    }

    @Then("the record should not be in the database")
    public void theRecordShouldNotBeInTheDatabase() throws SQLException {
        assertEquals(0, db.searchDB("ID", record1.getCaseNumber()).size());
        db.disconnectDatabase();
    }

    @When("I change the date of the record to {string}")
    public void iEditTheDateOfTheRecord(String newDate) throws SQLException, ParseException {
        record1 = new Record(Arrays.asList("JE163990", newDate, "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "0", "0", "0", "0"));
        db.manualUpdate(record1);
    }

    @Then("the date of the record in the database should be {string}")
    public void theDateOfTheRecordInTheDatabaseShouldBe(String newDate) throws SQLException {
        assertEquals(newDate, db.searchDB("ID", record1.getCaseNumber()).get(0).getDate());
        db.disconnectDatabase();
    }

    @Given("I have two records that occurred {int} minutes apart")
    public void iHaveTwoRecordsThatOccurredMinutesApart(int timeDifferenceMinutes) {
        record1 = new Record(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "0", "0", "0", "0"));
        record2 = new Record(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "0", "0", "0", "0"));
        record2.setDate(record1.getDateAsObject().plusMinutes(timeDifferenceMinutes));
    }

    @When("I analyse these two records time difference")
    public void iAnalyseTheseTwoRecordsTimeDifference() {
        analysisTimeResult = dataAnalyser.calculateTimeDifference(record1, record2);
    }

    @Then("the analysis should say the records occurred {int} minutes apart")
    public void theAnalysisShouldSayTheRecordsOccurredMinutesApart(int timeDifferenceMinutes) {
        assertEquals(timeDifferenceMinutes, (int) (analysisTimeResult.getSeconds()/60));
    }

    @Given("I have two records that occurred {int} kilometers apart")
    public void iHaveTwoRecordsThatOccurredKilometersApart(int locationDifferenceMeters) {
        record1 = new Record(Arrays.asList("JE1639090", "11/23/2020 03:25:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1155154", "1896404", "41.87154041", "-87.705838807"));
        record2 = new Record(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
    }

    @When("I analyse these two records location difference")
    public void iAnalyseTheseTwoRecordsLocationDifference() {
        analysisDistanceResult = dataAnalyser.calculateLocationDifferenceMeters(record1, record2);
    }

    @Then("the analysis should say the records occurred {int} kilometers apart")
    public void theAnalysisShouldSayTheRecordsOccurredKilometersApart(int locationDifferenceMeters) {
        assertEquals(locationDifferenceMeters, analysisDistanceResult / 1000);
    }

    @Given("I have a list of records")
    public void iHaveAListOfRecords() throws SQLException {
        db.setDatabasePath("src/test/resources/200kRecordTestDatabase.db");
        db.connectDatabase();
        records = db.getAll();
        db.disconnectDatabase();
    }

    @When("I sort the records by descending crime type frequency")
    public void iSortTheRecordsByDescendingCrimeTypeFrequency() {
        crimeFrequencyPairList = dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(records, crimeTypeColumn));
        crimeFrequencyPairList.sort(new FrequencyComparatorDescending());
    }

    @Then("the crime types should be in descending frequency order")
    public void theCrimeTypesShouldBeInDescendingFrequencyOrder() {
        TypeFrequencyPair previousType = crimeFrequencyPairList.get(0);
        for (TypeFrequencyPair currentType : crimeFrequencyPairList.subList(1, crimeFrequencyPairList.size()-1)) {
            if (currentType.getFrequency() > previousType.getFrequency()) {
                fail();
            }
            previousType = currentType;
        }
    }

    @When("I sort the records by descending block frequency")
    public void iSortTheRecordsByDescendingBlockFrequency() {
        blocksFrequencyPairList = dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(records, blockColumn));
        blocksFrequencyPairList.sort(new FrequencyComparatorDescending());
    }

    @Then("the blocks should be in descending frequency order")
    public void theBlocksShouldBeInDescendingFrequencyOrder() {
        TypeFrequencyPair previousType = blocksFrequencyPairList.get(0);
        for (TypeFrequencyPair currentType : blocksFrequencyPairList.subList(1, blocksFrequencyPairList.size()-1)) {
            if (currentType.getFrequency() > previousType.getFrequency()) {
                fail();
            }
            previousType = currentType;
        }
    }

    @Given("I am connected to a database that contains records")
    public void iAmConnectedToADatabaseThatContainsRecords() throws SQLException {
        db.setDatabasePath("src/test/resources/200kRecordTestDatabase.db");
        db.connectDatabase();
    }

    @When("I filter the crimes to only crimes in {string} {int}")
    public void iFilterTheCrimesToOnlyCrimesIn(String filterType, int filterValue) throws SQLException {
        if (filterType.equals("ward")) {
            records = db.getFilter(null, null, null, new ArrayList<>(), new ArrayList<>(), String.valueOf(filterValue), null, null, null, 0, null, null);
        } else {
            records = db.getFilter(null, null, null, new ArrayList<>(), new ArrayList<>(), null, String.valueOf(filterValue), null, null, 0, null, null);
        }
        db.disconnectDatabase();
    }

    @Then("only crimes from {string} {int} should be shown")
    public void onlyCrimesFromShouldBeShown(String filterType, int filterValue) {
        if (filterType.equals("ward")) {
            columnNumber = 10;
            } else {
            columnNumber = 9;
            }
        List<Object> filterResults = DataManipulator.extractCol(records, columnNumber);

        for (Object o : filterResults) {
            if ((int) o != filterValue) {
                fail();
            }
        }
    }

    @When("I filter the crimes to only crimes of type {string}")
    public void iFilterTheCrimesToOnlyCrimesOfType(String crimeType) throws SQLException {
        records = db.getFilter(null, null, null, new ArrayList<>(List.of(crimeType)), new ArrayList<>(), null, null, null, null, 0, null, null);
        db.disconnectDatabase();
    }

    @Then("only crimes of type {string} should be shown")
    public void onlyCrimesOfTypeShouldBeShown(String crimeType) {
        columnNumber = crimeTypeColumn;
        List<Object> filterResults = DataManipulator.extractCol(records, columnNumber);

        for (Object o : filterResults) {
            if (!o.equals(crimeType)) {
                fail();
            }
        }
    }

    @When("I filter the crimes to only crimes from a {string}")
    public void iFilterTheCrimesToOnlyCrimesFromA(String crimeLocation) throws SQLException {
        records = db.getFilter(null, null, null, new ArrayList<>(), new ArrayList<>(List.of(crimeLocation)), null, null, null, null, 0, null, null);
        db.disconnectDatabase();
    }

    @Then("only crimes from a {string} should be shown")
    public void onlyCrimesFromAShouldBeShown(String crimeLocation) {
        columnNumber = 6;
        List<Object> filterResults = DataManipulator.extractCol(records, columnNumber);

        for (Object o : filterResults) {
            if (!o.equals(crimeLocation)) {
                fail();
            }
        }
    }

    @When("I filter the crimes to only crimes within {int} meters of \\({float}, {float})")
    public void iFilterTheCrimesToOnlyCrimesWithinMetersOf(int radius, float lat, float lon) throws SQLException {
        records = db.getFilter(null, null, null, new ArrayList<>(), new ArrayList<>(), null, null, String.valueOf(lat), String.valueOf(lon), radius, null, null);
        db.disconnectDatabase();
    }

    @Then("only crimes within {int} meters of \\({float}, {float}) should be shown")
    public void onlyCrimesWithinMetersOfShouldBeShown(int radius, float lat, float lon) {
        int latColumnNumber = 14;
        int lonColumnNumber = 15;

        List<Object> latResults = DataManipulator.extractCol(records, latColumnNumber);
        List<Object> lonResults = DataManipulator.extractCol(records, lonColumnNumber);

        int distanceMeters;

        for (int i = 0; i < latResults.size(); i++) {
            distanceMeters = dataAnalyser.calculateLocationDifferenceMeters(lat, lon, (float) latResults.get(i), (float) lonResults.get(i));
            if (distanceMeters > radius) {
                fail();
            }
        }
    }
}
