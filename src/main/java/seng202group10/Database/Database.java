package seng202group10.Database;
import org.sqlite.core.*;

import java.sql.*;


public class Database {

    public void createDatabase() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:crimeRecords.db");
    }
    public Connection connectDatabase() throws SQLException {
        Connection c = DriverManager.getConnection("jdbc:sqlite:crimeRecords.db");
        return c;
    }
    public void initialiseTable() throws SQLException {
        Connection c = this.connectDatabase();
        Statement stmt = c.createStatement();
        //String sql = "Insert SQL Create string";
        //stmt.execute(sql);

    }
    public void addRow() throws SQLException {

    }

}
