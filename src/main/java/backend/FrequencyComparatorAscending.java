package backend;

import java.util.Comparator;

/**
 * Comparator for TypeFrequencyPair objects
 */

public class FrequencyComparatorAscending implements Comparator<TypeFrequencyPair> {
    /**
     * Compares TypeFrequencyPair object a and b passed as parameters
     * @param a TypeFrequencyPair object
     * @param b TypeFrequencyPair object
     * @return true if a > b or false other wise
     */
    @Override
    public int compare(TypeFrequencyPair a, TypeFrequencyPair b) {
        return Long.compare(a.getFrequency(), b.getFrequency());
    }
}
