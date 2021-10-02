package gui;

import backend.InputValidator;
import backend.Record;
import backend.Database;
import com.google.gson.JsonArray;
import com.opencsv.exceptions.CsvValidationException;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.css.PseudoClass;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import org.controlsfx.control.textfield.AutoCompletionBinding;
import org.controlsfx.control.textfield.TextFields;

import java.io.IOException;
import java.sql.SQLException;
import java.util.*;

public class EditRecordWindowController {

    @FXML
    private Button closeButton;
    @FXML
    private Button saveButton;
    @FXML
    private FlowPane buttonPane;
    @FXML
    private FlowPane fieldPane;
    @FXML
    private Label titleLabel;
    @FXML
    private BorderPane mapBorderPane;
    @FXML
    private WebView webView;
    private WebEngine webEngine;

    private int mapRequestCount = 0;

    /**
     * Defines the type of window, either edit (true) or add (false).
     * Enum was deemed too cumbersome as it would only have two possible values.
     */
    private boolean edit;

    private TableTabController parentController; // gives access to table methods, and thus main controller methods

    private static final List<String> textFieldNames = Arrays.asList("Case number", "Date", "Block", "IUCR",
            "Primary description", "Secondary description", "Location description", "Arrest", "Domestic", "Ward",
            "Beat", "FBICD", "X-Coordinate", "Y-Coordinate", "Latitude", "Longitude");
    private List<TextField> textFields = new ArrayList<>();
    private boolean edited = false; // was a record changed? - used to prompt for a table refresh on close
    
