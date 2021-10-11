package gui;

import data.DataAnalyser;
import data.DataManipulator;
import frequencyComparator.FrequencyComparatorDescending;
import data.Record;
import frequencyComparator.TypeFrequencyPair;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

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
    @FXML
    private TableColumn<TypeFrequencyPair, String> crimeCol;
    @FXML
    private TableColumn<TypeFrequencyPair, String> crimeFrequency;

    private final DataAnalyser dataAnalyser = new DataAnalyser();

    /**
     * Calls a method to populate crimes table.
     * @param currentRecord ArrayList<Record> type object that usually represents the list of displayed records
     */
    public void updateBlockDetails(List<Record> currentRecord) {
        populateCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));

    }

    /**
     * Populates crime table with the passed crimeFrequency pair objects
     * @param crimeFrequencyPair usually represents an object that represents crime type and its frequency encapsulated
     *                           as crimeFrequency pair object
     */
    public void populateCrimesTable(List<TypeFrequencyPair> crimeFrequencyPair) {
        // Sort descending
        crimeFrequencyPair.sort(new FrequencyComparatorDescending());
        //Table column set up
        crimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        crimeFrequency.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        //Table set up
        crimeTable.getItems().clear();

        //Populate table
        for (TypeFrequencyPair typeFrequencyPair : crimeFrequencyPair) {
            crimeTable.getItems().add(typeFrequencyPair);
        }
    }
}
