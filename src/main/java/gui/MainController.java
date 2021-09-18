package gui;

import backend.*;
import backend.Record;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.apache.commons.lang3.ObjectUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckModel;
import org.controlsfx.control.IndexedCheckModel;

import java.io.File;
import java.io.IOException;
import java.sql.Array;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;

public class MainController {

    @FXML
    private TabTableController tabTableController;

    @FXML
    private DataAnalyser dataAnalyser = new DataAnalyser(DataManipulator.getAllRecords());

    @FXML
    private GraphController graphController;

    // Filter Sidebar Elements
    @FXML
    private TitledPane filterPane;
    @FXML
    private Accordion sidebarAccordion;
    @FXML
    private ComboBox arrestComboBox;
    @FXML
    private ComboBox domesticComboBox;
    @FXML
    private CheckComboBox crimeTypeComboBox;
    @FXML
    private CheckComboBox locationDescriptionComboBox;
    @FXML
    private Slider radiusSlider;
    @FXML
    private Label radiusLabel;
    @FXML
    private DatePicker filterStartDate;
    @FXML
    private DatePicker filterEndDate;
    @FXML
    private TextField filterWardTextField;
    @FXML
    private TextField filterBeatsTextField;
    @FXML
    private TextField filterLatTextField;
    @FXML
    private TextField filterLongTextField;

    // Graph Sidebar Elements
    @FXML
    private ComboBox graphTypeComboBox;
    @FXML
    private Button generateGraphButton;
    @FXML
    private CheckComboBox graphFilterComboBox;



    public MainController() throws SQLException {
    }


    @FXML
    private void initialize() throws SQLException, IOException, CsvValidationException {
        filterSetup();
        graphSetup();
    }


    /**
     * Sets up combo boxes in filter pane
     * Sets filter pane as expanded
     */
    public void filterSetup() throws SQLException, IOException, CsvValidationException, java.io.IOException {
        // Sets filter pane to expanded pane
        sidebarAccordion.setExpandedPane(filterPane);

        // Set values for arrests and domestic combo boxes
        arrestComboBox.getItems().addAll("", "Yes", "No");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getItems().addAll("", "Yes", "No");
        domesticComboBox.getSelectionModel().select("");


        ArrayList<String> locationDescriptions  = (ArrayList<String>)(ArrayList<?>)(Database.extractCol("LOCATIONDESCRIPTION"));
        // Remove duplicate values
        locationDescriptions = new ArrayList<>(new HashSet<>(locationDescriptions));

        // Get Crime types
        List<String> crimeTypes = new ArrayList<String>();
        crimeTypes = InputValidator.getSetOfPrimaryDescriptions();

        // Sort lists alphabetically
        Collections.sort(crimeTypes);
        Collections.sort(locationDescriptions);

        // Set values for crime types combo box
        crimeTypeComboBox.getItems().addAll(crimeTypes);

        // Set values for location description combo box
        locationDescriptionComboBox.getItems().addAll(locationDescriptions);

    }

    /**
     * Sets up combo boxes in the graph pane
     */
    public void graphSetup() {
        graphTypeComboBox.getItems().addAll( "", "All Crimes", "Crimes Per Ward", "Crimes Per Beat", "Crimes Per Type");
        graphTypeComboBox.getSelectionModel().select("");

//        graphFilterComboBox.getCheckModel().getCheckedItems().addListener(new ListChangeListener<String>() {
//            public void onChanged(ListChangeListener.Change<? extends String> c) {
//                ObservableList<Integer> indicesToCheck = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
//                for (Object o : indicesToCheck) {
//                    System.out.println(o);
//                }
//                if (graphFilterComboBox.getCheckModel().getCheckedItems().size() > 5) {
//
//
//                    graphFilterComboBox.getCheckModel().checkAll();
//                    graphFilterComboBox.getCheckModel().clearChecks();
//                    for (Object index : indicesToCheck.subList(0, 4)) {
//                        graphFilterComboBox.getCheckModel().check(index);
//                    }
//
//                }
//            }
//        });


    }


    public void showGraphOptions() {

        if (graphTypeComboBox.getValue().equals("All Crimes")) {
            graphFilterComboBox.setVisible(false);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
        } else if (graphTypeComboBox.getValue().equals("Crimes Per Ward")) {
            graphFilterComboBox.setVisible(true);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
            ArrayList<Integer> crimeWards = dataAnalyser.getCrimeWards();
            graphFilterComboBox.getItems().clear();
            graphFilterComboBox.getItems().addAll(crimeWards);

        } else if (graphTypeComboBox.getValue().equals("Crimes Per Beat")) {
            graphFilterComboBox.setVisible(true);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
            ArrayList<Integer> crimeBeats = dataAnalyser.getCrimeBeats();
            graphFilterComboBox.getItems().clear();
            graphFilterComboBox.getItems().addAll(crimeBeats);

        } else if (graphTypeComboBox.getValue().equals("Crimes Per Type")) {
            graphFilterComboBox.setVisible(true);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
            ArrayList<String> crimeTypes = dataAnalyser.getCrimeTypes();
            graphFilterComboBox.getItems().clear();
            graphFilterComboBox.getItems().addAll(crimeTypes);

        } else {
            graphFilterComboBox.setVisible(false);
            generateGraphButton.setVisible(false);
            generateGraphButton.setDisable(true);
        }
    }

