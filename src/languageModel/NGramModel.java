package languageModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class NGramModel {

    /**
     * A method to estimate the Maximum Likelihood Probability (MLE) for a given n-gram
     *
     * @param ngrams : The container where the given n-gram resides.
     * @param key    : The n-gram we are trying to calculate to probability for.
     * @return nGramMLE : The log probability of the given n-gram.
     */
    public Double estimateProbability(HashMap<String, Integer> ngrams, String key, String corpus) {


        // An iterator for the given HashMap.
        Iterator<Entry<String, Integer>> it = ngrams.entrySet().iterator();

        Integer beginIndex = 0;
        String denom = key.substring(beginIndex, key.length() - 1);
        Double nGramMLE;
        Integer keyLength = key.length();
        Integer keyCount = ngrams.get(key);
        Integer corpusLength = corpus.length();
        Double denomCount = 0.0;

        if (keyLength > 1) {

            while (it.hasNext()) {
                Entry<String, Integer> entry = it.next();
                String ngram = entry.getKey();

                // If the key at the current index of the HashMap == the (n-1)-gram of 'key'
                // iterate the counter 'denomCount'
                if (ngram.startsWith(denom)) {
                    ++denomCount;
                }
            }

            // Calculate the MLE for the given n-gram 'key' as a log probability.
            // (count of given n-gram ) / (count of (n-1)-grams for given n-gram)
            nGramMLE = Math.log(keyCount / denomCount);

        } else {

            // Calculate the MLE for the given n-gram 'key' as a log probability.
            nGramMLE = Math.log(keyCount / corpusLength.doubleValue());
        }

        return nGramMLE;

    }

    public Double laplaceSmoothing(HashMap<String, Integer> ngrams, String key, String corpus) {
        // An iterator for the given HashMap.
        Iterator<Entry<String, Integer>> it = ngrams.entrySet().iterator();

        Double smoothingEstimate = 0.0;
        Integer keyLength = key.length();
        Integer keyCount;
        Double denomCount = 0.0;

        if (ngrams.containsKey(key)) {
            keyCount = ngrams.get(key);
        } else {
            keyCount = 0;
        }

        if (keyLength > 1) {

            while (it.hasNext()) {
                Entry<String, Integer> entry = it.next();
                String ngram = entry.getKey().toString();
                String denom = key.substring(0, key.length() - 1);

                // If the key at the current index of the HashMap == the (n-1)-gram of 'key'
                // iterate the counter 'denomCount'
                if (ngram.startsWith(denom)) {
                    ++denomCount;
                }
            }

            // Calculate the smoothing estimate for the given n-gram 'key' as a log probability.
            // (count of given n-gram ) + 1 / (count of (n-1)-grams for given n-gram) + Vocabulary size
            smoothingEstimate = Math.log((1.0 + keyCount) / (denomCount + ngrams.size()));

        } else {

            // Calculate the smoothing estimate for the given n-gram 'key' as a log probability.
            // (count of given n-gram) + 1 / (count of all characters in the corpus) + Vocabulary size
            smoothingEstimate = Math.log((1.0 + keyCount) / (corpus.length() + ngrams.size()));
        }

        return smoothingEstimate;
    }


}
