package backend;

import com.google.gson.JsonArray;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

/**
 * This is the class used to hold crime records in the application.
 * It is also used for display in the main TableView.
 *
 * Record is constructed using a list of Strings.
 * A Record object can be converted into one comma separated String or a list of Strings
 * using the provided methods.
 * @author Bede Skinner-Vennell
 * Date 09/10/2021
 */
public class Record {
    private String caseNumber;
    private LocalDateTime date;
    private String block;
    private String iucr;
    private String primaryDescription;
    private String secondaryDescription;
    private String locationDescription;
    private String arrest;
    private String domestic;
    private int beat;
    private int ward;
    private String fbicd;
    private int xcoord;
    private int ycoord;
    private Double latitude;
    private Double longitude;
    public static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);
    /**
     * List of strings that will be counted as "true" when parsing arrest and domestic fields.
     * Strings in lower case; use a case-insensitive check.
     */ // these are here to make it more forgiving for users to enter data if they are not used to Y/N
    public static final List<String> trueStrings = Arrays.asList("y", "true", "yes", "1");
    /**
     * List of strings that will be counted as "false" when parsing arrest and domestic fields.
     * Strings in lower case; use a case-insensitive check.
     */
    public static final List<String> falseStrings = Arrays.asList("n", "false", "no", "0");
    public static final String TRUE = "Y"; // for output display of arrest and domestic fields
    public static final String FALSE = "N";

    /**
     * Goes through the provided list of crime data and creates a new record object
     * Assumes that invalid empty fields have been dealt with before being sent to the constructor
     * @param data A list of data containing the required fields for a record to be created
     */
    public Record(List<String> data) {
        caseNumber = data.get(0);
        date = LocalDateTime.parse(data.get(1).toUpperCase(), formatter);
        block = data.get(2);
        iucr = data.get(3);
        primaryDescription = data.get(4);
        secondaryDescription = data.get(5);
        locationDescription = data.get(6);
        arrest = readBooleanString(data.get(7).toLowerCase());
        domestic = readBooleanString(data.get(8).toLowerCase());
        beat = Integer.parseInt(data.get(9));
        ward = Integer.parseInt(data.get(10));
        fbicd = data.get(11);
        if (Objects.equals(data.get(12), "")) {
            xcoord = -1;
        } else {
            xcoord = Integer.parseInt(data.get(12));
        }
        if (Objects.equals(data.get(13), "")) {
            ycoord = -1;
        } else {
            ycoord = Integer.parseInt(data.get(13));
        }
        if (Objects.equals(data.get(14), "")) {
            latitude = null;
        } else {
            latitude = Double.parseDouble(data.get(14));
        }
        if (Objects.equals(data.get(15), "")) {
            longitude = null;
        } else {
            longitude = Double.parseDouble(data.get(15));
        }

    }

    public String getCaseNumber() {
        return caseNumber;
    }

    /**
     * Uses the LocalDateTime formatter to convert the time to string form
     * @return the date in string form
     */
    public String getDate() {
        return date.format(formatter);
    }

    public LocalDateTime getDateAsObject() {
        return date;
    }

    public String getBlock() {
        return block;
    }

    public String getIucr() {
        return iucr;
    }

    public String getPrimaryDescription() {
        return primaryDescription;
    }

    public String getSecondaryDescription() {
        return secondaryDescription;
    }

    public String getLocationDescription() {
        return locationDescription;
    }

    public String getArrest() {
        return arrest;
    }

    public String getDomestic() {
        return domestic;
    }

    public int getBeat() {
        return beat;
    }

    public int getWard() {
        return ward;
    }

    public String getFbicd() {
        return fbicd;
    }

    public int getXcoord() {
        return xcoord;
    }

    public int getYcoord() {
        return ycoord;
    }

    public Double getLatitude() {
        return latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    /**
     * Checks that the record has valid location data and returns it in a tuple form if so
     * @return The lat long location of the crime in a string tuple form, or null if the
     *         record is missing location data
     */
    public String getLocation() {
        if (latitude == null || longitude == null) {
            return null;
        } else {
            return String.format("(%s, %s)", latitude, longitude);
        }
    }

    public void setCaseNumber(String caseNumber) {
        this.caseNumber = caseNumber;
    }

    public void setDate(LocalDateTime date) {
        this.date = date;
    }

    public void setBlock(String block) {
        this.block = block;
    }

    public void setIucr(String iucr) {
        this.iucr = iucr;
    }

    public void setPrimaryDescription(String primaryDescription) {
        this.primaryDescription = primaryDescription;
    }

    public void setSecondaryDescription(String secondaryDescription) {
        this.secondaryDescription = secondaryDescription;
    }

    public void setLocationDescription(String locationDescription) {
        this.locationDescription = locationDescription;
    }

    /**
     * Sets the arrest string value to either a valid input or null.
     * @param arrest input string
     */
    public void setArrest(String arrest) {
        this.arrest = readBooleanString(arrest);
    }

    /**
     * Sets the domestic string value to either a valid input or null.
     * @param domestic input string
     */
    public void setDomestic(String domestic) {
        this.domestic = readBooleanString(domestic);
    }

    public void setBeat(int beat) {
        this.beat = beat;
    }

    public void setWard(int ward) {
        this.ward = ward;
    }

    public void setFbicd(String fbicd) {
        this.fbicd = fbicd;
    }

    public void setXcoord(int xcoord) {
        this.xcoord = xcoord;
    }

    public void setYcoord(int ycoord) {
        this.ycoord = ycoord;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     * Returns "Y" if the input (case-insensitive) is contained within Record.trueStrings,
     * "N" if in Record.falseStrings. Returns null if it is in neither.
     * @param input the string to be read
     * @return corresponding boolean value
     */
    private static String readBooleanString(String input) {
        if (Record.trueStrings.contains(input.toLowerCase())) {
            return Record.TRUE;
        }
        else if (Record.falseStrings.contains(input.toLowerCase())) {
            return Record.FALSE;
        }
        else {
            return null;
        }
    }

    /**
     * Returns a string containing all the data in the record.
     * @return a string with all the data in the record, separated by commas
     */
    public String toString() {
        return String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %d, %d, %s, %d, %s, %s, %s",
                this.caseNumber, this.date.format(formatter), this.block, this.iucr, this.primaryDescription,
                this.secondaryDescription, this.locationDescription, this.arrest,
                this.domestic, this.beat, this.ward, this.fbicd, this.xcoord, this.ycoord,
                this.latitude, this.longitude);
    }

    /**
     * Returns a string containing all the data in the record, in the specified format.
     * Can be modified to support additional formats with different formatStrings.
     * @param formatString a string specifying the format of the returned string.
     *                     Options are: "labels" (shows labels for data).
     * @return a string with all the data in the record, separated by commas and in the specified format
     */
    public String toString(String formatString) {
        String output;
        // default to regular behaviour if formatString isn't valid
        if ("labels".equals(formatString)) {
            output = String.format("Case Number: %s, Date: %s, Block: %s, IUCR: %s, Primary Description: %s, " +
                            "Secondary Description: %s, Location Description: %s, Arrest: %s, Domestic: %s, " +
                            "Beat: %d, Ward: %d, FBICD: %s, X Coordinate: %d, Y Coordinate: %s, Latitude: %s, " +
                            "Longitude: %s",
                    this.caseNumber, this.date.format(formatter), this.block, this.iucr, this.primaryDescription,
                    this.secondaryDescription, this.locationDescription, this.arrest, this.domestic, this.beat,
                    this.ward, this.fbicd, this.xcoord, this.ycoord, this.latitude, this.longitude);
        } else {
            output = this.toString();
        }
        return output;
    }

    /**
     * Returns the record object as a list of strings.
     * Can be used to iterate over the object easily in a loop.
     * @return The record in list form
     */
    public List<String> toList() {
        return Arrays.asList(
        this.getCaseNumber(), this.getDate(), this.getBlock(), this.getIucr(),
                this.getPrimaryDescription(), this.getSecondaryDescription(), this.getLocationDescription(),
                this.getArrest(), this.getDomestic(), Integer.toString(this.getBeat()),
                Integer.toString(this.getWard()), this.getFbicd(),
                String.valueOf(this.getXcoord()), String.valueOf(this.getYcoord()), String.valueOf(this.getLatitude()),
                String.valueOf(this.getLongitude())
                );
    }

    /**
     * Creates a Json Array to be used to create map markers that contains the case number, latitude and longitude, date
     * and primary, secondary and location descriptions
     * @return a Json Array with the required data to create a map marker
     */
    public JsonArray getJsonArray() {
        JsonArray recordArray = new JsonArray();
        recordArray.add(this.getLatitude());
        recordArray.add(this.getLongitude());
        recordArray.add(this.getCaseNumber());
        recordArray.add(this.getDate());
        recordArray.add(this.getPrimaryDescription());
        recordArray.add(this.getSecondaryDescription());
        recordArray.add(this.getLocationDescription());
        return recordArray;
    }
}
