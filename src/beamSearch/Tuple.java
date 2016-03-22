package beamSearch;

/**
 * A class defining a custom data structure to hold plaintext candidates.
 *
 * @author Shaquille Momoh
 */
public class Tuple {

    private String plaintext_one;
    private String plaintext_two;
    private Double probability_one;
    private Double probability_two;

    public Tuple() {}

    public Tuple(String plaintext_one, String plaintext_two, Double percentage_one, Double percentage_two) {
        this.plaintext_one = plaintext_one;
        this.plaintext_two = plaintext_two;
        this.probability_one = percentage_one;
        this.probability_two = percentage_two;
    }

    public String getPlaintextOne() {
        return plaintext_one;
    }

    public void setPlaintextOne(String plaintext_one) {
        this.plaintext_one = plaintext_one;
    }

    public String getPlaintextTwo() {
        return plaintext_two;
    }

    public void setPlaintextTwo(String plaintext_two) {
        this.plaintext_two = plaintext_two;
    }

    public Double getProbOne() {
        return probability_one;
    }

    public Double getProbTwo() {
        return probability_two;
    }


    public boolean equals(Tuple one, Tuple two) {

        if (one.getPlaintextOne().equals(two.getPlaintextOne())
                && one.getPlaintextTwo().equals(two.getPlaintextTwo())) {
            return true;
        } else if (one.getPlaintextOne().equals(two.getPlaintextTwo())
                && one.getPlaintextTwo().equals(two.getPlaintextOne())) {
            return true;
        } else {
            return false;
        }

    }

    @Override
    public String toString() {
        return "[ Plaintext One: " + getPlaintextOne() + ", Plaintext Two: " + getPlaintextTwo() +
                ", Log probability: " + (getProbOne() + getProbTwo()) + " ]";
    }

}
