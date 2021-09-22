package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.awt.*;

public class BrowserTabController {
    @FXML
    private WebView browserWebView;
    @FXML
    private TextField searchQueryField;
    @FXML
    private AnchorPane searchPageAnchorPane;
    @FXML
    private Button webBrowserForwardButton;
    @FXML
    private Button webBrowserBackButton;
    @FXML
    private RadioButton govWebsitesRadioButton;
    @FXML
    private RadioButton newsWebsitesRadioButton;

    private WebEngine browserWebEngine;

    @FXML
    void initialize() {
        browserWebEngine= browserWebView.getEngine();

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
        webBrowserBackButton.setVisible(false);
        webBrowserForwardButton.setVisible(false);
        browserWebView.setVisible(false);
        searchQueryField.setText("");
        browserWebEngine.load("http://www.blank.org/");
    }

    @FXML
    public void searchFieldOnEnter(ActionEvent ae){
        searchQuery();
    }

    public void searchQuery() {

        String query = searchQueryField.getText();
        if (!query.equals("")) {
            String webpage = "http://google.com/search?q=";
            query.replace(" ", "+");
            if (govWebsitesRadioButton.isSelected()) {
                query += "+site:.gov";
            } else if (newsWebsitesRadioButton.isSelected()) {
                webpage = "http://news.google.com/search?q=";
            }

            browserWebEngine.load(webpage + query);

            webBrowserBackButton.setVisible(true);
            webBrowserForwardButton.setVisible(true);
            browserWebView.setVisible(true);
        }



    }
}
