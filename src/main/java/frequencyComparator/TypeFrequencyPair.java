package frequencyComparator;

/**
 * This class is used to encapsulate data that has type and frequency
 * @author Sofonias Tekele Tesfaye
 * Date 09/10/2021
 */

public class TypeFrequencyPair {
    private long frequency;
    private String type;

    public TypeFrequencyPair() {
    }

    /**
     * Constructor method to create a TypeFrequencyPair with given type and frequency
     * @param type The required type of the object
     * @param frequency The required frequency of the object
     */
    public TypeFrequencyPair(String type, int frequency) {
        this.type = type;
        this.frequency = frequency;
    }

    public long getFrequency() {
        return frequency;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrequency(long frequency) {
        this.frequency = frequency;
    }
}
