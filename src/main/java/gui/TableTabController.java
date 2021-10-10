package gui;

import data.DataAnalyser;
import data.CrimeDatabase;
import data.Record;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * The controller for the table view tab. This controller handles setting up the table, filling it with record objects,
 * adding/editing/deleting records (with the EditRecordWindowController class), creating the right-click menu for the
 * table, and listening for delete/backspace keypresses while the table is in focus.
 * @author Jonathan Tomlinson
 * @author Bede Skinner-Vennell (Double clicking on row and context menu)
 * Date 09/10/2021
 */
public class TableTabController {
    @FXML
    private TableView<Record> mainTableView;
    @FXML
    private FlowPane mainTableTogglePane;
    @FXML
    private ToggleButton toggleAllColumnsButton;

    private MainController parentController; // allows access to the currently active mainController
    private final DataAnalyser dataAnalyser = new DataAnalyser();


    /**
     * Initialises the table. Calls setupTable() and setupContextMenu(), and creates a key listener for the delete
     * or backspace keys when the table is focussed that will delete the selected rows.
     */
    @FXML
    public void initialize() {
        setupTable();
        setupContextMenu();

        // set the message for when there is no data in the table
        mainTableView.setPlaceholder(new Label("There is no data to display in the table.\n" +
                "This could be because there are no records that match the currently applied filters, there is " +
                "no data loaded into the app, or all columns are toggled off.\n" +
                "To re-set the current filters, click 'Clear Filter' in the Filters tab on the left of the screen.\n" +
                "To load data into the app, click the 'File' button in the top left of the screen, and then 'Import CSV', or 'Select Database'.\n" +
                "To toggle the visibility of columns, click on the 'Toggle Columns' tab below the table, and enable the desired columns."));

        // sets up a delete action for when the delete or backspace keys are pressed while
        mainTableView.setOnKeyPressed(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.DELETE || keyEvent.getCode() == KeyCode.BACK_SPACE) { // do the same thing
                try {
                    deleteSelectedRows();
                } catch (SQLException e) {
                    PopupWindow.displayPopup("Error", e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Creates a context menu for the table view, contains an edit and delete option, along with an analysis submenu
     * with time and distance options.
     */
    private void setupContextMenu() {

        // Create menus
        ContextMenu tableContextMenu = new ContextMenu();

        // Create menu items
        MenuItem editMenuItem = new MenuItem("Edit");
        MenuItem deleteMenuItem = new MenuItem("Delete");
        MenuItem analyseMenuItem = new MenuItem("Compare Two Records");

        // Add click event handlers to the menu items
        editMenuItem.setOnAction(actionEvent -> {
            try {
                editRow();
            } catch (IOException e) {
                PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
            }
        });

        deleteMenuItem.setOnAction(actionEvent -> {
            try {
                deleteSelectedRows();
            } catch (SQLException e) {
                PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
            }
        });

        analyseMenuItem.setOnAction(actionEvent -> analyseCrimeDifference());


        // Add menu items to the relevant menus
        tableContextMenu.getItems().addAll(editMenuItem, deleteMenuItem, analyseMenuItem);

        mainTableView.setContextMenu(tableContextMenu);
    }

    /**
     * Adds a column to the main table with the mentioned display and property names, taken from the record class.
     *
     * @param displayName  The name to be used as the column header. Can be whatever you fancy.
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
     *
     * @param displayName The String to be used as a label for the checkbox
     * @param col         The column object whose visibility will be bound by the checkbox
     */
    public void addTableColCheck(String displayName, TableColumn col) {
        CheckBox chk = new CheckBox(displayName);
        chk.selectedProperty().bindBidirectional(col.visibleProperty()); // ties checkbox to column visibility
        mainTableTogglePane.getChildren().add(chk);
    }

    /**
     * Runs through any checkboxes in the checkbox area, and sets their selected attribute to that of the
     * toggle button. Relies on the binding established in addTableColCheck.
     */
    public void toggleAllTableCols() {
        for (Node node : mainTableTogglePane.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(toggleAllColumnsButton.isSelected());
            }
        }
    }

    /**
     * Returns the number of rows the user has selected in the table view.
     *
     * @return integer number of selected rows
     */
    private int getNumSelectedRows() {
        return getSelectedRows().size();
    }

    /**
     * Returns a list of the records that the user has selected in the table view.
     *
     * @return list of currently selected records
     */
    private List<Record> getSelectedRows() {
        return mainTableView.getSelectionModel().getSelectedItems();
    }

    /**
     * Sets up the main table.
     * Disables direct table editing.
     * Allows for the selection of many rows at once.
     * Creates all columns necessary for viewing crime data.
     */
    private void setupTable() {
        mainTableView.setEditable(false); // stops user editing things in the table (should use the edit button)
        mainTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE); // allows multiple selected records

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

        // Setup double click event to open the edit window when a row is double-clicked
        mainTableView.setRowFactory(tv -> {
            TableRow<Record> row = new TableRow<>();
            row.setOnMouseClicked(event -> {
                if (event.getClickCount() == 2 && (!row.isEmpty())) {
                    Record rowData = row.getItem();
                    try {
                        setupEditRow(rowData);
                    } catch (IOException e) {
                        PopupWindow.displayPopup("Error", "Unknown error. Please try again.");
                    }
                } else if (event.getClickCount() == 1 && (!row.isEmpty())) {
                    Record rowData = row.getItem();
                    parentController.updateLatLong(rowData); // copies lat & long of selected record to filter sidebar
                }
            });
            return row;
        });
    }

    /**
     * Deletes the selected rows from the database. Requires confirmation from the user.
     * No table refresh required, as rows are deleted from the database and table simultaneously.
     */
    @FXML
    private void deleteSelectedRows() throws SQLException {
        int num = getNumSelectedRows();
        if (num == 0) {
            PopupWindow.displayPopup("Error", "You must have data in the table to delete records.\n" +
                    "Try clearing the filter or importing some data.");

        } else if (PopupWindow.displayTwoButtonPopup("Confirm Action", "You are about to delete " +
                num + " rows. This action cannot be undone.\nAre you sure you want" +
                " to do this?", "Yes", "No")) {
            CrimeDatabase d = new CrimeDatabase();
            List<Record> selectedRows = getSelectedRows();
            for (Record rec : selectedRows) {
                d.manualDelete(rec.getCaseNumber());
            }
            mainTableView.getItems().removeAll(selectedRows);
            mainTableView.getSelectionModel().clearSelection();

            d.disconnectDatabase();
            PopupWindow.displayPopup("Success", num + " rows successfully deleted.");
        }
    }

    /**
     * Attempts to let the user edit a row.
     * If only one row is selected, then call setupEditRow, else show an error message.
     */
    @FXML
    private void editRow() throws IOException {
        if (getNumSelectedRows() == 1) {
            setupEditRow(getSelectedRows().get(0));
        } else {
            PopupWindow.displayPopup("Error", "You must have exactly one row selected to edit a record.");
        }
    }

    /**
     * Opens the "edit" window, but configured to add rows instead of edit them.
     */
    @FXML
    private void addRow() throws IOException {
        setupEditRow(null); // no record is passed in
    }

    /**
     * Opens a new window with the record's data filled in, which the user can use to edit (or view) it with.
     * This window must be closed to return to the rest of the application.
     */
    private void setupEditRow(Record record) throws IOException {
        Stage popupEdit = new Stage();
        popupEdit.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("editRecordWindow.fxml")
        );
        if (record == null) {
            popupEdit.setTitle("Add Record");
        }
        else {
            popupEdit.setTitle("Edit Record");
        }
        popupEdit.setScene(new Scene(loader.load()));
        EditRecordWindowController controller = loader.getController();
        controller.initData(record);
        controller.setParentController(this); // gives it access to the table controller (and thus main controller)
        popupEdit.setResizable(false);
        popupEdit.showAndWait();
    }

    /**
     * Adds all record objects in an arraylist to the main viewing table.
     * Does not add anything to the database.
     *
     * @param records An ArrayList of record objects to be displayed in the table
     */
    public void addRecordsToTable(List<Record> records) {
        for (Record rec : records) {
            mainTableView.getItems().add(rec);
        }
    }

    /**
     * Adds a record object to the main viewing table.
     * Does not add anything to the database.
     *
     * @param rec A record object to be displayed in the table
     */
    public void addRecordsToTable(Record rec) {
        mainTableView.getItems().add(rec);
    }

    /**
     * Sets the record objects displayed in table to those contained in an arraylist.
     * Does not alter the database.
     *
     * @param records An ArrayList of record objects to be displayed in the table
     */
    public void setTableRecords(List<Record> records) {
        mainTableView.getItems().clear();
        addRecordsToTable(records);
    }

    /**
     * Returns an arraylist of all the record objects in the table.
     * @return an ArrayList of all record objects in the table.
     */
    public List<Record> getDisplayedRecords() {
        return new ArrayList<>(mainTableView.getItems());
    }

    /**
     * Returns the raw ObservableList object of the main table view.
     * @return the raw ObservableList object of the main table view.
     */
    public List<Record> getRawDisplayedRecords() {
        return mainTableView.getItems();
    }

    /**
     * Stores a reference to the parent controller, in this case mainController.
     *
     * @param mainController a reference to the main controller
     */
    public void setParentController(MainController mainController) {
        this.parentController = mainController;
    }

    /**
     * Shows a popup with the difference in location between two crimes if two were selected,
     * else shows an error message.
     */
    public void analyseCrimeDifference() {
        if (getNumSelectedRows() == 2) {
            Record crime1 = getSelectedRows().get(0);
            Record crime2 = getSelectedRows().get(1);
            int locationDifference = dataAnalyser.calculateLocationDifferenceMeters(crime1, crime2);
            if (locationDifference == -1) {
                PopupWindow.displayPopup("Error", "One or more of the selected records is missing location data.\n" +
                        "Try selecting a different record");
            } else {
                try {
                    analysisPopup(crime1, crime2);
                } catch (IOException e) {
                    PopupWindow.displayPopup("Error", "An unknown error occurred, please try again");
                }
            }
        } else {
            PopupWindow.displayPopup("Error", "You must have exactly two records selected to use this feature.\n" +
                    "Hold CTRL or SHIFT to select multiple records.");
        }
    }

    /**
     * Creates a popup showing the differences between the two given crimes TODO this isn't clear enough
     * @param crime1 The first record object
     * @param crime2 The second record object
     * @throws IOException
     */
    public void analysisPopup(Record crime1, Record crime2) throws IOException {
        Stage popupEdit = new Stage();
        popupEdit.initModality(Modality.APPLICATION_MODAL);
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("analysisPopup.fxml")
        );
        popupEdit.setScene(new Scene(loader.load()));
        AnalysisPopupController controller = loader.getController();
        controller.initData(crime1, crime2);
        popupEdit.setTitle("Analyse Crimes");

        popupEdit.setResizable(false);
        popupEdit.showAndWait();
    }
}
