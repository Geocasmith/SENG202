package gui;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class BrowserTabController {
    @FXML
    private WebView browserWebView;
    @FXML
    private TextField webBrowserSearchField;
    @FXML
    private Button webBrowserForwardButton;
    @FXML
    private Button webBrowserBackButton;
    @FXML
    private RadioButton govWebsitesRadioButton;
    @FXML
    private RadioButton newsWebsitesRadioButton;
    @FXML
    private Button webBrowserHomeButton;
    @FXML
    private Button webBrowserSearchButton;

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
        webBrowserSearchField.setText("");
        browserWebEngine.load("http://www.blank.org/");
    }

    @FXML
    public void searchFieldOnEnter(ActionEvent ae){
        searchQuery();
    }

    public void searchQuery() {

        String query = webBrowserSearchField.getText();
        if (!query.equals("")) {
            String webpage = "http://google.com/search?q=";
            query.replace(" ", "+");
            if (govWebsitesRadioButton.isSelected()) {
                query += "+site:.gov";
            } else if (newsWebsitesRadioButton.isSelected()) {
                webpage = "http://news.google.com/search?q=";
            }

            query += "&hl=en-US&gl=US&ceid=US:en";

            browserWebEngine.load(webpage + query);

            webBrowserBackButton.setVisible(true);
            webBrowserForwardButton.setVisible(true);
            browserWebView.setVisible(true);
        }



    }
}
