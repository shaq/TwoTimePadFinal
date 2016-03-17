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

    public Double getPercentageOne() {
        return percentage_one;
    }

    public void setPercentageOne(Double percentage_one) {
        this.percentage_one = percentage_one;
    }

    public Double getPercentageTwo() {
        return percentage_two;
    }

    public void setPercentageTwo(Double percentage_two) {
        this.percentage_two = percentage_two;
    }

    public boolean equals(Tuple one, Tuple two) {
        Double percentage_one = one.getPercentageOne() + one.getPercentageTwo();
        Double percentage_two = two.getPercentageTwo() + two.getPercentageTwo();
        return one.getPlaintextOne() == two.getPlaintextOne() && one.getPlaintextTwo() == two.getPlaintextTwo() &&
                percentage_one == percentage_two;

    }

    @Override
    public String toString() {
        return "[ Plaintext One: " + getPlaintextOne() + ", Plaintext Two: " + getPlaintextTwo() + ", Percentage: " +
                "log prob: " + (getPercentageOne() + getPercentageTwo()) + " ]";
    }

    /*static class TupleComparator implements Comparator<Tuple> {

        public int compare(Tuple t1, Tuple t2) {

            Double t1_perc = t1.getPercentageOne() + t1.getPercentageTwo();
            Double t2_perc = t2.getPercentageOne() + t2.getPercentageTwo();

            if (t1_perc == t2_perc) {
                return 0;
            } else if (t1_perc > t2_perc) {
                return 1;
            } else {
                return -1;
            }

        }

    }*/

}
