package backend;
import java.time.Duration;
import java.time.LocalDateTime;

public class DataAnalyser {

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
     * Uses the Haversine formula to calculate the difference between the two crimes in meters
     * @param record1 the first crime record
     * @param record2 the second crime record
     * @return an integer distance between the two crimes in meters, or -1 if either
     *         of the records are missing location data
     */
    public int calculateLocationDifferenceMeters(Record record1, Record record2) {
        if (record1.getLocation() == null || record2.getLocation() == null) {
            return -1;
        }

        double record1Lat = record1.getLatitude();
        double record1Long = record1.getLongitude();
        double record2Lat = record2.getLatitude();
        double record2Long = record2.getLongitude();

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
}
