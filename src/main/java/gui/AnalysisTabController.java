
package gui;


import backend.*;
import backend.Record;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.*;

public class AnalysisTabController {
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
    private int displayLimit = 10;
    private ArrayList<Record> displayedRecords;
    private final DataAnalyser dataAnalyser = new DataAnalyser();
    private static final double tableHeightMultiplier = 1.03; // Makes the table slightly taller than 10 rows to get rid of the scroll bar
    private int mapOpenedCounter = 0;



    /**
     * Initialises FXML properties, sets the max height of the tables to slightly over 10 rows
     */
    @FXML
    private void initialize() {

        updateTableHeight(topCrimeTable);
        updateTableHeight(bottomCrimeTable);
        updateTableHeight(bottomBlockTable);
        updateTableHeight(topBlockTable);
        setupContextMenu();

        topBlockTable.setRowFactory(tv -> {
            TableRow<TypeFrequencyPair> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TypeFrequencyPair rowData = row.getItem();
                    try {
                        showBlockCrimeDetails(rowData.getType());
                    } catch (IOException e) {
                        PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
                    }
                }
            });
            return row;
        });

        bottomBlockTable.setRowFactory(tv -> {
            TableRow<TypeFrequencyPair> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TypeFrequencyPair rowData = row.getItem();
                    try {
                        showBlockCrimeDetails(rowData.getType());
                    } catch (IOException e) {
                        PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
                    }
                }
            });
            return row;
        });


    }

    /**
     * Auto adjusts the height of the given table to be slightly over the height of 10 rows, as that's the max
     * allowed in each table
     * @param table the TableView which needs it's height adjusted
     */
    private void updateTableHeight(TableView<TypeFrequencyPair> table) {
        table.setFixedCellSize(25);
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(displayLimit + 1).multiply(tableHeightMultiplier));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }

    public void updateAnalysis(ArrayList<Record> currentRecord) {

        populateTopCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
        populateLowCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
        populateTopBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
        populateLowBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
        this.displayedRecords = currentRecord;

    }


    /**
     * Populates the top crime table with list of TypeFrequency objects that is passed to it
     * @param crimeFrequencyPair usually a list of TypeFrequency pair object
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
     * @param crimeFrequencyPair usually a list of TypeFrequency pair object
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
     * @param blocksFrequencyPair usually a list of TypeFrequency pair object containing blocks and their
     *                            frequency
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
     * @param blocksFrequencyPair usually a list of TypeFrequency pair object containing blocks and their
     *                            frequency
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


    private void setupContextMenu() {

        // Create menus
        ContextMenu tableContextMenu = new ContextMenu();

        // Create menu items
        MenuItem showOnMap = new MenuItem("Show on Map");
        MenuItem showDetails = new MenuItem("Show Details");

        // Add click event handlers to the menu items
        showOnMap.setOnAction(actionEvent -> {
            try {
                String block = topBlockTable.getSelectionModel().getSelectedItem().getType();
                showOnMap(block);
            } catch (NullPointerException | IOException e) {
                PopupWindow.displayPopup("Error", "Error displaying the map, please try again.\n" +
                        "If this problem persists, try restarting Insight.");
            }
        });

        showDetails.setOnAction(actionEvent -> {
            try {
                String block = topBlockTable.getSelectionModel().getSelectedItem().getType();
                showBlockCrimeDetails(block);

            } catch (NullPointerException | IOException e) {
                PopupWindow.displayPopup("Error", "Error displaying crime details, please try again.\n" +
                        "If this problem persists, try restarting Insight.");
            }
        });




        // Add menu items to the relevant menus
        tableContextMenu.getItems().addAll(showOnMap, showDetails);

        topBlockTable.setContextMenu(tableContextMenu);
    }

    public void showOnMap(String block) throws IOException {

        boolean connected = BrowserTabController.checkConnection();

        if (!connected) {
            PopupWindow.displayPopup("Error", "You must be connected to the internet to use the map.");
        } else {
            ArrayList<Record> records = new ArrayList<>();

            for (Record rec : displayedRecords) {
                if (rec.getBlock().equalsIgnoreCase(block)) {
                    records.add(rec);
                }
            }

            // Only show this warning the first time the user opens the map from this screen
            if (mapOpenedCounter == 0) {
                mapOpenedCounter++;
                PopupWindow.displayPopup("Note", "Crimes with incorrectly entered blocks or coordinates\n" +
                        "may show up in odd locations on the map.");
            }

            Stage popupMap = new Stage();
            popupMap.initModality(Modality.APPLICATION_MODAL);
            popupMap.setTitle("Crime Occurrences in " + records.get(0).getBlock());

            FXMLLoader loader = new FXMLLoader(getClass().getResource("mapTab.fxml"));
            popupMap.setScene(new Scene(loader.load()));
            MapTabController mapTabController = loader.getController();

            // Wait and make sure javascript has fully loaded before trying to plot the records
            mapTabController.getWebEngine().getLoadWorker().stateProperty().addListener(
                    (ov, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            mapTabController.clearMap();
                            mapTabController.plotMarkers(records, true);
                        }
                    });


            popupMap.showAndWait();
        }




    }

    public void showBlockCrimeDetails(String block) throws IOException {
        ArrayList<Record> records = new ArrayList<>();

        for (Record rec : displayedRecords) {
            if (rec.getBlock().equalsIgnoreCase(block)) {
                records.add(rec);
            }
        }

        Stage popupCrimeDetails= new Stage();
        popupCrimeDetails.initModality(Modality.APPLICATION_MODAL);

        FXMLLoader loader = new FXMLLoader(getClass().getResource("crimeDetails.fxml"));
        popupCrimeDetails.setScene(new Scene(loader.load()));

        CrimeDetailsController crimeDetailsController = loader.getController();

        if (records.size() != 0) {
            crimeDetailsController.updateBlockDetails(records);
            popupCrimeDetails.setTitle("Crime Details in " + records.get(0).getBlock());
            popupCrimeDetails.showAndWait();

        }
        else {
            PopupWindow.displayPopup("No Crime to show", "There is no crime to show");
        }


    }





}