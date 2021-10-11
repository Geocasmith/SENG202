package gui;

import data.*;
import com.opencsv.exceptions.CsvValidationException;
import data.Record;
import importExport.CsvWriter;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Cursor;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.IndexedCheckModel;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.List;


/**
 * Represents an object for main.fxml controller class
 * @author Bede Skinner-Vennell
 * @author Daniel Pallesen
 * @author George Carr-Smith
 * Date 09/10/2021
 */


public class MainController {

    @FXML // these controllers are assigned and used despite what IntelliJ says (see what happens if you delete them)
    private MapTabController mapTabController;
    @FXML
    private TableTabController tableTabController;
    @FXML
    private GraphTabController graphTabController;
    @FXML
    private AnalysisTabController analysisTabController;
    @FXML
    private DataAnalyser dataAnalyser;

    @FXML
    private Stage primaryStage;

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
    private int browserTabCount = 0;

    public MainController() {
    }

    /**
     * Gets and sets primary stage from the main gui
     * This is to be used in controlling cursor and potentially other properties of the main scene
     * @param primaryStage the stage to show the window on
     */

    public void setMyPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    /**
     * Runs the setup methods for the graph and filter panes, and sets the table tab's parent controller to the
     * maincontroller
     */
    @FXML
    private void initialize() {
        // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
        Platform.runLater(
                () -> {
                    try {
                        //Starts loading bar
                        JFrame loadingBar = null;
                        loadingBar = startProgressGIF();
                        filterSetup();
                        graphSetup();

                        // set sidebarAccordion to not affect geometry when hidden
                        sidebarAccordion.managedProperty().bind(sidebarAccordion.visibleProperty());

                        tableTabController.setParentController(this);
                        CrimeDatabase db = new CrimeDatabase();
                        List<Record> allRecords = null;
                        allRecords = db.getAll();
                        tableTabController.setTableRecords(allRecords);
                        dataAnalyser = new DataAnalyser(allRecords);

                        loadingBar.dispose();
                        updateAnalysis();
                        updateGraphOptions();
                        db.disconnectDatabase();
                    } catch (SQLException | CsvValidationException | IOException e) {
                        e.printStackTrace();
                    }


                }
        );

    }

    /**
     * Sets up combo boxes in filter pane
     * Sets filter pane as expanded
     */
    public void filterSetup() throws SQLException, CsvValidationException, java.io.IOException {
        // Sets filter pane to expanded pane
        sidebarAccordion.setExpandedPane(filterPane);

        // Set values for arrests and domestic combo boxes
        arrestComboBox.getItems().clear();
        domesticComboBox.getItems().clear();
        crimeTypeComboBox.getItems().clear();
        locationDescriptionComboBox.getItems().clear();
        arrestComboBox.getItems().addAll("", "Y", "N");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getItems().addAll("", "Y", "N");
        domesticComboBox.getSelectionModel().select("");

        CrimeDatabase d = new CrimeDatabase();
        d.connectDatabase();
        ArrayList<String> locationDescriptions = (ArrayList<String>) (ArrayList<?>) (d.extractCol("LOCATIONDESCRIPTION"));
        d.disconnectDatabase();
        // Remove duplicate values
        locationDescriptions = new ArrayList<>(new HashSet<>(locationDescriptions));

        // Disable slider
        radiusSlider.setDisable(true);

        // Get Crime types
        List<String> crimeTypes;
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
        graphTypeComboBox.getItems().addAll("", "All Crimes", "Crimes Per Ward", "Crimes Per Beat", "Crimes Per Type");
        graphTypeComboBox.getSelectionModel().select("");
    }

    /**
     * Checks that the user is connected to the internet, if not, then displays an error message and goes back to the
     * table tab, if they are, then it displays either the first 1000 records in the table, or all records in the table,
     * if there are less than 1000
     */
    @FXML
    private void refreshMarkers() {
        boolean connected = BrowserTabController.checkConnection();
        if (!connected) {
            PopupWindow.displayPopup("Error", "You must be connected to the internet to use the map.");
            mainTabPane.getSelectionModel().select(tableTabPane);
        } else {
            List<Record> displayedRecords = tableTabController.getDisplayedRecords();
            if (displayedRecords.size() < 500) {
                mapTabController.updateMarkers(displayedRecords);
            } else {

                mapTabController.updateMarkers(new ArrayList<>(displayedRecords.subList(0, 499)));
            }
        }
    }

