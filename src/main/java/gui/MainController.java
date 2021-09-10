package gui;

import backend.InputValidator;
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

    // Filter Sidebar Elements
    @FXML
    private TitledPane filterPane;
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
    private void initialize() throws SQLException, IOException, CsvValidationException {
        filterSetup();
    }


    /**
     * Sets up combo boxes in filter pane
     * Sets filter pane as expanded
     */
    public void filterSetup() throws SQLException, IOException, CsvValidationException {
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
        ArrayList<String> locationDescriptions  = (ArrayList<String>)(ArrayList<?>)(d.extractCol("LOCATIONDESCRIPTION"));

        // Remove duplicate values
        locationDescriptions = new ArrayList<>(new HashSet<>(locationDescriptions));

        // Get Crime types
        List<String> crimeTypes = new ArrayList<String>();
        crimeTypes = InputValidator.getSetOfPrimaryDescriptions();

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


