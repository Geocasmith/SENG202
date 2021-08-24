package gui;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.util.ArrayList;
import java.util.Arrays;

public class MapController {
    @FXML
    private WebView webView;

    private WebEngine webEngine;

    @FXML
    private void initialize() {
        webEngine= webView.getEngine();
        webEngine.load(getClass().getResource("googlemaps.html").toString());
    }

//    public void plotPoint(Double lat, Double lng) {
//
//    }

    public void plotPoint(ActionEvent actionEvent) {
        String rand = "document.plotPoint(" + 15 + ", " + 23 + ")";
        webEngine.executeScript(rand);
    }

    public void deleteMarkers(ActionEvent actionEvent) {
        webEngine.executeScript("document.deletePoints()");
    }
}
