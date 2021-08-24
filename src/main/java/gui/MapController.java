package gui;

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
//        plotPoint(new ArrayList<Double>(Arrays.asList(1.4, 2.2)));
    }

    public void plotPoint(ArrayList<Double> location) {
        webEngine.executeScript(String.format("plotPoint(%f, %f)", location.get(0), location.get(1)));
    }
}
