package gui;

import backend.DataAnalyser;
import backend.Record;
import backend.database.Database;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.sql.SQLException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Graph extends Application {
    private static final DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh a", Locale.ENGLISH);
    private static final DateTimeFormatter dayWeekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.ENGLISH);
    private static final DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);






    private static final DataAnalyser dataAnalyser = new DataAnalyser();
    private static final int oneHourInSeconds = 3600;
    private static final int oneDayInSeconds = 86400;
    private static final int oneWeekInSeconds = 604800;
    private static final int oneMonthInSeconds = 2629746;
    private static final int oneYearInSeconds = 31556952;
    @Override
    public void start(Stage stage) throws SQLException {
        Database db = new Database();
        db.connectDatabase();
        stage.setTitle("Simple Graph");
        //defining the axes
        //defining a series
        XYChart.Series series = createCrimesOverTimeGraph(db);

        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Number of Crimes");
        //creating the chart
        final BarChart barChart = new BarChart(xAxis,yAxis);
        barChart.setTitle("Crimes by Time");

        final CategoryAxis xAxis2 = new CategoryAxis();
        final NumberAxis yAxis2 = new NumberAxis();
        xAxis2.setLabel("Time");
        yAxis2.setLabel("Number of Crimes Per Ward");
        //creating the chart
        final LineChart lineChart = new LineChart(xAxis2,yAxis2);
        lineChart.setTitle("Crimes per Ward by Time");




//        Scene scene  = new Scene(barChart,1920,1080);
//        barChart.getData().add(series);

//        ArrayList<Integer> wards = new ArrayList<>(Arrays.asList(1, 2, 32, 7, 23, 28, 12));
//        Collections.sort(wards);
//
//        ArrayList<XYChart.Series> seriesList = createCrimesPerWardOverTimeGraph(db, wards);
//        Scene scene  = new Scene(lineChart,1920,1080);
//        for (int ward : wards) {
//            lineChart.getData().add(seriesList.get(wards.indexOf(ward)));
//        }

        ArrayList<String> crimeTypes = new ArrayList<>(Arrays.asList("THEFT", "ASSAULT", "HOMICIDE"));

        ArrayList<XYChart.Series> seriesList = createCrimesPerTypeOverTimeGraph(db, crimeTypes);
        Scene scene  = new Scene(lineChart,1920,1080);
        for (String crimetype : crimeTypes) {
            lineChart.getData().add(seriesList.get(crimeTypes.indexOf(crimetype)));
        }
//
//        ArrayList<Integer> beats = new ArrayList<>(Arrays.asList(1213, 1121, 332, 1724));
//        Collections.sort(beats);
//
//        ArrayList<XYChart.Series> seriesList = createCrimesPerBeatOverTimeGraph(db, beats);
//        Scene scene  = new Scene(lineChart,1920,1080);
//        for (int beat : beats) {
//            lineChart.getData().add(seriesList.get(beats.indexOf(beat)));
//        }

        stage.setScene(scene);
        stage.show();
    }



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
            } else if (timeToRound.getDayOfMonth() <=28) {
                timeToRound.withDayOfMonth(21);
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
        System.out.println(requiredDuration);
        return new ArrayList<>(Arrays.asList(requiredDuration, formatter, periodInSeconds));
    }

    /**
     *
     * @param times An ArrayList of times
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
     * @param db The database object used to retrieve the data
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     * @throws SQLException
     */
    private XYChart.Series createCrimesOverTimeGraph(Database db) throws SQLException {

        XYChart.Series series = new XYChart.Series<>();


        ArrayList<Record> data;
        data = db.getAll();
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

        return series;
    }

    /**
     *
     * @param db The database object used to retrieve the data
     * @param wards An ArrayList of integers which contains the wards that need to be graphed
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     * @throws SQLException
     */
    private ArrayList<XYChart.Series> createCrimesPerWardOverTimeGraph(Database db, ArrayList<Integer> wards) throws SQLException {

        ArrayList<Record> data = db.getAll();

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

        return seriesList;
    }

    /**
     *
     * @param db The database object used to retrieve the data
     * @param crimeTypes An ArrayList of Strings which contains the types of crimes that need to be graphed
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     * @throws SQLException
     */
    private ArrayList<XYChart.Series> createCrimesPerTypeOverTimeGraph(Database db, ArrayList<String> crimeTypes) throws SQLException {

        ArrayList<Record> data = db.getAll();

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

        return seriesList;
    }

    /**
     *
     * @param db The database object used to retrieve the data
     * @param crimeBeats An ArrayList of integers which contains the beats that need to be graphed
     * @return An XYChart.Series object containing all the data points as a <String, Integer> pair, where the String is
     *         the date and time in string form and the Integer is the number of crimes that occurred in the calculated
     *         timeframe
     * @throws SQLException
     */
    private ArrayList<XYChart.Series> createCrimesPerBeatOverTimeGraph(Database db, ArrayList<Integer> crimeBeats) throws SQLException {

        ArrayList<Record> data = db.getAll();

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
        if (overallTime.size() > 0) {
            ArrayList<Object> calculations = calculateFormatForGraph(overallTime);
            Duration periodInSeconds = (Duration) calculations.get(2);
            DateTimeFormatter formatter = (DateTimeFormatter) calculations.get(1);
            String requiredDuration = (String) calculations.get(0);
            for (Integer crimeBeat: crimeBeats) {
                sortCrimesByTimePeriod(timesList.get(crimeBeats.indexOf(crimeBeat)), periodInSeconds, formatter, seriesList.get(crimeBeats.indexOf(crimeBeat)), requiredDuration, overallTime.get(0), overallTime.get(overallTime.size()-1));
            }
        }
        return seriesList;
    }





    public static void main(String[] args) {
        launch(args);
    }
}
