package backend;

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
    private static String databasePath = "./Files/crimeRecords.db"; //default path
    private DataAnalyser dataAnalyser = new DataAnalyser();
    private static List<String> columns = Arrays.asList("IUCR TEXT", "PRIMARYDESCRIPTION TEXT", "SECONDARYDESCRIPTION TEXT",
            "LOCATIONDESCRIPTION TEXT", "ARREST TEXT", "DOMESTIC TEXT", "BEAT INTEGER", "WARD INTEGER", "FBICD TEXT",
            "XCOORDINATE INTEGER", "YCOORDINATE INTEGER", "LATITUDE REAL", "LONGITUDE REAL", "UNIXTIME REAL");


    public Database() {
        try {
            connectDatabase();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the static field databasePath to the given path so when the database in the new path
     * will be accessed when creating a database connection
     * @param databasePath a path to a database file
     */
    public void setDatabasePath(String databasePath) {
        this.databasePath = databasePath;
    }

    /**
     * Gets connection to the database and then calls the create table function. Used when creating a database object
     * and when the database path is changed
     * @throws SQLException
     */
    public void connectDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ databasePath);
            createTable();
        } catch (Exception e) {}
    }

    /**
     * Closes the database connection. This is used to prevent database locked errors from open connections
     * @throws SQLException
     */
    public void disconnectDatabase() throws SQLException {
        connection.close();
    }

    /**
     * Deletes every row in the given table in the database
     * @param tableName the name of the table for the records to be deleted from
     * @throws SQLException
     */
    public void deleteTable(String tableName) throws SQLException {
        Statement state = connection.createStatement();
        state.execute("DELETE FROM "+tableName);
        createTable();
    }

    /**
     * Creates the main table in the database which stores the records and adds the columns listed in the columns field to it
     * @throws SQLException
     */
    public void createTable() throws SQLException {

        //Formats and executes SQL to create the original table
        Statement createTable = connection.createStatement();
        createTable.execute("CREATE TABLE CRIMES " + "(ID TEXT PRIMARY KEY NOT NULL," +
                "DATE TEXT, " +
                "ADDRESS TEXT)");

        //Goes through the columns field and adds the columns to the list to the database
        for (int i = 0; i < columns.size(); i++) {
            Statement createColumns = connection.createStatement();
            createColumns.execute("ALTER TABLE CRIMES\n" +
                    "ADD COLUMN " + columns.get(i) + ";");
        }
    }


    /**
     * Gets an arrayList of string Lists and adds them to the database. Any empty values are entered as NULL type
     *
     * @param inputs an Arraylist of Lists of Strings that is passed into it from the CSV Reader
     * @throws SQLException
     */
    public void insertRows(ArrayList<List<String>> inputs) throws SQLException, ParseException {

        //Stops from executing SQL if the input list is empty
        if(inputs==null){
            return;
        }

        //Creates the statement to be run
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement(
                "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,UNIXTIME) " +
                        "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

        //Inserts the values in the List of strings into the preparedstatement
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
            if (c14.equals("") || c14.equals("null")) {
                s1.setString(10, "NULL");
            } else {
                s1.setFloat(15, Float.parseFloat(c14));
            }
            String c15 = (String) column.get(15);
            if (c15.equals("") || c15.equals("null")) {
                s1.setString(10, "NULL");
            } else {
                s1.setFloat(16, Float.parseFloat(c15));
            }

            //Batching reduces importing times
            s1.addBatch();
        }

        //Executes the batched prepared statement
        s1.executeBatch();
        connection.commit();

    }

    /**
     * Deletes current table, then gets an arrayList of string Lists and adds them to the database. Any empty values are entered as NULL type
     *
     * @param inputs an Arraylist of Lists of Strings that is passed into it from the CSV Reader
     * @throws SQLException
     */
    public void replaceRows(ArrayList<List<String>> inputs) throws SQLException, ParseException {

        //Creates the statement to be run
        connection.setAutoCommit(false);
        PreparedStatement s = connection.prepareStatement("DELETE FROM CRIMES");
        PreparedStatement s1 = connection.prepareStatement(
                "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,UNIXTIME) " +
                        "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

        //Inserts the values in the List of strings into the preparedstatement
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

            //Batching reduces importing times
            s1.addBatch();
        }
        //Executes the table deletion then executes prepared statement to import the new rows
        s.execute();
        s1.executeBatch();
        connection.commit();

    }


    /**
     * Adds the record in the parameter to the database. It adds it to an arraylist of lists of strings so it can be parsed into the previously
     *      * made insertRows() method and reuse that code
     *
     * @param rec Record class object to be added to the database
     * @throws SQLException if a database error occurs
     */
    public void manualAdd(Record rec) throws SQLException, ParseException {

        //Converts record to list
        List<String> recList = rec.toList();

        //Adds it to an arraylist so it can be passed to insertRows(ArrayList<List<String>>) method
        ArrayList<List<String>> input = new ArrayList<>();
        input.add(rec.toList());

        //Calls insertRows method on the arraylist to add the row to the database
        insertRows(input);
    }

    /**
     * Pass in a record class and it will delete the previous row of it in the database and
     * add the new record class to simulate updating the row in the database.
     * It adds it to an arraylist of lists of strings so it can be parsed into the previously
     * made insertRows() method and reuse that code
     *
     * @param rec Record class object to be updated in the database
     * @throws SQLException
     */
    public void manualUpdate(Record rec) throws SQLException, ParseException {

        //Converts record to list
        List<String> recList = rec.toList();

        //Adds it to an arraylist, so it can be passed to insertRows(ArrayList<List<String>>) method
        ArrayList<List<String>> input = new ArrayList<>();
        input.add(recList);

        //Deletes outdated entry by calling delete on index 0 of the list, which is the casenumber
        manualDelete(recList.get(0));

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
        return readColumnValues(rs, columnName);
    }


    /**
     * Returns record objects from the database whose column matches the search value. Searches for strings
     *
     * @param column      String of the database column to query and has to match: ID, DATE, ADDRESS,IUCR, PRIMARYDESCRIPTION, SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST, DOMESTIC
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
     * Returns record objects from the database whose column matches the search value. Searches for integers
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
     * Returns record objects from the database whose column matches the search value. Searches for doubles
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
     * @return arraylist of all the records in the database
     * @throws SQLException
     */
    public static ArrayList<Record> getAll() throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement s1 = connection.prepareStatement("SELECT * FROM CRIMES;");
        ResultSet rs = s1.executeQuery();
        return getRecord(rs);
    }


    /**
     * Returns an arraylist of records between the two dates
     * @param startDate date to start the query on
     * @param endDate date to end the query on
     * @return arraylist of records
     * @throws SQLException
     * @throws ParseException
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
     * Generates an arraylist of records from a given resultset. If given a radius it will return records in the area
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

    /**
     * Returns an arraylist of records from the database which matches the values parsed in from the filter in MainController
     * @param caseNumber
     * @param startDate
     * @param endDate
     * @param crimeTypes
     * @param locDes
     * @param ward
     * @param beat
     * @param lat
     * @param lon
     * @param radius
     * @param arrest
     * @param domestic
     * @return an arraylist of records
     * @throws SQLException
     */
    public ArrayList<Record> getFilter(String caseNumber, Date startDate, Date endDate,ArrayList<String> crimeTypes,ArrayList<String> locDes,String ward,String beat,String lat,String lon,int radius,String arrest,String domestic) throws SQLException {
        connection.setAutoCommit(false);
        String SQLString = "SELECT * FROM CRIMES where (UNIXTIME >= 0) ";

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

        //Applies the dates in the filter to a SQL query
        if(startDate!=null&&endDate!=null) {
            SQLString += "AND (UNIXTIME BETWEEN " + startDate.getTime() + " AND " + endDate.getTime()+") ";
        }else if(startDate!=null){
            SQLString+="AND (UNIXTIME >= "+startDate.getTime()+") ";
        }else if(endDate!=null){
            SQLString+="AND (UNIXTIME <= "+endDate.getTime()+") ";
        }

        //Appends string values to the SQl query if they are not null
        if(ward!=null){
            SQLString+="AND (WARD="+ward+")";
        }
        if(beat!=null) {
            SQLString += "AND (BEAT=" + beat + ")";
        }
        if(arrest!=null) {
            SQLString += "AND (ARREST='" + arrest + "')";
        }
        if(domestic!=null) {
            SQLString += "AND (DOMESTIC='" + domestic + "')";
        }
        if(caseNumber!=null) {
            SQLString += "AND (ID LIKE '%" + caseNumber + "%')";
        }

        PreparedStatement s1 = connection.prepareStatement(SQLString);
        ResultSet rs = s1.executeQuery();
        ArrayList<Record> recordsFromDBQuery =  getRecord(rs);
        ArrayList<Record> resultRecords = new ArrayList<>();

        //Adds records if they are within the radius of the location passed in from the filter
        if (lat != null&&lon != null && radius != 0) {
            double latDouble = Double.parseDouble(lat);
            double lonDouble = Double.parseDouble(lon);
            for (Record record : recordsFromDBQuery) {

                //Calculates the distance and adds if it is less than the radius in the filter
                if (dataAnalyser.calculateLocationDifferenceMeters(latDouble, lonDouble, record.getLatitude(), record.getLongitude()) <= radius) {
                    resultRecords.add(record);
                }
            }
        } else {
            resultRecords = recordsFromDBQuery;
        }
        return resultRecords;
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
     * @param date a string of the date (the same format as the dates in the database)
     * @return
     * @throws ParseException
     */
    public static long unixTimeConvert(String date) throws ParseException {
        Date d = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(date);
        return d.getTime();
    }

    /**
     * The method converts a date object to a unix time
     * @param d input date
     * @return a unix time
     * @throws ParseException
     */
    public static long unixTimeConvert(Date d) throws ParseException {
        return d.getTime();
    }


}

