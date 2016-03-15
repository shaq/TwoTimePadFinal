package languageModel;

import beamSearch.Tuple;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
            nGramMLE = (keyCount / denomCount);

        } else {

            // Calculate the MLE for the given n-gram 'key' as a log probability.
            nGramMLE = (keyCount / corpusLength.doubleValue());
        }

        return nGramMLE;

    }

    public Double laplaceSmoothing(Map<String, Integer> ngrams, String key, String corpus) {
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
            smoothingEstimate = (1.0 + keyCount) / (denomCount + ngrams.size());

        } else {

            // Calculate the smoothing estimate for the given n-gram 'key' as a log probability.
            // (count of given n-gram) + 1 / (count of all characters in the corpus) + Vocabulary size
            smoothingEstimate = (1.0 + keyCount) / (corpus.length() + ngrams.size());
        }

        return smoothingEstimate;
    }

    public Double[] calculateCandidateProbability(int n, String corpus, Map<String, Integer> ngrams, HashMap<String,
            Double> languageModel, String plaintext_one, String plaintext_two,
                                                  Tuple candidate) {

        String p_one_ngram;
        String p_two_ngram;
        String pOne_n_minus_one_gram;
        String pTwo_n_minus_one_gram;
        Double cand_prob_one;
        Double cand_prob_two;
        Double p_one_prob;
        Double p_two_prob;
        Double p_one_nminus_prob;
        Double p_two_nminus_prob;

        int p_length = plaintext_one.length();


        // Logic used to control of the initialisation of the ngrams and
        // (n-1)grams to be used in the probability
        // logic below.
        if (p_length > n) {

            p_one_ngram = plaintext_one.substring(p_length - n, p_length);
            p_two_ngram = plaintext_two.substring(p_length - n, p_length);
            pOne_n_minus_one_gram = p_one_ngram.substring(0, p_one_ngram.length() - 1);
            pTwo_n_minus_one_gram = p_two_ngram.substring(0, p_two_ngram.length() - 1);

        } else if (p_length > 1 && p_length < n) {

            p_one_ngram = plaintext_one;
            p_two_ngram = plaintext_two;
            pOne_n_minus_one_gram = plaintext_one.substring(0, p_length - 1);
            pTwo_n_minus_one_gram = plaintext_two.substring(0, p_length - 1);

        } else {

            p_one_ngram = plaintext_one;
            p_two_ngram = plaintext_two;
            pOne_n_minus_one_gram = plaintext_one.substring(p_length);
            pTwo_n_minus_one_gram = plaintext_two.substring(p_length);

        }

        if (!languageModel.containsKey(pOne_n_minus_one_gram) &&
                !languageModel.containsKey(pTwo_n_minus_one_gram)) {

            p_one_prob = Math.log(laplaceSmoothing(ngrams, p_one_ngram, corpus));
            p_two_prob = Math.log(laplaceSmoothing(ngrams, p_two_ngram, corpus));
            p_one_nminus_prob = Math.log(laplaceSmoothing(ngrams, pOne_n_minus_one_gram, corpus));
            p_two_nminus_prob = Math.log(laplaceSmoothing(ngrams, pTwo_n_minus_one_gram, corpus));
            System.out.println("both not in lm");
        } else if (!languageModel.containsKey(pOne_n_minus_one_gram) &&
                languageModel.containsKey(pTwo_n_minus_one_gram)) {

            p_one_prob = Math.log(laplaceSmoothing(ngrams, p_one_ngram, corpus));
            p_one_nminus_prob = Math.log(laplaceSmoothing(ngrams, pOne_n_minus_one_gram, corpus));
            p_two_nminus_prob = languageModel.get(pTwo_n_minus_one_gram);

            if (languageModel.containsKey(p_two_ngram)) {
                p_two_prob = languageModel.get(p_two_ngram);
            } else {
                p_two_prob = Math.log(laplaceSmoothing(ngrams, p_two_ngram, corpus));
            }

            System.out.println("p1 not in lm");
        } else if (languageModel.containsKey(pOne_n_minus_one_gram) &&
                !languageModel.containsKey(pTwo_n_minus_one_gram)) {

            p_two_prob = Math.log(laplaceSmoothing(ngrams, p_two_ngram, corpus));
            p_two_nminus_prob = Math.log(laplaceSmoothing(ngrams, pTwo_n_minus_one_gram, corpus));
            p_one_nminus_prob = languageModel.get(pOne_n_minus_one_gram);

            if (languageModel.containsKey(p_one_ngram)) {
                p_one_prob = languageModel.get(p_one_ngram);
            } else {
                p_one_prob = Math.log(laplaceSmoothing(ngrams, p_one_ngram, corpus));
            }

            System.out.println("p2 not in lm");
        } else {

            p_one_nminus_prob = languageModel.get(pOne_n_minus_one_gram);
            p_two_nminus_prob = languageModel.get(pTwo_n_minus_one_gram);

            if (languageModel.containsKey(p_one_ngram)) {
                p_one_prob = languageModel.get(p_one_ngram);
            } else {
                p_one_prob = Math.log(laplaceSmoothing(ngrams, p_one_ngram, corpus));
            }

            if (languageModel.containsKey(p_two_ngram)) {
                p_two_prob = languageModel.get(p_two_ngram);
            } else {
                p_two_prob = Math.log(laplaceSmoothing(ngrams, p_two_ngram, corpus));
            }

            System.out.println("both in lm");
        }

//        if(p_length > 1){
        cand_prob_one = candidate.getPercentageOne() + p_one_prob - p_one_nminus_prob;
        cand_prob_two = candidate.getPercentageTwo() + p_two_prob - p_two_nminus_prob;
/*        } else {
            cand_prob_one = candidate.getPercentageOne() + p_one_prob;
            cand_prob_two = candidate.getPercentageTwo() + p_two_prob;
        }*/

        return new Double[]{cand_prob_one, cand_prob_two};
    }


}
