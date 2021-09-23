package gui;

import backend.InputValidator;
import backend.Record;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class editRecordWindowController {

    @FXML private Button cancelButton;
    @FXML private Button saveButton;
    @FXML private FlowPane buttonPane;
    @FXML private FlowPane fieldPane;

    private static final List<String> textFieldNames = Arrays.asList("Case number", "Date", "Block", "IUCR",
            "Primary description", "Secondary description", "Location description", "Arrest", "Domestic", "Beat",
            "Ward", "FBICD", "X-Coordinate", "Y-Coordinate", "Latitude", "Longitude");
    private List<TextField> textFields = new ArrayList<>();
    private List<String> recStrings = new ArrayList<>();

    public void initData(Record record) {
        recStrings = record.toList();
        for (int i = 0; i < textFieldNames.size(); i++) {
            Label fieldTitleLabel = new Label(textFieldNames.get(i));
            VBox vbox = new VBox();
            vbox.setPrefHeight(Region.USE_COMPUTED_SIZE);
            vbox.setPrefWidth(Region.USE_COMPUTED_SIZE);
            vbox.setPadding(new Insets(10));
            TextField field = new TextField(recStrings.get(i));
            field.setPromptText(textFieldNames.get(i));
            Label reqLabel = new Label();
            if (i < 12) {
                reqLabel.setText("* Required");
            }
            vbox.getChildren().addAll(fieldTitleLabel, field, reqLabel);
            textFields.add(field);
            fieldPane.getChildren().add(vbox);
        }
    }

    @FXML
    private void initialize()  {
        buttonPane.setPadding(new Insets(0, 0, 15, 10)); // scenebuilder wasn't making this work so it goes here
    }


    @FXML private void closeWindow() {
        ((Stage)cancelButton.getScene().getWindow()).close();
    }

    @FXML private void saveRecord() {
        List<String> data = new ArrayList<>();
        for (TextField field : textFields){
            data.add(field.getText());
        }
        try {
            List<String> feedback = InputValidator.recordEntryFeedbackLong(data);
            System.out.println(feedback);

            // do outlines for textfields on valid and invalid attempts
            for (int i = 0; i < 16; i++) { // there is 1 value for each textfield
                TextField field = textFields.get(i);
                if (feedback.get(i) == "0") {
                    if (field.getStyleClass().contains("defaulttextfield")) {
                        field.getStyleClass().remove("defaulttextfield");
                    }
                    field.getStyleClass().add("required");
                }
                else {
                    if (field.getStyleClass().contains("required")) {
                        field.getStyleClass().remove("required");
                    }
                    field.getStyleClass().add("defaulttextfield");
                }
            }

            if (feedback.get(16) == "1") {
                Database d = new Database();
                d.manualUpdate(new Record(data));
                d.closeConnection();
                PopupWindow.displayPopup("Success", "The record has been updated.");
            }
            else {
                PopupWindow.displayPopup("Error", feedback.get(17));
            }
        } catch (Exception ex) {
            PopupWindow.displayPopup("Error", "An exception occurred while trying to save that record.");
            ex.printStackTrace();
            System.out.println(ex);
        }
    }
}
