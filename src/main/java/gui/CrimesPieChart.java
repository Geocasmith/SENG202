package gui;

import backend.TypeFrequencyPair;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.chart.PieChart;

import java.util.ArrayList;

public class CrimesPieChart {

    public void drawChart(String pieChartTitle, String titleLabel, ArrayList<TypeFrequencyPair> pairData) {
        //Preparing ObservbleList object
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        for (TypeFrequencyPair data : pairData) {
            pieChartData.add(new PieChart.Data(data.getType(), data.getFrequency()));


        }


        Stage stage = new Stage();

        //Creating a Pie chart
        PieChart pieChart = new PieChart(pieChartData);
        pieChart.setLabelsVisible(true);
        pieChart.setMinSize(750, 750);

        //Setting the title of the Pie chart
        pieChart.setTitle(pieChartTitle);

        //setting the direction to arrange the data
        pieChart.setClockwise(true);

        //Setting the length of the label line
        pieChart.setLabelLineLength(50);

        //Setting the labels of the pie chart visible
        pieChart.setLabelsVisible(true);

        //Setting the start angle of the pie chart
        pieChart.setStartAngle(0);
        pieChart.applyCss();

        //Creating a Group object
        //Group root = new Group(pieChart);
        BorderPane root = new BorderPane();
        root.setCenter(pieChart);

        //Creating a scene object
        Scene scene = new Scene(root, 1020, 810);

        //Setting title to the Stage
        stage.setTitle("Insight - " + titleLabel);

        //Adding scene to the stage
        stage.setScene(scene);

        //Displaying the contents of the stage
        stage.showAndWait();
    }

} 