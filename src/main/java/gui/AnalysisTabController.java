package gui;

import backend.*;
import backend.Record;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
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
    int displayLimit;
    private DataAnalyser dataAnalyser = new DataAnalyser();


    /**
     * Initialises FXML properties
     */
    @FXML
    void initialize() {
    }

    public void updateAnalysis(ArrayList<Record> currentRecord) {



        if (currentRecord.size() > 10) {
            displayLimit = 10;
        }
        else {
            displayLimit = currentRecord.size();
        }
        populateTopCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
        populateLowCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
        populateTopBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
        populateLowBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
    }



    public void populateTopCrimesTable(ArrayList<TypeFrequencyPair> crimeFrequencyPair) {
        Collections.sort(crimeFrequencyPair, new FrequencyComparatorDescending());
        topCrimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        topCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        topCrimeTable.getItems().clear();

        for (int i = 0; i < displayLimit; i++) {
            topCrimeTable.getItems().add(crimeFrequencyPair.get(i));
        }



    }

    public void populateLowCrimesTable(ArrayList<TypeFrequencyPair> crimeFrequencyPair) {
        Collections.sort(crimeFrequencyPair, new FrequencyComparatorAscending());
        bottomCrimeCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("type"));
        bottomCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("frequency"));
        bottomCrimeTable.getItems().clear();

        for (int i = 0; i < displayLimit; i++) {
            bottomCrimeTable.getItems().add(crimeFrequencyPair.get(i));
        }


    }

    public void populateTopBlocksTable(ArrayList<TypeFrequencyPair> blocksFrequencyPair) {
        Collections.sort(blocksFrequencyPair, new FrequencyComparatorDescending());
        topBlockCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("type"));
        topBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("frequency"));
        topBlockTable.getItems().clear();

        for (int i = 0; i < displayLimit; i++) {
            topBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }




    }

    public void populateLowBlocksTable(ArrayList<TypeFrequencyPair> blocksFrequencyPair) {
        Collections.sort(blocksFrequencyPair, new FrequencyComparatorAscending());
        bottomBlockCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("type"));
        bottomBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencyPair, String>("frequency"));
        bottomBlockTable.getItems().clear();

        for (int i = 0; i < displayLimit; i++) {
            bottomBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }


    }



}

