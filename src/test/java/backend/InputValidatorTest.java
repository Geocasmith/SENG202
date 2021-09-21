package backend;

import com.opencsv.exceptions.CsvValidationException;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class InputValidatorTest {

    private ArrayList<String> primaryDescriptionList = new ArrayList<String>(Arrays.asList("WEAPONS VIOLATION", "LIQUOR LAW VIOLATION",
            "OFFENSE INVOLVING CHILDREN", "NARCOTICS", "CRIMINAL SEXUAL ASSAULT", "PUBLIC INDECENCY",
            "CRIMINAL DAMAGE", "RITUALISM", "DECEPTIVE PRACTICE", "OBSCENITY", "ASSAULT", "ARSON",
            "MOTOR VEHICLE THEFT", "THEFT", "INTIMIDATION", "HOMICIDE", "BATTERY", "HUMAN TRAFFICKING",
            "ROBBERY", "PROSTITUTION", "SEX OFFENSE", "STALKING", "INTERFERENCE WITH PUBLIC OFFICER",
            "CRIMINAL TRESPASS", "GAMBLING", "OTHER OFFENSE", "CONCEALED CARRY LICENSE VIOLATION",
            "OTHER NARCOTIC VIOLATION", "NON-CRIMINAL", "PUBLIC PEACE VIOLATION", "BURGLARY", "KIDNAPPING"));




    @Test
    void hasValidCrimeDescriptions() throws CsvValidationException, IOException {
        List<String> crimeDesc = new ArrayList<String>(Arrays.asList("2095", "NARCOTICS", "ATTEMPT POSSESSION NARCOTICS", "18"));
        assertTrue(InputValidator.hasValidCrimeDescriptions(crimeDesc));
        crimeDesc = new ArrayList<String>(Arrays.asList("2095", "NARCOTICS", "HOME INVASION", "18"));
        assertFalse(InputValidator.hasValidCrimeDescriptions(crimeDesc));
        crimeDesc = new ArrayList<String>(Arrays.asList("880", "THEFT", "PURSE-SNATCHING", "6"));
        assertTrue(InputValidator.hasValidCrimeDescriptions(crimeDesc));
        crimeDesc = new ArrayList<String>(Arrays.asList("880", "THEFT", "PURSE-SNATCHING", "7"));
        assertFalse(InputValidator.hasValidCrimeDescriptions(crimeDesc));


    }


    @Test
    void hasValidCaseNumberTest() {
        assertTrue(InputValidator.hasValidCaseNumber("JD002222"));
        assertFalse(InputValidator.hasValidCaseNumber("D0022222"));
        assertFalse(InputValidator.hasValidCaseNumber("DJt00002"));
        assertFalse(InputValidator.hasValidCaseNumber("ABCDEFGH"));

    }


    @Test
    void hasValidDateAndTimeFormatTest() throws IOException {
        assertTrue(InputValidator.hasValidDateAndTimeFormat("06/15/2021 09:30:00 AM"));
        assertFalse(InputValidator.hasValidDateAndTimeFormat("06/15/2021 09:3ss0:00 AM"));
        assertFalse(InputValidator.hasValidDateAndTimeFormat("06/15/2021"));
        assertFalse(InputValidator.hasValidDateAndTimeFormat("06/15/2021 09:30 AM"));
        assertFalse(InputValidator.hasValidDateAndTimeFormat("06/15/2021 9:30:00 AM"));
    }


    @Test
    void getSetOfPrimaryDescriptionsTest() throws CsvValidationException, IOException {
        Set<String> primaryDes = new HashSet<>();
        for (List<String> descriptions : InputValidator.getCrimeDescriptions()) {
            primaryDes.add(descriptions.get(1));
        }
        ArrayList<String> testStringArray = new ArrayList<String>();
        for (String primaryDescription : primaryDes) {
            testStringArray.add(primaryDescription);
        }
        assertEquals(testStringArray, primaryDescriptionList);
    }


    @Test
    void getSetOfSecondaryDescriptionsTest() throws CsvValidationException, IOException {
        Set<String> secDes = new HashSet<>();
        Collections.addAll(secDes, "AGGRAVATED", "BY FIRE",
                "POSSESSION - EXPLOSIVE / INCENDIARY DEVICE", "BY EXPLOSIVE",
                "ATTEMPT ARSON", "POSSESSION - CHEMICAL / DRY-ICE DEVICE");
        assertEquals(secDes, InputValidator.getSetOfSecondaryDescriptions("ARSON"));

    }

    @Test
    void getIUCRTest() throws CsvValidationException, IOException {

        assertEquals("1025", InputValidator.getIucr("ARSON", "AGGRAVATED"));
        assertEquals("041A", InputValidator.getIucr("BATTERY", "AGGRAVATED - HANDGUN"));


    }

    @Test
    void getFbicdTest() throws CsvValidationException, IOException {

        assertEquals("04B", InputValidator.getFbicd("BATTERY", "AGGRAVATED - HANDGUN"));
        assertEquals("15", InputValidator.getFbicd("CONCEALED CARRY LICENSE VIOLATION", "ARMED WHILE UNDER THE INFLUENCE"));


    }


}