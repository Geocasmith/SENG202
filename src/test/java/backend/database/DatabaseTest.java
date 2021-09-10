package backend.database;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;

class DatabaseTest {

    @Test
    void extractCol() throws SQLException, CsvValidationException, IOException {
        Database db = new Database();
        db.connectDatabase("crimeRecords");
        ArrayList<Object> extractedCol = new ArrayList<>();
        extractedCol = db.extractCol(2);
        for (int i = 0; i < extractedCol.size(); i++) {
            System.out.println(extractedCol.get(i));
        }

    }
}