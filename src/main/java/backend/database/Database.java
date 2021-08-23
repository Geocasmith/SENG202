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
    private List<String> columns = Arrays.asList("IUCR INTEGER","PRIMARYDESCRIPTION TEXT","SECONDARYDESCRIPTION TEXT",
            "LOCATIONDESCRIPTION TEXT","ARREST TEXT", "DOMESTIC TEXT","BEAT INTEGER","WARD INTEGER","FBICD TEXT",
            "XCOORDINATE INTEGER","YCOORDINATE INTEGER","LATITUDE REAL","LONGITUDE REAL", "LOCATION TEXT");

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
        state2.execute("CREATE TABLE CRIMES " + "(ID TEXT PRIMARY KEY NOT NULL," +
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
    public void insertRows(ArrayList<String>inputs) throws SQLException {
//        for(String line:inputs) {
//            System.out.println(line);
//            String[] column = line.split(",");
//            String id = column[0];
//            String date = column[1];
//            String block = column[2];
//            int iucr = Integer.parseInt(column[3]);
//            String primary = column[4];
//            String secondary = column[5];
//            String location = column[6];
//            String arrest = column[7];
//            String domestic = column[8];
//            int beat = Integer.parseInt(column[9]);
//            int ward = Integer.parseInt(column[10]);
//            String fbicd = column[11];
//            int xcoord = Integer.parseInt(column[12]);
//            int ycoord = Integer.parseInt(column[13]);
//            float latitude = Float.parseFloat(column[14]);
////            float longitude = Float.parseFloat(column[15]);
////            String latlong = column[16];
//            for(int i =0;i<=12;i++){
//                System.out.println(column[i]);
//            }
//
//            Statement s1 = connection.createStatement();
//            String sql = "INSERT INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,LOCATION) " +
//                    "VALUES ('134', '11/23/2020 03:05:00 PM', '073XX S SOUTH SHORE DR',50,'THEFT','$500 AND UNDER','APARTMENT','N','N',334,7,6,1183633,1851786,41.74848637,-87.60267506,'(41.748486365, -87.602675062)');";
//
//        }
        Statement s1 = connection.createStatement();
        String sql = "INSERT INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,LOCATION) " +
                "VALUES ('134', '11/23/2020 03:05:00 PM', '073XX S SOUTH SHORE DR',50,'THEFT','$500 AND UNDER','APARTMENT','N','N',334,7,6,1183633,1851786,41.74848637,-87.60267506,'(41.748486365, -87.602675062)');";
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
