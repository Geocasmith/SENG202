package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class BrowserTabController {
    @FXML
    private WebView browserWebView;

    private WebEngine browserWebEngine;

    @FXML
    void initialize() {
        browserWebEngine= browserWebView.getEngine();
        browserWebEngine.load("http://google.com");
    }

    public void goBack() {
        Platform.runLater(() -> {
            browserWebEngine.executeScript("history.back()");
        });
    }

    public void goForward() {
        Platform.runLater(() -> {
            browserWebEngine.executeScript("history.forward()");
        });
    }

    public void goHome() {
        browserWebEngine.load("http://google.com");
    }
}
