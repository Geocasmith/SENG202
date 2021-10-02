package gui;

import backend.Record;
import com.google.gson.JsonArray;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.Objects;

public class MapTabController {
    @FXML
    private WebView webView;

    private WebEngine webEngine;

    /**
     * Loads the Google Maps html file with the webengine
     */
    @FXML
    private void initialize() {
        webEngine = webView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getResource("googlemaps.html")).toString());
    }

    /**
     * Plots each of the records on the map, and gives them an infoWindow with their case number, date, and their
     * primary, secondary and location descriptions
     * @param records An ArrayList of crime records to be plotted
     */
    public void plotPoints(ArrayList<Record> records) {
        String newLocationMarker;
        JsonArray recordArray;

        for (Record record : records) {
            recordArray = createJsonArray(record);
            newLocationMarker = "document.plotPoint(" + recordArray + ")";
            webEngine.executeScript(newLocationMarker);
        }


    }

    private JsonArray createJsonArray(Record record) {
        JsonArray recordArray = new JsonArray();
        recordArray.add(record.getLatitude());
        recordArray.add(record.getLongitude());
        recordArray.add(record.getCaseNumber());
        recordArray.add(record.getDate());
        recordArray.add(record.getPrimaryDescription());
        recordArray.add(record.getSecondaryDescription());
        recordArray.add(record.getLocationDescription());
        return recordArray;
    }

    /**
     * Clears existing markers then plots the given crime records
     * @param records An ArrayList of crime records to be plotted
     */
    public void updateMarkers(ArrayList<Record> records) {
        clearMarkers();
        plotPoints(records);
    }

    /**
     * Clears all the existing markers and lines from the map
     */
    public void clearMarkers() {
        webEngine.executeScript("document.clearMap()");
    }
}
