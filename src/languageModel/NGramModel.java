package languageModel;

import beamSearch.Tuple;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class NGramModel {

    /**
     * A method that gets the size of the vocabulary for a given language model (the number of ngrams).
     * @param mapArr : An array of Maps containing all n-grams for a given corpus.
     * @return : The vocab size.
     */
    public int getVocabSize(Map<String, Integer>[] mapArr){
        int vocabSize = 0;
        for(Map<String, Integer> m : mapArr){
            vocabSize += m.size();
        }

        return vocabSize;
    }

    /**
     *
     * @return : returns a really small (1 x10^(-7)) probability to unseen events. This is needed since
     * laplace smoothing gives too much probability to unseen events.
     */
    public Double estimateProbability(Map<String, Integer>[] mapArr, String key, String corpus, int vocabSize) {
        Double nGramMLE = 0.0000001;
        return nGramMLE;
    }


    public Double getD (int n1, int n2) {
        double n_1 = (double)n1;
        double n_2 = (double)n2;
        return n_1 / (n_1 + (2*n_2));
    }

    public Double knSmoothing (Map<String, Integer>[] mapArr, Map<String, Integer> ngrams, String key, Double D) {

        int keyLength = key.length();
        int keyCount;
        int nMinusCount;
        int mapIndex = keyLength - 1;

        int precedeCount = 0;

        if(keyLength == 1){

            Map<String, Integer> bigrams = mapArr[1];
            Set<Map.Entry<String, Integer>> bigramSet = bigrams.entrySet();

            for(Map.Entry<String, Integer> entry : bigramSet) {
                String bigram = entry.getKey();
                if(bigram.endsWith(key)) {
                    precedeCount++;
                }
            }

            double knSmoothingEstimate = (precedeCount / bigrams.size());

            return knSmoothingEstimate;

        } else {

            // Map of n-grams with the same length as 'key'.
            Map<String, Integer> keyLengthNGrams = mapArr[mapIndex];

            // Number of different n-grams in the corpus that can follow the (n-1)-gram ().
            int followCount = 0;

//            HashMap<String, Integer> ngrams = new HashMap<>();
//
//            for (int map = 0; map < ngrams.; map++) {
//                ngrams.putAll(mapArr[map]);
//            }

            String nMinusGram = key.substring(0, mapIndex);

            if(ngrams.containsKey(key)) {
                keyCount = ngrams.get(key);
            } else {
                keyCount = 0;
            }

            if(ngrams.containsKey(nMinusGram)) {
                nMinusCount = ngrams.get(nMinusGram);
            } else {
                nMinusCount = 0;
            }

//            System.out.println("keycount of " + key + "= " + keyCount + "\n");

            // The first term is kneser-ney.
            double discountedCount = (Math.max((double)keyCount - D, 0f) / nMinusCount);
//            System.out.println("discountedCount of " + key + "= " + discountedCount + "\n");

            // Entry set of all n-grams with the same length as the 'key'.
            Set<Map.Entry<String, Integer>> klNGramsSet = keyLengthNGrams.entrySet();

            for (Map.Entry<String, Integer> entry : klNGramsSet) {
                String ngram = entry.getKey();
                if (ngram.startsWith(nMinusGram)) {
                    followCount++;
                }
            }

            double lambda = ((D / nMinusCount) * followCount);
//            System.out.println("lambda of " + key + "= " + lambda + "\n");
            String lowerOrderNGram = key.substring(keyLength - (keyLength - 1), keyLength);

            double knSmoothingEstimate = discountedCount + (lambda * knSmoothing(mapArr, ngrams,lowerOrderNGram, D));

            return knSmoothingEstimate;
        }

    }

/*    public Double getDOne (int n1, int n2, Double D) {
        double dOne = (1 - (2*D*(n2/n1)));
        return dOne;
    }

    public Double getDTwo (int n2, int n3, Double D) {
        double dTwo = (2 - (3*D*(n3/n2)));
        return dTwo;
    }

    public Double getDThree (int n3, int n4, Double D) {
        double dThree = (3 - (4*D*(n4 / n3)));
        return dThree;
    }

    public Double getDForNGram (Map<String, Integer> ngrams, String n_minus_gram, Integer n1, Integer n2, Integer n3,
                        Integer n4) {

        double D = getD(n1, n2);

        if (ngrams.get(n_minus_gram) == null) {
            D = 0;
        } else if (ngrams.get(n_minus_gram) == 1) {
            D = getDOne(n1, n2, D);
        } else if (ngrams.get(n_minus_gram) == 2) {
            D = getDTwo(n2, n3, D);
        } else if (ngrams.get(n_minus_gram) > 2) {
            D = getDThree(n3, n4, D);
        }

        return D;
    }

    public Double mknSmoothing(Map<String, Integer>[] mapArr, String key, Integer n1, Integer n2, Integer n3,
                               Integer n4){

        String n_minus_gram = key.substring(0, key.length() - 1);
        int keyLength = key.length();
        int keycount = 0;
        int mapIndex = keyLength - 1;
        Map<String, Integer> keyLengthNGrams = mapArr[mapIndex];

        HashMap<String, Integer> ngrams = new HashMap<>();

        for (int map = 0; map < mapIndex; map++) {
            ngrams.putAll(mapArr[map]);
        }

        if(ngrams.containsKey(key)) {
            keycount = ngrams.get(key);
        } else {
            keycount = 0;
        }

        int precedeCount = 0;



        Set<Map.Entry<String, Integer>> entries = ngrams.entrySet();

        for(Map.Entry<String, Integer> entry : entries) {
            if(entry.getValue() == 1) {

            }
        }
    }*/

    /**
     * A method that implements laplace smoothing.
     * @param mapArr : An array of maps containing n-grams.
     * @param key : The n-grams to find the probability of.
     * @param corpus : The corpus containing the ng-rams.
     * @param vocabSize : The number of all n-grams obtained from the corpus.
     * @return : A Double value representing the probability of the given n-gram.
     */
    public Double laplaceSmoothing(Map<String, Integer>[] mapArr, String key, String corpus, int vocabSize) {

        Double smoothingEstimate = 0.0;
        Integer keyLength = key.length();
        Integer keyCount;
        Integer denomCount = 0;
        String denom = key.substring(0, key.length() - 1);
        int mapIndex = keyLength - 1;

        // Map containing all n-grams with length == keyLength from corpus.
        Map<String, Integer> ngrams = mapArr[mapIndex];

        if (ngrams.containsKey(key)) {
            keyCount = ngrams.get(key);
        } else {
            keyCount = 0;
        }

        if (keyLength > 1) {

            Map<String, Integer> n_minus_one_grams = mapArr[mapIndex - 1];

            if(n_minus_one_grams.containsKey(denom)){
                denomCount = n_minus_one_grams.get(denom);
            } else {
                denomCount = 0;
            }

            // Calculate the smoothing estimate for the given n-gram 'key' as a log probability.
            // (count of given n-gram ) + 1 / (count of (n-1)-grams for given n-gram) + Vocabulary size
            smoothingEstimate = (1.0 + keyCount) / (denomCount + vocabSize);

        } else {

            // Calculate the smoothing estimate for the given n-gram 'key' as a log probability.
            // (count of given n-gram) + 1 / (count of all characters in the corpus) + Vocabulary size
            smoothingEstimate = (1.0 + keyCount) / (corpus.length() + vocabSize);
        }

        return smoothingEstimate;
    }

    /**
     * A method which calculates the candidate probability to be used in the implementation of Beam Search.
     * @param n : The maximum size of n-grams taken from corpus.
     * @param corpus : The corpus as a string.
     * @param ngrams : An array of all n-grams taken from the corpus.
     * @param languageModel : The language model for a given corpus.
     * @param candidate : The candidate this method calculates the probability of.
     * @param vocabSize : The number of all n-grams in the language model.
     * @return : The log probabilities of the two plaintexts in the given candidate returned as a double.
     */
    public Double[] calculateCandidateProbability(int n, String corpus, Map<String, Integer>[] ngrams, HashMap<String,
            Double> languageModel, Tuple candidate, String plaintext_one, String plaintext_two,int vocabSize) {

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

//        System.out.println(p_length);
        // Logic used to control of the initialisation of the ngrams and
        // (n-1)grams to be used in the probability
        // logic below.
        if (p_length > n) {

            p_one_ngram = plaintext_one.substring(p_length - n, p_length);
            p_two_ngram = plaintext_two.substring(p_length - n, p_length);
            pOne_n_minus_one_gram = p_one_ngram.substring(0, p_one_ngram.length() - 1);
            pTwo_n_minus_one_gram = p_two_ngram.substring(0, p_two_ngram.length() - 1);

        } else if (p_length > 1 && p_length <= n) {

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

            p_one_prob = Math.log(estimateProbability(ngrams, p_one_ngram, corpus, vocabSize));
            p_two_prob = Math.log(estimateProbability(ngrams, p_two_ngram, corpus, vocabSize));
            p_one_nminus_prob = Math.log(estimateProbability(ngrams, pOne_n_minus_one_gram, corpus, vocabSize));
            p_two_nminus_prob = Math.log(estimateProbability(ngrams, pTwo_n_minus_one_gram, corpus, vocabSize));
//            System.out.println("both not in lm");
        } else if (!languageModel.containsKey(pOne_n_minus_one_gram) &&
                languageModel.containsKey(pTwo_n_minus_one_gram)) {

            p_one_prob = Math.log(estimateProbability(ngrams, p_one_ngram, corpus, vocabSize));
            p_one_nminus_prob = Math.log(estimateProbability(ngrams, pOne_n_minus_one_gram, corpus, vocabSize));
            p_two_nminus_prob = Math.log(languageModel.get(pTwo_n_minus_one_gram));

            if (languageModel.containsKey(p_two_ngram)) {
                p_two_prob = Math.log(languageModel.get(p_two_ngram));
            } else {
                p_two_prob = Math.log(estimateProbability(ngrams, p_two_ngram, corpus, vocabSize));
            }

//            System.out.println("p1 not in lm");
        } else if (languageModel.containsKey(pOne_n_minus_one_gram) &&
                !languageModel.containsKey(pTwo_n_minus_one_gram)) {

            p_two_prob = Math.log(estimateProbability(ngrams, p_two_ngram, corpus, vocabSize));
            p_two_nminus_prob = Math.log(estimateProbability(ngrams, pTwo_n_minus_one_gram, corpus, vocabSize));
            p_one_nminus_prob = Math.log(languageModel.get(pOne_n_minus_one_gram));

            if (languageModel.containsKey(p_one_ngram)) {
                p_one_prob = Math.log(languageModel.get(p_one_ngram));
            } else {
                p_one_prob = Math.log(estimateProbability(ngrams, p_one_ngram, corpus, vocabSize));
            }

//            System.out.println("p2 not in lm");
        } else {

            p_one_nminus_prob = Math.log(languageModel.get(pOne_n_minus_one_gram));
            p_two_nminus_prob = Math.log(languageModel.get(pTwo_n_minus_one_gram));

            if (languageModel.containsKey(p_one_ngram)) {
                p_one_prob = Math.log(languageModel.get(p_one_ngram));
            } else {
                p_one_prob = Math.log(estimateProbability(ngrams, p_one_ngram, corpus, vocabSize));
            }

            if (languageModel.containsKey(p_two_ngram)) {
                p_two_prob = Math.log(languageModel.get(p_two_ngram));
            } else {
                p_two_prob = Math.log(estimateProbability(ngrams, p_two_ngram, corpus, vocabSize));
            }

//            System.out.println("both in lm");
        }

        cand_prob_one = candidate.getProbOne() + p_one_prob - p_one_nminus_prob;
        cand_prob_two = candidate.getProbTwo() + p_two_prob - p_two_nminus_prob;

        return new Double[]{cand_prob_one, cand_prob_two};
    }


}
