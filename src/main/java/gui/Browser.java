package gui;

import javafx.scene.layout.Region;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class Browser extends Region {
    final WebView webView = new WebView();
    final WebEngine webEngine = webView.getEngine();

    public Browser() {
        webEngine.load(getClass().getResource("googlemaps.html").toString());
        getChildren().add(webView);
    }
}
