package gui;

import backend.*;
import backend.Record;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.GridPane;

import java.util.*;

public class AnalysisTabController {
    @FXML private GridPane grid;
    @FXML private TableView<TypeFrequencyPair> topCrimeTable;
    @FXML private TableView<TypeFrequencyPair> bottomCrimeTable;
    @FXML private TableView<TypeFrequencyPair> bottomBlockTable;
    @FXML private TableView<TypeFrequencyPair> topBlockTable;

    @FXML private TableColumn<TypeFrequencyPair, String> topCrimeCol = new TableColumn<>();
    @FXML private TableColumn<TypeFrequencyPair, String> topCrimeFrequencyCol;
    @FXML private TableColumn<TypeFrequencyPair, String> topBlockCol;
    @FXML private TableColumn<TypeFrequencyPair, String> topBlockFrequencyCol;

    @FXML private TableColumn<TypeFrequencyPair, String> bottomCrimeCol;
    @FXML private TableColumn<TypeFrequencyPair, String> bottomCrimeFrequencyCol;
    @FXML private TableColumn<TypeFrequencyPair, String> bottomBlockCol;
    @FXML private TableColumn<TypeFrequencyPair, String> bottomBlockFrequencyCol;
    private int displayLimit;
    private DataAnalyser dataAnalyser = new DataAnalyser();


    /**
     * Initialises FXML properties
     */
    @FXML
    private void initialize() {
    }

    public void updateAnalysis(ArrayList<Record> currentRecord) {

        populateTopCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
        populateLowCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
        populateTopBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
        populateLowBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
    }


    /**
     * Populates the top crime table with list of TypeFrequency objects that is passed to it
     * @param crimeFrequencyPair TypeFrequency pair object
     */
    public void populateTopCrimesTable(ArrayList<TypeFrequencyPair> crimeFrequencyPair) {
        // Sort descending
        Collections.sort(crimeFrequencyPair, new FrequencyComparatorDescending());
        //Table column set up
        topCrimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        topCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        //table set up
        topCrimeTable.getItems().clear();
        displayLimit = Math.min(crimeFrequencyPair.size(), 10);

        //Populate table
        for (int i = 0; i < displayLimit; i++) {
            topCrimeTable.getItems().add(crimeFrequencyPair.get(i));
        }



    }

    /**
     * Populates the bottom crime table with list of TypeFrequency objects that is passed to it
     * @param crimeFrequencyPair
     */

    public void populateLowCrimesTable(ArrayList<TypeFrequencyPair> crimeFrequencyPair) {

        Collections.sort(crimeFrequencyPair, new FrequencyComparatorAscending());
        // Table column setup
        bottomCrimeCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("type"));
        bottomCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("frequency"));
        //table set up
        bottomCrimeTable.getItems().clear();
        displayLimit = Math.min(crimeFrequencyPair.size(), 10);

        // populate table

        for (int i = 0; i < displayLimit; i++) {
            bottomCrimeTable.getItems().add(crimeFrequencyPair.get(i));
        }


    }

    /**
     * Populates the top block table with list of TypeFrequency objects that is passed to it
     * @param blocksFrequencyPair
     */

    public void populateTopBlocksTable(ArrayList<TypeFrequencyPair> blocksFrequencyPair) {

        Collections.sort(blocksFrequencyPair, new FrequencyComparatorDescending());
        // Set up table columns
        topBlockCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("type"));
        topBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("frequency"));

        // Set up table
        topBlockTable.getItems().clear();
        displayLimit = Math.min(blocksFrequencyPair.size(), 10);

        for (int i = 0; i < displayLimit; i++) {
            topBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }

    }

    /**
     * Populates the bottom block table with list of TypeFrequency objects that is passed to it
     * @param blocksFrequencyPair
     */

    public void populateLowBlocksTable(ArrayList<TypeFrequencyPair> blocksFrequencyPair) {


        Collections.sort(blocksFrequencyPair, new FrequencyComparatorAscending());
        // Set up table columns
        bottomBlockCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("type"));
        bottomBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("frequency"));

        // Set up table
        bottomBlockTable.getItems().clear();

        displayLimit = Math.min(blocksFrequencyPair.size(), 10);

        for (int i = 0; i < displayLimit; i++) {
            bottomBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }


    }



}

