package languageModel;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class LanguageModel {

    final static NGramModel model = new NGramModel();
    public static Split split = new Split();

    /**
     * A method that creates a n-gram language model from a given array of Maps.
     * The array is a container for all n-grams for a given corpus, along with their
     * frequencies.
     *
     * @param mapArr : An array of maps used as a container for all n-grams.
     * @return model : HashMap<String, Double> that is used to represent the language model.
     */
    public HashMap<String, Double> createModel(Map<String, Integer>[] mapArr, String corpus) {

        int vocabSize = model.getVocabSize(mapArr);

        // Creating a new HashMap to store the n-grams with their MLEs.
        HashMap<String, Double> languageModel = new HashMap<>();
        Double probEstimate;

        int n1 = 0;
        int n2 = 0;

        HashMap<String, Integer> ngrams = new HashMap<>();
        for(int map = 0; map < mapArr.length; map++) {
            ngrams.putAll(mapArr[map]);
        }

        Set<Map.Entry<String, Integer>> entries = ngrams.entrySet();

        for (Map.Entry<String, Integer> entry : entries) {
            Integer count = entry.getValue();
            if (count == 1) {
                n1++;
            } else if (count == 2) {
                n2++;
            }
        }

        System.out.println("n1 = " + n1 + " , n2 = " + n2);

        Double D = model.getD(n1, n2);
        System.out.println("D = " + D);

        for(int map = 0; map < mapArr.length; map++){
//            Map<String, Integer> tmpMap = mapArr[map];
//            Set<Map.Entry<String, Integer>> entries = tmpMap.entrySet();

            for(Map.Entry<String, Integer> e : entries){
                String ngram = e.getKey();
                probEstimate = model.knSmoothing(mapArr, ngrams, ngram, D);
//                probEstimate = model.laplaceSmoothing(mapArr, ngram, corpus, vocabSize);
                languageModel.put(ngram, probEstimate);
            }

        }

        return languageModel;
    }

}
