package languageModel;

import javax.swing.*;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;


/**
 * A class that  generate n-grams from a given corpus
 * (along with their counts and probabilities) and stores them within a language model.
 * The model is represented as a HashMap<String, Double> object.
 *
 * @author Shaquille Momoh
 */
public class NGram {


    /**
     * An empty constructor.
     */
    public NGram() {
    }

    /**
     * A method to generate n-grams from a given corpus and store them within a HashMap.
     *
     * @param text : The corpus to generate n-grams from.
     * @param min  : Minimum length n-grams to be stored.
     * @param max  : Maximum length n-grams to be stored.
     * @return ngrams : A HashMap containing all n-grams along with their counts.
     */
    public static HashMap<String, Integer> addNGrams(String text, final int min, final int max) {
        HashMap<String, Integer> ngrams = new HashMap<String, Integer>();
//        text = text.toLowerCase();

        for (int i = min; i < max + 1; i++) {
            for (int j = 0; i + j - 1 < text.length(); j++) {
                String ngram = text.substring(j, j + i);
                if (ngrams.containsKey(ngram)) {
                    ngrams.put(ngram, ngrams.get(ngram) + 1);
                } else {
                    ngrams.put(ngram, 1);
                }
            }
        }

        return ngrams;
    }

    /**
     * A method that returns the total number of n-grams in a HashMap.
     * It does this by calculating the sum of all counts of all n-grams.
     *
     * @param ngrams : A container for all n-grams.
     * @return count : The number of all n-grams.
     */
    public static int getNumberOfNGrams(final HashMap<String, Integer> ngrams) {
        // An iterator for the given HashMap.
        Iterator<Entry<String, Integer>> it = ngrams.entrySet().iterator();
        Integer count = 0;

        // Iterate through the HashMap and calculate the sum of all counts of all n-grams.
        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            String ngram = entry.getValue().toString();
            Integer val = Integer.parseInt(ngram);
            count += val;
        }

        return count;
    }

    /**
     * A method to allow user to input the value of n, for generating n-grams,
     * via  GUI.
     *
     * @return n : he value of n, for generating n-grams.
     */
    public Integer getN() {
        String input = JOptionPane.showInputDialog("Enter N:", "3");
        Integer n = Integer.parseInt(input);
        return n;
    }

}