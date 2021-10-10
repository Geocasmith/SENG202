package data;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class GraphCreatorTest {

    private final GraphCreator graphCreator = new GraphCreator();

    @Test
    /*
      Tests that the correct time unit is returned to create the graph
     */
    public void calculateFormatForGraphTest() {
        LocalDateTime baseTime = LocalDateTime.now();
        LocalDateTime plusThreeHours = baseTime.plusHours(3);
        LocalDateTime plusThreeDays = baseTime.plusDays(3);
        LocalDateTime plusOneMonth = baseTime.plusMonths(1);
        LocalDateTime plusOneYear = baseTime.plusYears(1);
        LocalDateTime plusThreeYears = baseTime.plusYears(3);
        LocalDateTime plusFiveYears = baseTime.plusYears(5);

        List<Object> calculations;
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

    @Test
    public void roundDateTimeTest() {
        LocalDateTime baseTime = LocalDateTime.now().withMonth(1).withDayOfMonth(1).withHour(0).withMinute(0).withSecond(0);
        LocalDateTime plusThirtySeconds = baseTime.plusSeconds(30);
        LocalDateTime plusThirtyMinutes = baseTime.plusMinutes(30);
        LocalDateTime plusTwelveHours = baseTime.plusHours(12);
        LocalDateTime plusTwoDays = baseTime.plusDays(2);
        LocalDateTime plusTenDays = baseTime.plusDays(10);
        LocalDateTime plusTwentyDays = baseTime.plusDays(20);
        LocalDateTime plusThirtyDays = baseTime.plusDays(30);
        LocalDateTime plusFiveMonths = baseTime.plusMonths(5);

        assertEquals(baseTime, graphCreator.roundDateTime(plusThirtySeconds, "Minutes"));
        assertEquals(baseTime, graphCreator.roundDateTime(plusThirtyMinutes, "Hours"));
        assertEquals(baseTime, graphCreator.roundDateTime(plusTwelveHours, "Days"));
        assertEquals(baseTime, graphCreator.roundDateTime(plusTwoDays, "Weeks"));
        assertEquals(baseTime.plusDays(6), graphCreator.roundDateTime(plusTenDays, "Weeks"));
        assertEquals(baseTime.plusDays(13), graphCreator.roundDateTime(plusTwentyDays, "Weeks"));
        assertEquals(baseTime.plusDays(20), graphCreator.roundDateTime(plusThirtyDays, "Weeks"));
        assertEquals(baseTime, graphCreator.roundDateTime(plusTenDays, "Months"));
        assertEquals(baseTime, graphCreator.roundDateTime(plusFiveMonths, "Years"));

    }
}
