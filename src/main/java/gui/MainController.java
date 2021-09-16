package gui;

import backend.InputValidator;
import backend.database.Database;
import com.opencsv.exceptions.CsvValidationException;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import backend.Record;
import javafx.scene.layout.FlowPane;
import org.apache.commons.lang3.ObjectUtils;
import org.controlsfx.control.CheckComboBox;
import org.controlsfx.control.CheckModel;
import org.controlsfx.control.IndexedCheckModel;

import java.io.IOException;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
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


        ArrayList<String> locationDescriptions  = (ArrayList<String>)(ArrayList<?>)(Database.extractCol("LOCATIONDESCRIPTION"));
        // Remove duplicate values
        locationDescriptions = new ArrayList<>(new HashSet<>(locationDescriptions));

        // Get Crime types
        List<String> crimeTypes = new ArrayList<String>();
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
     * Updates label for radius when slider is updated
     */
    public void updateRadiusText() {
        String radius = String.valueOf(Math.round(radiusSlider.getValue()));
        radiusLabel.setText(radius + " km");
    }

    /**
     * Applies all currently selected filters
     */
    public void applyFilters() {
        // Initialize variables for filter
        Date startDate = null;
        Date endDate = null;
        ArrayList<String> crimeTypes = new ArrayList<String>();
        ArrayList<String> locationDescriptions = new ArrayList<String>();
        String wards = "*";
        String beats = "*";
        String lat = "*";
        String lon = "*";
        int radius = 0;
        String arrest = null;
        String domestic = null;

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
            instant = Instant.from(localDate.atStartOfDay(ZoneId.systemDefault()));
            endDate = Date.from(instant);
        }

        // Get names of all checked items in crime type CheckComboBox
        IndexedCheckModel checkModel = crimeTypeComboBox.getCheckModel();
        ObservableList<Integer> checkedItems = checkModel.getCheckedIndices();
        for (Integer index : checkedItems)
        {
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
        String text;
        text = filterWardTextField.getText().trim();
        if (!text.isEmpty()) {
            wards = text;
        }
        // Beats
        text = filterBeatsTextField.getText().trim();
        if (!text.isEmpty()) {
            beats = text;
        }
        // Latitude
        text = filterLatTextField.getText().trim();
        if (!text.isEmpty()) {
            lat = text;
        }
        // Longitude
        text = filterLongTextField.getText().trim();
        if (!text.isEmpty()) {
            lon = text;
        }

        // Get value for radius
        radius = (int) Math.round(radiusSlider.getValue());

        // Get values for domestic and arrest if one has been selected
        text = domesticComboBox.getSelectionModel().getSelectedItem().toString();
        if (text != "") {
            domestic = text;
        }
        text = arrestComboBox.getSelectionModel().getSelectedItem().toString();
        if (text != "") {
            arrest = text;
        }



    }

    /**
     * Sets all filter parameters back to default
     */
    public void clearFilters() {
        filterStartDate.setValue(null);
        filterEndDate.setValue(null);
        crimeTypeComboBox.getCheckModel().clearChecks();
        locationDescriptionComboBox.getCheckModel().clearChecks();
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


