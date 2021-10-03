package gui;

import backend.DataAnalyser;
import backend.Record;
import com.google.gson.JsonArray;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.Objects;

public class AnalysisPopupController {

    @FXML
    private Label titleLabel;
    @FXML
    private Label infoLabel;
    @FXML
    private WebView mapWebView;
    private WebEngine mapWebEngine;
    private DataAnalyser dataAnalyser = new DataAnalyser();

    /**
     * Initialises the webengine and loads the google maps page
     */
    @FXML
    private void initialize() {
        mapWebEngine = mapWebView.getEngine();
        mapWebEngine.load(Objects.requireNonNull(getClass().getResource("googlemaps.html")).toString());
    }

    /**
     * Takes two crime records, calculates the time and location difference between them, and plots them on a map with
     * a line between them
     * @param record1 the first crime record
     * @param record2 the second crime record
     */
    public void initData(Record record1, Record record2) {
        String locationDifference = dataAnalyser.getLocationDifferenceString(record1, record2);
        String timeDifference = dataAnalyser.getTimeDifferenceString(record1, record2);
        titleLabel.setText(String.format("Differences between case numbers %s & %s",record1.getCaseNumber(), record2.getCaseNumber()));
        infoLabel.setText("Time Difference: " + timeDifference + "\n" + "Location Difference: " + locationDifference);
        initMap(record1, record2);
    }

    /**
     * Takes two records and plots them on a map with a line between them
     * @param record1 the first crime record
     * @param record2 the second crime record
     */
    public void initMap(Record record1, Record record2) {
        JsonArray record1Array = record1.getJsonArray();
        JsonArray record2Array = record2.getJsonArray();
        clearMap();
        runScript("document.analysePoints(" + record1Array + ", " + record2Array + ")");

    }

    /**
     * Takes a string of javascript code and checks that the webengine has loaded correctly, then runs the code
     * @param script the javascript code to run in string form
     */
    private void runScript(String script) {
        mapWebEngine.getLoadWorker().stateProperty().addListener(
                (ov, oldState, newState) -> {
                    if (newState == Worker.State.SUCCEEDED) {
                        mapWebEngine.executeScript(script);
                    }
                });

    }

    /**
     * Clears all the existing markers and lines from the map
     */
    public void clearMap() {
        runScript("document.clearMap()");
    }
}