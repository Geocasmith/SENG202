package gui;

import backend.Database;
import backend.InputValidator;
import backend.Record;
import com.google.gson.JsonArray;
import com.opencsv.exceptions.CsvValidationException;
import javafx.concurrent.Worker;
import javafx.css.PseudoClass;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.util.*;

/**
 * This class creates a popup to edit or add a record
 * @author Bede Skinner-Vennell (Mapping)
 * @author Sofonias Tekele Tesfaye (Auto suggest text boxes)
 * Date 09/10/2021
 */
public class EditRecordWindowController {

    @FXML private Button closeButton;
    @FXML private Button saveButton;
    @FXML private FlowPane buttonPane;
    @FXML private FlowPane fieldPane;
    @FXML private Label titleLabel;
    @FXML private BorderPane mapBorderPane;
    @FXML private WebView webView;

    @FXML private TextField caseNumberField;
    @FXML private TextField dateField;
    @FXML private TextField blockField;
    @FXML private TextField iucrField;
    @FXML private TextField primaryDescriptionField;
    @FXML private TextField secondaryDescriptionField;
    @FXML private TextField locationDescriptionField;
    @FXML private TextField wardField;
    @FXML private TextField beatField;
    @FXML private TextField fbicdField;
    @FXML private TextField xCoordinateField;
    @FXML private TextField yCoordinateField;
    @FXML private TextField latitudeField;
    @FXML private TextField longitudeField;
    @FXML private CheckBox domesticCheckBox;
    @FXML private CheckBox arrestCheckBox;

    private WebEngine webEngine;

    private int mapRequestCount = 0;
    private int windowOpenedCount = 0;

    /**
     * Defines the type of window, either edit (true) or add (false).
     * Enum was deemed too cumbersome as it would only have two possible values.
     */
    private boolean edit;

    private TableTabController parentController; // gives access to table methods, and thus main controller methods

    private static final List<String> textFieldNames = Arrays.asList("Case number", "Date", "Block", "IUCR",
            "Primary description", "Secondary description", "Location description", "Arrest", "Domestic", "Ward",
            "Beat", "FBICD", "X-Coordinate", "Y-Coordinate", "Latitude", "Longitude");
    private List<Node> textFields = new ArrayList<>(); // only to be used for iterating over during validation
    private Record editingRecord; // the record currently being edited

