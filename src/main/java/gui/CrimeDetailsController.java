package gui;

import backend.*;
import backend.Record;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents CrimeDetailsController Object
 * This class holds methods to create and populate crime details table view
 * @author Sofonias Tekele Tesfaye
 * Date 09/10/2021
 */
public class CrimeDetailsController {

    @FXML
    private TableView<TypeFrequencyPair> crimeTable;

    @FXML private TableColumn<TypeFrequencyPair, String> crimeCol;
    @FXML private TableColumn<TypeFrequencyPair, String> crimeFrequency;

    private static final double tableHeightMultiplier = 1.03; // Makes the table slightly taller than 10 rows to get rid of the scroll bar
    private int displayLimit = 10;

    private DataAnalyser dataAnalyser = new DataAnalyser();

    /**
     * Calls a method to populate crimes table.
     * @param currentRecord ArrayList<Record> type object that usually represents the list of displayed records
     */

    public void updateBlockDetails(ArrayList<Record> currentRecord) {


        populateCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
//        crimeTable.setFixedCellSize(25);
//        crimeTable.prefHeightProperty().bind(crimeTable.fixedCellSizeProperty().multiply(displayLimit + 1).multiply(tableHeightMultiplier));
//        crimeTable.minHeightProperty().bind(crimeTable.prefHeightProperty());
//        crimeTable.maxHeightProperty().bind(crimeTable.prefHeightProperty());

    }

    /**
     * Populates crime table with the passed crimeFrequency pair objects
     * @param crimeFrequencyPair usually represents an object that represents crime type and its frequency encapsulated
     *                           as crimeFrequency pair object
     */

    public void populateCrimesTable(List<TypeFrequencyPair> crimeFrequencyPair) {
        // Sort descending
        Collections.sort(crimeFrequencyPair, new FrequencyComparatorDescending());
        //Table column set up
        crimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        crimeFrequency.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        //Table set up
        crimeTable.getItems().clear();

        //Populate table
        for (int i = 0; i < crimeFrequencyPair.size(); i++) {
            crimeTable.getItems().add(crimeFrequencyPair.get(i));
        }
    }
}
