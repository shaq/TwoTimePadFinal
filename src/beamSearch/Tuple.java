package beamSearch;

/**
 * A class defining a custom data structure to hold plaintext candidates.
 *
 * @author Shaquille Momoh
 */
public class Tuple {

    private String plaintext_one;
    private String plaintext_two;
    private Double percentage_one;
    private Double percentage_two;

    public Tuple(String plaintext_one, String plaintext_two, Double percentage_one, Double percentage_two) {
        this.plaintext_one = plaintext_one;
        this.plaintext_two = plaintext_two;
        this.percentage_one = percentage_one;
        this.percentage_two = percentage_two;
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
        return percentage_one;
    }

    public Double getProbTwo() {
        return percentage_two;
    }

    public boolean equals(Tuple one, Tuple two) {
        Double percentage_one = one.getProbOne() + one.getProbTwo();
        Double percentage_two = two.getProbTwo() + two.getProbTwo();
        return one.getPlaintextOne() == two.getPlaintextOne() && one.getPlaintextTwo() == two.getPlaintextTwo() &&
                percentage_one == percentage_two;

    }

    @Override
    public String toString() {
        return "[ Plaintext One: " + getPlaintextOne() + ", Plaintext Two: " + getPlaintextTwo() +
                ", Log probability: " + (getProbOne() + getProbTwo()) + " ]";
    }

}