    /**
     * Creates the textfields and their layout, marks relevant ones as required,
     * and sets the padding on buttonPane.
     */
    @FXML private void initialize() throws IOException, CsvValidationException {
        buttonPane.setPadding(new Insets(0, 0, 15, 10)); // scenebuilder wasn't making this work so it goes here

        textFields.addAll(Arrays.asList(caseNumberField, dateField, blockField, iucrField, primaryDescriptionField,
                secondaryDescriptionField, locationDescriptionField, arrestCheckBox, domesticCheckBox, wardField,
                beatField, fbicdField, xCoordinateField, yCoordinateField, latitudeField, longitudeField));

        // Binds primary description text field to set of primary descriptions
        TextFields.bindAutoCompletion(primaryDescriptionField,InputValidator.getSetOfPrimaryDescriptions());
        AutoCompletionBinding auto = TextFields.bindAutoCompletion(secondaryDescriptionField, "");

        // Binds Secondary description text field to set of available set secondary descriptions
        secondaryDescriptionField.setOnMouseClicked(mouseEvent -> {
            try {
                Set<String> des;
                des = InputValidator.getSetOfSecondaryDescriptions(primaryDescriptionField.getText());
                AutoCompletionBinding auto1 = TextFields.bindAutoCompletion(secondaryDescriptionField, des);

                secondaryDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        iucrField.setText(InputValidator.getIucr(primaryDescriptionField.getText(), secondaryDescriptionField.getText()));
                        fbicdField.setText(InputValidator.getFbicd(primaryDescriptionField.getText(), secondaryDescriptionField.getText()));
                        auto1.dispose();
                    }  catch (IOException | CsvValidationException e) {
                        PopupWindow.displayPopup("Error", e.getMessage());
                    }
                });
            } catch (NullPointerException | IOException | CsvValidationException e) {
                primaryDescriptionField.requestFocus();
                PopupWindow.displayPopup("Error", "Enter valid Primary Description first");
            }
        });

        /* Resets associated text fields of IUCR, FBICD, Secondary description whenever change is made to
           the primary description text field
         */
        primaryDescriptionField.textProperty().addListener((observable, oldValue, newValue) -> secondaryDescriptionField.clear());

        // update the map when any of these textfields' text changes
        // you can live-locate where the record is when typing in the lat/long fields!
        caseNumberField.textProperty().addListener((observableValue, s, t1) -> updateMap());
        dateField.textProperty().addListener((observableValue, s, t1) -> updateMap());
        primaryDescriptionField.textProperty().addListener((observableValue, s, t1) -> updateMap());
        secondaryDescriptionField.textProperty().addListener((observableValue, s, t1) -> updateMap());
        locationDescriptionField.textProperty().addListener((observableValue, s, t1) -> updateMap());
        latitudeField.textProperty().addListener((observableValue, s, t1) -> updateMap());
        longitudeField.textProperty().addListener((observableValue, s, t1) -> updateMap());

        webEngine = webView.getEngine();
        webEngine.load(Objects.requireNonNull(getClass().getResource("googlemaps.html")).toString());
    }

    /**
     * Fills in the textfields with the record being edited, OR sets up the window as an "add" window instead.
     * If the user is editing, they cannot change the case number of a record; they must create a new one.
     * @param record the record object to be edited. If null, treated as an adding form.
     */
    public void initData(Record record) {
        if (record != null) {
            edit = true;
            editingRecord = record;

            caseNumberField.setText(record.getCaseNumber());
            dateField.setText(record.getDate());
            blockField.setText(record.getBlock());
            iucrField.setText(record.getIucr());
            primaryDescriptionField.setText(record.getPrimaryDescription());
            secondaryDescriptionField.setText(record.getSecondaryDescription());
            locationDescriptionField.setText(record.getLocationDescription());
            wardField.setText(String.valueOf(record.getWard()));
            beatField.setText(String.valueOf(record.getBeat()));
            fbicdField.setText(record.getFbicd());
            xCoordinateField.setText(String.valueOf(record.getXcoord()));
            yCoordinateField.setText(String.valueOf(record.getYcoord()));
            latitudeField.setText(String.valueOf(record.getLatitude()));
            longitudeField.setText(String.valueOf(record.getLongitude()));
            arrestCheckBox.setSelected(record.getArrest() == Record.TRUE);
            domesticCheckBox.setSelected(record.getDomestic() == Record.TRUE);

            caseNumberField.setDisable(true); // disables editing, focus, and click for casenum

            // Makes sure javascript in webengine has loaded before trying to use the functions
            webEngine.getLoadWorker().stateProperty().addListener(
                    (ov, oldState, newState) -> {
                        if (newState == Worker.State.SUCCEEDED) {
                            updateMap();
                        }
                    });
        }
        else {
            edit = false;
            titleLabel.setText("Add Record");
            saveButton.setText("Add record");
            updateMap();
        }
    }

    /**
     * Gets the relevant text fields data, checks that it's valid, and plots it on the map if so. If not, hide the map
     */
    private void updateMap() {

        // Retrieve the values in the relevant text fields
        String latText = latitudeField.getText();
        String lonText = longitudeField.getText();
        String caseNum = caseNumberField.getText();
        String date = dateField.getText();
        String primaryLocation = primaryDescriptionField.getText();
        String secondaryLocation = secondaryDescriptionField.getText();
        String locationDescription = locationDescriptionField.getText();

        // Check that the required fields contain valid values
        double lat = 0;
        double lon = 0;
        boolean validNumbers = false;
        boolean latLonNotEmpty = !latText.equals("") && !lonText.equals("");
        boolean displayInfoWindow = !caseNum.equals("");
        boolean connected = BrowserTabController.checkConnection();

        try {
            lat = Double.parseDouble(latText);
            lon = Double.parseDouble(lonText);
            validNumbers = (lat >= -90 && lat <= 90) && (lon >= -180 && lon <= 180);
        } catch (Exception ignored) {

        }

        // If the required fields are valid then display the point on the map, display an info window with the given
        if (latLonNotEmpty && validNumbers && connected) {
            JsonArray recordArray = new JsonArray();
            recordArray.add(lat);
            recordArray.add(lon);
            recordArray.add(caseNum);
            recordArray.add(date);
            recordArray.add(primaryLocation);
            recordArray.add(secondaryLocation);
            recordArray.add(locationDescription);

            boolean displayMarker = true;


            String script = "document.plotPoint(" + recordArray + ", " + displayInfoWindow + ", " + displayMarker + ")";


            mapBorderPane.setVisible(true);

            /* If it's the first time this map has been loaded, then wait until javascript has loaded fully before trying
               to use javascript functions, otherwise, go ahead
             */
            if (mapRequestCount == 0) {
                mapRequestCount++;
                webEngine.getLoadWorker().stateProperty().addListener(
                        (ov, oldState, newState) -> {
                            if (newState == Worker.State.SUCCEEDED) {
                                webEngine.executeScript("document.clearMap()");
                                webEngine.executeScript(script);
                                webEngine.executeScript("document.setZoom(12)");
                            }
                        });
            } else {
                webEngine.executeScript("document.clearMap()");
                webEngine.executeScript(script);
                webEngine.executeScript("document.setZoom(12)");
            }


        } else {
            if (!connected && windowOpenedCount == 0) {
                windowOpenedCount++;
                PopupWindow.displayPopup("Error", "You must be connected to the internet to use the map feature");
            }
            mapBorderPane.setVisible(false);
        }
    }

    /**
     * Closes the window.
     */
    @FXML private void closeWindow() {
        ((Stage) closeButton.getScene().getWindow()).close();
    }

    /**
     * Runs the provided record through validation and marks textfields with red outlines if they are invalid.
     * @param data the provided record to validate
     * @return the feedback list generated by the method in InputValidator
     */
    private List<String> validateRecord(List<String> data) throws CsvValidationException, IOException {
        List<String> feedback = InputValidator.recordEntryFeedbackLong(data, true);

        // check outlines for textfields on both valid and invalid attempts
        for (int i = 0; i < 16; i++) { // there is 1 value for each textfield
            if (textFields.get(i) instanceof TextField) { // don't try to do this to the checkboxes
                TextField field = (TextField) textFields.get(i);
                field.pseudoClassStateChanged(PseudoClass.getPseudoClass("textfield-required"), Objects.equals(feedback.get(i), "0"));
            }
            // although the line is long, pseudoclasses are better than regular css classes for this!
        }

        return feedback;
    }

    /**
     * Attempts to either add or save the changes to the record specified by the user.
     * Calls validateRecord on the record.
     * If the user is adding a record, the casenumber is checked for uniqueness in the database.
     * Provides popup success/fail messages based on the outcome.
     */
    @FXML private void saveRecord() {
        try {
            List<String> data = getListFromTextFields();
            List<String> feedback = validateRecord(data);

            if (Objects.equals(feedback.get(16), "1")) { // 16 is where the overall valid status is stored
                Database d = new Database();

                if (edit) {
                    d.manualUpdate(new Record(data));
                    int replaceIndex = parentController.getRawDisplayedRecords().indexOf(editingRecord);
                    Record newRecord = new Record(data);
                    parentController.getRawDisplayedRecords().set(replaceIndex, newRecord);
                    editingRecord = newRecord; // so that any additional changes also go through
                    PopupWindow.displayPopup("Success", "The record has been updated.");
                } else {
                    if (d.searchDB("ID", data.get(0)).size() > 0) {
                        PopupWindow.displayPopup("Error", "That case number is already used in the " +
                                "database.\nCase numbers must be unique.");
                        caseNumberField.pseudoClassStateChanged(PseudoClass.getPseudoClass("textfield-required"), true);
                        // marks casenumber as invalid if it is already in use
                    }
                    else {
                        d.manualAdd(new Record(data));
                        parentController.addRecordsToTable(new Record(data));
                        PopupWindow.displayPopup("Success", "The record has been added.");
                    }
                }
                d.disconnectDatabase();

            } else { // 17 is where the feedback message is held
                PopupWindow.displayPopup("Error", feedback.get(17));
            }
        }
        catch (Exception ex) { // this is here so that exceptions are more obvious when they occur
            PopupWindow.displayPopup("Error", ex.getMessage());
            ex.printStackTrace();
        }
    }

    /**
     * Returns a list of the strings in the textfields + checkboxes that could be used to make a Record object,
     * if valid.
     * @return a list of strings similar to Record.toList(), taken from the textfields.
     */
    public List<String> getListFromTextFields() {
        return Arrays.asList(
                caseNumberField.getText(), dateField.getText(), blockField.getText(), iucrField.getText(),
                primaryDescriptionField.getText(), secondaryDescriptionField.getText(), locationDescriptionField.getText(),
                String.valueOf(arrestCheckBox.isSelected()), String.valueOf(domesticCheckBox.isSelected()),
                wardField.getText(), beatField.getText(), fbicdField.getText(), xCoordinateField.getText(),
                yCoordinateField.getText(), latitudeField.getText(), longitudeField.getText()
                );
    }

    /**
     * Sets the parent controller of this window, so that it can access the rest of the application.
     * @param tableTabController the tableTabController that the window wants to access
     */
    public void setParentController(TableTabController tableTabController) {
        parentController = tableTabController;
    }
}
