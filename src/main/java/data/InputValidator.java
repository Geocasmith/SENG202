package data;

import com.opencsv.exceptions.CsvValidationException;
import importExport.CsvReader;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

/**
 * This class holds static methods for validating Record objects and their fields.
 * @author Sofonias Tekele Tesfaye
 * @author Jonathan Tomlinson (recordEntryFeedbackLong method)
 * Date 09/10/2021
 */
public class InputValidator {

   private static Set<List<String>> crimeDescription;

   // these are declared here rather than in the method for ease of access
   private static final int LONGITUDEUPPERBOUND = 180;
   private static final int LONGITUDELOWERBOUND = -180;
   private static final int LATITUDEUPPERBOUND = 90;
   private static final int LATITUDELOWERBOUND = -90;

   private static final String CRIMEDESCRIPTIONSDATASOURCECSV = "Files/Crime_Descriptions_Data_Source.csv";

   /**
    * Initializes set of valid crime descriptions (Primary, Secondary and IUCR information from
    * a given CSV file
    * @return Set of String descriptions of a crime (IUCR, Primary Description, Secondary Description
    * and FBICD)
    */
   public static Set<List<String>> initializeCrimeDescriptions() throws CsvValidationException, IOException {
      List<List<String>> readData;
      readData = CsvReader.read(CRIMEDESCRIPTIONSDATASOURCECSV);
      assert readData != null;
      return new HashSet<>(readData);

   }

