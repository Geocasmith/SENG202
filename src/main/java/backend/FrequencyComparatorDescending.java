package backend;

import java.util.Comparator;
/**
 * Comparator for TypeFrequencyPair objects
 * @author Sofonias Tekele Tesfaye
 * Date 09/10/2021
 */
public class FrequencyComparatorDescending implements Comparator<TypeFrequencyPair> {
    /**
     * Compares TypeFrequencyPair object a and b passed as parameters
     * @param a TypeFrequencyPair object
     * @param b TypeFrequencyPair object
     * @return true if a < b or false otherwise
     */
    @Override
    public int compare(TypeFrequencyPair a, TypeFrequencyPair b) {
        int compareResult;
        compareResult = Long.compare(b.getFrequency(), a.getFrequency());
        if (compareResult == 0) {
            compareResult = a.getType().compareTo(b.getType());
        }
        return compareResult;
    }
}
