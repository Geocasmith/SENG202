package backend.readerwriter;

import backend.Record;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
/**
 * CSV Writer class for writing CSV files
 */

import static java.lang.String.valueOf;

public class CsvWriter {
    /**
     *
     * @param fileName Name of the CSV file to be written
     * @param data data to be recorded into the CSV file
     * @throws IOException
     */

    public void write(String fileName, ArrayList<Record> data) throws IOException {
        CSVWriter writer = new CSVWriter(new FileWriter(fileName));

        // CSV file header
        String[] header = new String[]{"Case Number", "Date", "Block", "IUCR", "Primary Description",
        "Secondary Description", "Location", "Arrest", "Domestic", "Beat", "Ward", "FBCID",
        "X-Coordinate", "Y-Coordinate", "Latitude", "Longitude"};
        // Insert header to CSV file
        writer.writeNext(header);

        // Write CSV records on by one
        for (int i = 0; i < data.size(); i++) {
            String[] nextRecord = new String[]{data.get(i).getCaseNumber(), data.get(i).getDate().toString(),
                    data.get(i).getBlock(), data.get(i).getIucr(), data.get(i).getPrimaryDescription(),
                    data.get(i).getSecondaryDescription(), data.get(i).getLocation(), data.get(i).getArrest().toString(),
                    data.get(i).getDomestic().toString(), valueOf(data.get(i).getBeat()), valueOf(data.get(i).getWard()),
                    data.get(i).getFbicd(), valueOf(data.get(i).getXcoord()), valueOf(data.get(i).getYcoord()),
                    valueOf(data.get(i).getLatitude()), valueOf(data.get(i).getLongitude())};
            writer.writeNext(nextRecord);
        }
        writer.close();
    }
}
