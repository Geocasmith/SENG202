package gui;

import backend.*;
import backend.Record;
import backend.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
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
import java.nio.file.FileAlreadyExistsException;
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
    private DataAnalyser dataAnalyser;

    @FXML
    private GraphTabController graphTabController;

    @FXML
    private AnalysisTabController analysisTabController;

    // Filter Sidebar Elements
    @FXML
    private TitledPane filterPane;
    @FXML
    private TitledPane graphPane;
    @FXML
    private TextField filterCaseNumberTextField;
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
    private int analysisTabCount = 0;



    public MainController() throws SQLException {
    }


    /**
     * Runs the setup methods for the graph and filter panes, and sets the table tab's parent controller to the
     * maincontroller
     * @throws SQLException
     * @throws IOException
     * @throws CsvValidationException
     * @throws URISyntaxException
     */
    @FXML
    private void initialize() throws SQLException, IOException, CsvValidationException, URISyntaxException {
        filterSetup();
        graphSetup();

        tableTabController.setParentController(this);
        dataAnalyser = new DataAnalyser(tableTabController.getDisplayedRecords());
//        Database d = new Database();
//        tableTabController.setTableRecords(d.getAll());
//        d.closeConnection();
        analysisSetUp();
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

        // Disable slider
        radiusSlider.setDisable(true);

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
    }

    /**
     * Updates the tables in the analysis tab
     */
    private void analysisSetUp() {
        updateAnalysis();
    }

    /**
     * Checks that the user is connected to the internet, if not, then displays a error message and goes back to the
     * table tab, if they are, then it displays either the first 1000 records in the table, or all records in the table,
     * if there are less than 1000
     */
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
            if (displayedRecords.size() < 1000) {
                mapTabController.updateMarkers(displayedRecords);
            } else {
                mapTabController.updateMarkers(new ArrayList<Record>(displayedRecords.subList(0, 999)));
            }

        }

    }


    /**
     * Clears the checks in the graph filter combo box, then checks what graphing option has been selected and
     * displays the relevant objects, and loads the crime types/wards/beats into the graph filter combo box
     * @throws SQLException
     */
    public void updateGraphOptions() throws SQLException {
        graphFilterComboBox.getCheckModel().clearChecks();

        if (graphTypeComboBox.getValue().equals("All Crimes")) {
            if (tableTabController.getDisplayedRecords().size() == 0) {
                PopupWindow.displayPopup("Error", "You must have data in the table to create a graph.\n" +
                        "Try clearing the filter or importing some data.");
                graphTypeComboBox.getSelectionModel().select(0);
            } else {
                graphOptionLabel.setVisible(false);
                graphFilterComboBox.setVisible(false);
                generateGraphButton.setDisable(false);
            }


        } else if (graphTypeComboBox.getValue().equals("Crimes Per Ward")) {


            ArrayList<Integer> crimeWards = dataAnalyser.getCrimeWards();
            if (crimeWards.size() == 0) {
                PopupWindow.displayPopup("Error", "You must have data in the table to create a graph.\n" +
                        "Try clearing the filter or importing some data.");
                graphTypeComboBox.getSelectionModel().select(0);
            } else {
                graphOptionLabel.setText("Select which wards to graph");
                graphOptionLabel.setVisible(true);
                graphFilterComboBox.setVisible(true);
                generateGraphButton.setDisable(false);
                graphFilterComboBox.getItems().clear();
                graphFilterComboBox.getItems().addAll(crimeWards);
            }


        } else if (graphTypeComboBox.getValue().equals("Crimes Per Beat")) {


            ArrayList<Integer> crimeBeats = dataAnalyser.getCrimeBeats();
            if (crimeBeats.size() == 0) {
                PopupWindow.displayPopup("Error", "You must have data in the table to create a graph.\n" +
                        "Try clearing the filter or importing some data.");
                graphTypeComboBox.getSelectionModel().select(0);
            } else {
                graphOptionLabel.setText("Select which beats to graph");
                graphOptionLabel.setVisible(true);
                graphFilterComboBox.setVisible(true);
                generateGraphButton.setDisable(false);
                graphFilterComboBox.getItems().clear();
                graphFilterComboBox.getItems().addAll(crimeBeats);
            }


        } else if (graphTypeComboBox.getValue().equals("Crimes Per Type")) {


            ArrayList<String> crimeTypes = dataAnalyser.getCrimeTypes();
            if (crimeTypes.size() == 0) {
                PopupWindow.displayPopup("Error", "You must have data in the table to create a graph.\n" +
                        "Try clearing the filter or importing some data.");
                graphTypeComboBox.getSelectionModel().select(0);
            } else {
                graphOptionLabel.setText("Select which crime types to graph");
                graphOptionLabel.setVisible(true);
                graphFilterComboBox.setVisible(true);
                generateGraphButton.setDisable(false);
                graphFilterComboBox.getItems().clear();
                graphFilterComboBox.getItems().addAll(crimeTypes);
            }


        } else {
            graphOptionLabel.setVisible(false);
            graphFilterComboBox.setVisible(false);
            generateGraphButton.setDisable(true);
        }
    }

    /**
     * Displays the graph pane on the sidebar if the user has clicked on the graph tab, otherwise it displays the filter
     * pane
     */
    public void showGraphPane() {
        graphTabCount++;

        /* Checks if the count is an odd number because clicking off of the graph tab still registers the graph tab
           as selected and this method gets around that bug */
        if (graphTabCount % 2 == 1) {
            sidebarAccordion.setExpandedPane(graphPane);
        } else {
            sidebarAccordion.setExpandedPane(filterPane);
        }

    }

    /**
     * Checks that there is enough data in the table to create a graph, then checks if the user has selected the "All
     * Crimes" option. If so, it generates the graph. Then checks that the user has selected a valid number of crime
     * type/ward/beat options. If so, it creates a list of the checked options and then creates the requested graph
     */
    public void generateGraph() {
        ArrayList<Record> currentRecords = tableTabController.getDisplayedRecords();
        if (currentRecords.size() == 0) {
            PopupWindow.displayPopup("Error", "You must have data in the table to create a graph.\n" +
                                                           "Try clearing the filter or importing some data.");
        } else if (graphTypeComboBox.getValue().equals("All Crimes")) {
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
    public void applyFilters() throws SQLException {
        // Initialize variables for filter
        String caseNumber = null;
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
        String text = "";

        text = filterCaseNumberTextField.getText();
        if (text != "") {
            caseNumber = text;
        }

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
        text = filterWardTextField.getText();
        if (!(text.equals(""))) {
            wards = text;

            if (!InputValidator.hasValidInt(wards, false)) {
                filterErrorLabel.setText("Ward must be a number");
                validFilter = false;
            }
        }

        // Beats
        text = filterBeatsTextField.getText();
        if (!(text.equals(""))) {
            beats = text;

            if (!InputValidator.hasValidInt(beats, false)) {
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
            ArrayList<Record> records = d.getFilter(caseNumber, startDate, endDate, crimeTypes, locationDescriptions, wards, beats, lat, lon, radius, arrest, domestic);
            d.closeConnection();
            // Set table to records
            tableTabController.setTableRecords(records);
            refreshMarkers();
            dataAnalyser.updateRecords(records);
            updateGraphOptions();
            updateAnalysis();

        } else {
            filterErrorLabel.setVisible(true);
        }
        
    }

    /**
     *  Updates latitude and longitude field in filter sidebar to lat and long of record object
     * @param record Record selected
     */
    public void updateLatLong(Record record) {
        filterLatTextField.setText(record.getLatitude().toString());
        filterLongTextField.setText(record.getLongitude().toString());
        checkSliderUnlock();
    }

    /**
     * Enables slider if lat and long are valid in the filter
     */
    public void checkSliderUnlock () {
        String lat;
        String lon;
        lat = filterLatTextField.getText();
        lon = filterLongTextField.getText();
        // Checks that both lat and long field are valid
        if ( !(lat.equals("") && lon.equals("")) && !(lat.equals(null) && lon.equals(null)) && (InputValidator.hasValidDouble(lat) && InputValidator.hasValidDouble(lon))) {
            radiusSlider.setDisable(false);
        } else {
            radiusSlider.setDisable(true);
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
        filterCaseNumberTextField.setText("");
        filterWardTextField.setText("");
        filterBeatsTextField.setText("");
        filterLatTextField.setText("");
        filterLongTextField.setText("");
        radiusSlider.setValue(0);
        radiusSlider.setDisable(true);
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
     * Opens file explorer for user to select a file
     * @param fileType  Type of file
     * @param fileExtension Extension of file
     * @return path to csv file
     */
    public String getPathToFile(String fileType, String fileExtension) {
        String filepath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select " + fileType + " file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileType + " Files", "*."+fileExtension),
                                                 new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showOpenDialog(new Stage());
        if (selectedFile != null) {
            filepath = selectedFile.getAbsolutePath();
        }

        return filepath;
    }

    /**
     * Opens the file explorer for the user to select a location to save the selected file, then returns the selected
     * location.
     * @param fileType The type of file to be saved (CSV or Database)
     * @param fileExtension The extension of the file to be saved (.csv or .db)
     * @return The file path to the location the user selects
     */
    public String getFileSavePath(String fileType, String fileExtension) {
        String filepath = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location to save " + fileType + " file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileType + " Files", "*."+fileExtension),
                new FileChooser.ExtensionFilter("All Files", "*.*"));
        File selectedFile = fileChooser.showSaveDialog(new Stage());
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
            String filepath = addExtension(getFileSavePath("CSV", "csv"),".csv");
            CsvWriter.write(filepath, tableTabController.getDisplayedRecords());
        }catch (NullPointerException e){
            // the user closed the file chooser
        } catch (Exception e) {
            // something unknown happened
            System.out.println("Unknown error exporting csv, error: "+e);
        }
    }

    /**
     * Opens a file explorer for the user to select csv file to import then loads it
     */
    public void importCsv() throws SQLException, IOException {
        String filepath = getPathToFile("CSV", "csv");

        if (filepath != null) {
            //If user imports incorrect filetype it will do nothing and display a pop up
            if (matchFileType(filepath, ".csv")) {
                PopupWindow.displayPopup("Error", "Selected file is not a CSV file");
                return;
            }

            Boolean replace = null;
            Boolean newDB = null;
            Boolean newDBSuccess = false;


            newDB = PopupWindow.displayTwoButtonPopup("Create New Database?", "Do you want to store this data in a new database?", "New Database", "Existing Database");
            if (newDB != null && !newDB) {
                replace = PopupWindow.displayTwoButtonPopup("Replace data?", "Do you want to replace the current data or append to it?", "Replace", "Append");
            }
            if (newDB != null && newDB) {
                newDBSuccess = newDatabase();
                replace = false;
            }
            if (replace != null && newDBSuccess) {
                try {
                    Database d = new Database();
                    d.connectDatabase();

                    ArrayList<ArrayList<List<String>>> dataValidation = DataManipulator.getRowsfromCsv(filepath);

                    if (!replace) {
                        d.insertRows(dataValidation.get(0));

                    } else {
                        d.replaceRows(dataValidation.get(0));

                    }
                    if (dataValidation.get(1).size() != 0) {
                        displayInvalid(dataValidation.get(1));
                    }
                    ArrayList<Record> records = d.getAll();
                    tableTabController.setTableRecords(records);
                    d.closeConnection();
                    filterSetup();
                    dataAnalyser.updateRecords(records);
                    updateGraphOptions();

                } catch (Exception e) {
                    System.out.println("Error " + e);
                } // TODO this is printing an error when you import csv, click new database, then close the file picker
            } // TODO the specific error is "Error java.lang.ArrayIndexOutOfBoundsException: Index 1 out of bounds for length 1"
        }
    }

    /**
     * Creates a pop up window which displays the number of invalid rows
     * @param invalid
     */
    public void displayInvalid(ArrayList<List<String>> invalid){
        String invalidRows = invalid.size()+" rows could not be imported because their format is invalid";
        PopupWindow.displayPopup("Invalid Rows", invalidRows);

    }
    /**
     * Opens the file explorer for the user to select a save location and then passes
     * this to the database path method which will change the static variable path in database
     * which is accessed every time the database is connected to
     * @throws IOException
     */
    public void changeDatabase() throws IOException, SQLException{
        String filepath = null;
        filepath = getPathToFile("Database", "db");
        if (filepath != null) {
            //Changes the database to the selected path
            Database d = new Database();

            //If user imports incorrect filetype it will do nothing and display a pop up
            if(matchFileType(filepath,".db")){
                PopupWindow.displayPopup("Error", "Selected file is not a database file");
                d.closeConnection();
                return;
            }

            d.setDatabasePath(filepath);
            d.closeConnection();

            //Refresh GUI
            tableTabController.refreshTableData();
        }



    }

    /**
     * Prompts the user to select a location to save the new database file, then creates a database file there
     * @return Boolean true/false if the database was created successfully
     * @throws NullPointerException
     * @throws SQLException
     * @throws IOException
     */
    public Boolean newDatabase() throws NullPointerException, SQLException, IOException {
        String filepath = getFileSavePath("Database", "db");

        if (!(filepath == null)) {

            try{
                filepath = addExtension(filepath,".db");
                File file = new File(filepath);
                file.createNewFile();
                // Set new database path
                Database d = new Database();
                d.setDatabasePath(filepath);


                //Refresh GUI
                tableTabController.setTableRecords(d.getAll());
                d.closeConnection();
                return true;
            } catch(FileAlreadyExistsException e){
                PopupWindow.displayPopup("Error", "File already exists");
            } catch (Exception e) {
                // something else went wrong
                PopupWindow.displayPopup("Error", "Unknown error");
            }


        }
        return false;
    }

    /**
     * Helper method which checks the filepath to check if it has the correct extension and adds it if it does not
     * This stops the user from exporting to a file without an extension if they dont
     * type the extension in the file name.
     * @param path file path
     * @param extension correct extension
     * @return filepath or filepath with appended extension
     */
    public String addExtension(String path, String extension){
        String substr = path.substring(path.length() - extension.length());

        //If the correct extension exists return path otherwise append the extension and return
        if(substr.equals(extension)){
            return path;
        }else{
            return path+=extension;
        }
    }

    /**
     * Checks if the inputted filepath's extension matches the required extension, this stops the user from
     * importing data from incorrect file types
     * @param path file path
     * @param extension correct extension
     * @return true if file type incorrect
     */
    public Boolean matchFileType(String path, String extension){
        String substr = path.substring(path.length() - extension.length());

        return !substr.equals(extension);
    }

    /**
     * Forwards the request to analyse the crime locations to the table tab controller
     */
    public void analyseCrimeLocationDifference() {
        tableTabController.analyseCrimeLocationDifference();
    }

    /**
     * Forwards the request to analyse the crime times to the table tab controller
     */
    public void analyseCrimeTimeDifference() {
        tableTabController.analyseCrimeTimeDifference();
    }

    /**
     * Passes the analysis update request to the analysis tab controller
     */
    private void updateAnalysis() {
        analysisTabController.updateAnalysis(tableTabController.getDisplayedRecords());
    }

    /**
     * Keeps track of the number of times the analysis tab has been clicked on/off of, and updates the tables if the tab
     * has just been clicked on to
     */
    public void analysisTabClick() {
        analysisTabCount++;
        if (analysisTabCount % 2 == 1) {
            updateAnalysis();
        }
    }

}


