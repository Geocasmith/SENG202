package gui;


import backend.*;
import backend.Record;
import backend.TypeFrequencypair;
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
    @FXML private TableView<TypeFrequencypair> topCrimeTable;
    @FXML private TableView<TypeFrequencypair> bottomCrimeTable;
    @FXML private TableView<TypeFrequencypair> bottomBlockTable;
    @FXML private TableView<TypeFrequencypair> topBlockTable;

    @FXML private TableColumn<TypeFrequencypair, String> topCrimeCol = new TableColumn<>();
    @FXML private TableColumn<TypeFrequencypair, String> topCrimeFrequencyCol;
    @FXML private TableColumn<TypeFrequencypair, String> topBlockCol;
    @FXML private TableColumn<TypeFrequencypair, String> topBlockFrequencyCol;

    @FXML private TableColumn<TypeFrequencypair, String> bottomCrimeCol;
    @FXML private TableColumn<TypeFrequencypair, String> bottomCrimeFrequencyCol;
    @FXML private TableColumn<TypeFrequencypair, String> bottomBlockCol;
    @FXML private TableColumn<TypeFrequencypair, String> bottomBlockFrequencyCol;
    int displayLimit;



    class frequecyComparatorDescending implements Comparator<TypeFrequencypair> {
        @Override
        public int compare(TypeFrequencypair a, TypeFrequencypair b) {
            return a.getFrequency() > b.getFrequency() ? -1 : a.getFrequency() == b.getFrequency() ? 0 : 1;
        }
    }

    public class frequecyComparatorAscending implements Comparator<TypeFrequencypair> {
        @Override
        public int compare(TypeFrequencypair a, TypeFrequencypair b) {
            return a.getFrequency() < b.getFrequency() ? -1 : a.getFrequency() == b.getFrequency() ? 0 : 1;
        }
    }


    /**
     * Comparator for TypeFrequency pair objects (Compares on basis of frequency in an ascending order)
     */

  public void start(ArrayList<Record> currentRecord) throws SQLException {

      Database db = new Database();
      db.connectDatabase();
      DataAnalyser dataAnalyser = new DataAnalyser();

      if (currentRecord.size() > 10) {
          displayLimit = 10;
      }
      else {
          displayLimit = currentRecord.size();
      }
      //populateTopCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
      //populateLowCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));
      //populateTopBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));
      //populateLowBlocksTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 2)));

  }



    public void populateTopCrimesTable(ArrayList<TypeFrequencypair> crimeFrequencyPair) throws SQLException {
        Collections.sort(crimeFrequencyPair, new AnalysisTabController.frequecyComparatorDescending());
        topCrimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        topCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<>("frequency"));

        for (int i = 0; i < displayLimit; i ++) {
            topCrimeTable.getItems().add(crimeFrequencyPair.get(i));
            System.out.println(crimeFrequencyPair.get(i).getType());
        }



    }

    public void populateLowCrimesTable(ArrayList<TypeFrequencypair> crimeFrequencyPair) throws SQLException {
        Collections.sort(crimeFrequencyPair, new AnalysisTabController.frequecyComparatorAscending());

        bottomCrimeCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencypair, String>("type"));
        bottomCrimeFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencypair, String>("frequency"));
        for (int i = 0; i < displayLimit; i ++) {
            bottomCrimeTable.getItems().add(crimeFrequencyPair.get(i));
            System.out.println(crimeFrequencyPair.get(i).getType());
        }


    }

    public void populateTopBlocksTable(ArrayList<TypeFrequencypair> blocksFrequencyPair) throws SQLException {
        Collections.sort(blocksFrequencyPair, new AnalysisTabController.frequecyComparatorDescending());
        topBlockCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencypair, String>("type"));
        topBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencypair, String>("frequency"));

        for (int i = 0; i < displayLimit; i ++) {
            topBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }




    }

    public void populateLowBlocksTable(ArrayList<TypeFrequencypair> blocksFrequencyPair) throws SQLException {
        Collections.sort(blocksFrequencyPair, new AnalysisTabController.frequecyComparatorAscending());
        bottomBlockCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencypair, String>("type"));
        bottomBlockFrequencyCol.setCellValueFactory(new PropertyValueFactory<TypeFrequencypair, String>("frequency"));
        for (int i = 0; i < displayLimit; i ++) {
            bottomBlockTable.getItems().add(blocksFrequencyPair.get(i));
        }


    }



}

