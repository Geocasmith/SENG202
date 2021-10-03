package gui;

import backend.Record;
import com.google.gson.JsonArray;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.Objects;

public class MapTabController {
    @FXML
    private WebView webView;
    @FXML
    private RadioButton heatmapRadioButton;
    @FXML
    private RadioButton markersRadioButton;
    @FXML
    private Slider heatmapDensitySlider;

    private WebEngine webEngine;

    /**
     * Loads the Google Maps html file with the webengine and sets listeners to change the map type when the radio
     * buttons are clicked
     */
    @FXML
    private void initialize() {
        webEngine = webView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getResource("googlemaps.html")).toString());

        // Make the radio buttons load their respective map when clicked
        heatmapRadioButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (heatmapRadioButton.isSelected()) {
                enableHeatmap();
                heatmapDensitySlider.setDisable(false);
            }
        });
        markersRadioButton.selectedProperty().addListener((observableValue, aBoolean, t1) -> {
            if (markersRadioButton.isSelected()) {
                enableMarkers();
                heatmapDensitySlider.setDisable(true);
            }
        });
    }

    /**
     * Plots each of the records on the map, and gives them an infoWindow with their case number, date, and their
     * primary, secondary and location descriptions
     * @param records An ArrayList of crime records to be plotted
     * @param displayed A boolean, true if the markers should be displayed, false if they shouldn't
     */
    public void plotMarkers(ArrayList<Record> records, boolean displayed) {
        String newLocationMarker;
        JsonArray recordArray;
        boolean infoWindow = true;

        for (Record record : records) {
            recordArray = record.getJsonArray();
            newLocationMarker = "document.plotPoint(" + recordArray + ", " + infoWindow + ", " + displayed + ")";
            webEngine.executeScript(newLocationMarker);
        }
    }

    /**
     * Takes a list of records and creates markers for them that are hidden, then turns on the heatmap
     * @param records An ArrayList of crime records to be displayed on the heatmap
     */
    public void plotHeatmap(ArrayList<Record> records) {
        plotMarkers(records, false);
        enableHeatmap();
    }

    /**
     * Enables the heatmap and disables the markers
     */
    public void enableHeatmap() {
        webEngine.executeScript("document.enableHeatmap()");
        updateHeatmapDensity();
    }

    /**
     * Enables the markers and disables the heatmap
     */
    public void enableMarkers() {
        webEngine.executeScript("document.enableMarkers()");
    }

    /**
     * Clears existing data on the map, then plots the given crime records on a heatmap or with markers, depending
     * on which radio button is selected
     * @param records An ArrayList of crime records to be plotted
     */
    public void updateMarkers(ArrayList<Record> records) {
        clearMap();

        if (markersRadioButton.isSelected()) {
            plotMarkers(records, true);
        } else {
            plotHeatmap(records);
        }
    }

    /**
     * Updates the heatmap density with the current value in the radius slider
     */
    public void updateHeatmapDensity() {
        int heatmapSliderValue = (int) heatmapDensitySlider.getValue();
        webEngine.executeScript("document.updateHeatmapDensity(" + heatmapSliderValue +")");
    }

    /**
     * Clears all the existing markers and lines from the map
     */
    public void clearMap() {
        webEngine.executeScript("document.clearMap()");
    }
}
