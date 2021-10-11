
package gui;


import data.DataAnalyser;
import data.DataManipulator;
import frequencyComparator.FrequencyComparatorAscending;
import frequencyComparator.FrequencyComparatorDescending;
import data.Record;
import frequencyComparator.TypeFrequencyPair;
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

/**
 * Represents AnalysisTab controller object
 * Holds methods to control analysis tab
 * @author Sofonias Tekele Tesfaye
 * @author Bede Skinner-Vennell (Integration to main program)
 * Date 09/10/2021
 */
public class AnalysisTabController {

    @FXML private TableView<TypeFrequencyPair> topCrimeTable;
    @FXML private TableView<TypeFrequencyPair> bottomCrimeTable;
    @FXML private TableView<TypeFrequencyPair> bottomBlockTable;
    @FXML private TableView<TypeFrequencyPair> topBlockTable;

    @FXML private TableColumn<TypeFrequencyPair, String> topCrimeCol;
    @FXML private TableColumn<TypeFrequencyPair, String> topCrimeFrequencyCol;
    @FXML private TableColumn<TypeFrequencyPair, String> topBlockCol;
    @FXML private TableColumn<TypeFrequencyPair, String> topBlockFrequencyCol;

    @FXML private TableColumn<TypeFrequencyPair, String> bottomCrimeCol;
    @FXML private TableColumn<TypeFrequencyPair, String> bottomCrimeFrequencyCol;
    @FXML private TableColumn<TypeFrequencyPair, String> bottomBlockCol;
    @FXML private TableColumn<TypeFrequencyPair, String> bottomBlockFrequencyCol;
    private int displayLimit = 10;
    private static final int CRIMETYPECOLUMN = 4;
    private static final int BLOCKCOLUMN = 2;
    private List<Record> displayedRecords;
    private final DataAnalyser dataAnalyser = new DataAnalyser();
    private static final double TABLEHEIGHTMULTIPLIER = 1.03; // Makes the table slightly taller than 10 rows to get rid of the scroll bar
    private int mapOpenedCounter = 0;
    private List<TypeFrequencyPair> crimeFrequencyPair = new ArrayList<>();
    private List<TypeFrequencyPair> blocksFrequencyPair = new ArrayList<>();
    private final CrimesPieChartController crimesChart = new CrimesPieChartController();


    /**
     * Initialises FXML properties, sets the max height of the tables to slightly over 10 rows
     */
    @FXML
    private void initialize() {
        updateTableHeight(topCrimeTable);
        updateTableHeight(bottomCrimeTable);
        updateTableHeight(bottomBlockTable);
        updateTableHeight(topBlockTable);

        /* Set up context menus for topBlockTable and bottomBlockTable */
        setupTableContextMenu(topBlockTable);
        setupTableContextMenu(bottomBlockTable);


        /* topBlockTable row event Listener */
        rowEventListener(topBlockTable);

        /* bottomBlockTable row event listener */
        rowEventListener(bottomBlockTable);

    }

    /**
     * Triggers an event in case of a double click on topBlockTable or bottomBlockTable.
     * @param blockTable TableView usually representing topBlockTable or bottomBlockTable
     */
    private void rowEventListener(TableView<TypeFrequencyPair> blockTable) {
        blockTable.setRowFactory(tv -> {
            TableRow<TypeFrequencyPair> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    TypeFrequencyPair rowData = row.getItem();
                    try { showBlockCrimeDetails(rowData.getType()); }
                    catch (IOException e) {
                        PopupWindow.displayPopup("Error", "An error occurred. Exception details follow:\n" +
                                e.getMessage());
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
        table.prefHeightProperty().bind(table.fixedCellSizeProperty().multiply(displayLimit + 1).multiply(TABLEHEIGHTMULTIPLIER));
        table.minHeightProperty().bind(table.prefHeightProperty());
        table.maxHeightProperty().bind(table.prefHeightProperty());
    }

    /**
     * Calls all the methods that populate the 4 different tables that the controller has access to
     * @param currentRecord Array list of records that are displayed in main table
     */
    public void updateAnalysis(List<Record> currentRecord) {

        crimeFrequencyPair = dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, CRIMETYPECOLUMN));
        blocksFrequencyPair = dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, BLOCKCOLUMN));

