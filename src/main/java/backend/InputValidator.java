package backend;

import com.opencsv.exceptions.CsvValidationException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class InputValidator {
   private static DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a", Locale.ENGLISH);

   private static Set<List<String>> crimeDescription;

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
    * @param candidateCrimedescriptions
    * @return
    */
   public static boolean hasValidCrimeDescriptions(List<String> candidateCrimedescriptions) throws CsvValidationException, IOException {
      crimeDescription = initializeCrimeDescriptions("Crime_Descriptions_Data_Source.csv");

      if (crimeDescription.contains(candidateCrimedescriptions)) {
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
    * Checks if case number has a valid length (eight), The first two starting charachters are letters
    * and the rest of the charachters are digits (No special character is allowed)
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
         LocalDateTime date = LocalDateTime.parse(dateAndTime, formatter);
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
    *
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
