package frequencyComparator;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;

class FrequencyComparatorDescendingTest {

    @Test
    void compare() {

        TypeFrequencyPair testPair = new TypeFrequencyPair();
        ArrayList<TypeFrequencyPair> testList = new ArrayList<>();
        ArrayList<TypeFrequencyPair>  comparisonList = new ArrayList<>();
        int frequency = 100;

        // Generate a TypeFrequency pair list sorted in descending frequency order
        for (int i= 0; i <=4; i++) {
            testPair.setType("Theft");
            testPair.setFrequency(frequency);
            comparisonList.add(testPair);
            frequency -= 20;

        }

        // Generate a TypeFrequency pair list sorted in ascending frequency order
        frequency = 20;
        for (int i= 0; i <=4; i++) {
            testPair.setType("Theft");
            testPair.setFrequency(frequency);
            testList.add(testPair);
            frequency += 20;

        }
        //Sort testList in ascending order of frequency
        testList.sort(new FrequencyComparatorDescending());

        assertEquals(testList.get(0).getFrequency(), comparisonList.get(0).getFrequency());
        assertEquals(testList.get(4).getFrequency(), comparisonList.get(4).getFrequency());
    }
}