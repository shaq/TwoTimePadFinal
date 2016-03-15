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
     * A method that creates a n-gram language model from a given HashMap.
     * The HashMap is a container for all n-grams for a given corpus, along with their
     * frequencies.
     *
     * @param ngrams : Container of all n-grams
     * @return model : HashMap<String, Double> that is used to represent the language model.
     */
    public HashMap<String, Double> createModel(Map<String, Integer>[] mapArr, String corpus) {

        int vocabSize = model.getVocabSize(mapArr);
        // An iterator for the given HashMap.
        /*Map<String, Integer> ngrams = split.getMap(mapArr, mapArr.length);
        Iterator<Entry<String, Integer>> it = ngrams.entrySet().iterator();*/

        // Creating a new HashMap to store the n-grams with their MLEs.
        HashMap<String, Double> languageModel = new HashMap<>();
        Double probEstimate = 0.0;

   /*     while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            String ngram = entry.getKey().toString();
            Map<String, Integer> tmpMap = split.getMap(mapArr, ngram.length());
            probEstimate = model.laplaceSmoothing(ngrams, ngram, corpus, vocabSize);
//            System.out.println("probEstimate: " + probEstimate);
            Double negLogProb = Math.log(probEstimate);
            negLogProb *= -1;

            // Storing the negative log of the probability as they do in J. Mason et al.
            languageModel.put(ngram, negLogProb);
        }*/

        for(int map = 0; map < mapArr.length; map++){
            int keyLength = map + 1;
            Map<String, Integer> tmpMap = mapArr[map];
            Set<Map.Entry<String, Integer>> entries = tmpMap.entrySet();

            for(Map.Entry<String, Integer> e : entries){
                String ngram = e.getKey();
                probEstimate = model.laplaceSmoothing(mapArr, ngram, corpus, vocabSize);
                Double negLogProb = Math.log(probEstimate);
                negLogProb *= -1;
                languageModel.put(ngram, negLogProb);
            }

        }

        return languageModel;
    }

}
