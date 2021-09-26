package backend;

import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * CSV Writer class for writing CSV files
 */
public class CsvWriter {
    /**
     *
     * @param fileName Name of the CSV file to be written
     * @param data data to be recorded into the CSV file
     */

    public static void write(String fileName, ArrayList<Record> data) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(fileName));

        // CSV file header
        String[] header = new String[]{"Case Number", "Date", "Block", "IUCR", "Primary Description",
        "Secondary Description", "Location Description", "Arrest", "Domestic", "Beat", "Ward", "FBCID",
        "X-Coordinate", "Y-Coordinate", "Latitude", "Longitude", "Location"};
        // Insert header to CSV file
        writer.writeNext(header);
        // Write CSV records on by one
        for (Record datum : data) {
            String[] nextRecord = new String[]{datum.getCaseNumber(), datum.getDate(),
                    datum.getBlock(), datum.getIucr(), datum.getPrimaryDescription(),
                    datum.getSecondaryDescription(), datum.getLocationDescription(), datum.getArrest(),
                    datum.getDomestic(), valueOf(datum.getBeat()), valueOf(datum.getWard()),
                    datum.getFbicd(), valueOf(datum.getXcoord()), valueOf(datum.getYcoord()),
                    valueOf(datum.getLatitude()), valueOf(datum.getLongitude()), valueOf(datum.getLocation())};
            writer.writeNext(nextRecord);
        }
        writer.close();
    }


}