        populateTopCrimesTable();
        populateLowCrimesTable();
        populateTopBlocksTable();
        populateLowBlocksTable();
        this.displayedRecords = currentRecord;
    }

    /**
     * Populates the top crime table with list of TypeFrequency objects that is passed to it
     *
     */
    public void populateTopCrimesTable() {
        // Sort descending
        crimeFrequencyPair.sort(new FrequencyComparatorDescending());
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
     * Populates the bottom crime table with list of TypeFrequency objects the class owns
     */
    public void populateLowCrimesTable() {

        crimeFrequencyPair.sort(new FrequencyComparatorAscending());

        // Table column setup
        bottomCrimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        bottomCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
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
     */
    public void populateTopBlocksTable() {

        blocksFrequencyPair.sort(new FrequencyComparatorDescending());
        // Set up table columns
        topBlockCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        topBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        // Set up table
        topBlockTable.getItems().clear();
        displayLimit = Math.min(blocksFrequencyPair.size(), 10);

        for (int i = 0; i < displayLimit; i++) {
            topBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }

    }

    /**
     * Populates the bottom block table with list of TypeFrequency objects that is passed to it
     */
    public void populateLowBlocksTable() {


        blocksFrequencyPair.sort(new FrequencyComparatorAscending());
        // Set up table columns
        bottomBlockCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        bottomBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        // Set up table
        bottomBlockTable.getItems().clear();

        displayLimit = Math.min(blocksFrequencyPair.size(), 10);

        for (int i = 0; i < displayLimit; i++) {
            bottomBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }
    }


    /**
     * Sets up context menu and its associated events for topBlockTable and bottomBlockTable
     */
    private void setupTableContextMenu(TableView<TypeFrequencyPair> blockTable) {
        ContextMenu tableContextMenu = new ContextMenu();

        // Create menu items
        MenuItem showOnMap = new MenuItem("Show on Map");
        MenuItem showDetails = new MenuItem("Show Details");

        // Add click event handlers to the menu items
        showOnMap.setOnAction(actionEvent -> {
            try {
                String block = blockTable.getSelectionModel().getSelectedItem().getType();
                showOnMap(block);
            } catch (NullPointerException | IOException e) {
                PopupWindow.displayPopup("Error", "Error displaying the map, please try again.\n" +
                        "If this problem persists, try restarting Insight.\nException details follow:\n" +
                        e.getMessage());
            }
        });

        showDetails.setOnAction(actionEvent -> {
            try {
                String block = blockTable.getSelectionModel().getSelectedItem().getType();
                showBlockCrimeDetails(block);

            } catch (NullPointerException | IOException e) {
                PopupWindow.displayPopup("Error", "Error displaying crime details, please try again.\n" +
                        "If this problem persists, try restarting Insight.\nException details follow:\n" +
                        e.getMessage());
            }
        });

        // Add menu items to the relevant menus
        tableContextMenu.getItems().addAll(showOnMap, showDetails);

        blockTable.setContextMenu(tableContextMenu);
    }



    /**
     * Shows crime location of a given block in a map. To achieve this the method calls a pop up map window
     * @param block a string parameter that usually represents the block
     * @throws IOException
     */
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

    /**
     *  Shows list of crime types together with their frequency that have occurred in the block parameter that is
     *  passed to the method. To display out put the method calls a pop up window.
     * @param block a string parameter that usually represents the block
     * @throws IOException
     */
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

        if (!records.isEmpty()) {
            crimeDetailsController.updateBlockDetails(records);
            popupCrimeDetails.setTitle("Insight - Crime Details in " + records.get(0).getBlock());
            popupCrimeDetails.showAndWait();

        }
        else {
            PopupWindow.displayPopup("No Crime to show", "There is no crime to show");
        }
    }

    /**
     * This method is triggered by a button click event from Analysis tab. It calls drawChart method of crimeChart object
     * to display a pie chart of bottom top crimes against their frequency
     */
    public void showCrimeTypePieChart() {

        String windowTitle = "Top crime types";
        crimeFrequencyPair.sort(new FrequencyComparatorDescending());
        if (crimeFrequencyPair.size() > 10) {
            String pieChartTitle = "Top 10 crime types from filtered records";
            crimesChart.drawChart( pieChartTitle, windowTitle , (new ArrayList<> (crimeFrequencyPair.subList(0, 10))));
        }
        else {
            String pieChartTitle = "Top " + crimeFrequencyPair.size() + " crime types from filtered records";
            crimesChart.drawChart( pieChartTitle, windowTitle , crimeFrequencyPair);
        }


    }

    /**
     * This method is triggered by a button click event from Analysis tab. It calls drawChart method of crimeChart object
     * to display a pie chart of top 10 blocks against the frequency of crime in that block
     */
    public void showBlockCrimePieChart() {
        String windowTitle = "Top blocks for crime occurrences";
        blocksFrequencyPair.sort(new FrequencyComparatorDescending());
        if (blocksFrequencyPair.size() > 10) {
            String pieChartTitle = "Top 10 blocks from filtered records for highest crime types";
            crimesChart.drawChart( pieChartTitle, windowTitle , (new ArrayList<> (blocksFrequencyPair.subList(0, 10))));
        }
        else {
            String pieChartTitle = "Top " + blocksFrequencyPair.size() + " blocks from filtered records for highest crime types";
            crimesChart.drawChart( pieChartTitle, windowTitle , blocksFrequencyPair);
        }
    }

    /**
     * This method is triggered by a button click event from Analysis tab. It calls drawChart method of crimeChart object
     * to display a pie chart of bottom 10 blocks against the frequency of crime in that block
     */
    public void showBottomBlockCrimePieChart() {
        String windowTitle = "Bottom blocks for crime occurrences";
        blocksFrequencyPair.sort(new FrequencyComparatorAscending());
        if (blocksFrequencyPair.size() > 10) {
            String pieChartTitle = "Bottom 10 blocks from filtered records for highest crime types";
            crimesChart.drawChart( pieChartTitle, windowTitle , (new ArrayList<> (blocksFrequencyPair.subList(0, 10))));
        }
        else {
            String pieChartTitle = "Bottom " + blocksFrequencyPair.size() + " blocks from filtered records for lowest crime types";
            crimesChart.drawChart( pieChartTitle, windowTitle , blocksFrequencyPair);
        }
    }

    /**
     * This method is triggered by a button click event from Analysis tab. It calls drawChart method of crimeChart object
     * to display a pie chart of bottom top crimes against their frequency
     */
    public void showBottomCrimeTypePieChart() {

        String windowTitle = "Bottom crime types";
        crimeFrequencyPair.sort(new FrequencyComparatorAscending());
        if (crimeFrequencyPair.size() > 10) {
            String pieChartTitle = "Bottom 10 crime types from filtered records";
            crimesChart.drawChart( pieChartTitle, windowTitle , (new ArrayList<> (crimeFrequencyPair.subList(0, 10))));
        }
        else {
            String pieChartTitle = "Bottom " + crimeFrequencyPair.size() + " crime types from filtered records";
            crimesChart.drawChart( pieChartTitle, windowTitle , crimeFrequencyPair);
        }
    }

}