package data;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static java.lang.String.valueOf;

/**
 * TODO
 * @author
 * Date 09/10/2021
 */
public class CrimeDatabase {
    private static Connection connection;
    private static String databasePath = "./Files/crimeRecords.db"; //default path
    private final DataAnalyser dataAnalyser = new DataAnalyser();


    /**
     * Creates new database object and establishes a connection to the database file
     */
    public CrimeDatabase() {
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
        CrimeDatabase.databasePath = databasePath;
    }

    /**
     * Gets the path to the current database
     * @return String object containing the path to the current database
     */
    public String getDatabasePath() { return databasePath; }

    /**
     * Gets connection to the database (or creates new database if none exists at the specified path) and then calls the create table function.
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void connectDatabase() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:"+ databasePath);
            createTable();
        } catch (Exception ignored) {}
    }

    /**
     * Closes the database connection. This is used to prevent database locked errors from open connections
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void disconnectDatabase() throws SQLException {

            connection.close();

    }

    /**
     * Removes all rows but keeps database structure
     * @param tableName the name of the table for the records to be deleted from
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void deleteTable(String tableName) throws SQLException {
        Statement statement = connection.createStatement();
        statement.execute("DELETE FROM "+tableName);

        statement.close();

    }

    /**
     * Creates the main table in the database which stores the records and adds the columns listed in the columns field to it
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void createTable() throws SQLException {

        //Formats and executes SQL to create the original table
        Statement statement = connection.createStatement();
        statement.execute("CREATE TABLE CRIMES  " + "(ID TEXT PRIMARY KEY NOT NULL," +
                "DATE TEXT, " +
                "ADDRESS TEXT)");

        statement.close();
        //Goes through the columns list and adds the columns to the database
        List<String> COLUMNS = Arrays.asList("IUCR TEXT", "PRIMARYDESCRIPTION TEXT", "SECONDARYDESCRIPTION TEXT",
                "LOCATIONDESCRIPTION TEXT", "ARREST TEXT", "DOMESTIC TEXT", "BEAT INTEGER", "WARD INTEGER", "FBICD TEXT",
                "XCOORDINATE INTEGER", "YCOORDINATE INTEGER", "LATITUDE REAL", "LONGITUDE REAL", "UNIXTIME REAL");
        for (String column : COLUMNS) {
            Statement statement1 = connection.createStatement();
            statement1.execute("ALTER TABLE CRIMES\n" +
                    "ADD COLUMN " + column + ";");
            statement1.close();
        }
    }


    /**
     * Uses SQL Pragma to get the column names from the current database. It then goes through the returned column names and combines them into
     * a string actualColumnFormat. This string is the column names appended to each other in order from left to right. If the column names
     * and the order are correct it will match the validColumnFormat string. If they do not match the database is invalid.
     * Will return invalid if the table name is incorrect
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public Boolean checkValidDB() throws SQLException {

        //Creates a string of the expected column names and order to match
        String validColumnFormat = "IDDATEADDRESSIUCRPRIMARYDESCRIPTIONSECONDARYDESCRIPTIONLOCATIONDESCRIPTIONARRESTDOMESTICBEATWARDFBICDXCOORDINATEYCOORDINATELATITUDELONGITUDEUNIXTIME";
        String actualColumnFormat = "";

        //SQL Query returns column names of the DB. Will be empty if table name incorrect
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement("PRAGMA table_info('CRIMES');");
        ResultSet columns = statement.executeQuery();

        //Goes through column names in the resultset and appends them to the string actualColumnFormat
        while (columns.next()) {
            actualColumnFormat += columns.getString("name");
        }

        //close connections
        columns.close();
        statement.close();

        //Matches the expected and actual column names to see if table valid
        return validColumnFormat.equals(actualColumnFormat);
    }

    /**
     * Gets an arrayList of string Lists and adds them to the database
     *
     * @param inputs an Arraylist of Lists of Strings of crime data
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void insertRows(List<List<String>> inputs) throws SQLException, ParseException {

        //Stops from executing SQL if the input list is empty
        if(inputs==null){
            return;
        }

        //Creates the prepared statement
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement(
                "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,UNIXTIME) " +
                        "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");


        //Goes through each list of strings and inserts the values into the prepared statement to their respective places
        for (List column : inputs) {
            statement.setString(1, (String) column.get(0)); //Sets the id
            statement.setString(2, (String) column.get(1)); //Sets the date
            statement.setLong(17,  unixTimeConvert((String) column.get(1))); //Sets the unixtime
            statement.setString(3, (String) column.get(2)); //Sets the address
            statement.setString(4, (String) column.get(3)); //Sets the iucr
            statement.setString(5, (String) column.get(4)); //Sets the primary description
            statement.setString(6, (String) column.get(5)); //Sets the secondary description
            statement.setString(7, (String) column.get(6)); //Sets the location description
            statement.setString(8, (String) column.get(7)); //Sets the arrest
            statement.setString(9, (String) column.get(8)); //Sets the domestic


            //Sets beat to null if empty, otherwise parses the integer
            String c9 = (String) column.get(9); //Sets the beat
            if (c9.equals("")) {
                statement.setString(10, "NULL");
            } else {
                statement.setInt(10, Integer.parseInt(c9));
            }

            //Sets ward to null if empty, otherwise parses the integer
            String c10 = (String) column.get(10);//Sets the beat
            if (c10.equals("")) {
                statement.setString(11, "NULL");
            } else {
                statement.setInt(11, Integer.parseInt(c10));
            }

            //Sets the FBICD
            statement.setString(12, (String) column.get(11));

            //Sets xcoordinate to null if empty, otherwise parses the integer
            String c12 = (String) column.get(12);
            if (c12.equals("")) {
                statement.setString(10, "NULL");
            } else {
                statement.setInt(13, Integer.parseInt(c12));
            }

            //Sets xcoordinate to null if empty, otherwise parses the integer
            String c13 = (String) column.get(13);
            if (c13.equals("")) {
                statement.setString(10, "NULL");
            } else {
                statement.setInt(14, Integer.parseInt(c13));
            }

            //Sets latitude to null if empty, otherwise parses the float
            String c14 = (String) column.get(14);
            if (c14.equals("") || c14.equals("null")) {
                statement.setString(10, "NULL");
            } else {
                statement.setFloat(15, Float.parseFloat(c14));
            }

            //Sets longitude to null if empty, otherwise parses the float
            String c15 = (String) column.get(15);
            if (c15.equals("") || c15.equals("null")) {
                statement.setString(10, "NULL");
            } else {
                statement.setFloat(16, Float.parseFloat(c15));
            }

            //Adds statement to batch
            statement.addBatch();
        }

        //Executes the batched prepared statement
        statement.executeBatch();
        connection.commit();
        statement.close();

    }



    /**
     * Deletes current table, then gets an arrayList of string Lists and adds them to the database. Any empty values are entered as NULL type
     *
     * @param inputs an Arraylist of Lists of Strings that is passed into it from the CSV Reader
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void replaceRows(List<List<String>> inputs) throws SQLException, ParseException {

        //Creates the statement to be run
        connection.setAutoCommit(false);
        PreparedStatement deleteStatement = connection.prepareStatement("DELETE FROM CRIMES");
        PreparedStatement insertStatement = connection.prepareStatement(
                "INSERT OR IGNORE INTO CRIMES (ID, DATE, ADDRESS,IUCR,PRIMARYDESCRIPTION,SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST,DOMESTIC,BEAT,WARD,FBICD,XCOORDINATE,YCOORDINATE,LATITUDE,LONGITUDE,UNIXTIME) " +
                        "VALUES (?, ?, ?,?,?,?,?,?,?,?,?,?,?,?,?,?,?);");

        //Inserts the values in the List of strings into the prepared statement
        for (List column : inputs) {
            insertStatement.setString(1, (String) column.get(0)); //Sets the id
            insertStatement.setString(2, (String) column.get(1)); //Sets the date
            insertStatement.setLong(17,  unixTimeConvert((String) column.get(1))); //Sets the unixtime
            insertStatement.setString(3, (String) column.get(2)); //Sets the address
            insertStatement.setString(4, (String) column.get(3)); //Sets the iucr
            insertStatement.setString(5, (String) column.get(4)); //Sets the primary description
            insertStatement.setString(6, (String) column.get(5)); //Sets the secondary description
            insertStatement.setString(7, (String) column.get(6)); //Sets the location description
            insertStatement.setString(8, (String) column.get(7)); //Sets the arrest
            insertStatement.setString(9, (String) column.get(8)); //Sets the domestic


            //Sets beat to null if empty, otherwise parses the integer
            String c9 = (String) column.get(9); //Sets the beat
            if (c9.equals("")) {
                insertStatement.setString(10, "NULL");
            } else {
                insertStatement.setInt(10, Integer.parseInt(c9));
            }

            //Sets ward to null if empty, otherwise parses the integer
            String c10 = (String) column.get(10);//Sets the beat
            if (c10.equals("")) {
                insertStatement.setString(11, "NULL");
            } else {
                insertStatement.setInt(11, Integer.parseInt(c10));
            }

            //Sets the FBICD
            insertStatement.setString(12, (String) column.get(11));

            //Sets xcoordinate to null if empty, otherwise parses the integer
            String c12 = (String) column.get(12);
            if (c12.equals("")) {
                insertStatement.setString(10, "NULL");
            } else {
                insertStatement.setInt(13, Integer.parseInt(c12));
            }

            //Sets xcoordinate to null if empty, otherwise parses the integer
            String c13 = (String) column.get(13);
            if (c13.equals("")) {
                insertStatement.setString(10, "NULL");
            } else {
                insertStatement.setInt(14, Integer.parseInt(c13));
            }

            //Sets latitude to null if empty, otherwise parses the float
            String c14 = (String) column.get(14);
            if (c14.equals("") || c14.equals("null")) {
                insertStatement.setString(10, "NULL");
            } else {
                insertStatement.setFloat(15, Float.parseFloat(c14));
            }

            //Sets longitude to null if empty, otherwise parses the float
            String c15 = (String) column.get(15);
            if (c15.equals("") || c15.equals("null")) {
                insertStatement.setString(10, "NULL");
            } else {
                insertStatement.setFloat(16, Float.parseFloat(c15));
            }

            //Batching reduces importing times
            insertStatement.addBatch();
        }
        //Executes the table deletion then executes prepared statement to import the new rows
        deleteStatement.execute();
        insertStatement.executeBatch();
        connection.commit();
        deleteStatement.close();
        insertStatement.close();

    }


    /**
     * Adds the record in the parameter to the database. It adds it to an arraylist of lists of strings so it can be parsed into the previously
     *      * made insertRows() method and reuse that code
     *
     * @param rec Record class object to be added to the database
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void manualAdd(Record rec) throws SQLException, ParseException {

        //Adds record to an arraylist in string format, so it can be passed to insertRows method
        ArrayList<List<String>> input = new ArrayList<>();
        input.add(rec.toList());

        //Adds the row to the database
        insertRows(input);
    }

    /**
     * Pass in a record class and it will delete the previous row of it in the database and
     * add the new record class to simulate updating the row in the database.
     * It adds it to an arraylist of lists of strings so it can be parsed into the previously
     * made insertRows() method and reuse that code
     *
     * @param rec Record class object to be updated in the database
     * @throws SQLException If an exception occurs when executing the SQL statement
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
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public void manualDelete(String caseNum) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement("delete from CRIMES where ID = " + " '" + caseNum + "'");
        statement.executeUpdate();
        connection.commit();
        statement.close();
    }


    /**
     * Extracts and returns valued of a column from Crime database table
     *
     * @param columnName String object representing the column name that is to be returned
     * @return ColumnValues ArrayList<Object> type generated from reading column values
     * @throws SQLException If an exception occurs when executing the SQL statement
     */

