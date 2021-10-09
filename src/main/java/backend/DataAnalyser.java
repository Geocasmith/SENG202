package backend;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class contains methods for analysing crime data, including lists of the currently shown wards, beats and types
 * @author Bede Skinner-Vennell
 * Date 09/10/2021
 */
public class
DataAnalyser {

    ArrayList<Integer> crimeWards = new ArrayList<>();
    ArrayList<String> crimeTypes = new ArrayList<>();
    ArrayList<Integer> crimeBeats = new ArrayList<>();

    public DataAnalyser() {}

    /**
     * Calls the updateRecords method to initialise the lists of crime types, wards and beats
     * @param records a list of all the crime records currently shown in the table
     */
    public DataAnalyser(ArrayList<Record> records) {
        updateRecords(records);
    }

    /**
     * Adds all the crime types, wards and beats to the relevant arraylists and then sorts them
     * @param records a list of all the crime records currently shown in the table
     */
    public void updateRecords(ArrayList<Record> records) {
        crimeTypes.clear();
        crimeWards.clear();
        crimeBeats.clear();

        for (Record record : records) {
            if (!crimeWards.contains(record.getWard())) {
                crimeWards.add(record.getWard());
            }
            if (!crimeTypes.contains(record.getPrimaryDescription())) {
                crimeTypes.add(record.getPrimaryDescription());
            }
            if (!crimeBeats.contains(record.getBeat())) {
                crimeBeats.add(record.getBeat());
            }
        }
        Collections.sort(crimeTypes);
        Collections.sort(crimeWards);
        Collections.sort(crimeBeats);
    }

    /**
     * Calculates the absolute value of the time difference between the crimes
     * @param record1 the first crime record
     * @param record2 the second crime record
     * @return a duration object that holds the time difference between the crimes
     */
    public Duration calculateTimeDifference(Record record1, Record record2) {
        return Duration.between(record1.getDateAsObject(), record2.getDateAsObject()).abs();
    }

    /**
     * Calculates the absolute value of the time difference between the crimes
     * @param time1 the date and time of the first crime record
     * @param time2 the date and time of the second crime record
     * @return a duration object that holds the time difference between the crimes
     */
    public Duration calculateTimeDifference(LocalDateTime time1, LocalDateTime time2) {
        return Duration.between(time1, time2).abs();
    }

    /**
     * Calculates the time difference between the two records, and a suitable unit and returns a string with that information
     * @param record1 the first crime record
     * @param record2 the second crime record
     * @return A String detailing the time difference between the two given records
     */
    public String getTimeDifferenceString(Record record1, Record record2) {
        Duration timeDifference = calculateTimeDifference(record1, record2);
        int timeDifferenceInt;
        String timeUnit;
        if (timeDifference.getSeconds() < Duration.ofHours(2).getSeconds()) {
            timeDifferenceInt = (int) (timeDifference.getSeconds() / Duration.ofMinutes(1).getSeconds());
            timeUnit = "minutes";
        } else if (timeDifference.getSeconds() < Duration.ofDays(2).getSeconds()) {
            timeDifferenceInt = (int) (timeDifference.getSeconds() / Duration.ofHours(1).getSeconds());
            timeUnit = "hours";
        } else if (timeDifference.getSeconds() < Duration.ofDays(30).getSeconds()) {
            timeDifferenceInt = (int) (timeDifference.getSeconds() / Duration.ofDays(1).getSeconds());
            timeUnit = "days";
        } else {
            timeDifferenceInt = (int) (timeDifference.getSeconds() / Duration.ofDays(30).getSeconds());
            timeUnit = "months";
        }
        return timeDifferenceInt + " " + timeUnit;
    }


    /**
     * Uses the Haversine formula to calculate the difference between the two crimes in meters, allowing for the
     * curvature of the earth
     * @param record1Lat the first crime record's latitude
     * @param record1Long the second crime record's longitude
     * @param record2Lat the first crime record's latitude
     * @param record2Long the second crime record's longitude
     * @return an integer distance between the two crimes in meters, or -1 if either
     *         of the records are missing location data
     */
    public int calculateLocationDifferenceMeters(double record1Lat, double record1Long, double record2Lat, double record2Long) {

        double earthRadius = 6371;

        double latDiff = Math.toRadians(record2Lat - record1Lat);
        double lonDiff = Math.toRadians(record2Long - record1Long);

        record1Lat = Math.toRadians(record1Lat);
        record2Lat = Math.toRadians(record2Lat);

        double a = Math.pow(Math.sin(latDiff / 2), 2) +
                Math.pow(Math.sin(lonDiff / 2), 2) *
                        Math.cos(record1Lat) *
                        Math.cos(record2Lat);
        double result = 2 * earthRadius * Math.asin(Math.sqrt(a));

        // Convert to meters and round to the nearest integer
        return (int) Math.round(result * 1000.0);
    }

    public int calculateLocationDifferenceMeters(Record record1, Record record2) {
        if (record1.getLocation() == null || record2.getLocation() == null) {
            return -1;
        }
        return calculateLocationDifferenceMeters(record1.getLatitude(), record1.getLongitude(), record2.getLatitude(), record2.getLongitude());
    }

    public String getLocationDifferenceString(Record record1, Record record2) {
        int locationDifference = calculateLocationDifferenceMeters(record1, record2);
        String locationUnit;
        if (locationDifference < 3000) {
            locationUnit = "meters";
        } else {
            locationUnit = "kilometers";
            locationDifference = locationDifference / 1000;

        }
        return locationDifference + " " + locationUnit;
    }


    /**
     * Takes a data column and returns list of data column items together with their appearance frequency
     * @param column represents the data column
     * @return List of TypeFrequency pair objects
     */
    public ArrayList<TypeFrequencyPair> getTypeFrequencyDescending(ArrayList<Object> column) {

        ArrayList<TypeFrequencyPair> res = new ArrayList<>();
        long frequency;
        HashSet types = new HashSet(column);
        Map<Object, Long> resultMap = new HashMap<>();
        column.forEach(e -> resultMap.merge(e, 1L, Long::sum));

        for (Object type : types) {
            TypeFrequencyPair pair = new TypeFrequencyPair();
            frequency = resultMap.get(type);
            // Add to list if items appears in list
            pair.setType((String) type);
            pair.setFrequency(frequency);
            res.add(pair);
        }
        return res;
    }



    public ArrayList<String> getCrimeTypes() {
        return crimeTypes;
    }

    public ArrayList<Integer> getCrimeBeats() {
        return crimeBeats;
    }

    public ArrayList<Integer> getCrimeWards() {
        return crimeWards;
    }

}
