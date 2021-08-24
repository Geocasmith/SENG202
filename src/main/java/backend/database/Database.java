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
    private List<String> columns = Arrays.asList("IUCR TEXT","PRIMARYDESCRIPTION TEXT","SECONDARYDESCRIPTION TEXT",
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

        for(String line:inputs) {
            String[] column = line.split(",",-1);
            for(int i=0;i<column.length;i++){
                System.out.println(column[i]);
            }
            connection.setAutoCommit(false);
            PreparedStatement s1 = connection.prepareStatement(
                    "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,LOCATION) " +
                "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");
            s1.setString(1,column[0]);
            s1.setString(2,column[1]);
            s1.setString(3,column[2]);
            s1.setString(4,column[3]);
            s1.setString(5,column[4]);
            s1.setString(6,column[5]);
            s1.setString(7,column[6]);
            System.out.println(column[7]);
            s1.setString(8,column[7]);
            s1.setString(9,column[8]);
            if(column[9].equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(10,Integer.parseInt(column[9]));
            }
            if(column[10].equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(11,Integer.parseInt(column[10]));
            }
            s1.setString(12,column[11]);
            if(column[12].equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(13,Integer.parseInt(column[12]));
            }
            if(column[13].equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(14,Integer.parseInt(column[13]));
            }
            if(column[14].equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setFloat(15,Float.parseFloat(column[14]));
            }
            if(column[15].equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setFloat(16,Float.parseFloat(column[15]));
            }

            s1.setString(17,column[16]);

            s1.executeUpdate();
            connection.commit();
        }
//        Statement s1 = connection.createStatement();
//        String sql = "INSERT INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,LOCATION) " +
//                "VALUES ('134', '11/23/2020 03:05:00 PM', '073XX S SOUTH SHORE DR',50,'THEFT','$500 AND UNDER','APARTMENT','N','N',334,7,6,1183633,1851786,41.74848637,-87.60267506,'(41.748486365, -87.602675062)');";
//        s1.executeUpdate(sql);
//        s1.close();
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
