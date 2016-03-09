package languageModel;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

public class LanguageModel {

    final static ParseCorpus parse = new ParseCorpus();
    final static NGram ngram = new NGram();
    final static NGramModel model = new NGramModel();
    public static String referenceText;

    /**
     * A method that creates a n-gram language model from a given HashMap.
     * The HashMap is a container for all n-grams for a given corpus, along with their
     * frequencies.
     *
     * @param ngrams : Container of all n-grams
     * @return model : HashMap<String, Double> that is used to represent the language model.
     */
    public HashMap<String, Double> createModel(HashMap<String, Integer> ngrams, String corpus) {
        // An iterator for the given HashMap.
        Iterator<Entry<String, Integer>> it = ngrams.entrySet().iterator();

        // Creating a new HashMap to store the n-grams with their MLEs.
        HashMap<String, Double> languageModel = new HashMap<String, Double>();
        Double mle = 0.0;

        while (it.hasNext()) {
            Entry<String, Integer> entry = it.next();
            String ngram = entry.getKey().toString();
            mle = model.estimateProbability(ngrams, ngram, corpus);
            languageModel.put(ngram, mle);
        }

        return languageModel;
    }

    /**
     * The main method that is used to Parse in a corpus and generates the language model using this.
     *
     * @return : The language model used to generate the corpus
     * @throws IOException
     */
    public HashMap<String, Double> generateLanguageModel(String corpus, int n) throws IOException {

        HashMap<String, Integer> ngrams = new HashMap<String, Integer>();
        HashMap<String, Double> languageModel = new HashMap<String, Double>();

        // Size of n-gram
        int n_size = n;

        // Getting the user selected corpus and storing it in a string.
        referenceText = corpus;

        // Creating all n-grams of size from 1 to n, using the parsed corpus.
        ngrams = NGram.addNGrams(referenceText, 1, n_size);

        // Creating the language model (estimating all probabilities of n-grams created above).
        languageModel = createModel(ngrams, corpus);

        return languageModel;

    }

}
