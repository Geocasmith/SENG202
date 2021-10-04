package gui;

import backend.*;
import backend.Record;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.ArrayList;
import java.util.Collections;

public class CrimeDetailsController {

    @FXML
    private TableView<TypeFrequencyPair> crimeTable;

    @FXML private TableColumn<TypeFrequencyPair, String> crimeCol;
    @FXML private TableColumn<TypeFrequencyPair, String> crimeFrequency;

    private DataAnalyser dataAnalyser = new DataAnalyser();

    public void updateBlockDetails(ArrayList<Record> currentRecord) {


        populateCrimesTable(dataAnalyser.getTypeFrequencyDescending(DataManipulator.extractCol(currentRecord, 4)));


    }


    public void populateCrimesTable(ArrayList<TypeFrequencyPair> crimeFrequencyPair) {
        // Sort descending
        Collections.sort(crimeFrequencyPair, new FrequencyComparatorDescending());
        //Table column set up
        crimeCol.setCellValueFactory(new PropertyValueFactory<>("type"));
        crimeFrequency.setCellValueFactory(new PropertyValueFactory<>("frequency"));
        //table set up
        crimeTable.getItems().clear();


        //Populate table
        for (int i = 0; i < crimeFrequencyPair.size(); i++) {
            crimeTable.getItems().add(crimeFrequencyPair.get(i));
        }



    }



}
