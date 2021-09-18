package backend;

import javafx.scene.chart.*;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class GraphCreator {
    private static final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy hh a", Locale.ENGLISH);
    private static final DateTimeFormatter dayWeekFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);

    private static final DataAnalyser dataAnalyser = new DataAnalyser();

    private static final int oneHourInSeconds = 3600;
    private static final int oneDayInSeconds = 86400;
    private static final int oneWeekInSeconds = 604800;
    private static final int oneMonthInSeconds = 2629746;
    private static final int oneYearInSeconds = 31556952;

    /**
     * Rounds the given LocalDateTime down to the given duration
     * @param timeToRound A LocalDateTime object that needs to be rounded
     * @param requiredDuration A string containing the time duration the timeToRound needs to be rounded down to
     * @return A LocalDateTime object that has been rounded to the given duration
     */
    private LocalDateTime roundDateTime(LocalDateTime timeToRound, String requiredDuration) {
        if (Objects.equals(requiredDuration, "Hours")) {
            timeToRound.withMinute(0).withSecond(0);
        } else if (Objects.equals(requiredDuration, "Days")) {
            timeToRound.withHour(0);
        } else if (Objects.equals(requiredDuration, "Weeks")) {
            if (timeToRound.getDayOfMonth() <= 7) {
                timeToRound.withDayOfMonth(1);
            } else if (timeToRound.getDayOfMonth() <=14) {
                timeToRound.withDayOfMonth(7);
            } else if (timeToRound.getDayOfMonth() <=21) {
                timeToRound.withDayOfMonth(14);
            } else {
                timeToRound.withDayOfMonth(21);
            }
        } else if (Objects.equals(requiredDuration, "Months")) {
            timeToRound.withDayOfMonth(1);
        } else if (Objects.equals(requiredDuration, "Years")) {
            timeToRound.withMonth(1);
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
    private ArrayList<Object> calculateFormatForGraph(ArrayList<LocalDateTime> times) {
        Duration width = dataAnalyser.calculateTimeDifference(times.get(0), times.get(times.size() - 1));
        Duration periodInSeconds;
        DateTimeFormatter formatter;
        String requiredDuration;
        if (width.getSeconds() < oneDayInSeconds) {
            requiredDuration = "Hours";
            formatter = hourFormatter;
            periodInSeconds = Duration.ofSeconds(oneHourInSeconds);
        } else if (width.getSeconds() < oneMonthInSeconds) {
            requiredDuration = "Days";
            formatter = dayWeekFormatter;
            periodInSeconds = Duration.ofSeconds(oneDayInSeconds);
        } else if (width.getSeconds() < oneYearInSeconds) {
            requiredDuration = "Weeks";
            formatter = dayWeekFormatter;
            periodInSeconds = Duration.ofSeconds(oneWeekInSeconds);
        } else if (width.getSeconds() < 2 * oneYearInSeconds) {
            requiredDuration = "Months";
            formatter = monthFormatter;
            periodInSeconds = Duration.ofSeconds(oneMonthInSeconds);
        } else {
            requiredDuration = "Years";
            formatter = yearFormatter;
            periodInSeconds = Duration.ofSeconds(oneYearInSeconds);
        }
        return new ArrayList<>(Arrays.asList(requiredDuration, formatter, periodInSeconds));
    }

    /**
     *
     * @param times An ArrayList of LocalDateTime Objects
     * @param periodInSeconds
     * @param formatter
     * @param series
     * @param requiredDuration
     * @param lowerBound
     * @param maxUpperBound
     */
    private void sortCrimesByTimePeriod(ArrayList<LocalDateTime> times, Duration periodInSeconds, DateTimeFormatter formatter, XYChart.Series series, String requiredDuration, LocalDateTime lowerBound, LocalDateTime maxUpperBound) {
        int i = 0;
        int count;
        ArrayList<LocalDateTime> outputTimes = new ArrayList<>();
        LinkedHashMap outputHashMap = new LinkedHashMap();
        LocalDateTime upperBound = lowerBound;
        while (Duration.between(maxUpperBound, upperBound).getSeconds() < 0) {
            upperBound = roundDateTime(lowerBound.plusSeconds(periodInSeconds.getSeconds() + 50000), requiredDuration);
            count = 0;
            while (i < times.size() && Duration.between(times.get(i), upperBound).getSeconds() > 0) {
                count++;
                i++;
            }
            outputTimes.add(lowerBound);
            outputHashMap.put(lowerBound, count);
            lowerBound = upperBound;
        }
        outputTimes.add(lowerBound);
        outputHashMap.put(lowerBound, 0);

        for (LocalDateTime t: outputTimes) {
            series.getData().add(new XYChart.Data(t.format(formatter), outputHashMap.get(t)));
        }
    }

    /**
     *
     * @param data An ArrayList of the crime records wanted to graph
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     */
    public ArrayList<Object> createCrimesOverTimeGraph(ArrayList<Record> data){

        XYChart.Series series = new XYChart.Series<>();

        ArrayList<LocalDateTime> times = new ArrayList<>();

        for (Record r: data) {
            times.add(r.getDateAsObject());
        }
        Collections.sort(times);
//        times = new ArrayList<>(times.subList(0, 2000));



        ArrayList<Object> calculations = calculateFormatForGraph(times);

        Duration periodInSeconds = (Duration) calculations.get(2);
        DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
        String requiredDuration = (String) calculations.get(0);

        sortCrimesByTimePeriod(times, periodInSeconds, formatter, series, requiredDuration, times.get(0), times.get(times.size()-1));

        return new ArrayList<>(Arrays.asList(requiredDuration, series));
    }

    /**
     *
     * @param data An ArrayList of the crime records wanted to graph
     * @param wards An ArrayList of integers which contains the wards that need to be graphed
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     */
    public ArrayList<Object> createCrimesPerWardOverTimeGraph(ArrayList<Record> data, ArrayList<Integer> wards) {

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();
        ArrayList<ArrayList<LocalDateTime>> timesList = new ArrayList<>();
        ArrayList<LocalDateTime> overallTime = new ArrayList<>();

        XYChart.Series series;
        // Fill arraylists with a series and list of times for each ward
        for (int ward: wards) {
            series = new XYChart.Series();
            series.setName(String.format("Ward: %d", ward));
            seriesList.add(series);

            timesList.add(new ArrayList<>());
        }

        // Populate
        for (Record r: data) {
            if (wards.contains(r.getWard())) {
                timesList.get(wards.indexOf(r.getWard())).add(r.getDateAsObject());
                overallTime.add(r.getDateAsObject());
            }
        }
        Collections.sort(overallTime);

        for (ArrayList<LocalDateTime> times: timesList) {
            Collections.sort(times);
        }

        ArrayList<Object> calculations = calculateFormatForGraph(overallTime);

        Duration periodInSeconds = (Duration) calculations.get(2);
        DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
        String requiredDuration = (String) calculations.get(0);

        for (int ward : wards) {
            sortCrimesByTimePeriod(timesList.get(wards.indexOf(ward)), periodInSeconds, formatter, seriesList.get(wards.indexOf(ward)), requiredDuration, overallTime.get(0), overallTime.get(overallTime.size()-1));
        }

        return new ArrayList<>(Arrays.asList(requiredDuration, seriesList));
    }

    /**
     *
     * @param data An ArrayList of the crime records wanted to graph
     * @param crimeTypes An ArrayList of Strings which contains the types of crimes that need to be graphed
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     */
    public ArrayList<Object> createCrimesPerTypeOverTimeGraph(ArrayList<Record> data, ArrayList<String> crimeTypes) {

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();
        ArrayList<ArrayList<LocalDateTime>> timesList = new ArrayList<>();
        ArrayList<LocalDateTime> overallTime = new ArrayList<>();

        XYChart.Series series;
        // Fill arraylists with a series and list of times for each ward
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

        ArrayList<Object> calculations = calculateFormatForGraph(overallTime);

        Duration periodInSeconds = (Duration) calculations.get(2);
        DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
        String requiredDuration = (String) calculations.get(0);

        for (String crimeType: crimeTypes) {
            sortCrimesByTimePeriod(timesList.get(crimeTypes.indexOf(crimeType)), periodInSeconds, formatter, seriesList.get(crimeTypes.indexOf(crimeType)), requiredDuration, overallTime.get(0), overallTime.get(overallTime.size()-1));
        }

        return new ArrayList<>(Arrays.asList(requiredDuration, seriesList));
    }

    /**
     *
     * @param data An ArrayList of the crime records wanted to graph
     * @param crimeBeats An ArrayList of integers which contains the beats that need to be graphed
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     */
    public ArrayList<Object> createCrimesPerBeatOverTimeGraph(ArrayList<Record> data, ArrayList<Integer> crimeBeats) {

        ArrayList<XYChart.Series> seriesList = new ArrayList<>();
        ArrayList<ArrayList<LocalDateTime>> timesList = new ArrayList<>();
        ArrayList<LocalDateTime> overallTime = new ArrayList<>();

        XYChart.Series series;
        // Fill arraylists with a series and list of times for each ward
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
        if (overallTime.size() > 0) {
            ArrayList<Object> calculations = calculateFormatForGraph(overallTime);
            Duration periodInSeconds = (Duration) calculations.get(2);
            DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
            requiredDuration = (String) calculations.get(0);
            for (Integer crimeBeat: crimeBeats) {
                sortCrimesByTimePeriod(timesList.get(crimeBeats.indexOf(crimeBeat)), periodInSeconds, formatter, seriesList.get(crimeBeats.indexOf(crimeBeat)), requiredDuration, overallTime.get(0), overallTime.get(overallTime.size()-1));
            }
        }
        return new ArrayList<>(Arrays.asList(requiredDuration, seriesList));
    }
}
