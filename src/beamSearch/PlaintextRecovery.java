package beamSearch;

import languageModel.LanguageModel;
import languageModel.NGram;
import languageModel.ParseCorpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by Shaquizzle on 09/03/
 */
public class PlaintextRecovery {

    public static BeamSearch beam = new BeamSearch();
    private static ParseCorpus parse = new ParseCorpus();
    private static LanguageModel lm = new LanguageModel();
    private static NGram ngram = new NGram();

    /**
     * A method that prints the given candidates in the desired format, along with their probabilities.
     *
     * @param candidates : The list of plaintext candidates to print.
     */
    public static void recoverPlaintexts(ArrayList<Tuple> candidates) {
        for (Tuple candidate : candidates) {
            System.out.println(candidate.toString());
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

//        String corpus = parse.processFiles();
//        int n = ngram.getN();
//        int pruneNumber = 10;
        ConcurrentHashMap<String, Integer> ngramModel = new ConcurrentHashMap<String, Integer>();
//        byte[] ciphertext = beam.getCipherText(10, corpus);
        ngramModel = parse.processFiles(ngramModel);
//        HashMap<String, Double> languageModel = lm.generateLanguageModel(corpus, n);

//        System.out.println("corpus length " + corpus.length());
//        System.out.println("vocab size " + languageModel.size());
        System.out.println(ngramModel);
//        System.out.println(languageModel);
//        languageModel.put("", Math.log(1.0));
//        System.out.println("Log probability of the empty string = " + languageModel.get(""));

//        ArrayList<Tuple> candidates = beam.beamSearch(corpus, ngramModel, languageModel, n, pruneNumber, ciphertext);
//        recoverPlaintexts(candidates);

    }

}