    /**
     * Clears the checks in the graph filter combo box, then checks what graphing option has been selected and
     * displays the relevant objects, and loads the crime types/wards/beats into the graph filter combo box
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


            List<Integer> crimeWards = dataAnalyser.getCrimeWards();
            if (crimeWards.isEmpty()) {
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

            List<Integer> crimeBeats = dataAnalyser.getCrimeBeats();
            if (crimeBeats.isEmpty()) {
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
            List<String> crimeTypes = dataAnalyser.getCrimeTypes();
            if (crimeTypes.isEmpty()) {
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
        List<Record> currentRecords = tableTabController.getDisplayedRecords();
        if (currentRecords.isEmpty()) {
            PopupWindow.displayPopup("Error", "You must have data in the table to create a graph.\n" +
                    "Try clearing the filter or importing some data.");
        } else if (graphTypeComboBox.getValue().equals("All Crimes")) {
            graphTabController.createCrimesOverTimeGraph(currentRecords);

        } else {
            if (graphFilterComboBox.getCheckModel().getCheckedItems().size() > 5) {
                PopupWindow.displayPopup("Error", "You must select 5 or less options to graph");

            } else if (graphFilterComboBox.getCheckModel().getCheckedItems().isEmpty()) {
                PopupWindow.displayPopup("Error", "You must select at least one option to graph");

            } else {

                if (graphTypeComboBox.getValue().equals("Crimes Per Ward")) {
                    ArrayList<Integer> checkedWards = new ArrayList<>();
                    ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
                    for (Integer index : checkedIndices) {
                        checkedWards.add((int) graphFilterComboBox.getCheckModel().getItem(index));
                    }
                    graphTabController.createCrimesPerWardOverTimeGraph(currentRecords, checkedWards);

                } else if (graphTypeComboBox.getValue().equals("Crimes Per Beat")) {
                    ArrayList<Integer> checkedBeats = new ArrayList<>();
                    ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
                    for (Integer index : checkedIndices) {
                        checkedBeats.add((int) graphFilterComboBox.getCheckModel().getItem(index));
                    }
                    graphTabController.createCrimesPerBeatOverTimeGraph(currentRecords, checkedBeats);

                } else if (graphTypeComboBox.getValue().equals("Crimes Per Type")) {
                    ArrayList<String> checkedTypes = new ArrayList<>();
                    ObservableList<Integer> checkedIndices = (ObservableList<Integer>) graphFilterComboBox.getCheckModel().getCheckedIndices();
                    for (Integer index : checkedIndices) {
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
        String radius = String.valueOf(Math.round(radiusSlider.getValue()) * 100);
        radiusLabel.setText(radius + " m");
    }

    /**
     * Applies all currently selected filters
     */
    public void applyFilters() throws SQLException {
        // Initialize variables for filter
        String caseNumber = null;
        Date startDate = null;
        Date endDate = null;
        ArrayList<String> crimeTypes = new ArrayList<>();
        ArrayList<String> locationDescriptions = new ArrayList<>();
        String wards = null;
        String beats = null;
        String lat = null;
        String lon = null;
        int radius;
        String arrest = null;
        String domestic = null;
        boolean validFilter = true;
        String text;

        text = filterCaseNumberTextField.getText();
        if (!Objects.equals(text, "")) {

            caseNumber = text;

            //Does nothing if SQL injection detected
            if (containsInjection(caseNumber)) {
                return;
            }

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
            instant = Instant.from(localDate.plusDays(1).atStartOfDay(ZoneId.systemDefault()));
            endDate = Date.from(instant.minus(1, ChronoUnit.SECONDS));
        }

        if ((startDate != null) && (endDate != null) && startDate.after(endDate)) {
            filterErrorLabel.setText("Filter end date must come before start date");
            filterErrorLabel.setVisible(true);
            validFilter = false;

        }

        // Get names of all checked items in crime type CheckComboBox
        IndexedCheckModel checkModel = crimeTypeComboBox.getCheckModel();
        ObservableList<Integer> checkedItems = checkModel.getCheckedIndices();
        for (Integer index : checkedItems) {
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
        radius *= 100;

        // Get values for domestic and arrest if one has been selected
        text = domesticComboBox.getSelectionModel().getSelectedItem().toString();
        if (!Objects.equals(text, "")) {
            domestic = text;
        }
        text = arrestComboBox.getSelectionModel().getSelectedItem().toString();
        if (!Objects.equals(text, "")) {
            arrest = text;
        }

        if (validFilter) {
            // Fields below are copied since accessing them inside the new task constructor requires them to be constant
            String finalCaseNumber = caseNumber;
            Date finalStartDate = startDate;
            Date finalEndDate = endDate;
            String finalArrest = arrest;
            String finalDomestic = domestic;
            int finalRadius = radius;
            String finalLon = lon;
            String finalLat = lat;
            String finalBeats = beats;
            String finalWards = wards;

            // Sets the primary stage cursor busy
            primaryStage.getScene().getRoot().setCursor(Cursor.WAIT);

            // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
            Platform.runLater(
                    () -> {
                        //Starts loading bar
                        JFrame loadingBar = null;
                        loadingBar = startProgressGIF();

                        filterErrorLabel.setVisible(false);
                        CrimeDatabase d = new CrimeDatabase();
                        List<Record> records = null;
                        try {
                            records = d.getFilter(finalCaseNumber, finalStartDate, finalEndDate, crimeTypes, locationDescriptions,
                                    finalWards, finalBeats, finalLat, finalLon, finalRadius, finalArrest, finalDomestic);
                            d.disconnectDatabase();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }

                        // Set table to records
                        tableTabController.setTableRecords(records);

                        dataAnalyser.updateRecords(records);
                        graphTypeComboBox.getSelectionModel().select(0);

                        //Remove loading bar
                        loadingBar.dispose();

                        //Update views
                        updateAnalysis();
                        refreshMarkers();
                        try {
                            updateGraphOptions();
                        } catch (SQLException e) {
                            e.printStackTrace();
                        }
                    }
            );
            // Return the primary stage cursor to normal
            primaryStage.getScene().getRoot().setCursor(Cursor.DEFAULT);

        } else {
            filterErrorLabel.setVisible(true);
        }

    }

    /**
     * Updates latitude and longitude field in filter sidebar to lat and long of record object
     *
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
    public void checkSliderUnlock() {
        String lat = filterLatTextField.getText();
        String lon = filterLongTextField.getText();

        // Checks that both lat and long field are valid
        radiusSlider.setDisable(lat.equals("") || lon.equals("") || !InputValidator.hasValidDouble(lat) || !InputValidator.hasValidDouble(lon));
    }

    /**
     * Sets all filter parameters back to default, and then retrieves all data again, updating the analysis,
     * map markers, graph options, and the data loaded into the data analyser.
     */
    public void clearFilters() throws SQLException, InterruptedException {
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
        radiusLabel.setText("0 m");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getSelectionModel().select("");

        // Sets primary stage cursor busy



        // Avoid throwing IllegalStateException by running from a non-JavaFX thread.
        Platform.runLater(
                () -> {
                    //Starts loading bar
                    JFrame loadingBar = null;
                    try {
                        loadingBar = startProgressGIF();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    CrimeDatabase db = new CrimeDatabase();
                    List<Record> records = null;
                    try {
                        records = db.getAll();
                        db.disconnectDatabase();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    tableTabController.setTableRecords(records);
                    dataAnalyser.updateRecords(records);
                    filterErrorLabel.setVisible(false);

                    //Remove loading bar
                    loadingBar.dispose();

                    updateAnalysis();
                    refreshMarkers();
                    try {
                        updateGraphOptions();
                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    /**
     * Opens file explorer for user to select a file
     *
     * @param fileType      Type of file
     * @param fileExtension Extension of file
     * @return path to csv file
     */
    public String getPathToFile(String fileType, String fileExtension) {
        String filepath = null;
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select " + fileType + " file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileType + " Files", "*." + fileExtension),
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
     *
     * @param fileType      The type of file to be saved (CSV or Database)
     * @param fileExtension The extension of the file to be saved (.csv or .db)
     * @return The file path to the location the user selects
     */
    public String getFileSavePath(String fileType, String fileExtension) {
        String filepath = null;

        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select location to save " + fileType + " file");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter(fileType + " Files", "*." + fileExtension),
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
     */
    public void exportCsv() throws IOException, NullPointerException {
        try {
            String filepath = addExtension(getFileSavePath("CSV", "csv"), ".csv");
            CsvWriter.write(filepath, tableTabController.getDisplayedRecords());
        } catch (NullPointerException e) {
            // the user closed the file chooser
        } catch (Exception e) {
            // something unknown happened
            PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
        }
    }

    /**
     * Opens a file explorer for the user to select csv file to import then loads it
     */
    public void importCsv() throws SQLException, IOException {

        String filepath = getPathToFile("CSV", "csv");

        if (filepath != null) {
            //If user imports incorrect filetype it will do nothing and display a pop-up
            if (matchFileType(filepath, ".csv")) {
                PopupWindow.displayPopup("Error", "An unknown error occurred when exporting CSV.\n" +
                        "Please try again");
                return;
            }

            Boolean newDB;
            Boolean replace = null;
            Boolean newDBSuccess = true;
            Boolean csvSuccess = false;
            ArrayList<ArrayList<List<String>>> dataValidation = new ArrayList<>();

            //Pop up windows prompting the user to select a new or existing database and to replace or append the data
            try {
                List<Object> csvRows = DataManipulator.getRowsfromCsv(filepath);
                dataValidation = (ArrayList<ArrayList<List<String>>>) csvRows.get(0);
                csvSuccess = (Boolean) csvRows.get(1);


            } catch (Exception e) {
                PopupWindow.displayPopup("Error", "An unknown error occurred when importing CSV.\n" +
                        "Please try again");
            }

            if (!csvSuccess) {
                PopupWindow.displayPopup("Error", "The selected CSV is in the wrong format.\n" +
                        "Please try again");
                return;
            }

            newDB = PopupWindow.displayTwoButtonPopup("Create New Database?", "Do you want to store this data in a new database?", "New Database", "Existing Database", true);
            if (newDB != null && !newDB) {
                replace = PopupWindow.displayTwoButtonPopup("Replace data?", "Do you want to replace the current data or append to it?", "Replace", "Append", true);
            }

            //Starts loading bar
            JFrame loadingBar = startProgressGIF();

            if (newDB != null && newDB) {
                newDBSuccess = newDatabase();
                replace = false;
            }

            if (replace != null && newDBSuccess) {
                try {
                    CrimeDatabase d = new CrimeDatabase();
                    d.connectDatabase();

                    if (!replace) {
                        d.insertRows(dataValidation.get(0));

                    } else {
                        d.replaceRows(dataValidation.get(0));
                    }
                    d.disconnectDatabase();


                    //Updates the GUI table from the database
                    CrimeDatabase db = new CrimeDatabase();
                    List<Record> records = db.getAll();
                    db.disconnectDatabase();
                    tableTabController.setTableRecords(records);
                    updateAnalysis();
                    dataAnalyser.updateRecords(records);

                    updateGraphOptions();
                    filterSetup();
                    loadingBar.dispose();
                    if (!dataValidation.get(1).isEmpty()) {
                        displayInvalid(dataValidation.get(1));
                    }


                } catch (Exception e) {
                    PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
                }
            }

        }
    }

    /**
     * Creates a pop-up window which displays the number of invalid rows
     * @param invalid List of Lists of Strings representing the rows of the imported data which were invalid
     */
    public void displayInvalid(List<List<String>> invalid) {
        String invalidRows = invalid.size() + " rows could not be imported because their format is invalid";
        PopupWindow.displayPopup("Invalid Rows", invalidRows);
    }

    /**
     * Creates a Swing loading bar
     * @throws InterruptedException
     */
    public JFrame startProgressGIF() {
        //Initialises frame, panel and bar
        JFrame frame = new JFrame();
        JPanel panel = new JPanel();
        JPanel outerPanel = new JPanel();
        panel.setBorder(new EmptyBorder(50, 50, 50, 50));
        panel.setLayout(new FlowLayout());
        outerPanel.setBorder(BorderFactory.createLineBorder(Color.black));


        //Sets up GIF & Loading Text
        JLabel text = new JLabel("        Loading...");
        Icon icon = new ImageIcon("./Files/loading.gif");
        JLabel gif = new JLabel(icon);

        //Adds components to each other
        panel.add(text);
        panel.add(gif);
        outerPanel.add(panel);
        frame.add(outerPanel);

        //Removes exit buttons from frame
        frame.setUndecorated(true);
        frame.getRootPane().setWindowDecorationStyle(JRootPane.NONE);

        BoxLayout boxLayout = new BoxLayout(panel, BoxLayout.PAGE_AXIS);
        panel.setLayout(boxLayout);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
        return frame;
    }

    /**
     * Opens the file explorer for the user to select a save location and then passes
     * this to the database path method which will change the static variable path in database
     * which is accessed every time the database is connected to
     */
    public void changeDatabase() throws SQLException {

        String filepath;


        filepath = getPathToFile("Database", "db");
        if (filepath != null) {
            //Changes the database to the selected path
            CrimeDatabase d = new CrimeDatabase();

            //If user imports incorrect filetype it will do nothing and display a pop-up
            if (matchFileType(filepath, ".db")) {
                PopupWindow.displayPopup("Error", "The selected file is not a database file.");
                d.disconnectDatabase();
                return;
            }

            String previousPath = d.getDatabasePath();
            d.setDatabasePath(filepath);
            if (!d.checkValidDB()) {
                PopupWindow.displayPopup("Error", "Database format invalid. Please choose a valid database.");

                //reverts back to previous path
                d.setDatabasePath(previousPath);
            }
            d.disconnectDatabase();

            //Starts loading bar
            JFrame f = startProgressGIF();

            //Refresh GUI
            applyFilters();

            //stops loading bar
            f.dispose();
        }

    }

    /**
     * Prompts the user to select a location to save the new database file, then creates a database file there
     *
     * @return Boolean true/false if the database was created successfully
     */
    public Boolean newDatabase() throws NullPointerException, SQLException, IOException {
        String filepath = getFileSavePath("Database", "db");

        if ((filepath != null)) {
            try {
                filepath = addExtension(filepath, ".db");
                File file = new File(filepath);

                if (file.createNewFile()) {
                    // Set new database path
                    CrimeDatabase d = new CrimeDatabase();
                    d.setDatabasePath(filepath);
                    d.disconnectDatabase();

                    //Refresh GUI
                    CrimeDatabase db = new CrimeDatabase();
                    tableTabController.setTableRecords(db.getAll());
                    db.disconnectDatabase();

                    return true;
                }
            } catch (FileAlreadyExistsException e) {
                PopupWindow.displayPopup("Error", "File already exists.");
            } catch (Exception e) {
                // something else went wrong
                PopupWindow.displayPopup("Error", "An error occurred.\nException details follow:\n" +
                        e.getMessage());
            }
        }
        return false;
    }

    /**
     * Checks and returns if characters used in SQLInjection are used.
     *
     * @param injection input from user
     * @return if injection characters are used.
     */
    public Boolean containsInjection(String injection) {
        if (injection.matches(".*[%'\"\\-=<>;()].*")) {
            PopupWindow.displayPopup("Input Error", "Invalid characters in input (SQL Injection Protection).");

            return true;
        }
        return false;
    }

    /**
     * Helper method which checks the filepath to check if it has the correct extension and adds it if it does not
     * This stops the user from exporting to a file without an extension if they don't
     * type the extension in the file name.
     *
     * @param path      file path
     * @param extension correct extension
     * @return filepath or filepath with appended extension
     */
    public String addExtension(String path, String extension) {
        String substr = path.substring(path.length() - extension.length());

        //If the correct extension exists return path otherwise append the extension and return
        if (substr.equals(extension)) {
            return path;
        } else {
            return path + extension;
        }
    }

    /**
     * Checks if the inputted filepath's extension matches the required extension, this stops the user from
     * importing data from incorrect file types
     *
     * @param path      file path
     * @param extension correct extension
     * @return true if file type incorrect
     */
    public Boolean matchFileType(String path, String extension) {
        String substr = path.substring(path.length() - extension.length());
        return !substr.equals(extension);
    }

    /**
     * Passes the analysis update request to the analysis tab controller
     */
    private void updateAnalysis() { analysisTabController.updateAnalysis(tableTabController.getDisplayedRecords()); }

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

    /**
     * Keeps track of the number of times the analysis tab has been clicked on/off of, and updates the tables if the tab
     * has just been clicked on to
     */
    public void browserTabClick() {
        browserTabCount++;
        if (browserTabCount % 2 == 1) {

            boolean connected = BrowserTabController.checkConnection();
            if (!connected) {
                PopupWindow.displayPopup("Error", "You must be connected to the internet to use the browser.");
                mainTabPane.getSelectionModel().select(tableTabPane);
                browserTabCount++; // To make sure the tab count is the correct number
            } else {
                sidebarAccordion.setVisible(false);
            }
        }
        if (browserTabCount % 2 == 0) {
            sidebarAccordion.setVisible(true);
        }
    }
}