    public void generateGraph() {
        ArrayList<Record> currentRecords = tabTableController.getDisplayedRecords();
        if (graphTypeComboBox.getValue().equals("All Crimes")) {
            graphController.createCrimesOverTimeGraph(currentRecords);

        } else if (graphTypeComboBox.getValue().equals("Crimes Per Ward")) {
            ArrayList<Integer> checkedWards = new ArrayList<>();
            ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
            for (Integer index : checkedIndices)
            {
                checkedWards.add((int) graphFilterComboBox.getCheckModel().getItem(index));
            }
            graphController.createCrimesPerWardOverTimeGraph(currentRecords, checkedWards);


        } else if (graphTypeComboBox.getValue().equals("Crimes Per Beat")) {
            ArrayList<Integer> checkedBeats = new ArrayList<>();
            ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
            for (Integer index : checkedIndices)
            {
                checkedBeats.add((int) graphFilterComboBox.getCheckModel().getItem(index));
            }
            graphController.createCrimesPerBeatOverTimeGraph(currentRecords, checkedBeats);

        } else if (graphTypeComboBox.getValue().equals("Crimes Per Type")) {
            ArrayList<String> checkedTypes = new ArrayList<>();
            ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
            for (Integer index : checkedIndices)
            {
                checkedTypes.add((String) graphFilterComboBox.getCheckModel().getItem(index));
            }
            graphController.createCrimesPerTypeOverTimeGraph(currentRecords, checkedTypes);

        }

    }

    /**
     * Updates label for radius when slider is updated
     */
    public void updateRadiusText() {
        String radius = String.valueOf(Math.round(radiusSlider.getValue()));
        radiusLabel.setText(radius + " km");
    }

    /**
     * Applies all currently selected filters
     */
    public void applyFilters() throws SQLException, IOException {
        // Initialize variables for filter
        Date startDate = null;
        Date endDate = null;
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String wards = null;
        String beats = null;
        String lat = null;
        String lon = null;
        int radius = 0;
        String arrest = null;
        String domestic = null;

        // Get local start data and convert to date
        Instant instant;
        LocalDate localDate;
        localDate = filterStartDate.getValue();
        if (localDate != null) {
        instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
        startDate = Date.from(instant);
        }

        // Do the same for end date
        localDate = filterEndDate.getValue();
        if (localDate != null) {
            instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            endDate = Date.from(instant);
        }

        // Get names of all checked items in crime type CheckComboBox
        IndexedCheckModel checkModel = crimeTypeComboBox.getCheckModel();
        ObservableList<Integer> checkedItems = checkModel.getCheckedIndices();
        for (Integer index : checkedItems)
        {
            crimeTypes.add(checkModel.getItem(index).toString());
        }

        // Do the same for location descriptions
        checkModel = locationDescriptionComboBox.getCheckModel();
        checkedItems = checkModel.getCheckedIndices();
        for (Integer index : checkedItems) {
            locationDescriptions.add(checkModel.getItem(index).toString());
        }

        // Get values for each text field as long as they aren't empty
        // Wards
        String text;
        text = filterWardTextField.getText();
        if (!(text == "")) {
            wards = text;
        }
        // Beats
        text = filterBeatsTextField.getText();
        if (!(text == "")) {
            beats = text;
        }
        // Latitude
        text = filterLatTextField.getText();
        if (!(text == "")) {
            lat = text;
        }
        // Longitude
        text = filterLongTextField.getText();
        if (!(text == "")) {
            lon = text;
        }


        // Get value for radius
        radius = (int) Math.round(radiusSlider.getValue());

        // Get values for domestic and arrest if one has been selected
        text = domesticComboBox.getSelectionModel().getSelectedItem().toString();
        if (text != "") {
            domestic = text;
        }
        text = arrestComboBox.getSelectionModel().getSelectedItem().toString();
        if (text != "") {
            arrest = text;
        }

        ArrayList<Record> records = Database.getFilter(startDate,endDate,crimeTypes, locationDescriptions, wards, beats, lat, lon, radius, arrest, domestic);
        // Set table to records
        tabTableController.setTableRecords(records);
    }




    /**
     * Sets all filter parameters back to default
     */
    public void clearFilters() {
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        crimeTypeComboBox.getCheckModel().clearChecks();
        locationDescriptionComboBox.getCheckModel().clearChecks();
        filterWardTextField.setText(null);
        filterBeatsTextField.setText(null);
        filterLatTextField.setText(null);
        filterLongTextField.setText(null);
        radiusSlider.setValue(0);
        radiusLabel.setText("0 km");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getSelectionModel().select("");

    }

    /**
     * Opens file explorer for user to select a csv file to import
     * @return
     */
    public String getPathToCsv() {
        String filepath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select csv file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                                                 new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        filepath = selectedFile.getAbsolutePath();

        return filepath;
    }

}


