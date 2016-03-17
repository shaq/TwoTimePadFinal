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
import java.util.HashMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

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
    public static void getTopPlaintextCandidates(ArrayList<Tuple> candidates) {
        for (Tuple candidate : candidates) {
            System.out.println(candidate.toString());
        }
    }

    /**
     * Main method for testing.
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException {

        File corpus = parse.getCorpus();
        InputStream is = new FileInputStream(corpus);
        String stringCorpus = split.fileToString(is);
        int n = parse.getN();
        int pruneNumber = 256;
        ConcurrentHashMap<String, Integer> ngramModel;
        byte[] ciphertext = beam.getXOROfPlaintext(10, stringCorpus);
        ngramModel = parse.processFiles(corpus, n);
        ConcurrentHashMap<String, Integer>[] mapArr = split.splitMap(ngramModel, n);
        HashMap<String, Double> languageModel = lm.createModel(mapArr, stringCorpus);

        System.out.println("corpus length " + corpus.length());
//        System.out.println("vocab size " + languageModel.size());
//        System.out.println(ngramModel);
        split.mapArrToString(mapArr);
//        System.out.println(languageModel);

        ArrayList<Tuple> candidates;
        candidates = beam.beamSearch(stringCorpus, mapArr, languageModel, n, pruneNumber, ciphertext);
        getTopPlaintextCandidates(candidates);

    }

}
