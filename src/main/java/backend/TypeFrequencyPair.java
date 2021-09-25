package backend;

/**
 * This class is used to encapsulate data that has type and frequency
 */

public class TypeFrequencyPair {
    private long frequency;
    private String type;

    public TypeFrequencyPair() {
    }

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
