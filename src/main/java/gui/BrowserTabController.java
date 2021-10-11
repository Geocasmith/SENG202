package gui;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.util.Objects;

/**
 * Represents BrowserTabController object,
 * Holds attribute variables and associated methods used in manipulation of BrowserTab controller object
 * @author Bede Skinner-Vennell
 * Date 09/10/2021
 */

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

    private WebEngine browserWebEngine;

    /**
     * Starts up the retrieves the webEngine to allow other methods to access it
     */
    @FXML
    private void initialize() {
        browserWebEngine = browserWebView.getEngine();

    }
    /**
     * Goes to the previous page in the browsers history
     */
    public void goBack() {
        Platform.runLater(() -> browserWebEngine.executeScript("history.back()"));
    }

    /**
     * Goes to the next page in the browsers' history, if any
     */
    public void goForward() {
        Platform.runLater(() -> browserWebEngine.executeScript("history.forward()"));
    }

    /**
     * Sets the back and forward buttons and the browser invisible, clears the search field, and loads a blank page
     * on the webengine so that the old search isn't visible when the user clicks search the next time
     */
    public void resetPage() {
        webBrowserBackButton.setVisible(false);
        webBrowserForwardButton.setVisible(false);
        browserWebView.setVisible(false);
        webBrowserSearchField.setText("");
        browserWebEngine.load(Objects.requireNonNull(getClass().getResource("blankPage.html")).toString());
    }

    /**
     * Executes the search if the user presses enter while in the search field
     */
    @FXML
    public void searchFieldOnEnter(){
        searchQuery();
    }

    /**
     * Checks that the user has entered a search term, then checks if the user wants to search government sites or news
     * articles and adds the relevant search terms to the query and finally loads it on the webEngine and makes the
     * back and forwards buttons and the browser visible
     */
    public void searchQuery() {

        String query = webBrowserSearchField.getText();
        if (!query.equals("")) {
            String webpage = "http://google.com/search?q=";

            if (govWebsitesRadioButton.isSelected()) {
                query += "+site:.gov";

            } else if (newsWebsitesRadioButton.isSelected()) {
                webpage = "http://news.google.com/search?q=";
            }

            // Makes the browser retrieve US search results
            query += "&hl=en-US&gl=US&ceid=US:en";

            browserWebEngine.load(webpage + query);

            webBrowserBackButton.setVisible(true);
            webBrowserForwardButton.setVisible(true);
            browserWebView.setVisible(true);
        }
    }

    /**
     * Checks that the program has an internet connection
     * @return a boolean, true if the program has an internet connection, false otherwise
     */
    public static boolean checkConnection() {
        boolean connected;
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            connected = true;
        } catch (IOException e) {
            connected = false;
        }
        return connected;
    }
}
