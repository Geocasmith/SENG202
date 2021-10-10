package unitTests;

import data.GraphCreator;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphCreatorTest {

    private final GraphCreator graphCreator = new GraphCreator();

    @Test
    public void calculateFormatForGraphTest() {
        LocalDateTime baseTime = LocalDateTime.now();
        LocalDateTime plusThreeHours = baseTime.plusHours(3);
        LocalDateTime plusThreeDays = baseTime.plusDays(3);
        LocalDateTime plusOneMonth = baseTime.plusMonths(1);
        LocalDateTime plusOneYear = baseTime.plusYears(1);
        LocalDateTime plusThreeYears = baseTime.plusYears(3);
        LocalDateTime plusFiveYears = baseTime.plusYears(5);

        ArrayList<Object> calculations;
        String requiredDuration;

        // Test Minutes
        calculations = graphCreator.calculateFormatForGraph(new ArrayList<>(Arrays.asList(baseTime, plusThreeHours)));
        requiredDuration = (String) calculations.get(0);
        assertEquals("Minutes", requiredDuration);

        // Test Hours
        calculations = graphCreator.calculateFormatForGraph(new ArrayList<>(Arrays.asList(baseTime, plusThreeDays)));
        requiredDuration = (String) calculations.get(0);
        assertEquals("Hours", requiredDuration);

        // Test Days
        calculations = graphCreator.calculateFormatForGraph(new ArrayList<>(Arrays.asList(baseTime, plusOneMonth)));
        requiredDuration = (String) calculations.get(0);
        assertEquals("Days", requiredDuration);

        // Test Weeks
        calculations = graphCreator.calculateFormatForGraph(new ArrayList<>(Arrays.asList(baseTime, plusOneYear)));
        requiredDuration = (String) calculations.get(0);
        assertEquals("Weeks", requiredDuration);

        // Test Months
        calculations = graphCreator.calculateFormatForGraph(new ArrayList<>(Arrays.asList(baseTime, plusThreeYears)));
        requiredDuration = (String) calculations.get(0);
        assertEquals("Months", requiredDuration);

        // Test Years
        calculations = graphCreator.calculateFormatForGraph(new ArrayList<>(Arrays.asList(baseTime, plusFiveYears)));
        requiredDuration = (String) calculations.get(0);
        assertEquals("Years", requiredDuration);



    }
}
