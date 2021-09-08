package gui;

import backend.Record;
import backend.database.Database;
import backend.DataAnalyser;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;

import java.sql.SQLException;

public class Graph extends Application {
    private static DateTimeFormatter hourFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh a", Locale.ENGLISH);
    private static DateTimeFormatter dayWeekFormatter = DateTimeFormatter.ofPattern("MM/dd/yyyy", Locale.ENGLISH);
    private static DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MM/yyyy", Locale.ENGLISH);
    private static DateTimeFormatter yearFormatter = DateTimeFormatter.ofPattern("yyyy", Locale.ENGLISH);






    private DataAnalyser dataAnalyser = new DataAnalyser();
    private static final int oneHourInSeconds = 3600;
    private static final int oneDayInSeconds = 86400;
    private static final int oneWeekInSeconds = 604800;
    private static final int twoWeeksInSeconds = 1209600;
    private static final int oneMonthInSeconds = 2629746;
    private static final int fourMonthsInSeconds = 10518984;
    private static final int oneYearInSeconds = 31556952;
    @Override
    public void start(Stage stage) throws SQLException {
        Database db = new Database();
        LinkedHashMap hm = new LinkedHashMap();
        db.connectDatabase();
        stage.setTitle("Simple Graph");
        //defining the axes
        //defining a series
        XYChart.Series series = new XYChart.Series();

        BarChart barChart = createCrimesOverTimeGraph(db, hm, series);
//        LineChart lineChart = createWardCrimesOverTimeGraph(db, hm, series);


        Scene scene  = new Scene(barChart,1920,1080);
        barChart.getData().add(series);

        stage.setScene(scene);
        stage.show();
    }

    private BarChart createCrimesOverTimeGraph(Database db, LinkedHashMap hm, XYChart.Series series) throws SQLException {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Number of Crimes");
        //creating the chart
        final BarChart barChart = new BarChart(xAxis,yAxis);

        barChart.setTitle("Crimes by Time");

        ArrayList<Record> data = new ArrayList<>();
        data = db.getAll();
        ArrayList<LocalDateTime> times = new ArrayList<>();
        for (Record r: data) {
            times.add(r.getDateAsObject());
            if (hm.containsKey(r.getDateAsObject())) {
                hm.put(r.getDateAsObject(), (int) hm.get(r.getDateAsObject()) + 1);
            } else {
                hm.put(r.getDateAsObject(), 1);
            }
        }
        Collections.sort(times);
//        times = new ArrayList<>(times.subList(0, 2000));




        Duration width = dataAnalyser.calculateTimeDifference(times.get(0), times.get(times.size() - 1));
        Duration periodInSeconds;
        DateTimeFormatter formatter;
        String requiredDuration;
        if (width.getSeconds() < oneDayInSeconds) {
            requiredDuration = "Hours";
            formatter = hourFormatter;
            periodInSeconds = Duration.ofSeconds(oneHourInSeconds);
        } else if (width.getSeconds() < twoWeeksInSeconds) {
            requiredDuration = "Days";
            formatter = dayWeekFormatter;
            periodInSeconds = Duration.ofSeconds(oneDayInSeconds);
        } else if (width.getSeconds() < fourMonthsInSeconds) {
            requiredDuration = "Weeks";
            formatter = dayWeekFormatter;
            periodInSeconds = Duration.ofSeconds(oneWeekInSeconds);
        } else if (width.getSeconds() < oneYearInSeconds) {
            requiredDuration = "Months";
            formatter = monthFormatter;
            periodInSeconds = Duration.ofSeconds(oneMonthInSeconds);
        } else {
            requiredDuration = "Years";
            formatter = yearFormatter;
            periodInSeconds = Duration.ofSeconds(oneYearInSeconds);
        }
        sortCrimesByTimePeriod(times, periodInSeconds, formatter, series, requiredDuration);

        return barChart;
    }

    private LocalDateTime roundDateTime(LocalDateTime timeToRound, String requiredDuration) {
        if (requiredDuration == "Hours") {
            timeToRound.withMinute(0).withSecond(0);
        } else if (requiredDuration == "Days") {
            timeToRound.withHour(0);
        } else if (requiredDuration == "Weeks") {
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
        } else if (requiredDuration == "Months") {
            timeToRound.withDayOfMonth(1);
        } else if (requiredDuration == "Years") {
            timeToRound.withMonth(1);
        }
        return timeToRound;
    }

    private void sortCrimesByTimePeriod(ArrayList<LocalDateTime> times, Duration periodInSeconds, DateTimeFormatter formatter, XYChart.Series series, String requiredDuration) {
        int i = 0;
        int count;
        ArrayList<LocalDateTime> outputTimes = new ArrayList<>();
        LinkedHashMap outputHashMap = new LinkedHashMap();
        LocalDateTime lowerBound = roundDateTime(times.get(0), requiredDuration);
        LocalDateTime upperBound;
        while (Duration.between(times.get(times.size() - 1), lowerBound).getSeconds() < 0) {
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

    private LineChart createWardCrimesOverTimeGraph(Database db, LinkedHashMap hm, XYChart.Series series) throws SQLException {
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        xAxis.setLabel("Time");
        yAxis.setLabel("Number of Crimes");
        //creating the chart
        final LineChart lineChart = new LineChart(xAxis,yAxis);

        lineChart.setTitle("Crimes per Ward by Time");

        ArrayList<Record> data = new ArrayList<>();
        ArrayList<Integer> wards = new ArrayList<>();
        data = db.getAll();
        List<LocalDateTime> times = new ArrayList<>();
        for (Record r: data) {
            times.add(r.getDateAsObject());
            if (!wards.contains(r.getWard())) {
                wards.add(r.getWard());
            }
            if (hm.containsKey(r.getDateAsObject())) {
                hm.put(r.getDateAsObject(), (int) hm.get(r.getDateAsObject()) + 1);
            } else {
                hm.put(r.getDateAsObject(), 1);
            }
        }
        Collections.sort(wards);
//        for (int i: wards) {
//            System.out.println(i);
//        }
        Collections.sort(times);
        for (LocalDateTime t: times) {
            series.getData().add(new XYChart.Data(t.format(monthFormatter), hm.get(t)));
        }
        return lineChart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
