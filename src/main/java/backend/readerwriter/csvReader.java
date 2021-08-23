package backend.readerwriter;


import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class csvReader {

    public ArrayList<String> read() throws FileNotFoundException {
        ArrayList<String>csvValues = new ArrayList<String>();
        File in = new File("seng202_2021_crimes_one_year_prior_to_present_5k.csv");

            Scanner sc = new Scanner(in);
            sc.nextLine();
            //System.out.println(sc.nextLine());
            while (sc.hasNext()) {
                String line = sc.nextLine();
                csvValues.add(line);
            }
        return csvValues;
    }
}
