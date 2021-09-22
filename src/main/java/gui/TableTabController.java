package gui;

import backend.DataManipulator;
import backend.InputValidator;
import backend.Record;
import com.opencsv.exceptions.CsvValidationException;
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
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class TableTabController {
    @FXML private BorderPane tableTab;
    @FXML private TableView<Record> mainTableView;
    @FXML private FlowPane mainTableTogglePane;
    @FXML private FlowPane mainTableAddPane;
    @FXML private ToggleButton toggleAllColumnsButton;
    @FXML private Button addRecordButton;
    @FXML private Label addRecordLabel;
    @FXML private TextField addCaseNumberField;
    @FXML private TextField addDateField;
    @FXML private TextField addBlockField;
    @FXML private TextField addIUCRField;
    @FXML private TextField addPrimaryDescField;
    @FXML private TextField addSecondaryDescField;
    @FXML private TextField addLocationDescField;
    @FXML private TextField addArrestField;
    @FXML private TextField addDomesticField;
    @FXML private TextField addBeatField;
    @FXML private TextField addWardField;
    @FXML private TextField addFBICDField;
    @FXML private TextField addXCoordField;
    @FXML private TextField addYCoordField;
    @FXML private TextField addLatitudeField;
    @FXML private TextField addLongitudeField;
    @FXML private Button deleteRowsButton;
    @FXML private Button editRowButton;
    @FXML private Accordion tableAccordion;
    @FXML private TitledPane toggleColumnsAccordionTab;
    @FXML private TitledPane addAccordionTab;

    private List<TextField> textFieldsAdd = new ArrayList<TextField>();


    @FXML
    private void initialize() throws CsvValidationException, SQLException, IOException {
        tableSetup();
        textFieldsAdd = Arrays.asList(addCaseNumberField, addDateField, addBlockField, addIUCRField,
                addPrimaryDescField, addSecondaryDescField, addLocationDescField, addArrestField, addDomesticField,
                addBeatField, addWardField, addFBICDField, addXCoordField, addYCoordField, addLatitudeField,
                addLongitudeField);
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
                ((CheckBox) node).setSelected(toggleAllColumnsButton.isSelected());
            }
        }
    }

    /**
     * Marks all EMPTY (not invalid!) fields with a red outline.
     */
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
                addRecordLabel.setText((InputValidator.recordEntryFeedback(recStrings).get(1)));
                return rec;
            } else {
                addRecordLabel.setText((InputValidator.recordEntryFeedback(recStrings).get(1)));

            }
        //}
        // returns null if the record can't be created (missing/invalid fields)
        return null;
    }

    /**
     * Returns the number of rows the user has selected in the table view.
     * @return integer number of selected rows
     */
    private int getNumSelectedRows() {
        return getSelectedRows().size();
    }

    /**
     * Returns a READ ONLY (!!!) list of records the user has selected in the table view.
     * @return
     */
    private ObservableList<Record> getSelectedRows() {
        return mainTableView.getSelectionModel().getSelectedItems();
    }

    /**
     * Sets up the main table.
     * Disables direct table editing.
     * Allows for the selection of many rows at once.
     * Creates all columns necessary for viewing crime data.
     */
    public void tableSetup() throws CsvValidationException, IOException, SQLException {
        mainTableView.setEditable(false); // for now, until this can be linked up to the database
        mainTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

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
     * Deletes the selected rows. Don't know how this will work.
     */
    @FXML
    private void deleteSelectedRows() throws SQLException {
        for (Record rec : getSelectedRows()) {
            //Database.manualDelete(rec.getCaseNumber());
        }
    }

    /**
     * If only one row is selected, then call setupEditRow, else show an error somewhere.
     */
    @FXML
    private void editRow() throws IOException {
        if (getNumSelectedRows() == 1) {
            setupEditRow(getSelectedRows().get(0));
        }
        else {
            PopupWindow.displayPopup("Error", "You must have only one row selected to edit a record.");
        }
    }

    /**
     * Should fill in the values of the specific record in the add record pane
     * maybe disable the case number field?
     */
    private void setupEditRow(Record rec) throws IOException {
//        tableAccordion.setExpandedPane(addAccordionTab);
//        EditRecordWindow.displayEditRecordWindow(rec);

        Stage popupEdit = new Stage();
        popupEdit.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("editRecordWindow.fxml")
        );
        popupEdit.setTitle("Edit Record");
        popupEdit.setScene(new Scene(loader.load()));
        editRecordWindowController controller = loader.getController();
        controller.initData(rec);
        popupEdit.setResizable(false);
        popupEdit.showAndWait();
    }

    /**
     * Clears the text from the feedback label in the "add record" accordion tab.
     */
    public void clearAddFeedbackLabel() {
        addRecordLabel.setText("");
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
