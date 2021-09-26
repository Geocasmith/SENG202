package gui;

import backend.Record;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;

public class MapTabController {
    @FXML
    private WebView webView;

    private WebEngine webEngine;

    /**
     * Loads the Google Maps html file with the webengine
     */
    @FXML
    void initialize() {
        webEngine= webView.getEngine();
        webEngine.load(getClass().getResource("googlemaps.html").toString());
    }

    /**
     * Plots each of the records on the map, and gives them an infoWindow with their case number, date, and their
     * primary, secondary and location descriptions
     * @param records An ArrayList of crime records to be plotted
     */
    public void plotPoints(ArrayList<Record> records) {
        String newLocationMarker;
        for (Record record : records) {
            newLocationMarker = "document.plotPoint(" + record.getLatitude() + ", " + record.getLongitude() + ", '" +
                    record.getCaseNumber() + "', '" + record.getDate() + "', '" + record.getPrimaryDescription() + "', '" + record.getSecondaryDescription() +
                    "', '" + record.getLocationDescription() + "')";
            webEngine.executeScript(newLocationMarker);
        }


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
     * Clears all the existing markers from the map
     */
    public void clearMarkers() {
        webEngine.executeScript("document.deletePoints()");
    }
}
