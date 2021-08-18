package backend.database;

import java.sql.*;

/***
 * TODO: Make CSV import to the database
 * Make it reset the database each time
 * Get all of the fields
 */
public class Database {
    private static Connection connection;
    private static boolean notEmpty = false;


    public ResultSet getCrimes() throws SQLException {
        if (connection == null) {
            connectDatabase();
        }
        Statement state = connection.createStatement();
        ResultSet res = state.executeQuery("SELECT  fname, lname FROM user");
        return res;
    }

    /**
     * Helper class to establish database connection
     * @throws SQLException
     */
    public void connectDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:crimeRecords.db");
            createTable();
        } catch (Exception e) {
            System.out.println("Database connection failed");
        }
    }

    /**
     * Creates java database table
     * @throws SQLException
     */
    public void createTable() throws SQLException {
        Statement state2 = connection.createStatement();
        state2.execute("CREATE TABLE CRIMES " + "(ID INT PRIMARY KEY NOT NULL," +
                "NAME TEXT NOT NULL, " +
                "ADDRESS        CHAR(50))");

    }

    /**
     * Inserts row into java database
     * @throws SQLException
     */
    public void insertRow() throws SQLException {
        Statement s1 = connection.createStatement();
        String sql = "INSERT INTO CRIMES (ID, NAME, ADDRESS) " + "VALUES (134, 'Paul', 'Detroit');";
        s1.executeUpdate(sql);
        s1.close();
        //connection.commit();

    }

    public void readRows() throws SQLException {
        Statement s2 = connection.createStatement();
        ResultSet rs = s2.executeQuery("SELECT * FROM CRIMES;");

        while (rs.next()) {
            int id = rs.getInt("id");
            String name = rs.getString("name");

            System.out.println("ID = " + id);
            System.out.println("NAME = " + name);
        }
    }
}
