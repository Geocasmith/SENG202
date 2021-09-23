package gui;

import backend.*;
import backend.Record;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.beans.value.ChangeListener;
import javafx.collections.ObservableList;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.event.EventType;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.sql.Array;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.*;
import java.util.List;

public class MainController {

    @FXML
    private MapTabController mapTabController;

    @FXML
    private TableTabController tableTabController;

    @FXML
    private DataAnalyser dataAnalyser = new DataAnalyser(DataManipulator.getAllRecords());

    @FXML
    private GraphTabController graphTabController;

    // Filter Sidebar Elements
    @FXML
    private TitledPane filterPane;
    @FXML
    private TitledPane graphPane;
    @FXML
    private TabPane mainTabPane;
    @FXML
    private Tab graphTabPane;
    @FXML
    private Tab tableTabPane;
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
    @FXML
    private Label filterErrorLabel;


    // Graph Sidebar Elements
    @FXML
    private ComboBox graphTypeComboBox;
    @FXML
    private Button generateGraphButton;
    @FXML
    private CheckComboBox graphFilterComboBox;
    @FXML
    private Label graphOptionLabel;

    private int graphTabCount = 0;



    public MainController() throws SQLException {
    }


    @FXML
    private void initialize() throws SQLException, IOException, CsvValidationException, URISyntaxException {
        filterSetup();
        graphSetup();
    }

    @FXML
    private void refreshMarkers() {
        boolean connected;
        try {
            URL url = new URL("http://www.google.com");
            URLConnection connection = url.openConnection();
            connection.connect();
            connected = true;
        } catch (MalformedURLException e) {
            connected = false;
        } catch (IOException e) {
            connected = false;
        }
        if (!connected) {
            PopupWindow.displayPopup("Error", "You must be connected to the internet to use this feature");
            mainTabPane.getSelectionModel().select(tableTabPane);
        } else {
            ArrayList<Record> displayedRecords = tableTabController.getDisplayedRecords();
            if (displayedRecords.size() < 1001) {
                mapTabController.updateMarkers(displayedRecords);
            } else {
                mapTabController.updateMarkers(new ArrayList<Record>(displayedRecords.subList(0, 1000)));
            }

        }

    }


