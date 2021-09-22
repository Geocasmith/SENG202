package backend;

public class TypeFrequencypair {
    private int frequency;
    private String type;

    public TypeFrequencypair() {
    }

    public TypeFrequencypair(String type, int frequency) {
        this.type = type;
        this.frequency = frequency;
    }

    public int getFrequency() {
        return frequency;
    }
    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setFrequency(int frequency) {
        this.frequency = frequency;
    }
}
