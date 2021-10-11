package gui;

import data.Record;
import com.google.gson.JsonArray;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.List;
import java.util.Objects;

/**
 * This class contains methods to control the map on functions of the program, primarily in the map tab
 * It can display markers or a heatmap, and can control the density of the heatmap.
 * @author Bede Skinner-Vennell
 * Date 09/10/2021
 */
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

    private int mapRequestCount = 0;

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
     * @param records A List of crime records to be plotted
     * @param displayed A boolean, true if the markers should be displayed, false if they shouldn't
     */
    public void plotMarkers(List<Record> records, boolean displayed) {
        String newLocationMarker;
        JsonArray recordArray;
        for (Record record : records) {

            recordArray = record.getJsonArray();
            newLocationMarker = "document.plotPoint(" + recordArray + ", " + true + ", " + displayed + ")";
            runScript(newLocationMarker);
        }
    }

    /**
     * Takes a list of records and creates markers for them that are hidden, then turns on the heatmap
     * @param records A List of crime records to be displayed on the heatmap
     */
    public void plotHeatmap(List<Record> records) {
        plotMarkers(records, false);
        enableHeatmap();
    }

    /**
     * Enables the heatmap and disables the markers
     */
    public void enableHeatmap() {
        runScript("document.enableHeatmap()");
        updateHeatmapDensity();
    }

    /**
     * Enables the markers and disables the heatmap
     */
    public void enableMarkers() {
        runScript("document.enableMarkers()");
    }

    /**
     * Clears existing data on the map, then plots the given crime records on a heatmap or with markers, depending
     * on which radio button is selected
     * @param records A List of crime records to be plotted
     */
    public void updateMarkers(List<Record> records) {
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
        runScript("document.updateHeatmapDensity(" + heatmapSliderValue +")");
    }

    /**
     * Clears all the existing markers and lines from the map
     */
    public void clearMap() {
        runScript("document.clearMap()");
    }

    /**
     * If this is one of the first 2 times the controller is running a script, then wait until the webengine has fully loaded
     * the javascript then run the given script, otherwise, run it straight away
     * @param script the javascript to be run.
     */
    public void runScript(String script) {
        if (mapRequestCount < 2) {
            mapRequestCount++;
            webEngine.getLoadWorker().stateProperty().addListener(
                    (ov, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            webEngine.executeScript(script);
                        }
                    });
        } else {
            webEngine.executeScript(script);
        }
    }

    /**
     * Returns the current webengine object. Used in AnalysisTabController to add a listener to wait until
     * the javascript has loaded correctly before trying to plot markers
     * @return The current WebEngine object
     */
    public WebEngine getWebEngine() {
        return webEngine;
    }
}
