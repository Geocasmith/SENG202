package gui;

import backend.InputValidator;
import backend.Record;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EditRecordWindow {
    private static final List<String> textFieldNames = Arrays.asList("Case number", "Date", "Block", "IUCR",
            "Primary description", "Secondary description", "Location description", "Arrest", "Domestic", "Beat",
            "Ward", "FBICD", "X-Coordinate", "Y-Coordinate", "Latitude", "Longitude");

    public static void displayEditRecordWindow(Record record) {
        String caseNum = record.getCaseNumber(); // saved to send to database

        Stage popupWindow = new Stage();
        popupWindow.initModality(Modality.APPLICATION_MODAL);
        popupWindow.setTitle("Edit Record");

        // Create save and close buttons
        FlowPane buttonPane = new FlowPane();
        buttonPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        buttonPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        buttonPane.setAlignment(Pos.CENTER_LEFT);
        buttonPane.setVgap(10);
        buttonPane.setHgap(10);
        buttonPane.setPadding(new Insets(0, 0, 25, 10)); // to match v/hgap of fieldPane
        Button saveButton = new Button("Save changes");
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> popupWindow.close());
        buttonPane.getChildren().addAll(saveButton, cancelButton);

        // Create & populate textfields
        List<VBox> vBoxes = new ArrayList<>(); // created for use in validation
        FlowPane fieldPane = new FlowPane();
        fieldPane.setPrefWidth(Region.USE_COMPUTED_SIZE);
        fieldPane.setPrefHeight(Region.USE_COMPUTED_SIZE);
        fieldPane.setRowValignment(VPos.TOP);
        fieldPane.setColumnHalignment(HPos.LEFT);
        fieldPane.setPrefWrapLength(750); // seems to be the best way to control width/height of whole window
        fieldPane.setHgap(10);
        fieldPane.setVgap(10);
        List<String> recStrings = record.toList();
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
            vBoxes.add(vbox);
        }
        fieldPane.getChildren().addAll(vBoxes);


        // Set up validation & attempt at saving data...
        saveButton.setOnAction(e -> {
            List<String> data = new ArrayList<>();
            for (VBox vbox : vBoxes){
                data.add(((TextField) vbox.getChildren().get(1)).getText());
            }
            try {
                ArrayList<String> feedback = InputValidator.recordEntryFeedback(data);
                if (feedback.get(0) == "1") {
                    // TODO find how to edit/delete/add from the database!!!!
                    PopupWindow.displayPopup("Success", "That record is good, but this feature isn't finished yet.");
                }
                else {
                    PopupWindow.displayPopup("Error", feedback.get(1));
                }
            } catch (Exception ex) {
                PopupWindow.displayPopup("Error", "An exception occurred while trying to save that record.");
            }
        });

        // setup overall window layout
        VBox layout = new VBox();
        layout.setPrefHeight(Region.USE_COMPUTED_SIZE);
        layout.setPrefWidth(Region.USE_COMPUTED_SIZE);
        Label titleLabel = new Label("Edit Record");
        titleLabel.setPadding(new Insets(20));
        titleLabel.setStyle("-fx-font: 24 arial");
        layout.getChildren().addAll(titleLabel, fieldPane, buttonPane);
        layout.setAlignment(Pos.TOP_CENTER);
        layout.setPadding(new Insets(20));

        Scene scene = new Scene(layout);
        popupWindow.setScene(scene);
        popupWindow.showAndWait();
    }
}
