package gui;

import backend.Record;
import backend.database.Database;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.stage.Stage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import java.sql.SQLException;

public class Graph extends Application {
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
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


        Scene scene  = new Scene(barChart,800,600);
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
        List<LocalDateTime> times = new ArrayList<>();
        for (Record r: data) {
            times.add(r.getDateAsObject());
            if (hm.containsKey(r.getDateAsObject())) {
                hm.put(r.getDateAsObject(), (int) hm.get(r.getDateAsObject()) + 1);
            } else {
                hm.put(r.getDateAsObject(), 1);
            }
        }
        Collections.sort(times);
        for (LocalDateTime t: times) {
            series.getData().add(new XYChart.Data(t.format(formatter), hm.get(t)));
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

        lineChart.setTitle("Crimes by Time");

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
        for (int i: wards) {
            System.out.println(i);
        }
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
