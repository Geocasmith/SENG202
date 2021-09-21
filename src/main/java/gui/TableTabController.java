package gui;

import backend.DataManipulator;
import backend.InputValidator;
import backend.Record;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TableTabController {
    @FXML private BorderPane tabTable;
    @FXML private TableView mainTableView;
    @FXML private FlowPane mainTableTogglePane;
    @FXML private FlowPane mainTableAddPane;
    @FXML private ToggleButton mainTableToggleAllButton;
    @FXML private Button mainTableAddRecordButton;
    @FXML private Label mainTableAddRecordLabel;
    @FXML private TextField mainTableAddCaseNumberField;
    @FXML private TextField mainTableAddDateField;
    @FXML private TextField mainTableAddBlockField;
    @FXML private TextField mainTableAddIUCRField;
    @FXML private TextField mainTableAddPrimaryDescField;
    @FXML private TextField mainTableAddSecondaryDescField;
    @FXML private TextField mainTableAddLocationDescField;
    @FXML private TextField mainTableAddArrestField;
    @FXML private TextField mainTableAddDomesticField;
    @FXML private TextField mainTableAddBeatField;
    @FXML private TextField mainTableAddWardField;
    @FXML private TextField mainTableAddFBICDField;
    @FXML private TextField mainTableAddXCoordField;
    @FXML private TextField mainTableAddYCoordField;
    @FXML private TextField mainTableAddLatitudeField;
    @FXML private TextField mainTableAddLongitudeField;
    @FXML private TitledPane mainTableAddAccordionTab;


    @FXML
    private void initialize() throws CsvValidationException, SQLException, IOException {
        tableSetup();
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

    public void checkRequiredFields() {
        for (Node vbox : mainTableAddPane.getChildren()){
            if (vbox instanceof VBox) { // only look at vboxes, which contain a textfield + label
                TextField field = (TextField) ((VBox) vbox).getChildren().get(0);
                Label label = (Label) ((VBox) vbox).getChildren().get(1);

                // label text is faster than looking at the list of field's classes
                if (Objects.equals(label.getText(), "* Required") && field.getText().isEmpty()) {
                    if (field.getStyleClass().contains("defaulttextfield")) {
                        field.getStyleClass().remove("defaulttextfield");
                    }
                    field.getStyleClass().add("required");
                } // the style changes are this clumsy because of a bug with javafx updating styles - not my problem!
                else if (field.getStyleClass().contains("required")) {
                    field.getStyleClass().remove("required");
                    field.getStyleClass().add("defaulttextfield");
                }
            }
        }
    }

    /**
     * Runs through the text fields and "lines them up" with the attributes of a Record object to create one, which
     * can then be passed to the database.
     *
     * Gives the user feedback on their provided record. Messages for success, missing fields, invalid record.
     * Validation is currently checking if required fields are filled in and whether an exception is encountered
     * trying to create the record.
     *
     * @return A record object if the input is complete and valid, else null.
     */
    public Record getRecordFromTextFields() throws IOException, CsvValidationException {
        List<String> recStrings = new ArrayList<String>();
        int emptyFields = 0;

        for (Node vbox : mainTableAddPane.getChildren()){
            if (vbox instanceof VBox) { // only look at vboxes, which contain a textfield + label
                TextField field = (TextField) ((VBox) vbox).getChildren().get(0);
                Label label = (Label) ((VBox) vbox).getChildren().get(1);

                // label text is faster than looking at the list of field's classes
                if (Objects.equals(label.getText(), "* Required") && field.getText().isEmpty()) {
                    emptyFields++;
                }
                recStrings.add(field.getText());
            }
        }

        checkRequiredFields();

        //if (emptyFields > 0) {
           // mainTableAddRecordLabel.setText(String.format("%d required field(s) are empty.", emptyFields));
      //  }
        //else { // create the record
            if (InputValidator.isValidRecord(recStrings)) {
                Record rec = new Record(recStrings);
                System.out.println(rec);
                mainTableAddRecordLabel.setText((InputValidator.recordEntryFeedback(recStrings).get(1)));
                return rec;
            } else {
                mainTableAddRecordLabel.setText((InputValidator.recordEntryFeedback(recStrings).get(1)));

            }
        //}
        // returns null if the record can't be created (missing/invalid fields)
        return null;
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

        ArrayList<Record> allRecords = DataManipulator.getAllRecords();
        for (Record r : allRecords) {

            mainTableView.getItems().add(r);
        }
    }

    /**
     * Clears the text from the feedback label in the "add record" accordion tab.
     */
    public void clearAddFeedbackLabel() {
        mainTableAddRecordLabel.setText("");
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
     * Sets the record objects displayed in table to those contained in an arraylist.
     * @param records An ArrayList of record objects to be displayed in the table
     */
    public void setTableRecords(ArrayList<Record> records) {
        mainTableView.getItems().clear();
        addRecordsToTable(records);
    }

    /**
     * Returns an arraylist of all of the record objects in the table.
     * @return an ArrayList of all record objects in the table.
     */
    public ArrayList<Record> getDisplayedRecords() {
        ArrayList<Record> currentRecords = new ArrayList<>();
        for (Object o : mainTableView.getItems()) {
            currentRecords.add((Record) o);
        }
        return currentRecords;
    }
}
