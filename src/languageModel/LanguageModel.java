package languageModel;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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

        for(int map = 0; map < mapArr.length; map++){
            int keyLength = map + 1;
            Map<String, Integer> tmpMap = mapArr[map];
            Set<Map.Entry<String, Integer>> entries = tmpMap.entrySet();

            for(Map.Entry<String, Integer> e : entries){
                String ngram = e.getKey();
                probEstimate = model.laplaceSmoothing(mapArr, ngram, corpus, vocabSize);
                languageModel.put(ngram, probEstimate);
            }

        }

        return languageModel;
    }

}
