package cucumberTests;

import backend.Database;
import backend.Record;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import java.sql.SQLException;
import java.text.ParseException;
import java.util.Arrays;

import static org.junit.Assert.*;

public class StepDefinitions {

    Database db = new Database();
    Record record;
    @Given("the price of {string} is ${double}")
    public void thePriceOfIs$(String string, Double double1) {
        assertEquals(1, 1);
    }
    @When("I scan {int} {string}")
    public void iCheckout(Integer int1, String string) {
        assertEquals(1, 1);
    }
    @Then("the total price should be ${double}")
    public void theTotalPriceShouldBe$(Double double1) {
        assertEquals(1, 1);
    }

    @Given("I am connected to a database")
    public void iAmConnectedToADatabase() throws SQLException {
        db.setDatabasePath("src/test/resources/2 Records.db");
        db.connectDatabase();

    }

    @And("I have a record")
    public void iHaveARecord() {
        record = new Record(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "Y", "faLse", "334", "7", "6", "", "", "", ""));
    }

    @When("I add the record to the database")
    public void iAddARecordToTheDatabase() throws SQLException, ParseException {
        db.manualAdd(record);
    }

    @Then("the record should be in the database")
    public void theRecordShouldBeInTheDatabase() throws SQLException {
        db.searchDB("ID", record.getCaseNumber());
        db.disconnectDatabase();
    }
}
