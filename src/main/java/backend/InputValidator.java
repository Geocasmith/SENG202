package backend;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

public class InputValidator {

   private static Set<List<String>> crimeDescription;
   private static int LONGITUDEUPPERBOUND = 180;
   private static int LONGITUDELOWERBOUND = -180;
   private static int LATITUDEUPPERBOUND = 90;
   private static int LATITUDELOWERBOUND = -90;

   /**
    * Initializes set of valid crime descriptions (Primary, Secondary and IUCR information from
    * a given CSV file
    * @param filename (file where crime descriptions live)
    * @throws CsvValidationException
    * @throws IOException
    */
   public static Set<List<String>>  initializeCrimeDescriptions(String filename) throws CsvValidationException, IOException {
      Set<List<String>> descriptions = new HashSet<>();
      CsvReader reader = new CsvReader();
      ArrayList<List<String>> readData;
      readData = reader.read(filename);
      for (List<String> row : readData) {
         descriptions.add(row);
      }
      return descriptions;

   }

   /**
    * Checks the given List of Strings has valid crime descriptions
    * @param candidateCrimeDescriptions
    * @return
    */
   public static boolean hasValidCrimeDescriptions(List<String> candidateCrimeDescriptions) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");

      if (crimeDescription.contains(candidateCrimeDescriptions)) {
         return true;
      }
      return false;
   }

   /**
    * Checks if the given coordinate is valid based on the lower range and upper range parameters provided
    * @param gpsCoordinate
    * @param lowerRange
    * @param upperRange
    * @return true or false depending on the validity of the coordinate
    * @throws IOException
    */
   public static boolean hasGpsCoordinate(String gpsCoordinate, float lowerRange, float upperRange) {
      if (gpsCoordinate.isEmpty()) {
         return true;
      }
      else {
         try {
            float floatValue;
            floatValue = Float.parseFloat(gpsCoordinate);
            if ((lowerRange <= floatValue) && (floatValue <= upperRange)) {
               return true;
            }
         } catch (Exception e) {
            return false;
         }
         return false;
      }

   }

   /**
    * Checks whether the string provided is a valid integer value.
    * @param data the String to be checked
    * @param empty whether or not the number can empty
    * @return true or false
    */
   public static boolean hasValidInt(String data, boolean empty)
   {
      if (empty && data.isEmpty()) {
         return true;
      }
      else {
         try {
            Integer.parseInt(data);
            return true;
         } catch (Exception e) {
            return false;
         }
      }
   }

   /**
    * Checks whether the string provided is a valid double value.
    * @param data the String to be checked
    * @return true or false
    */
   public static boolean hasValidDouble(String data)
   {
      try {
         Double.parseDouble(data);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /**
    * Checks whether the string is a true, false, or null value for a field as specified in the Record class.
    * TODO: check how the user might add null values
    * @param domestic a String representing a potential boolean value
    * @return boolean true/false whether the string is valid or not
    */
   public static boolean hasValidBooleanData(String domestic)
   {
      return (Record.trueStrings.contains(domestic.toLowerCase()) ||
              Record.falseStrings.contains(domestic.toLowerCase()) ||
              domestic == "null");
   }

   public static ArrayList<String> recordEntryFeedbackLong(List<String> record) throws CsvValidationException, IOException {
      // will have 1 = good, 0 = bad for all rows and then a 1 or 0 at the end along with the first error message
      ArrayList<String> result = new ArrayList<>(Arrays.asList("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"));
      ArrayList<String> dataFieldFeedBack = new ArrayList<>();
      String errMsg = "";
      String isValid = "1";

      if (!hasValidCaseNumber(record.get(0))) {
         result.set(0, "0");
         isValid = "0";
         errMsg = "Invalid case number. The case number should be two letters followed by six digits.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasValidDateAndTimeFormat(record.get(1))) {
         result.set(1, "0");
         isValid = "0";
         errMsg = "Invalid date and time. The correct format is mm/dd/yyyy hh:mm:ss am/pm.";
         dataFieldFeedBack.add(errMsg);
      }

      // block is not validated
      if (record.get(2) == null || record.get(2).isEmpty()) {
         result.set(2, "0");
         isValid = "0";
         errMsg = "Block is a required field.";
         dataFieldFeedBack.add(errMsg);
      }

      // check IUCR, primary and secondary descriptions, and FBICD
      List<String> crimeDes = Arrays.asList(record.get(3), record.get(4), record.get(5), record.get(11));
      if (!hasValidCrimeDescriptions(crimeDes)) {
         result.set(3, "0");
         result.set(4, "0");
         result.set(5, "0");
         result.set(11, "0");
         isValid = "0";
         errMsg = "Invalid crime description, FBICD and/or IUCR. These fields need to correspond to one another.";
         dataFieldFeedBack.add(errMsg);
      }

      // location description is not validated
      if (record.get(6) == null || record.get(6).isEmpty()) {
         result.set(6, "0");
         isValid = "0";
         errMsg = "Location description is a required field.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasValidBooleanData(record.get(7))) {
         result.set(7, "0");
         isValid = "0";
         errMsg = "Invalid arrest value. Arrest should be either Y or N.";
         dataFieldFeedBack.add(errMsg);
      }
      if (!hasValidBooleanData(record.get(8))) {
         result.set(8, "0");
         isValid = "0";
         errMsg = "Invalid domestic value. Domestic should be either Y or N.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasValidInt(record.get(9), false)) {
         result.set(9, "0");
         isValid = "0";
         errMsg = "Invalid beat value. Beat should be an integer.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasValidInt(record.get(10), false)) {
         result.set(10, "0");
         isValid = "0";
         errMsg = "Invalid ward value. Ward should be an integer.";
         dataFieldFeedBack.add(errMsg);
      }

      // FBICD already validated

      if (record.get(12).length() > 0 && !hasValidInt(record.get(12), true)) {
         result.set(12, "0");
         isValid = "0";
         errMsg = "Invalid X-Coordinate value. X-Coordinate should be an integer or empty.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasValidInt(record.get(13), true)) {
         result.set(13, "0");
         isValid = "0";
         errMsg = "Invalid Y-Coordinate value. Y-Coordinate should be an integer or empty.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasGpsCoordinate(record.get(14), LATITUDELOWERBOUND, LATITUDEUPPERBOUND)) {
         result.set(14, "0");
         isValid = "0";
         errMsg ="Invalid latitude value. Latitude should be between 90 and -90 or empty.";
         dataFieldFeedBack.add(errMsg);
      }

      if (!hasGpsCoordinate(record.get(15), LONGITUDELOWERBOUND, LONGITUDEUPPERBOUND)) {
         result.set(15, "0");
         isValid = "0";
         errMsg = "Invalid longitude value. Longitude should be between 180 and -180 or empty.";
         dataFieldFeedBack.add(errMsg);
      }
      result.add(isValid);
      if (dataFieldFeedBack.size() > 0) {
         result.add(dataFieldFeedBack.get(0));
      }

      return result;
   }

   /**
    * Calls several methods of InputValidator class on the record parameter to make sure that the given record
    * is a valid record
    * @param record
    * @return boolean value depending on the validity of the record
    * @throws IOException
    */


   public static boolean isValidRecord(List<String> record) throws IOException, CsvValidationException {
      if (recordEntryFeedbackLong(record).get(16) == "1") {
         return true;
      }
      return false;
   }



   /**
    * Prints valid list of crime descriptions
    * @throws CsvValidationException
    * @throws IOException
    */
   public static void printCrimeDescriptions() throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");

      for (List<String> row : crimeDescription)
      {
         System.out.println(row.get(0) + ", " + row.get(1) + ", " + row.get(2) + ", " + row.get(3));
      }
   }


   /**
    * Checks if case number has a valid length (eight), The first two starting characters are letters
    * and the rest of the characters are digits (No special character is allowed)
    * @param caseNumber
    * @return
    */
   public static boolean hasValidCaseNumber(String caseNumber) {
      if ( caseNumber.length() == 8 ) {
         if (Character.isAlphabetic(caseNumber.charAt(0)) && Character.isAlphabetic(caseNumber.charAt(1))) {
            String substring = caseNumber.substring(2);
            int count = 0;
            for (int i = 2; i <= 7; i ++) {
               if (Character.isDigit(caseNumber.charAt(i))) {
                  count++;
               }
            }
            return count == 6;
         }
      }
      return false;
   }


   /**
    * Checks input for valid Date and Time format
    * @param dateAndTime
    * @return true or false depending on validity of the passed parameter
    * @throws IOException
    */
   public static boolean hasValidDateAndTimeFormat(String dateAndTime) {
      try {
         LocalDateTime.parse(dateAndTime.toUpperCase(), Record.formatter);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /**
    * Initializes Set of list of valid crime descriptions and returns in the order of (IUCR number,
    * Primary Description, Secondary Description and FBICD)
    * @return Set of list of Crime Description
    * @throws CsvValidationException
    * @throws IOException
    */
   public static Set<List<String>> getCrimeDescriptions() throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");
      return crimeDescription;
   }


   /**
    * Initializes crimeDescription, selects and returns set of primary description from the list of
    * the initialized crime descriptions
    * @return Set of Strings (Primary Description)
    * @throws CsvValidationException
    * @throws IOException
    */
   public static List<String> getSetOfPrimaryDescriptions() throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");
      Set<String> primaryDes = new HashSet<>();
      for (List<String> descriptions : crimeDescription) {
         primaryDes.add(descriptions.get(1));
      }
      List<String> primaryDesList = new ArrayList<>();
      primaryDesList.addAll(primaryDes);
      return primaryDesList;
   }


   /**
    * Filters and returns set of Secondary descriptions that relate to the passed primary description parameter
    * @param primaryDescription
    * @return
    * @throws CsvValidationException
    * @throws IOException
    */
   public static Set<String> getSetOfSecondaryDescriptions(String primaryDescription) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");
      Set<String> secondaryDescription = new HashSet<>();
      if (getSetOfPrimaryDescriptions().contains(primaryDescription)) {
         for (List<String> descriptions : crimeDescription) {
            if (descriptions.get(1).equalsIgnoreCase(primaryDescription)) {
               secondaryDescription.add(descriptions.get(2));
            }
         }
         return secondaryDescription;
      }
      return null;
   }


   /**
    * Given the primary description and secondary description for a crime, it returns the associated IUCR value
    * @param primaryDescription
    * @param secondaryDescription
    * @return
    * @throws CsvValidationException
    * @throws IOException
    */
   public static String getIucr(String primaryDescription, String secondaryDescription) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");
      if (getSetOfPrimaryDescriptions().contains(primaryDescription)) {
         for (List<String> descriptions : crimeDescription) {
            if (descriptions.get(1).equalsIgnoreCase(primaryDescription) && (descriptions.get(2).equalsIgnoreCase(secondaryDescription))) {
               return descriptions.get(0);
            }
         }
      }
      return null;
   }

   /**
    * Given the primary description and secondary description for a crime, it returns the associated FBICD value
    * @param primaryDescription
    * @param secondaryDescription
    * @return
    * @throws CsvValidationException
    * @throws IOException
    */
   public static String getFbicd(String primaryDescription, String secondaryDescription) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");
      if (getSetOfPrimaryDescriptions().contains(primaryDescription)) {
         for (List<String> descriptions : crimeDescription) {
            if (descriptions.get(1).equalsIgnoreCase(primaryDescription) && (descriptions.get(2).equalsIgnoreCase(secondaryDescription))) {
               return descriptions.get(3);
            }
         }
      }
      return null;
   }
}
