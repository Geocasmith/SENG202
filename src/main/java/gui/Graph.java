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
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.sql.SQLException;

public class Graph extends Application {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
    private DataAnalyser dataAnalyser = new DataAnalyser();
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
        LineChart lineChart = createWardCrimesOverTimeGraph(db, hm, series);


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
        for (Record r: data.subList(0, 50)) {
            times.add(r.getDateAsObject());
            if (hm.containsKey(r.getDateAsObject())) {
                hm.put(r.getDateAsObject(), (int) hm.get(r.getDateAsObject()) + 1);
            } else {
                hm.put(r.getDateAsObject(), 1);
            }
        }
        Collections.sort(times);

        LocalDateTime lowerBound = times.get(0);
        LocalDateTime upperBound;
        int i = 0;
        int count;
        LinkedHashMap hm2 = new LinkedHashMap();
        ArrayList<LocalDateTime> times2 = new ArrayList<>();
        Duration width = dataAnalyser.calculateTimeDifference(times.get(0), times.get(times.size() - 1));
        Duration segments = Duration.ofSeconds(width.getSeconds() / 5);
        while (Duration.between(times.get(times.size() - 1), lowerBound).getSeconds() < 0) {
            upperBound = lowerBound.plusSeconds(segments.getSeconds());
            count = 0;
            while (i < times.size() && Duration.between(times.get(i), upperBound).getSeconds() > 0) {
                count += (int) hm.get(times.get(i));
                i++;
            }
            times2.add(lowerBound);
            hm2.put(lowerBound, count);
            lowerBound = upperBound;
        }
        times2.add(lowerBound);
        hm2.put(lowerBound, 0);

        for (LocalDateTime t: times2) {
            series.getData().add(new XYChart.Data(t.format(formatter), hm2.get(t)));
            System.out.println(t.format(formatter));
        }
        return barChart;
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
            series.getData().add(new XYChart.Data(t.format(formatter), hm.get(t)));
        }
        return lineChart;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
