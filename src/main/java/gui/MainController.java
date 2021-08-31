package gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.Pane;

import java.io.IOException;
import java.util.Objects;

public class MainController {

    @FXML
    private TabPane mainTabPane;

    @FXML
    private void initialize()  throws IOException {
    // Pane newLoadedPane = FXMLLoader.load(getClass().getResource("map.fxml"));
    // mainTabPane.getTabs().addAll((Tab)FXMLLoader.load(this.getClass().getResource("testmap.fxml")));
    }
}
