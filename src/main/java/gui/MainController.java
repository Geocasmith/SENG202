package gui;

import backend.csvReader;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import backend.Record;
import javafx.scene.layout.FlowPane;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class MainController {

    @FXML
    private TableView mainTableView;
    @FXML
    private FlowPane mainTableTogglePane;
    @FXML
    private FlowPane mainTableAddPane;
    @FXML
    private ToggleButton mainTableToggleAllButton;
    @FXML
    private Button mainTableAddRecordButton;
    @FXML
    private Label mainTableAddRecordLabel;
    @FXML
    private TextField mainTableAddCaseNumberField;
    @FXML
    private TextField mainTableAddDateField;
    @FXML
    private TextField mainTableAddBlockField;
    @FXML
    private TextField mainTableAddIUCRField;
    @FXML
    private TextField mainTableAddPrimaryDescField;
    @FXML
    private TextField mainTableAddSecondaryDescField;
    @FXML
    private TextField mainTableAddLocationDescField;
    @FXML
    private TextField mainTableAddArrestField;
    @FXML
    private TextField mainTableAddDomesticField;
    @FXML
    private TextField mainTableAddBeatField;
    @FXML
    private TextField mainTableAddWardField;
    @FXML
    private TextField mainTableAddFBICDField;
    @FXML
    private TextField mainTableAddXCoordField;
    @FXML
    private TextField mainTableAddYCoordField;
    @FXML
    private TextField mainTableAddLatitudeField;
    @FXML
    private TextField mainTableAddLongitudeField;
    @FXML
    private TitledPane filterPane;

    // Filter Sidebar Elements
    @FXML
    private Accordion sidebarAccordion;
    @FXML
    private ComboBox arrestComboBox;
    @FXML
    private ComboBox domesticComboBox;
    @FXML
    private ComboBox crimeTypeComboBox;
    @FXML
    private ComboBox locationDescriptionComboBox;
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
    private void initialize() throws IOException, CsvValidationException, SQLException {
        tableSetup();
        filterSetup();
    }

    /**
     * Adds a column to the main table with the mentioned display and property names, taken from the record class.
     * @param displayName The name to be used as the column header. Can be whatever you fancy.
     * @param propertyName The name of the attribute in the record class. This needs to match but doesn't seem
     *                     to be case-sensitive. This hooks up to a get[propertyName] method and uses the output
     *                     to fill in the column. Can work with at least strings and bools.
     */
    public void addTableCol(String displayName, String propertyName) {
        TableColumn<Record, String> col = new TableColumn<>(displayName);
        col.setCellValueFactory(new PropertyValueFactory<>(propertyName));
        mainTableView.getColumns().add(col);
        addTableColCheck(displayName, col);
//        addTableTextField(displayName);
    }

    /**
     * Creates a checkbox in the panel below the main table, and binds it to its column's visibility.
     * @param displayName The String to be used as a label for the checkbox
     * @param col The column object whose visibility will be bound by the checkbox
     */
    public void addTableColCheck(String displayName, TableColumn col) {
        CheckBox chk = new CheckBox(displayName);
        chk.selectedProperty().bindBidirectional(col.visibleProperty());
        mainTableTogglePane.getChildren().add(chk);
    }

    /**
     * Runs through any checkboxes in the checkbox area, and sets their selected attribute to that of the
     * toggle button.
     */
    public void toggleAllTableCols() {
        for (Node node : mainTableTogglePane.getChildren()){
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(mainTableToggleAllButton.isSelected());
            }
        }
    }

    /**
     * Adds a new textfield with the specified prompt text to the panel underneath the table view.
     * This will be used to add a new record.
     *
     * This might be better off done in the .fxml file so that we can refer to the prompts to give better feedback
     * if the user tried to insert an incorrectly formatted record. This would also help with 'wiring it up', as the
     * text field needs to be linked to or able to access the relevant column.
     *
     * @param prompt the String to be used as prompt text
     */
    public void addTableTextField(String prompt) {
        TextField field = new TextField();
        field.setPromptText(prompt);
        mainTableAddPane.getChildren().add(field);
    }

    /**
     * Runs through the text fields and "lines them up" with the attributes of a Record object to create one, which
     * can then be passed to the database.
     *
     * There is no error handling yet, so be careful!
     *
     * Returns a record object.
     */
    public void getRecordFromTextFields() {
        List<String> recStrings = new ArrayList<String>();

        for (Node node : mainTableAddPane.getChildren()){
            if (node instanceof TextField) {
                recStrings.add(((TextField) node).getText());
            }
        }
        try {
            Record rec = new Record(recStrings);
            System.out.println(rec.toString());
            mainTableAddRecordLabel.setText("That's a valid record. Well done!");
        }
        catch (Exception e) {
            mainTableAddRecordLabel.setText("That's not a valid record, but I can't tell you why!");
        }
    }

    /**
     * Sets up the main table.
     * Disables editing (provisional).
     * Creates all columns necessary for viewing crime data.
     * For now, creates a test record and adds it to the table (for testing).
     */
    public void tableSetup() throws CsvValidationException, IOException, SQLException {
        mainTableView.setEditable(false); // for now, until this can be linked up to the database

        // create all the columns
        addTableCol("Case Number", "caseNumber");
        addTableCol("Date", "date");
        addTableCol("Block", "block");
        addTableCol("IUCR", "iucr");
        addTableCol("Primary Description", "primaryDescription");
        addTableCol("Secondary Description", "secondaryDescription");
        addTableCol("Location Description", "locationDescription");
        addTableCol("Arrest", "arrest");
        addTableCol("Domestic", "domestic");
        addTableCol("Beat", "beat");
        addTableCol("Ward", "ward");
        addTableCol("FBICD", "fbicd");
        addTableCol("X-Coordinate", "xcoord");
        addTableCol("Y-Coordinate", "ycoord");
        addTableCol("Latitude", "latitude");
        addTableCol("Longitude", "longitude");

        // Test code
        Database d = new Database();
        d.connectDatabase();
        ArrayList<Record> allRecords = d.getAll();
        for (Record r : allRecords) {

            mainTableView.getItems().add(r);
        }
    }

    /**
     * Adds all record objects in an arraylist to the main viewing table.
     * @param records An ArrayList of record objects to be displayed in the table
     */
    public void addRecordsToTable(ArrayList<Record> records) {
        for (Record rec : records) {
            mainTableView.getItems().add(rec);
        }
    }

    /**
     * Adds a record object to the main viewing table.
     * @param rec A record object to be displayed in the table
     */
    public void addRecordsToTable(Record rec) {
        mainTableView.getItems().add(rec);
    }

    /**
     * Sets up combo boxes in filter pane
     * Sets filter pane as expanded
     */
    public void filterSetup() throws SQLException {
        // Sets filter pane to expanded pane
        sidebarAccordion.setExpandedPane(filterPane);

        // Set values for arrests and domestic combo boxes
        arrestComboBox.getItems().addAll("", "Yes", "No");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getItems().addAll("", "Yes", "No");
        domesticComboBox.getSelectionModel().select("");

        // Basic code to get a list of all crime types - MIGHT BE SLOW WITH LOTS OF RECORDS
        Database d = new Database();
        d.connectDatabase();
        ArrayList<Record> allRecords = d.getAll();
        ArrayList<String> crimeTypes = new ArrayList<>();
        ArrayList<String> locationDescriptions = new ArrayList<>();
        for (int i = 0; i < allRecords.size(); i++) {
            crimeTypes.add(allRecords.get(i).getPrimaryDescription());
            locationDescriptions.add(allRecords.get(i).getLocationDescription());
        }
        // Remove duplicate values
        crimeTypes = new ArrayList<>(new HashSet<>(crimeTypes));
        locationDescriptions = new ArrayList<>(new HashSet<>(locationDescriptions));

        // Sort lists alphabetically
        Collections.sort(crimeTypes);
        Collections.sort(locationDescriptions);

        // Set values for crime types combo box
        crimeTypeComboBox.getItems().add("Select Crime Type");
        crimeTypeComboBox.getSelectionModel().select("Select Crime Type");
        crimeTypeComboBox.getItems().addAll(crimeTypes);

        // Set values for location description combo box
        locationDescriptionComboBox.getItems().add("Location Description");
        locationDescriptionComboBox.getSelectionModel().select("Location Description");
        locationDescriptionComboBox.getItems().addAll(locationDescriptions);
    }

    /**
     * Updates label for radius when slider is updated
     */
    public void updateRadiusText() {
        String radius = String.valueOf(Math.round(radiusSlider.getValue()));
        radiusLabel.setText(radius + " km");
    }

    /**
     * Sets all filter parameters back to default
     */
    public void clearFilters() {
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        crimeTypeComboBox.getSelectionModel().select("Select Crime Type");
        locationDescriptionComboBox.getSelectionModel().select("Location Description");
        filterWardTextField.setText(null);
        filterBeatsTextField.setText(null);
        filterLatTextField.setText(null);
        filterLongTextField.setText(null);
        radiusSlider.setValue(1);
        radiusLabel.setText("1 km");
        arrestComboBox.getSelectionModel().select("");
        domesticComboBox.getSelectionModel().select("");

    }

}


