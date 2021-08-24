package backend.database;

import backend.Record;

import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/***
 * TODO: FOR GETTING RECORD OBJECT, MAKE SURE IT WORKS WITH NULL
 */
public class Database {
    private static Connection connection;
    private static boolean notEmpty = false;
    private List<String> columns = Arrays.asList("IUCR TEXT","PRIMARYDESCRIPTION TEXT","SECONDARYDESCRIPTION TEXT",
            "LOCATIONDESCRIPTION TEXT","ARREST TEXT", "DOMESTIC TEXT","BEAT INTEGER","WARD INTEGER","FBICD TEXT",
            "XCOORDINATE INTEGER","YCOORDINATE INTEGER","LATITUDE REAL","LONGITUDE REAL", "LOCATION TEXT");



    /**
     * Gets connection to the database and then calls the create table function
     * @throws SQLException
     */
    public void connectDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:crimeRecords.db");
            createTable();
        } catch (Exception e) {
            //System.out.println("Database connection failed");
        }
    }


    /**
     * Creates java database table by executing an SQL command to create the table and then appends the rows on from the list columns
     * @throws SQLException
     */
    public void createTable() throws SQLException {

        //Creates the original table
        Statement state2 = connection.createStatement();
        state2.execute("CREATE TABLE CRIMES " + "(ID TEXT PRIMARY KEY NOT NULL," +
                "DATE TEXT, " +
                "ADDRESS TEXT)");

        //Appends the columns in from the columns list
        for (int i = 0; i < this.columns.size(); i++){
            Statement state3 = connection.createStatement();
            state3.execute("ALTER TABLE CRIMES\n" +
                    "ADD COLUMN "+this.columns.get(i)+";");
        }

        //Probably dont need but Im not going to delete it now because I dont want to break the code
        Statement state3 = connection.createStatement();
        state3.execute("ALTER TABLE CRIMES\n" +
                "ADD COLUMN IUCR INTEGER;\n");
    }

    /**
     * Gets an array of Lists from the CSV reader and adds them to the database. Any empty values are entered as NULL type
     * @param inputs an Arraylist of Lists of Strings that is passed into it from the CSV Reader
     * @throws SQLException
     */
    public void insertRows(ArrayList<List<String>>inputs) throws SQLException {

        for(List column:inputs) {
            //Creates the statement to be run
            connection.setAutoCommit(false);
            PreparedStatement s1 = connection.prepareStatement(
                    "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,LOCATION) " +
                "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

            //Sets the ? values in the statement to their corresponding values.
            s1.setString(1, (String) column.get(0));
            s1.setString(2,(String) column.get(1));
            s1.setString(3,(String) column.get(2));
            s1.setString(4,(String) column.get(3));
            s1.setString(5,(String) column.get(4));
            s1.setString(6,(String) column.get(5));
            s1.setString(7,(String) column.get(6));
            s1.setString(8,(String) column.get(7));
            s1.setString(9,(String) column.get(8));
            String c9= (String) column.get(9);
            if(c9.equals("")){
                s1.setString(10,"NULL"); //if Value is empty in list
            }else{
                s1.setInt(10,Integer.parseInt(c9));
            }
            String c10= (String) column.get(10);
            if(c10.equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(11,Integer.parseInt(c10));
            }

            s1.setString(12,(String) column.get(11));
            String c12= (String) column.get(12);
            if(c12.equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(13,Integer.parseInt(c12));
            }
            String c13= (String) column.get(13);
            if(c13.equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setInt(14,Integer.parseInt(c13));
            }
            String c14= (String) column.get(14);
            if(c14.equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setFloat(15,Float.parseFloat(c14));
            }
            String c15= (String) column.get(15);
            if(c15.equals("")){
                s1.setString(10,"NULL");
            }else{
                s1.setFloat(16,Float.parseFloat(c15));
            }
            s1.setString(17,(String) column.get(16));

            //Executes the prepared statement
            s1.executeUpdate();
            connection.commit();
        }

    }

    /**
     * Returns record objects based on the inputted search terms.
     * @param column String and has to match: ID, DATE, ADDRESS,IUCR, PRIMARYDESCRIPTION, SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST, DOMESTIC
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException
     */
    public ArrayList<Record> searchDB(String column,String searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select * from CRIMES where "+column+ " = "+" \'"+searchValue+"\'");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }
    /**
     * Returns record objects based on the inputted search terms.
     * @param column String and has to match: BEAT,WARD,XCOORDINATE,YCOORDINATE
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException
     */
    public ArrayList<Record> searchDB(String column,int searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select * from CRIMES where "+column+ " = "+" \'"+searchValue+"\'");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }
    /**
     * Returns record objects based on the inputted search terms.
     * @param column String and has to match: LATITUDE,LONGITUDE
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException
     */
    public ArrayList<Record> searchDB(String column,double searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select * from CRIMES where "+column+ " = "+" \'"+searchValue+"\'");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }

    /**
     * Generates an arraylist of records from a given resultset
     * @param rs Resultset passed in from other methods
     * @return Arraylist of Records
     * @throws SQLException
     */
    public ArrayList<Record> getRecord(ResultSet rs) throws SQLException {
        ArrayList<Record> records = new ArrayList<Record>();

        while (rs.next()) {

            //Gets the values from the resultset
            String id = rs.getString("ID");
            String date = rs.getString("DATE");
            String address = rs.getString("ADDRESS");
            String iucr = rs.getString("IUCR");
            String primary = rs.getString("PRIMARYDESCRIPTION");
            String secondary = rs.getString("SECONDARYDESCRIPTION");
            String locationDescription = rs.getString("LOCATIONDESCRIPTION");
            String arrest = rs.getString("ARREST");
            String domestic = rs.getString("DOMESTIC");
            String beat = Integer.toString(rs.getInt("BEAT"));
            String ward = Integer.toString(rs.getInt("WARD"));
            String fbicd = rs.getString("FBICD");
            String xcoor = Integer.toString(rs.getInt("XCOORDINATE"));
            String ycoor = Integer.toString(rs.getInt("YCOORDINATE"));
            String lat = Double.toString(rs.getDouble("LATITUDE"));
            String lon = Double.toString(rs.getDouble("LONGITUDE"));

            //Passes the given values into creating a new Record class. Adds the record class to the arraylist
            ArrayList<String> add = new ArrayList<String>(Arrays.asList(id,date,address,iucr,primary,secondary,locationDescription,
                    arrest,domestic,beat,ward,fbicd,xcoor,ycoor,lat,lon));
            Record r = new Record(add);
            records.add(r);
        }
        return records;
    }
}