    public static List<Object> extractCol(String columnName) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement("select " + columnName + " from CRIMES");
        ResultSet rs = statement.executeQuery();
        List<Object> results = readColumnValues(rs, columnName);

        rs.close();
        statement.close();
        return results;
    }


    /**
     * Returns record objects from the database whose column matches the search value. Searches for strings
     *
     * @param column      String of the database column to query and has to match: ID, DATE, ADDRESS,IUCR, PRIMARYDESCRIPTION, SECONDARYDESCRIPTION,LOCATIONDESCRIPTION,ARREST, DOMESTIC
     * @param searchValue the value you are searching for
     * @return an Arraylist of Record Objects
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public List<Record> searchDB(String column, String searchValue) throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement("select * from CRIMES where " + column + " = " + " '" + searchValue + "'");
        ResultSet rs = statement.executeQuery();

        List<Record> results = getRecord(rs);
        rs.close();
        statement.close();
        return results;
    }


    /**
     * Returns whole database of record objects to pass to the tableviewer
     *
     * @return arraylist of all the records in the database
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public static List<Record> getAll() throws SQLException {
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM CRIMES;");
        ResultSet rs = statement.executeQuery();

        List<Record> results = getRecord(rs);
        rs.close();
        statement.close();
        return results;
    }


    /**
     * Returns an arraylist of records between the two dates
     * @param startDate date to start the query on
     * @param endDate date to end the query on
     * @return arraylist of records
     * @throws SQLException If an exception occurs when executing the SQL statement
     * @throws ParseException If an exception occurs when converting the string to a unix time
     */
    public List<Record> getDateRange(String startDate, String endDate) throws SQLException, ParseException {
        long startUnix = unixTimeConvert(startDate);
        long endUnix = unixTimeConvert(endDate);
        connection.setAutoCommit(false);
        PreparedStatement statement = connection.prepareStatement("SELECT * FROM CRIMES WHERE UNIXTIME BETWEEN "+startUnix+" and "+ endUnix+";");
        ResultSet rs = statement.executeQuery();

        List<Record> results = getRecord(rs);
        rs.close();
        statement.close();
        return results;
    }

    /**
     * Generates an arraylist of records from a given resultset. If given a radius it will return records in the area
     *
     * @param rs Resultset passed in from other methods
     * @return Arraylist of Records
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public static List<Record> getRecord(ResultSet rs) throws SQLException {
        ArrayList<Record> records = new ArrayList<>();

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
     * @param caseNumber String data type that represents crime case number
     * @param startDate Date data type that represents start date
     * @param endDate   Data data type that represents end date
     * @param crimeTypes String data type that represents crime type
     * @param locDes String data type that represents Location description
     * @param ward int data type representing ward
     * @param beat int data type representing ward
     * @param lat Float data type representing latitude coordinate of a location
     * @param lon Float data type representing longitude coordinate of a location
     * @param radius
     * @param arrest
     * @param domestic
     * @return an arraylist of records
     * @throws SQLException If an exception occurs when executing the SQL statement
     */
    public List<Record> getFilter(String caseNumber, Date startDate, Date endDate,List<String> crimeTypes,List<String> locDes,String ward,String beat,String lat,String lon,int radius,String arrest,String domestic) throws SQLException {
        connection.setAutoCommit(false);
        String SQLString = "SELECT * FROM CRIMES where true ";

        //Cycles through Crime Type values if not empty and appends to SQL string
        if(!crimeTypes.isEmpty()){
            for (int i = 0; i < crimeTypes.size(); i++){
                //Does not add the OR for the first one. Adds the IUCR values from the filter to the SQL statement
                if(i==0){
                    SQLString +="AND (PRIMARYDESCRIPTION='"+crimeTypes.get(i)+"' ";
                } else{
                    SQLString +="OR PRIMARYDESCRIPTION='"+crimeTypes.get(i)+"' ";
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
                    SQLString +="AND (LOCATIONDESCRIPTION='"+locDes.get(i)+"' ";
                }else{
                    SQLString +="OR LOCATIONDESCRIPTION='"+locDes.get(i)+"' ";
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
        PreparedStatement statement = connection.prepareStatement(SQLString);
        ResultSet rs = statement.executeQuery();
        List<Record> recordsFromDBQuery =  getRecord(rs);
        List<Record> resultRecords = new ArrayList<>();

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

        //close connections
        statement.close();
        rs.close();

        return resultRecords;
    }

    /**
     * Reads column values from the provided ResultSet object
     *
     * @param rs     ResultSet object passed from other methods
     * @param column String object passed from other methods
     * @return colValues ArrayList<Object> generated from reading ResultSet object
     * @throws SQLException If an exception occurs when executing the SQL statement
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
     * @throws ParseException If an exception occurs when converting the string to a unix time
     */
    public static long unixTimeConvert(String date) throws ParseException {
        Date d = new SimpleDateFormat("MM/dd/yyyy h:mm:ss a").parse(date);
        return d.getTime();
    }

    /**
     * The method converts a date object to a unix time
     * @param d input date
     * @return a unix time
     * @throws ParseException If an exception occurs when converting the string to a unix time
     */
    public static long unixTimeConvert(Date d) {
        return d.getTime();
    }

    /**
     * Returns the number of rows in the database
     * @return String of the number of rows
     */
    public String getNumRows(){
        try (PreparedStatement stmt = connection.prepareStatement("SELECT count(*) AS ROWS FROM CRIMES;")) {
            try (ResultSet rs = stmt.executeQuery()) {

                String numRows = rs.getString("ROWS");
                return numRows;
            }
        } catch (SQLException throwables) {

        }
        return "";
    }

}

