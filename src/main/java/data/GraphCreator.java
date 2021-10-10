package data;

import javafx.scene.chart.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This class contains methods for creating graphs that automatically scale to time periods and group data points that
 * are from the same time period
 * @author Bede Skinner-Vennell
 * Date 09/10/2021
 */
public class GraphCreator {
    private static final DateTimeFormatter minuteHourFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm a", Locale.ENGLISH);
    private static final DateTimeFormatter dayWeekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);

    private static final DataAnalyser dataAnalyser = new DataAnalyser();

    private static final int ONEMINUTEINSECONDS = 60;
    private static final int ONEHOURINSECONDS = 3600;
    private static final int ONEDAYINSECONDS = 86400;
    private static final int ONEWEEKINSECONDS = 604800;
    private static final int ONEMONTHINSECONDS = 2678400;
    private static final int ONEYEARINSECONDS = 31622400;

    /**
     * Rounds the given LocalDateTime down to the given duration
     * @param timeToRound A LocalDateTime object that needs to be rounded
     * @param requiredDuration A string containing the time duration the timeToRound needs to be rounded down to
     * @return A LocalDateTime object that has been rounded to the given duration
     */
    private LocalDateTime roundDateTime(LocalDateTime timeToRound, String requiredDuration) {
        switch (requiredDuration) {
            case "Minutes":
                timeToRound = timeToRound.withSecond(0);
                break;
            case "Hours":
                timeToRound = timeToRound.withMinute(0).withSecond(0);
                break;
            case "Days":
                timeToRound = timeToRound.withHour(0);
                break;
            case "Weeks":
                if (timeToRound.getDayOfMonth() <= 7) {
                    timeToRound = timeToRound.withDayOfMonth(1);
                } else if (timeToRound.getDayOfMonth() <= 14) {
                    timeToRound = timeToRound.withDayOfMonth(7);
                } else if (timeToRound.getDayOfMonth() <= 21) {
                    timeToRound = timeToRound.withDayOfMonth(14);
                } else {
                    timeToRound = timeToRound.withDayOfMonth(21);
                }
                break;
            case "Months":
                timeToRound = timeToRound.withDayOfMonth(1);
                break;
            case "Years":
                timeToRound = timeToRound.withMonth(1);
                break;
        }
        return timeToRound;
    }

    /**
     * Calculates the total range of the given list of LocalDateTime objects and uses that range to choose the spacing
     * for the graph that fits the best
     * @param times A sorted arrayList of LocalDateTimes
     * @return An ArrayList of Objects containing a String of the required duration, a DateTime formatter and a Duration
     *         object of the required time period
     */
    public List<Object> calculateFormatForGraph(List<LocalDateTime> times) {
        Duration width = dataAnalyser.calculateTimeDifference(times.get(0), times.get(times.size() - 1));

        Duration periodInSeconds;
        DateTimeFormatter formatter;
        String requiredDuration;
        if (width.getSeconds() <= 3 * ONEHOURINSECONDS) {
            requiredDuration = "Minutes";
            formatter = minuteHourFormatter;
            periodInSeconds = Duration.ofSeconds(ONEMINUTEINSECONDS);
        } else if (width.getSeconds() <= 3 * ONEDAYINSECONDS) {
            requiredDuration = "Hours";
            formatter = minuteHourFormatter;
            periodInSeconds = Duration.ofSeconds(ONEHOURINSECONDS);
        } else if (width.getSeconds() <= ONEMONTHINSECONDS) {
            requiredDuration = "Days";
            formatter = dayWeekFormatter;
            periodInSeconds = Duration.ofSeconds(ONEDAYINSECONDS);
        } else if (width.getSeconds() <= ONEYEARINSECONDS) {
            requiredDuration = "Weeks";
            formatter = dayWeekFormatter;
            periodInSeconds = Duration.ofSeconds(ONEWEEKINSECONDS);
        } else if (width.getSeconds() <= 3 * ONEYEARINSECONDS) {

            requiredDuration = "Months";
            formatter = monthFormatter;
            periodInSeconds = Duration.ofSeconds(ONEMONTHINSECONDS);
        } else {
            requiredDuration = "Years";
            formatter = yearFormatter;
            periodInSeconds = Duration.ofSeconds(ONEYEARINSECONDS);
        }
        return new ArrayList<>(Arrays.asList(requiredDuration, formatter, periodInSeconds));
    }

    /**
     * Runs through all the time periods contained between lower bound and max upper bound, counts the number of crimes
     * in that time period and adds the count to the series, with the lower bound as the key.
     * @param periodInSeconds The time period the time objects need to be rounded to
     * @param formatter The relevant datetime formatter
     * @param series The XYChart.Series object for the times to be added to
     * @param requiredDuration The time period the time objects need to be rounded to in a string form
     * @param lowerBound The earliest time object in the data set
     * @param maxUpperBound The last time object in the data set
     */
    private void sortCrimesByTimePeriod(ArrayList<LocalDateTime> times, Duration periodInSeconds, DateTimeFormatter formatter, XYChart.Series series, String requiredDuration, LocalDateTime lowerBound, LocalDateTime maxUpperBound) {
        int i = 0;
        int count;

        LocalDateTime upperBound = lowerBound;
        lowerBound = roundDateTime(lowerBound, requiredDuration);
        while (Duration.between(maxUpperBound, upperBound).getSeconds() <= 0) {

            // 1.8 * is to make sure the new time period goes well into the next period and doesn't fall just short
            upperBound = roundDateTime(lowerBound.plusSeconds((int) (1.8 * periodInSeconds.getSeconds())), requiredDuration);
            count = 0;

            while (i < times.size() && Duration.between(times.get(i), upperBound).getSeconds() > 0) {
                count++;
                i++;
            }
            series.getData().add(new XYChart.Data(lowerBound.format(formatter), count));
            lowerBound = upperBound;
        }
    }

    /**
     * Creates a new XYChart.Series, then creates an ArrayList of LocalDateTimes for all the given crimes, which is then
     * sorted, and used to calculate the graph x-axis spacing. Then the crimes are placed on the XYChart.Series to
     * be placed on the graph
     * @param data An ArrayList of the crime records wanted to graph
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     */
    public List<Object> createCrimesOverTimeGraph(List<Record> data){

        XYChart.Series series = new XYChart.Series<>();

        ArrayList<LocalDateTime> times = new ArrayList<>();

        for (Record r: data) {
            times.add(r.getDateAsObject());
        }
        Collections.sort(times);

        List<Object> calculations = calculateFormatForGraph(times);

        Duration periodInSeconds = (Duration) calculations.get(2);
        DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
        String requiredDuration = (String) calculations.get(0);

        sortCrimesByTimePeriod(times, periodInSeconds, formatter, series, requiredDuration, times.get(0),
                times.get(times.size()-1));

        return new ArrayList<>(Arrays.asList(requiredDuration, series));
    }

    /**
     * Creates a new XYChart.Series for each ward the user has selected, then creates an ArrayList of LocalDateTimes
     * for each crime in each of those wards, along with an ArrayList of LocalDateTimes for all the wards selected,
     * which are both then sorted, and the overall list of times is used to calculate the graph x-axis spacing, and then
     * the crimes in each of the wards are rounded to that spacing, and then added to the ArrayList of XYChart.Series to
     * be placed on the graph
     * @param data An ArrayList of the crime records wanted to graph
     * @param crimeWards An ArrayList of integers which contains the wards that need to be graphed
     * @return An ArrayList of XYChart.Series objects containing all the data points as a <String, Integer> pair, where
     *         the String is the date and time in string form and the Integer is the number of crimes that occurred in
     *         the calculated timeframe
     */
    public List<Object> createCrimesPerWardOverTimeGraph(List<Record> data, List<Integer> crimeWards) {

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();
        ArrayList<ArrayList<LocalDateTime>> timesList = new ArrayList<>();
        ArrayList<LocalDateTime> overallTime = new ArrayList<>();

        XYChart.Series series;
        // Fill arraylists with a series and list of times for each ward
        for (int ward: crimeWards) {
            series = new XYChart.Series();
            series.setName(String.format("Ward: %d", ward));
            seriesList.add(series);

            timesList.add(new ArrayList<>());
        }

        // Populate
        for (Record r: data) {
            if (crimeWards.contains(r.getWard())) {
                timesList.get(crimeWards.indexOf(r.getWard())).add(r.getDateAsObject());
                overallTime.add(r.getDateAsObject());
            }
        }
        Collections.sort(overallTime);

        for (ArrayList<LocalDateTime> times: timesList) {
            Collections.sort(times);
        }

        List<Object> calculations = calculateFormatForGraph(overallTime);

        Duration periodInSeconds = (Duration) calculations.get(2);
        DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
        String requiredDuration = (String) calculations.get(0);

        for (int ward : crimeWards) {
            sortCrimesByTimePeriod(timesList.get(crimeWards.indexOf(ward)), periodInSeconds, formatter,
                    seriesList.get(crimeWards.indexOf(ward)), requiredDuration, overallTime.get(0),
                    overallTime.get(overallTime.size()-1));
        }

        return new ArrayList<>(Arrays.asList(requiredDuration, seriesList));
    }

    /**
     * Creates a new XYChart.Series for each crime type the user has selected, then creates an ArrayList of LocalDateTimes
     * for each crime in each of those crime types, along with an ArrayList of LocalDateTimes for all the crime types selected,
     * which are both then sorted, and the overall list of times is used to calculate the graph x-axis spacing, and then
     * the crimes of each crime type are rounded to that spacing, and then added to the ArrayList of XYChart.Series to
     * be placed on a graph
     * @param data An ArrayList of the crime records wanted to graph
     * @param crimeTypes An ArrayList of Strings which contains the types of crimes that need to be graphed
     * @return An ArrayList of XYChart.Series objects containing all the data points as a <String, Integer> pair, where
     *         the String is the date and time in string form and the Integer is the number of crimes that occurred in
     *         the calculated timeframe
     */
    public List<Object> createCrimesPerTypeOverTimeGraph(List<Record> data, List<String> crimeTypes) {

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();
        ArrayList<ArrayList<LocalDateTime>> timesList = new ArrayList<>();
        ArrayList<LocalDateTime> overallTime = new ArrayList<>();

        XYChart.Series series;
        // Fill arraylists with a series and list of times for each type
        for (String crimeType: crimeTypes) {
            series = new XYChart.Series();
            series.setName(crimeType);
            seriesList.add(series);

            timesList.add(new ArrayList<>());
        }

        // Populate
        for (Record r: data) {
            if (crimeTypes.contains(r.getPrimaryDescription())) {
                timesList.get(crimeTypes.indexOf(r.getPrimaryDescription())).add(r.getDateAsObject());
                overallTime.add(r.getDateAsObject());
            }
        }
        Collections.sort(overallTime);

        for (ArrayList<LocalDateTime> times: timesList) {
            Collections.sort(times);
        }

        List<Object> calculations = calculateFormatForGraph(overallTime);

        Duration periodInSeconds = (Duration) calculations.get(2);
        DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
        String requiredDuration = (String) calculations.get(0);

        for (String crimeType: crimeTypes) {
            sortCrimesByTimePeriod(timesList.get(crimeTypes.indexOf(crimeType)), periodInSeconds, formatter,
                    seriesList.get(crimeTypes.indexOf(crimeType)), requiredDuration, overallTime.get(0),
                    overallTime.get(overallTime.size()-1));
        }

        return new ArrayList<>(Arrays.asList(requiredDuration, seriesList));
    }

    /**
     * Creates a new XYChart.Series for each beat the user has selected, then creates an ArrayList of LocalDateTimes
     * for each crime in each of those beats, along with an ArrayList of LocalDateTimes for all the beats selected,
     * which are both then sorted, and the overall list of times is used to calculate the graph x-axis spacing, and then
     * the crimes in each of the beats are rounded to that spacing, and then added to the ArrayList of XYChart.Series to
     * be placed on a graph
     * @param data An ArrayList of the crime records wanted to graph
     * @param crimeBeats An ArrayList of integers which contains the beats that need to be graphed
     * @return An ArrayList of XYChart.Series objects containing all the data points as a <String, Integer> pair, where
     *         the String is the date and time in string form and the Integer is the number of crimes that occurred in
     *         the calculated timeframe
     */
    public List<Object> createCrimesPerBeatOverTimeGraph(List<Record> data, List<Integer> crimeBeats) {

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();
        ArrayList<ArrayList<LocalDateTime>> timesList = new ArrayList<>();
        ArrayList<LocalDateTime> overallTime = new ArrayList<>();

        XYChart.Series series;
        // Fill arraylists with a series and list of times for each beat
        for (Integer crimeBeat: crimeBeats) {
            series = new XYChart.Series();
            series.setName(String.format("Beat: %d", crimeBeat));
            seriesList.add(series);

            timesList.add(new ArrayList<>());
        }

        // Populate
        for (Record r: data) {
            if (crimeBeats.contains(r.getBeat())) {
                timesList.get(crimeBeats.indexOf(r.getBeat())).add(r.getDateAsObject());
                overallTime.add(r.getDateAsObject());
            }
        }
        Collections.sort(overallTime);

        for (ArrayList<LocalDateTime> times: timesList) {
            Collections.sort(times);
        }
        String requiredDuration = null;
        if (!overallTime.isEmpty()) {
            List<Object> calculations = calculateFormatForGraph(overallTime);
            Duration periodInSeconds = (Duration) calculations.get(2);
            DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
            requiredDuration = (String) calculations.get(0);
            for (Integer crimeBeat: crimeBeats) {
                sortCrimesByTimePeriod(timesList.get(crimeBeats.indexOf(crimeBeat)), periodInSeconds, formatter,
                        seriesList.get(crimeBeats.indexOf(crimeBeat)), requiredDuration, overallTime.get(0),
                        overallTime.get(overallTime.size()-1));
            }
        }
        return new ArrayList<>(Arrays.asList(requiredDuration, seriesList));
    }
}