   /**
    * Checks the given List of Strings has valid crime descriptions
    * @param candidateCrimeDescriptions usually String array of crime descriptions ((IUCR, Primary Description,
    * Secondary Description and FBICD)
    * @return boolean value if the crime descriptions are valid
    */
   public static boolean hasValidCrimeDescriptions(List<String> candidateCrimeDescriptions) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions();
      return crimeDescription.contains(candidateCrimeDescriptions);
   }

   /**
    * Checks if the given coordinate lies within or is equal to the lower and upper range parameters provided.
    * @param gpsCoordinate the coordinate, which is one number as a String, e.g. "-47.123456"
    * @param lowerRange a float specifying the lower bound (inclusive)
    * @param upperRange a float specifying the upper bound (inclusive)
    * @return true or false depending on the validity of the coordinate
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
    * @param emptyAllowed whether the number can be empty
    * @return true or false
    */
   public static boolean hasValidInt(String data, boolean emptyAllowed)
   {
      if (emptyAllowed && data.isEmpty()) {
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
    * @param domestic a String representing a potential boolean value
    * @return boolean true/false whether the string is valid or not
    */
   public static boolean hasValidBooleanData(String domestic)
   {
      return (Record.TRUESTRINGS.contains(domestic.toLowerCase()) ||
              Record.FALSESTRINGS.contains(domestic.toLowerCase()) ||
              domestic.equals("null"));
   }

   /**
    * Identifies whether each field in a record is valid, whether the record overall is valid, and provides an error
    * message if applicable.
    * @param record a list of strings representing a Record object. This can be generated with the toList() method
    *               of the Record class.
    * @param notImport true if the record is not being imported (e.g. if it is being edited or added). Used to avoid
    *                  rejecting many records when data is imported.
    * @return a list consisting of 16 values (1 = valid, 0 = invalid) corresponding to each field of a Record object
    *         in the order produced by toList(), with an additional 1/0 field for overall validity, and an error
    *         message ONLY IF there was an error - field is not included (i.e. not a blank field) if data is valid. The
    *         message corresponds to the first invalid field.
    */
   public static List<String> recordEntryFeedbackLong(List<String> record, boolean notImport) throws CsvValidationException, IOException {
      // will have 1 = good, 0 = bad for all rows and then a 1 or 0 at the end along with the first error message
      ArrayList<String> result = new ArrayList<>(Arrays.asList("1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1", "1"));
      ArrayList<String> dataFieldFeedBack = new ArrayList<>();
      String errMsg;
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

      // block is not validated, only required
      if (record.get(2) == null || record.get(2).isEmpty()) {
         result.set(2, "0");
         isValid = "0";
         errMsg = "Block is a required field.";
         dataFieldFeedBack.add(errMsg);
      }


      // check IUCR, primary and secondary descriptions, and FBICD
      if (notImport) {
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

      }


      // location description is not validated, only required
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
      if (!dataFieldFeedBack.isEmpty()) {
         result.add(dataFieldFeedBack.get(0));
      }

      return result;
   }

   /**
    * Identifies whether a given record is valid using recordEntryFeedbackLong.
    * Provides no feedback other than true/false.
    * @param record a list of strings representing a Record object. This can be generated with the toList() method
    *               of the Record class.
    * @return boolean value depending on the validity of the record
    */
   public static boolean isValidRecord(List<String> record, boolean notImport) throws IOException, CsvValidationException {
      return Objects.equals(recordEntryFeedbackLong(record, notImport).get(16), "1");
   }

   /**
    * Checks if case number has a valid length (eight), the first two starting characters are letters
    * and the rest of the characters are digits (no special characters are allowed).
    * @param caseNumber the case number to validate
    * @return boolean value whether the case number is valid
    */
   public static boolean hasValidCaseNumber(String caseNumber) {
      if ( caseNumber.length() == 8 && Character.isAlphabetic(caseNumber.charAt(0)) && Character.isAlphabetic(caseNumber.charAt(1))) {
         int count = 0;
         for (int i = 2; i <= 7; i ++) {
            if (Character.isDigit(caseNumber.charAt(i))) {
               count++;
            }
         }
         return count == 6;
      }
      return false;
   }
   
   /**
    * Checks whether the provided string is a valid date/time based on the format defined in the Record class.
    * @param dateAndTime a candidate date and time String
    * @return true or false depending on validity of data and time string.
    */
   public static boolean hasValidDateAndTimeFormat(String dateAndTime) {
      try {
         LocalDateTime.parse(dateAndTime.toUpperCase(), Record.DATETIMEFORMATTER);
         return true;
      } catch (Exception e) {
         return false;
      }
   }

   /**
    * Initializes Set of list of valid crime descriptions and returns it in the order of (IUCR number,
    * Primary Description, Secondary Description and FBICD)
    * @return Set of list of Crime Description
    */
   public static Set<List<String>> getCrimeDescriptions() throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions();
      return crimeDescription;
   }


   /**
    * Initializes crimeDescription, selects and returns set of primary description from the list of
    * the initialized crime descriptions
    * @return Set of Strings (Primary Description)
    */
   public static List<String> getSetOfPrimaryDescriptions() throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions();
      Set<String> primaryDes = new HashSet<>();
      for (List<String> descriptions : crimeDescription) {
         primaryDes.add(descriptions.get(1));
      }
      return new ArrayList<>(primaryDes);
   }


   /**
    * Filters and returns set of Secondary descriptions that relate to the passed primary description parameter
    * @param primaryDescription a crime's primary description string, which usually identifies the type of crime
    * @return Set of Strings secondary descriptions if any or null if empty
    */
   public static Set<String> getSetOfSecondaryDescriptions(String primaryDescription) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions();
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
    * @param primaryDescription  a crime's primary description string, which usually identifies the type of crime
    * @param secondaryDescription a crime's secondary description, usually further specifying the type of crime
    * @return returns String (IUCR) of a crime type, if crime type does not exists it returns null
    */
   public static String getIucr(String primaryDescription, String secondaryDescription) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions();
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
    * @param primaryDescription a crime's primary description string, which usually identifies the type of crime
    * @param secondaryDescription a crime's secondary description, usually further specifying the type of crime
    * @return returns String (FBICD) of a crime type, if crime type does not exists it returns null
    */
   public static String getFbicd(String primaryDescription, String secondaryDescription) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions();
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
