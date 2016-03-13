package beamSearch;

import languageModel.LanguageModel;
import languageModel.NGram;
import languageModel.ParseCorpus;
import languageModel.Split;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

/**
 * A class that recovers the plaintexts using methods from 'BeamSearch' and 'LanguageModel'.
 *
 * @author Shaquille Momoh
 */
public class PlaintextRecovery {

    public static BeamSearch beam = new BeamSearch();
    private static ParseCorpus parse = new ParseCorpus();
    private static LanguageModel lm = new LanguageModel();
    private static NGram ngram = new NGram();
    private static Split split = new Split();

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

        File corpus = parse.getCorpus();
        InputStream is = new FileInputStream(corpus);
        String stringCorpus = split.fileToString(is);
        int n = parse.getN();
//        int pruneNumber = 10;
        ConcurrentHashMap<String, Integer> ngramModel = new ConcurrentHashMap<String, Integer>();
//        byte[] ciphertext = beam.getCipherText(10, corpus);
        ngramModel = parse.processFiles(ngramModel, corpus, n);
//        HashMap<String, Double> languageModel = lm.generateLanguageModel(stringCorpus, n);

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
