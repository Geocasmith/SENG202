package gui;

import backend.Record;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.Arrays;

public class MapTabController {
    @FXML
    private WebView webView;

    private WebEngine webEngine;

    @FXML
    void initialize() {
        webEngine= webView.getEngine();
        webEngine.load(getClass().getResource("googlemaps.html").toString());
    }

    public void plotOnePoint(ActionEvent actionEvent) {
        String rand = "document.plotPoint(" + 41.748486365 + ", " + -87.602675062 + ")";
        webEngine.executeScript(rand);
        rand = "document.plotPoint(" + 41.87154041 + ", " + -87.705838807 + ")";
        webEngine.executeScript(rand);
    }

    public void plotMultiplePoints(ArrayList<Record> records) {
        String newLocationMarker;
        for (Record record : records) {
            newLocationMarker = "document.plotPoint(" + record.getLatitude() + ", " + record.getLongitude() + ", '" +
                    record.getCaseNumber() + "', '" + record.getDate() + "', '" + record.getPrimaryDescription() + "', '" + record.getSecondaryDescription() +
                    "', '" + record.getLocationDescription() + "')";
            webEngine.executeScript(newLocationMarker);
        }


    }

    public void updateMarkers(ArrayList<Record> records) {
        clearMarkers();
        plotMultiplePoints(records);
    }

    public void clearMarkers() {
        webEngine.executeScript("document.deletePoints()");
    }
}
