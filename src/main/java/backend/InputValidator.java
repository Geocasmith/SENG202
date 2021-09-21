package backend;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
   public static boolean hasGpsCoordinate(String gpsCoordinate, float lowerRange, float upperRange) throws IOException{
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
    * @return true or false
    */
   public static boolean hasValidInt(String data)
   {
      try {
         Integer.parseInt(data);
         return true;
      } catch (Exception e) {
         return false;
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

   /**
    * Calls several methods of InputValidator class on the record parameter to make sure that the given record
    * is a valid record
    * @param record
    * @return boolean value depending on the validity of the record
    * @throws IOException
    */
   public static ArrayList<String> recordEntryFeedback(List<String> record) throws IOException, CsvValidationException {
      ArrayList<String> result = new ArrayList<>();
      if (hasValidCaseNumber(record.get(0))) {
         if(hasValidDateAndTimeFormat(record.get(1))) {
            if(hasValidInt(record.get(9))) {
               if(hasValidInt(record.get(10))){
                  if(hasGpsCoordinate(record.get(14),LATITUDELOWERBOUND, LATITUDEUPPERBOUND)) {
                     if(hasGpsCoordinate(record.get(15), LONGITUDELOWERBOUND, LONGITUDEUPPERBOUND)) {
                        if (hasValidBooleanData(record.get(7))) {
                           if(hasValidBooleanData(record.get(8))) {
                              List<String> crimeDes = new ArrayList<>();
                              crimeDes.add(record.get(3));
                              crimeDes.add(record.get(4));
                              crimeDes.add(record.get(5));
                              crimeDes.add(record.get(11));
                              if (hasValidCrimeDescriptions(crimeDes)) {
                                 result.add("1");
                                 result.add("Valid record data");
                                 return result;
                              }
                              result.add("0");
                              result.add("Make sure primary & secondary descriptions have the right IUCR and FBICD values");
                              return result;
                           }
                           result.add("0");
                           result.add("Accepted values  for Domestic entry are y or n");
                           return result;
                        }
                        result.add("0");
                        result.add("Accepted values  for Arrest entry are y or n");
                        return result;
                     }
                     result.add("0");
                     result.add("Accepted values for Longitude Coordinate are between -180 to 180");
                     return result;
                  }
                  result.add("0");
                  result.add("Accepted values for Latitude Coordinate are between -90 to 90");
                  return result;
               }
               result.add("0");
               result.add("Ward should be a number");
               return result;
            }
            result.add("0");
            result.add("Beat should be a number");
            return result;
         }
         else
         {
            result.add("0");
            result.add("Accepted date and type format is MM/dd/yyyy hh:mm:ss am/pm");
            return result;
         }

      }
      result.add("0");
      result.add("Invalid Case Number. Please make sure case number starts with two letters followed by 6 digits");
      return result;


   }

   public static boolean isValidRecord(List<String> record) throws IOException, CsvValidationException {
      if (recordEntryFeedback(record).get(0) == "1") {
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
   public static boolean hasValidDateAndTimeFormat(String dateAndTime) throws IOException{
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
