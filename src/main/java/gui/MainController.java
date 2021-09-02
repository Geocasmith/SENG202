package gui;

import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.Pane;
import backend.Record;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;

public class MainController {

    @FXML
    private TabPane mainTabPane;
    @FXML
    private TableView mainTableView;

    @FXML
    private void initialize()  throws IOException {
    // Pane newLoadedPane = FXMLLoader.load(getClass().getResource("map.fxml"));
    // mainTabPane.getTabs().addAll((Tab)FXMLLoader.load(this.getClass().getResource("testmap.fxml")));
        tableThing();
    }

    /**
     * Adds a column to the main table with the mentioned display and property names, taken from the record class.
     * @param displayName The name to be used as the column header. Can be whatever you fancy.
     * @param propertyName The name of the attribute in the record class. This needs to match but doesn't seem
     *                     to be case-sensitive. This hooks up to a get[propertyName] method and uses the output
     *                     to fill in the column. Can work with at least strings and bools.
     */
    public void addTableColumn(String displayName, String propertyName) {
        TableColumn<Record, String> col = new TableColumn<>(displayName);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        mainTableView.getColumns().add(col);
    }

    public void tableThing() { // testing things with tables
        mainTableView.setEditable(false);

        addTableColumn("Case Number", "caseNumber");
        addTableColumn("Date", "date");
        addTableColumn("Block", "block");
        addTableColumn("IUCR", "iucr");
        addTableColumn("Primary Description", "primaryDescription");
        addTableColumn("Secondary Description", "secondaryDescription");
        addTableColumn("Location Description", "locationDescription");
        addTableColumn("Arrest", "arrest");
        addTableColumn("Domestic", "domestic");
        addTableColumn("Beat", "beat");
        addTableColumn("Ward", "ward");
        addTableColumn("FBICD", "fbicd");
        addTableColumn("X-Coordinate", "xcoord");
        addTableColumn("Y-Coordinate", "ycoord");
        addTableColumn("Latitude", "latitude");
        addTableColumn("Longitude", "longitude");

        Record testRecord;
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        testRecord = new Record(data);

        for (int i = 0; i < 50; i++){
            mainTableView.getItems().add(testRecord);
        }
    }
}
