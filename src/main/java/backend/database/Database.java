package backend.database;

import backend.Record;

import java.sql.*;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Date;
import java.text.SimpleDateFormat;


import static java.lang.String.valueOf;

/***
 * TODO: FOR GETTING RECORD OBJECT, MAKE SURE IT WORKS WITH NULL
 * Search does not work with LATITUDE (returns empty list)
 * Import from csv right into db
 */
public class Database {
    private static Connection connection;
    private static boolean notEmpty = false;
    private static List<String> columns = Arrays.asList("IUCR TEXT", "PRIMARYDESCRIPTION TEXT", "SECONDARYDESCRIPTION TEXT",
            "LOCATIONDESCRIPTION TEXT", "ARREST TEXT", "DOMESTIC TEXT", "BEAT INTEGER", "WARD INTEGER", "FBICD TEXT",
            "XCOORDINATE INTEGER", "YCOORDINATE INTEGER", "LATITUDE REAL", "LONGITUDE REAL", "UNIXTIME REAL");


    /**
     * Gets connection to the database and then calls the create table function
     *
     * @throws SQLException
     */
    public static void connectDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:./Files/crimeRecords.db");
            createTable();
        } catch (Exception e) {
            //System.out.println("Database connection failed");
        }
    }


    /**
     * Creates java database table by executing an SQL command to create the table and then appends the rows on from the list columns
     *
     * @throws SQLException
     */
    public static void createTable() throws SQLException {

        //Creates the original table
        Statement state2 = connection.createStatement();
        state2.execute("CREATE TABLE CRIMES " + "(ID TEXT PRIMARY KEY NOT NULL," +
                "DATE TEXT, " +
                "ADDRESS TEXT)");

        //Appends the columns in from the columns list
        for (int i = 0; i < columns.size(); i++) {
            Statement state3 = connection.createStatement();
            state3.execute("ALTER TABLE CRIMES\n" +
                    "ADD COLUMN " + columns.get(i) + ";");
        }

        //Probably dont need but Im not going to delete it now because I dont want to break the code
        Statement state3 = connection.createStatement();
        state3.execute("ALTER TABLE CRIMES\n" +
                "ADD COLUMN IUCR INTEGER;\n");
    }

    /**
     * Gets an array of Lists from the CSV reader and adds them to the database. Any empty values are entered as NULL type
     *
     * @param inputs an Arraylist of Lists of Strings that is passed into it from the CSV Reader
     * @throws SQLException
     */
    public void insertRows(ArrayList<List<String>> inputs) throws SQLException, ParseException {


        //Creates the statement to be run
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement(
                "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,UNIXTIME) " +
                        "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

        for (List column : inputs) {
            //Sets the ? values in the statement to their corresponding values.
            s1.setString(1, (String) column.get(0));

            //Date and unix time
            s1.setString(2, (String) column.get(1));
            s1.setLong(17,  unixTimeConvert((String) column.get(1)));

            s1.setString(3, (String) column.get(2));
            s1.setString(4, (String) column.get(3));
            s1.setString(5, (String) column.get(4));
            s1.setString(6, (String) column.get(5));
            s1.setString(7, (String) column.get(6));
            s1.setString(8, (String) column.get(7));
            s1.setString(9, (String) column.get(8));
            String c9 = (String) column.get(9);
            if (c9.equals("")) {
                s1.setString(10, "NULL"); //if Value is empty in list
            } else {
                s1.setInt(10, Integer.parseInt(c9));
            }
            String c10 = (String) column.get(10);
            if (c10.equals("")) {
                s1.setString(10, "NULL");
            } else {
                s1.setInt(11, Integer.parseInt(c10));
            }

            s1.setString(12, (String) column.get(11));
            String c12 = (String) column.get(12);
            if (c12.equals("")) {
                s1.setString(10, "NULL");
            } else {
                s1.setInt(13, Integer.parseInt(c12));
            }
            String c13 = (String) column.get(13);
            if (c13.equals("")) {
                s1.setString(10, "NULL");
            } else {
                s1.setInt(14, Integer.parseInt(c13));
            }
            String c14 = (String) column.get(14);
            if (c14.equals("")) {
                s1.setString(10, "NULL");
            } else {
                s1.setFloat(15, Float.parseFloat(c14));
            }
            String c15 = (String) column.get(15);
            if (c15.equals("")) {
                s1.setString(10, "NULL");
            } else {
                s1.setFloat(16, Float.parseFloat(c15));
            }


            s1.addBatch();
        }
        //Executes the prepared statement
        s1.executeBatch();
        connection.commit();
    }


    /**
     * Pass in a record class and it will be added to the database. I decided to add it to an arraylist of lists of strings so I can pass it into the previously
     * made insertRows() method and reuse that code
     *
     * @param rec Record class object
     * @throws SQLException
     */
    public void manualAdd(Record rec) throws SQLException, ParseException {

        //Gets string from record and adds null to end for the Location Column as that is not needed (can be calculated from Latitude and Longitude)
        String recString = rec.toString();
        recString += ",NULL";

        //Creates a string array by splitting the string and adds it to an arraylist so it can be passed to insertRows(ArrayList<List<String>>) method
        String[] column = recString.trim().split("\\s*,\\s*");
        ArrayList<List<String>> input = new ArrayList<List<String>>();
        input.add(List.of(column));

        //Calls insertRows method on the arraylist to add the row to the database
        insertRows(input);
    }

    /**
     * Pass in a record class and it will be added to the database. I decided to add it to an arraylist of lists of strings so I can pass it into the previously
     * made insertRows() method and reuse that code
     *
     * @param rec Record class object
     * @throws SQLException
     */
    public void manualUpdate(Record rec) throws SQLException, ParseException {

        //Gets string from record and adds null to end for the Location Column as that is not needed (can be calculated from Latitude and Longitude)
        String recString = rec.toString();
        recString += ",NULL";

        //Creates a string array by splitting the string and adds it to an arraylist so it can be passed to insertRows(ArrayList<List<String>>) method
        String[] column = recString.trim().split("\\s*,\\s*");
        ArrayList<List<String>> input = new ArrayList<List<String>>();
        input.add(List.of(column));

        //Deletes outdated entry by calling delete on column[0] which is the string of the Case Number
        manualDelete(column[0]);

        //Calls insertRows method on the arraylist to add the row to the database
        insertRows(input);
    }

    /**
     * Removes the row which matches the case number in the parameter
     *
     * @param caseNum pass in the case number for the row that will be deleted
     * @throws SQLException
     */
    public void manualDelete(String caseNum) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("delete from CRIMES where ID = " + " \'" + caseNum + "\'");
        s1.executeUpdate();
        connection.commit();
    }

    /**
     * This method takes in a column number and maps it against column headers in Crime Table
     * & returns  the corresponding column name
     *
     * @param colId int type argument representing the column number
     * @return columnName String object that maps to the given column number
     */
    public static String getColName(int colId) {
        String columnName = "";
        switch (colId) {
            case 0:
                columnName = "ID";
                break;
            case 1:
                columnName = "DATE";
                break;
            case 2:
                columnName = "ADDRESS";
                break;
            case 3:
                columnName = "IUCR";
                break;
            case 4:
                columnName = "PRIMARYDESCRIPTION";
                break;
            case 5:
                columnName = "SECONDARYDESCRIPTION";
                break;
            case 6:
                columnName = "LOCATIONDESCRIPTION";
                break;
            case 7:
                columnName = "ARREST";
                break;
            case 8:
                columnName = "DOMESTIC";
                break;
            case 9:
                columnName = "BEAT";
                break;
            case 10:
                columnName = "WARD";
                break;
            case 11:
                columnName = "FBICD";
                break;
            case 12:
                columnName = "XCOORDINATE";
                break;
            case 13:
                columnName = "YCOORDINATE";
                break;
            case 14:
                columnName = "LATITUDE";
                break;
            case 15:
                columnName = "LONGITUDE";
                break;
            case 16:
                columnName = "LOCATION";
                break;
            default:
                break;
        }

        return columnName;
    }


    /**
     * Extracts and returns valued of a column from Crime database table
     *
     * @param columnName String object representing the column name that is to be returned
     * @return ColumnValues ArrayList<Object> type generated from reading column values
     * @throws SQLException
     */

    public static ArrayList<Object> extractCol(String columnName) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select " + columnName + " from CRIMES");
        ResultSet rs = s1.executeQuery();
        ArrayList<Object> columnValues = readColumnValues(rs, columnName);
        return columnValues;
    }

    /**
     * Extracts and returns valued of a column from Crime database table
     *
     * @param colNum String object representing the column name that is to be returned
     * @return ColumnValues ArrayList<Object> type generated from reading column values
     * @throws SQLException
     */

    public ArrayList<Object> extractCol(int colNum) throws SQLException {
        String columnName = getColName(colNum);
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select " + columnName + " from CRIMES");
        ResultSet rs = s1.executeQuery();
        ArrayList<Object> columnValues = readColumnValues(rs, columnName);
        return columnValues;
    }

    /**
     * Returns record objects based on the inputted search terms.
     *
     * @param column      String and has to match: ID, DATE, ADDRESS,IUCR, PRIMARYDESCRIPTION, SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST, DOMESTIC
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException
     */
    public ArrayList<Record> searchDB(String column, String searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select * from CRIMES where " + column + " = " + " \'" + searchValue + "\'");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }


    /**
     * Returns record objects based on the inputted search terms.
     *
     * @param column      String and has to match: BEAT,WARD,XCOORDINATE,YCOORDINATE
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException
     */
    public ArrayList<Record> searchDB(String column, int searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select * from CRIMES where " + column + " = " + " \'" + searchValue + "\'");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }

    /**
     * Returns record objects based on the inputted search terms.
     *
     * @param column      String and has to match: LATITUDE,LONGITUDE
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException
     */
    public ArrayList<Record> searchDB(String column, double searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("select * from CRIMES where " + column + " = " + " \'" + searchValue + "\'");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }

    /**
     * Returns whole database of record objects to pass to the tableviewer
     *
     * @return
     * @throws SQLException
     */
    public static ArrayList<Record> getAll() throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("SELECT * FROM CRIMES;");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }

    /**
     * Returns records from the database within the area of the location
     * Unsure of how to implement, would need the inverse haversine to find the maximum and minimum latitudes
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<Record> getLocationRange() throws SQLException {
        return null;
    }
    /**
     * Returns records between the two dates
     *
     * @return
     * @throws SQLException
     */
    public ArrayList<Record> getDateRange(String startDate, String endDate) throws SQLException, ParseException {
        long startUnix = unixTimeConvert(startDate);
        long endUnix = unixTimeConvert(endDate);
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("SELECT * FROM CRIMES WHERE UNIXTIME BETWEEN "+startUnix+" and "+ endUnix+";");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }
    /**
     * Generates an arraylist of records from a given resultset
     *
     * @param rs Resultset passed in from other methods
     * @return Arraylist of Records
     * @throws SQLException
     */
    public static ArrayList<Record> getRecord(ResultSet rs) throws SQLException {
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
            ArrayList<String> add = new ArrayList<String>(Arrays.asList(id, date, address, iucr, primary, secondary, locationDescription,
                    arrest, domestic, beat, ward, fbicd, xcoor, ycoor, lat, lon));
            Record r = new Record(add);
            records.add(r);
        }
        return records;
    }

    //Test with different filters
    public static ArrayList<Record> getFilter(Date startDate, Date endDate,ArrayList<String> crimeTypes,ArrayList<String> locDes,String ward,String beat,String lat,String lon,int radius,String arrest,String domestic) throws SQLException {
        connection.setAutoCommit(false);
        String SQLString = "SELECT * FROM CRIMES where (UNIXTIME >= 0) ";
        double radiusInDegrees = radius*(1/110.54);

        //Cycles through Crime Type values if not empty and appends to SQL string
        if(!crimeTypes.isEmpty()){
            for (int i = 0; i < crimeTypes.size(); i++){
                //Does not add the OR for the first one. Adds the IUCR values from the filter to the SQL statement
                if(i==0){
                    SQLString+="AND (PRIMARYDESCRIPTION='"+crimeTypes.get(i)+"' ";
                } else{
                    SQLString+="OR PRIMARYDESCRIPTION='"+crimeTypes.get(i)+"' ";
                }

            }
            //Appends parenthesis to group the SQL WHERE statements
            SQLString+=") ";
        }

        //Cycles through IUCR values if not empty and appends to SQL string
        if(!locDes.isEmpty()){
            for (int i = 0; i < locDes.size(); i++){
                //Does not add the OR for the first one. Adds the LOCATIONDESCRIPTION values from the filter to the SQL statement
                if(i==0){
                    SQLString+="AND (LOCATIONDESCRIPTION='"+locDes.get(i)+"' ";
                }else{
                    SQLString+="OR LOCATIONDESCRIPTION='"+locDes.get(i)+"' ";
                }
            }
            //Appends parenthesis to group the SQL WHERE statements
            SQLString+=") ";
        }
        if(startDate!=null&&endDate!=null) {
            SQLString += "AND (UNIXTIME BETWEEN " + startDate.getTime() + " AND " + endDate.getTime()+") ";
        }else if(startDate!=null){
            SQLString+="AND (UNIXTIME >= "+startDate.getTime()+") ";
        }else if(endDate!=null){
            SQLString+="AND (UNIXTIME <= "+endDate.getTime()+") ";
        }

        /***
         * NEEDS INPUT VALIDATION
        ***/
        if(ward!=null){
            SQLString+="AND (WARD="+ward+")";
        }
        if(beat!=null) {
            SQLString += "AND (BEAT=" + beat + ")";
        }
        if(radius==0) {
            if (lat != null&&lon != null) {
                SQLString += "AND (LATITUDE=" + lat + ")";
                SQLString += "AND (LONGITUDE=" + lon + ")";
            }
        }else{
            if (lat != null&&lon != null) {
                double latDouble = Double.parseDouble(lat);
                double lonDouble = Double.parseDouble(lon);
                SQLString += " AND (LATITUDE BETWEEN " + (latDouble - radiusInDegrees) + " AND " + (latDouble + radiusInDegrees) + ")";
                SQLString += " AND (LONGITUDE BETWEEN " + (lonDouble - radiusInDegrees) + " AND " + (lonDouble + radiusInDegrees) + ")";
            }
        }
        if(arrest!=null) {
            SQLString += "AND (ARREST='" + arrest + "')";
        }
        if(domestic!=null) {
            SQLString += "AND (DOMESTIC='" + domestic + "')";
        }

        System.out.println(SQLString);
        PreparedStatement s1 = connection.prepareStatement(SQLString);
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }

    /**
     * Reads column values from the provided ResultSet object
     *
     * @param rs     ResultSet object passed from other methods
     * @param column String object passed from other methods
     * @return colValues ArrayList<Object> generated from reading ResultSet object
     * @throws SQLException
     */
    public static ArrayList<Object> readColumnValues(ResultSet rs, String column) throws SQLException {
        ArrayList<Object> colValues = new ArrayList<>();

        while (rs.next()) {

            String value = valueOf(rs.getString(column));
            colValues.add(value);
        }
        return colValues;
    }

    /**
     * HELPER METHODS
     */

    /**
     * Parse in the date format from the csv and will convert the value to a UNIX time value. Used for calculating date ranges as SQLlite does not have
     * date data types so it needs the dates stored in unix time to calculate date ranges
     * @param date
     * @return
     * @throws ParseException
     */
    public long unixTimeConvert(String date) throws ParseException {
        Date d = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(date);
        return d.getTime();
    }

    /**
     * The method converts a date object to a unix time
     * @param d input date
     * @return a unix time
     * @throws ParseException
     */
    public long unixTimeConvert(Date d) throws ParseException {
        return d.getTime();
    }

}

