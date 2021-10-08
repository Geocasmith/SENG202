package backend;

class dataBaseValidTest {
    //    @BeforeEach
//    public void init() throws SQLException, CsvValidationException, IOException, ParseException {
//        Database db = new Database();
//        db.connectDatabase();
//        db.insertRows(CsvReader.read("./Files/smallTest.csv"));
//        db.disconnectDatabase();
//    }
//    @Test
//    void validDB() throws SQLException, CsvValidationException, IOException, ParseException {
//    //Sets up new database
//    Database db = new Database();
//    db.connectDatabase();
//    db.dropTable("CRIMES");
//    db.createTable();
//    db.insertRows(CsvReader.read("./test/Files/smallTest.csv"));
//
//
//    assert (db.checkValidDB());
//    db.disconnectDatabase();
//}
//    @Test
//    void invalidDB() throws SQLException, CsvValidationException, IOException, ParseException {
//        //Sets up new database
//        Database db = new Database();
//        db.connectDatabase();
//        db.dropTable("CRIMES");
//        db.createTable();
//        db.insertRows(CsvReader.read("./test/Files/smallTest.csv"));
//
//        db.executeQuery("ALTER TABLE CRIMES DROP DATE;");
//
//        assert (!db.checkValidDB());
//        db.disconnectDatabase();
//    }
}