    /**
     * Creates the textfields and their layout, marks relevant ones as required,
     * and sets the padding on buttonPane.
     */
    @FXML private void initialize() throws IOException, CsvValidationException, SQLException {

        buttonPane.setPadding(new Insets(0, 0, 15, 10)); // scenebuilder wasn't making this work so it goes here
        for (int i = 0; i < textFieldNames.size(); i++) {
            Label fieldTitleLabel = new Label(textFieldNames.get(i));
            VBox vbox = new VBox(); // used to keep textfield and label together and on same line
            vbox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            vbox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            vbox.setPadding(new Insets(10));
            TextField field = new TextField();
            field.setPromptText(textFieldNames.get(i));
            field.setPrefSize(222, 22);
            Label reqLabel = new Label();
            if (i < 12) {
                reqLabel.setText("* Required"); // only coordinates and lat/long are optional
                if (i == 1) { reqLabel.setText("* Required" + "\n"  + "mm/dd/yyyy hh:mm:ss am/pm"); }
                if (i == 7 || i == 8) { reqLabel.setText("* Required (y/n)"); }
            }


            vbox.getChildren().addAll(fieldTitleLabel, field, reqLabel);
            textFields.add(field);
            fieldPane.getChildren().add(vbox);
        }


        // Binds primary description text field to set of primary descriptions
        TextFields.bindAutoCompletion(textFields.get(4),InputValidator.getSetOfPrimaryDescriptions());
        AutoCompletionBinding auto = TextFields.bindAutoCompletion(textFields.get(5), "");

        // Binds Secondary description text field to set of available set secondary descriptions
        textFields.get(5).setOnMouseClicked(mouseEvent -> {
            try {
                Set<String> des;
                des = InputValidator.getSetOfSecondaryDescriptions(textFields.get(4).getText());
                AutoCompletionBinding auto1 = TextFields.bindAutoCompletion(textFields.get(5), des);

                textFields.get(5).textProperty().addListener((observable, oldValue, newValue) -> {
                    try {
                        textFields.get(3).setText(InputValidator.getIucr(textFields.get(4).getText(), textFields.get(5).getText()));
                        textFields.get(11).setText(InputValidator.getFbicd(textFields.get(4).getText(), textFields.get(5).getText()));
                        auto1.dispose();
                    }  catch (IOException | CsvValidationException e) {
                        PopupWindow.displayPopup("Error", e.getMessage());
                    }

                });

            } catch (NullPointerException | IOException | CsvValidationException e) {
                textFields.get(4).requestFocus();
                PopupWindow.displayPopup("Error", "Enter valid Primary Description first");

            }

        });

        /* Resets associated text fields of IUCR, FBICD, Secondary description whenever change is made to
           the primary description text field
         */
        textFields.get(4).textProperty().addListener((observable, oldValue, newValue) -> textFields.get(5).clear());

        for (int i = 0; i < textFields.size(); i++) {
            if (Arrays.asList(1, 4, 5, 6, 14, 15).contains(i)) {
                textFields.get(i).textProperty().addListener((observableValue, s, t1) -> updateMap());
            }
        }

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
            List<String> recStrings = record.toList();

            for (int i = 0; i < textFieldNames.size(); i++) {
                textFields.get(i).setText(recStrings.get(i));
            }
            textFields.get(0).setDisable(true); // disables editing, focus, and click for casenum
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

    private void updateMap() {
        String latText = textFields.get(14).getText();
        String lonText = textFields.get(15).getText();
        String caseNum = textFields.get(0).getText();
        String date = textFields.get(1).getText();
        String primaryLocation = textFields.get(4).getText();
        String secondaryLocation = textFields.get(5).getText();
        String locationDescription = textFields.get(6).getText();
        double lat = 0;
        double lon = 0;
        boolean notEmpty = !latText.equals("") && !lonText.equals("") && !caseNum.equals("") && !date.equals("") &&
                !primaryLocation.equals("") && !secondaryLocation.equals("") && !locationDescription.equals("");
        boolean validNumbers = false;
        try {
            lat = Double.parseDouble(latText);
            lon = Double.parseDouble(lonText);
            validNumbers = (lat >= -90 && lat <= 90) && (lon >= -180 && lon <= 180);
        } catch (Exception ignored) {

        }
        if (notEmpty && validNumbers) {
            JsonArray recordArray = new JsonArray();
            recordArray.add(lat);
            recordArray.add(lon);
            recordArray.add(caseNum);
            recordArray.add(date);
            recordArray.add(primaryLocation);
            recordArray.add(secondaryLocation);
            recordArray.add(locationDescription);

            mapBorderPane.setVisible(true);
            if (mapRequestCount == 0) {
                mapRequestCount++;
                webEngine.getLoadWorker().stateProperty().addListener(
                        (ov, oldState, newState) -> {
                            if (newState == Worker.State.SUCCEEDED) {
                                webEngine.executeScript("document.clearMap()");
                                webEngine.executeScript("document.plotPoint(" + recordArray + ")");
                                webEngine.executeScript("document.setZoom(12)");
                            }
                        });
            } else {
                webEngine.executeScript("document.clearMap()");
                webEngine.executeScript("document.plotPoint(" + recordArray + ")");
                webEngine.executeScript("document.setZoom(12)");
            }


        } else {
            mapBorderPane.setVisible(false);
        }
    }

    /**
     * Closes the window and, if the user has edited or added data,
     * prompts them to refresh the table to view their changes.
     */
    @FXML private void closeWindow() throws SQLException, IOException {
        if (edited) {
            if (PopupWindow.displayTwoButtonPopup("Refresh Table", "Any changes or additions to the data will not be " +
                    "visible unless the table is refreshed.\nThis could take some time if there are many records loaded.\n" +
                    "Would you like to refresh the table?", "Yes", "No")) {
                parentController.refreshTableData();
            }
        }
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
            TextField field = textFields.get(i);
            field.pseudoClassStateChanged(PseudoClass.getPseudoClass("textfield-required"), Objects.equals(feedback.get(i), "0"));
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
            ArrayList<String> data = new ArrayList<>();
            for (TextField field : textFields) {
                data.add(field.getText());
            }
            List<String> feedback = validateRecord(data);

            if (Objects.equals(feedback.get(16), "1")) { // 16 is where the overall valid status is stored
                Database d = new Database();

                if (edit) {
                    d.manualUpdate(new Record(data));
                    PopupWindow.displayPopup("Success", "The record has been updated.");
                } else {
                    if (d.searchDB("ID", data.get(0)).size() > 0) {
                        PopupWindow.displayPopup("Error", "That case number is already used in the " +
                                "database.\nCase numbers must be unique.");
                        textFields.get(0).pseudoClassStateChanged(PseudoClass.getPseudoClass("textfield-required"), true);
                        // marks casenumber as invalid if it is already in use
                    }
                    else {
                        d.manualAdd(new Record(data));
                        PopupWindow.displayPopup("Success", "The record has been added.");
                    }
                }
                this.edited = true;
                d.disconnectDatabase();

            } else { // 17 is where the feedback message is held
                PopupWindow.displayPopup("Error", feedback.get(17));
            }
        }
        catch (Exception ex) { // this is here so that exceptions are more obvious when they occur
            PopupWindow.displayPopup("Error", ex.getMessage());

        }
    }

    /**
     * Sets the parent controller of this window, so that it can access the rest of the application.
     * @param tableTabController the tableTabController that the window wants to access
     */
    public void setParentController(TableTabController tableTabController) {
        parentController = tableTabController;
    }
}