    /**
     * Sets up combo boxes in filter pane
     * Sets filter pane as expanded
     */
    public void filterSetup() throws SQLException, IOException, CsvValidationException, java.io.IOException {
        // Sets filter pane to expanded pane
        sidebarAccordion.setExpandedPane(filterPane);

        // Set values for arrests and domestic combo boxes
        arrestComboBox.getItems().addAll("", "Y", "N");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getItems().addAll("", "Y", "N");
        domesticComboBox.getSelectionModel().select("");

        Database d = new Database();
        ArrayList<String> locationDescriptions  = (ArrayList<String>)(ArrayList<?>)(d.extractCol("LOCATIONDESCRIPTION"));
        d.closeConnection();
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
    public void graphSetup() throws SQLException {
        graphTypeComboBox.getItems().addAll( "", "All Crimes", "Crimes Per Ward", "Crimes Per Beat", "Crimes Per Type");
        graphTypeComboBox.getSelectionModel().select("");
    }


    public void showGraphOptions() throws SQLException {
        dataAnalyser = new DataAnalyser(DataManipulator.getAllRecords());

        graphFilterComboBox.getCheckModel().clearChecks();

        if (graphTypeComboBox.getValue().equals("All Crimes")) {
            graphOptionLabel.setVisible(false);
            graphFilterComboBox.setVisible(false);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
        } else if (graphTypeComboBox.getValue().equals("Crimes Per Ward")) {
            graphOptionLabel.setText("Select which wards to graph");
            graphOptionLabel.setVisible(true);
            graphFilterComboBox.setVisible(true);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
            ArrayList<Integer> crimeWards = dataAnalyser.getCrimeWards();
            graphFilterComboBox.getItems().clear();
            graphFilterComboBox.getItems().addAll(crimeWards);

        } else if (graphTypeComboBox.getValue().equals("Crimes Per Beat")) {
            graphOptionLabel.setText("Select which beats to graph");
            graphOptionLabel.setVisible(true);
            graphFilterComboBox.setVisible(true);
            generateGraphButton.setVisible(true);
            generateGraphButton.setDisable(false);
            ArrayList<Integer> crimeBeats = dataAnalyser.getCrimeBeats();
            graphFilterComboBox.getItems().clear();
            graphFilterComboBox.getItems().addAll(crimeBeats);

        } else if (graphTypeComboBox.getValue().equals("Crimes Per Type")) {
            graphOptionLabel.setText("Select which crime types to graph");
            graphOptionLabel.setVisible(true);
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

    public void showGraphPane() {
        graphTabCount++;
        if (graphTabCount % 2 == 1) {
            sidebarAccordion.setExpandedPane(graphPane);
        } else {
            sidebarAccordion.setExpandedPane(filterPane);
        }

    }

    public void generateGraph() {
        ArrayList<Record> currentRecords = tableTabController.getDisplayedRecords();
        if (graphTypeComboBox.getValue().equals("All Crimes")) {
            graphTabController.createCrimesOverTimeGraph(currentRecords);

        } else {
            if (graphFilterComboBox.getCheckModel().getCheckedItems().size() > 5) {
                PopupWindow.displayPopup("Error", "You must select 5 or less options to graph");
            } else if (graphFilterComboBox.getCheckModel().getCheckedItems().size() < 1) {
                PopupWindow.displayPopup("Error", "You must select at least one option to graph");
            } else {
                if (graphTypeComboBox.getValue().equals("Crimes Per Ward")) {
                    ArrayList<Integer> checkedWards = new ArrayList<>();
                    ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
                    for (Integer index : checkedIndices)
                    {
                        checkedWards.add((int) graphFilterComboBox.getCheckModel().getItem(index));
                    }
                    graphTabController.createCrimesPerWardOverTimeGraph(currentRecords, checkedWards);

                } else if (graphTypeComboBox.getValue().equals("Crimes Per Beat")) {
                    ArrayList<Integer> checkedBeats = new ArrayList<>();
                    ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
                    for (Integer index : checkedIndices)
                    {
                        checkedBeats.add((int) graphFilterComboBox.getCheckModel().getItem(index));
                    }
                    graphTabController.createCrimesPerBeatOverTimeGraph(currentRecords, checkedBeats);

                } else if (graphTypeComboBox.getValue().equals("Crimes Per Type")) {
                    ArrayList<String> checkedTypes = new ArrayList<>();
                    ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
                    for (Integer index : checkedIndices)
                    {
                        checkedTypes.add((String) graphFilterComboBox.getCheckModel().getItem(index));
                    }
                    graphTabController.createCrimesPerTypeOverTimeGraph(currentRecords, checkedTypes);

                }
            }
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
        Boolean validFilter = true;

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

        if ((startDate != null) && (endDate != null)) {
            if (startDate.after(endDate)) {
                filterErrorLabel.setText("Filter end date must come before start date");
                filterErrorLabel.setVisible(true);
                validFilter = false;
            }
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
        String text = "";
        text = filterWardTextField.getText();
        if (!(text.equals(""))) {
            wards = text;

            if (!InputValidator.hasValidInt(wards)) {
                filterErrorLabel.setText("Ward must be a number");
                validFilter = false;
            }
        }

        // Beats
        text = filterBeatsTextField.getText();
        if (!(text.equals(""))) {
            beats = text;

            if (!InputValidator.hasValidInt(beats)) {
                filterErrorLabel.setText("Beat must be a number");
                validFilter = false;
            }
        }

        // Latitude
        text = filterLatTextField.getText();
        if (!(text.equals(""))) {
            lat = text;

            if (!InputValidator.hasValidDouble(lat)) {
                filterErrorLabel.setText("Latitude must be a number");
                validFilter = false;
            }
        }

        // Longitude
        text = filterLongTextField.getText();
        if (!(text.equals(""))) {
            lon = text;

            if (!InputValidator.hasValidDouble(lon)) {
                filterErrorLabel.setText("Longitude must be a number");
                validFilter = false;
            }
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

        if (validFilter) {
            filterErrorLabel.setVisible(false);
            Database d = new Database();
            ArrayList<Record> records = d.getFilter(startDate, endDate, crimeTypes, locationDescriptions, wards, beats, lat, lon, radius, arrest, domestic);
            d.closeConnection();
            // Set table to records
            tableTabController.setTableRecords(records);
            refreshMarkers();
        } else {
            filterErrorLabel.setVisible(true);
        }
        
    }




    /**
     * Sets all filter parameters back to default
     */
    public void clearFilters() throws SQLException {
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        crimeTypeComboBox.getCheckModel().clearChecks();
        locationDescriptionComboBox.getCheckModel().clearChecks();
        filterWardTextField.setText("");
        filterBeatsTextField.setText("");
        filterLatTextField.setText("");
        filterLongTextField.setText("");
        radiusSlider.setValue(0);
        radiusLabel.setText("0 km");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getSelectionModel().select("");
        Database d = new Database();
        tableTabController.setTableRecords(d.getAll());
        d.closeConnection();
        filterErrorLabel.setVisible(false);
        refreshMarkers();
    }

    /**
     * Opens file explorer for user to select a csv file to import
     * @return path to csv file
     */
    public String getPathToCsv() {
        String filepath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select csv file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                                                 new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            filepath = selectedFile.getAbsolutePath();
        }

        return filepath;
    }

    /**
     * Opens the file explorer for the user to select a save location and then passes
     * this to the CsvWriter along with the currently displayed records.
     * @throws IOException
     */
    public void exportCsv() throws IOException, NullPointerException{
        try{
            String filepath = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location to save csv file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("CSV Files", "*.csv"),
                                                 new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        filepath = selectedFile.getAbsolutePath();
        System.out.println(filepath);
        CsvWriter.write(filepath, tableTabController.getDisplayedRecords());
        }catch (Exception e){
            System.out.println("Path selected is null, error: "+e);
        }
    }

    public void importCsv(){
        String filepath = getPathToCsv();
        Boolean replace = null;
        if (filepath != null) {
            replace = PopupWindow.displayYesNoPopup("Replace Data?", "Do you want to replace the current data?");
        }
        if (replace != null) {
            try {
                Database d = new Database();
                d.connectDatabase();
                if (!replace) {
                    d.insertRows(CsvReader.read(filepath));
                } else {
                    d.replaceRows(CsvReader.read(filepath));
                }
                tableTabController.setTableRecords(d.getAll());
                d.closeConnection();
                filterSetup();

            } catch (Exception e) {
                System.out.println("Error " + e);
            }



        }

    }
    /**
     * Opens the file explorer for the user to select a save location and then passes
     * this to the database path method which will change the static variable path in database
     * which is accessed every time the database is connected to
     * @throws IOException
     */
    public void changeDatabase() throws IOException{
        String filepath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location to save csv file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Database Files", "*.db"),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(new Stage());
        filepath = selectedFile.getAbsolutePath();

        //Changes the database to the selected path
        Database d = new Database();
        d.setDatabasePath(filepath);

        //Refresh all GUI
    }


}


