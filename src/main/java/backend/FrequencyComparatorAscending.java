package backend;

import java.util.Comparator;

public class FrequencyComparatorAscending implements Comparator<TypeFrequencyPair> {
    @Override
    public int compare(TypeFrequencyPair a, TypeFrequencyPair b) {
        return a.getFrequency() < b.getFrequency() ? -1 : a.getFrequency() == b.getFrequency() ? 0 : 1;
    }
}
