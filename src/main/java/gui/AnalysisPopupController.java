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
    private final DataAnalyser dataAnalyser = new DataAnalyser();
    private int mapRequestCount = 0;

    /**
     * Initialises the webengine and loads the Google Maps page
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
        boolean connected = BrowserTabController.checkConnection();
        if (!connected) {
            PopupWindow.displayPopup("Error", "You must be connected to the internet to use the map feature");
            mapWebView.setVisible(false);
        } else {
            mapWebView.setVisible(true);
            initMap(record1, record2);
        }

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
     * If this is one of the first 2 times the controller is running a script, then wait until the webengine has fully loaded
     * the javascript then run the given script, otherwise, run it straight away
     * @param script the javascript to be run.
     */
    private void runScript(String script) {
        if (mapRequestCount < 2) {
            mapRequestCount++;
            mapWebEngine.getLoadWorker().stateProperty().addListener(
                    (ov, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            mapWebEngine.executeScript(script);
                        }
                    });
        } else {
            mapWebEngine.executeScript(script);
        }
    }

    /**
     * Clears all the existing markers and lines from the map
     */
    public void clearMap() {
        runScript("document.clearMap()");
    }
}