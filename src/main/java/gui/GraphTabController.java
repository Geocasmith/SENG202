package gui;

import backend.GraphCreator;
import backend.Record;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.ArrayList;

public class GraphTabController {
    GraphCreator graphCreator = new GraphCreator();

    CategoryAxis xAxis = new CategoryAxis();
    NumberAxis yAxis = new NumberAxis();

    LineChart lineChart = new LineChart(xAxis, yAxis);

    @FXML
    private BorderPane graphBorderPane;

    @FXML
    private void initialize() {

    }



    public void createCrimesPerTypeOverTimeGraph(ArrayList<Record> currentRecords, ArrayList<String> crimeTypes) {
        ArrayList<Record> filteredRecords = new ArrayList<>();
        for (Record record : currentRecords) {
            if (crimeTypes.contains(record.getPrimaryDescription())) {
                filteredRecords.add(record);
            }
        }
        ArrayList<Object> returnedInfo = graphCreator.createCrimesPerTypeOverTimeGraph(filteredRecords, crimeTypes);
        ArrayList<XYChart.Series> seriesList = (ArrayList<XYChart.Series>) returnedInfo.get(1);
        String label = (String) returnedInfo.get(0);

        lineChart.setTitle("Number of Crimes Per Type Over Time");
        xAxis.setLabel("Time (" + label + ")");
        yAxis.setLabel("Number of Crimes Per Type");

        lineChart.getData().clear();
        lineChart.setLegendVisible(true);

        for (String crimeType : crimeTypes) {
            lineChart.getData().add(seriesList.get(crimeTypes.indexOf(crimeType)));
        }

        graphBorderPane.setCenter(lineChart);
    }
    public void createCrimesPerBeatOverTimeGraph(ArrayList<Record> currentRecords, ArrayList<Integer> crimeBeats) {
        ArrayList<Record> filteredRecords = new ArrayList<>();
        for (Record record : currentRecords) {
            if (crimeBeats.contains(record.getBeat())) {
                filteredRecords.add(record);
            }
        }
        ArrayList<Object> returnedInfo = graphCreator.createCrimesPerBeatOverTimeGraph(filteredRecords, crimeBeats);
        ArrayList<XYChart.Series> seriesList = (ArrayList<XYChart.Series>) returnedInfo.get(1);
        String label = (String) returnedInfo.get(0);

        lineChart.setTitle("Number of Crimes Per Beat Over Time");
        xAxis.setLabel("Time (" + label + ")");
        yAxis.setLabel("Number of Crimes Per Beat");

        lineChart.getData().clear();
        lineChart.setLegendVisible(true);

        for (int beat : crimeBeats) {
            lineChart.getData().add(seriesList.get(crimeBeats.indexOf(beat)));
        }

        graphBorderPane.setCenter(lineChart);
    }

    public void createCrimesPerWardOverTimeGraph(ArrayList<Record> currentRecords, ArrayList<Integer> crimeWards) {
        ArrayList<Record> filteredRecords = new ArrayList<>();
        for (Record record : currentRecords) {
            if (crimeWards.contains(record.getWard())) {
                filteredRecords.add(record);
            }
        }
        ArrayList<Object> returnedInfo = graphCreator.createCrimesPerWardOverTimeGraph(filteredRecords, crimeWards);
        ArrayList<XYChart.Series> seriesList = (ArrayList<XYChart.Series>) returnedInfo.get(1);
        String label = (String) returnedInfo.get(0);

        lineChart.setTitle("Number of Crimes Per Ward Over Time");
        xAxis.setLabel("Time (" + label + ")");
        yAxis.setLabel("Number of Crimes Per Ward");

        lineChart.getData().clear();
        lineChart.setLegendVisible(true);

        for (int ward : crimeWards) {
            lineChart.getData().add(seriesList.get(crimeWards.indexOf(ward)));
        }

        graphBorderPane.setCenter(lineChart);

    }

    public void createCrimesOverTimeGraph(ArrayList<Record> currentRecords) {
        ArrayList<Object> returnedInfo = graphCreator.createCrimesOverTimeGraph(currentRecords);

        XYChart.Series series = (XYChart.Series) returnedInfo.get(1);
        String label = (String) returnedInfo.get(0);

        lineChart.setTitle("Crimes Over Time");
        xAxis.setLabel("Time (" + label + ")");
        yAxis.setLabel("Number of Crimes");

        lineChart.setLegendVisible(false);
        lineChart.getData().clear();
        lineChart.getData().add(series);

        graphBorderPane.setCenter(lineChart);
    }

    public void start(Stage stage) {
        stage.setTitle("Simple Graph");
        //defining the axes
        //defining a series
        XYChart.Series series;
        ArrayList<Object> returnedInfo = graphCreator.createCrimesOverTimeGraph(new ArrayList<Record>());
        series = (XYChart.Series) returnedInfo.get(1);
        String label = (String) returnedInfo.get(0);





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

//        ArrayList<String> crimeTypes = new ArrayList<>(Arrays.asList("THEFT", "ASSAULT", "HOMICIDE"));
//
//        ArrayList<XYChart.Series> seriesList;
//        returnedInfo = createCrimesPerTypeOverTimeGraph(db, crimeTypes);
//        seriesList = (ArrayList<XYChart.Series>) returnedInfo.get(1);
//        label = (String) returnedInfo.get(0);


        xAxis.setLabel("Time (" + label + ")");
        yAxis.setLabel("Number of Crimes");
        //creating the chart
        final BarChart barChart = new BarChart(xAxis,yAxis);
        barChart.setTitle("Crimes by Time");

        final CategoryAxis xAxis2 = new CategoryAxis();
        final NumberAxis yAxis2 = new NumberAxis();

        yAxis2.setLabel("Number of Crimes Per Ward");
        //creating the chart
        final LineChart lineChart = new LineChart(xAxis2,yAxis2);
        lineChart.setTitle("Crimes per Ward by Time");

        Scene scene  = new Scene(lineChart,1920,1080);
//        for (String crimetype : crimeTypes) {
//            lineChart.getData().add(seriesList.get(crimeTypes.indexOf(crimetype)));
//        }
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
}
