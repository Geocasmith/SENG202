package backend.database;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * TODO: Make CSV import to the database
 * Make it reset the database each time
 * Get all of the fields
 */
public class Database {
    private static Connection connection;
    private static boolean notEmpty = false;
    private List<String> columns = Arrays.asList("IUCR INTEGER","PRIMARYDESCRIPTION TEXT","SECONDARYDESCRIPTION TEXT","LOCATIONDESCRIPTION TEXT");

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


    public void setColumns(ArrayList<String> columns) {
        List<String> list1 = Arrays.asList("IUCR INTEGER","PRIMARYDESCRIPTION TEXT","SECONDARYDESCRIPTION TEXT","LOCATIONDESCRIPTION TEXT");
        //this.columns = columns;
    }

    /**
     * Creates java database table
     * @throws SQLException
     */
    public void createTable() throws SQLException {
        Statement state2 = connection.createStatement();
        state2.execute("CREATE TABLE CRIMES " + "(ID INT PRIMARY KEY NOT NULL," +
                "DATE TEXT, " +
                "ADDRESS TEXT)");
        for (int i = 0; i < this.columns.size(); i++){
            Statement state3 = connection.createStatement();
            state3.execute("ALTER TABLE CRIMES\n" +
                    "ADD COLUMN "+this.columns.get(i)+";");
        }
        Statement state3 = connection.createStatement();
        state3.execute("ALTER TABLE CRIMES\n" +
                "ADD COLUMN IUCR INTEGER;\n");
    }

    /**
     * Inserts row into java database
     * @throws SQLException
     */
    public void insertRow() throws SQLException {
        Statement s1 = connection.createStatement();
        String sql = "INSERT INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION) " +
                "VALUES (134, '11/23/2020 03:05:00 PM', '073XX S SOUTH SHORE DR',50,'THEFT','$500 AND UNDER','APARTMENT');";
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
