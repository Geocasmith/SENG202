package backend;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class Record {
    private String caseNumber;
    private LocalDateTime date;
    private String block;
    private String iucr;
    private String primaryDescription;
    private String secondaryDescription;
    private String locationDescription;
    private Boolean arrest;
    private Boolean domestic;
    private int beat;
    private int ward;
    private String fbicd;
    private int xcoord;
    private int ycoord;
    private Double latitude;
    private Double longitude;
    private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);

    public Record(List<String> data) {
        caseNumber = data.get(0);

        date = LocalDateTime.parse(data.get(1), formatter);
        block = data.get(2);
        iucr = data.get(3);
        primaryDescription = data.get(4);
        secondaryDescription = data.get(5);
        locationDescription = data.get(6);
        if (Objects.equals(data.get(7), "Y")) {
            arrest = true;
        } else if (Objects.equals(data.get(7), "N")) {
            arrest = false;
        } else {
            arrest = null;
        }
        if (Objects.equals(data.get(8), "Y")) {
            domestic = true;
        } else if (Objects.equals(data.get(8), "N")) {
            domestic = false;
        } else {
            domestic = null;
        }
        beat = Integer.parseInt(data.get(9));
        ward = Integer.parseInt(data.get(10));
        fbicd = data.get(11);
        if (Objects.equals(data.get(12), "")) {
            xcoord = -1;
            ycoord = -1;
            latitude = null;
            longitude = null;
        } else {
            xcoord = Integer.parseInt(data.get(12));
            ycoord = Integer.parseInt(data.get(13));
            latitude = Double.parseDouble(data.get(14));
            longitude = Double.parseDouble(data.get(15));
        }

    }

    public String getCaseNumber() {
        return caseNumber;
    }

    public String getDate() {
        return date.format(formatter);
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

    public Boolean getArrest() {
        return arrest;
    }

    public Boolean getDomestic() {
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

    public void setArrest(Boolean arrest) {
        this.arrest = arrest;
    }

    public void setDomestic(Boolean domestic) {
        this.domestic = domestic;
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

    public static void main(String[] args) {
        ArrayList<String> data = new ArrayList<>(Arrays.asList("JE163990", "11/23/2020 03:05:00 PM", "073XX S SOUTH SHORE DR", "820", "THEFT", "$500 AND UNDER", "APARTMENT", "N", "N", "334", "7", "6", "1183633", "1851786", "41.748486365", "-87.602675062"));
        Record record = new Record(data);
        System.out.println(record.getDate());
    }


    /**
     * Returns "Y" or "N" for true or false input values, respectively.
     * @param input a true/false boolean
     * @return "Y" for true, "N" for false
     */
    private String booleanStringChanger(boolean input) {
        if (input) {
            return "Y";
        }
        else {
            return "N";
        }
    }

    /**
     * Returns a string containing all of the data in the record.
     * @return a string with all of the data in the record, separated by commas
     */
    public String toString() {
        String output = String.format("%s, %s, %s, %s, %s, %s, %s, %s, %s, %d, %d, %s, %d, %s, %s, %s",
                this.caseNumber, this.date.format(formatter), this.block, this.iucr, this.primaryDescription, this.secondaryDescription, this.locationDescription, booleanStringChanger(this.arrest), booleanStringChanger(this.domestic), this.beat, this.ward, this.fbicd, this.xcoord, this.ycoord, this.latitude, this.longitude);
        return output;
    }

    /**
     * Returns a string containing all of the data in the record, in the specified format.
     * Can be modified to support additional formats with different formatStrings.
     * @param formatString a string specifying the format of the returned string. Options are: "labels" (shows labels for data).
     * @return a string with all of the data in the record, separated by commas and in the specified format
     */
    public String toString(String formatString) {
        String output;
        switch (formatString){
            case "labels":
                output = String.format("Case Number: %s, Date: %s, Block: %s, IUCR: %s, Primary Description: %s, Secondary Description: %s, Location Description: %s, Arrest: %s, Domestic: %s, Beat: %d, Ward: %d, FBICD: %s, X Coordinate: %d, Y Coordinate: %s, Latitude: %s, Longitude: %s",
                        this.caseNumber, this.date.format(formatter), this.block, this.iucr, this.primaryDescription, this.secondaryDescription, this.locationDescription, booleanStringChanger(this.arrest), booleanStringChanger(this.domestic), this.beat, this.ward, this.fbicd, this.xcoord, this.ycoord, this.latitude, this.longitude);
                break;
            default:
                output = this.toString();
                break;
        }
        return output;
    }
}
