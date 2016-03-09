package beamSearch;

import languageModel.LanguageModel;
import languageModel.NGram;
import languageModel.ParseCorpus;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by Shaquizzle on 09/03/2016.
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

    public static void main(String[] args) throws IOException {

        String corpus = parse.processFiles();
        int n = ngram.getN();
        int pruneNumber = 100;
        byte[] ciphertext = beam.getCipherText(10, corpus);
        HashMap<String, Integer> ngramModel = NGram.addNGrams(corpus, 1, n);
        HashMap<String, Double> languageModel = lm.generateLanguageModel(corpus, n);

        ArrayList<Tuple> candidates = beam.beamSearch(corpus, ngramModel, languageModel, n, pruneNumber, ciphertext);
//
//		for(Tuple t: candidates){
//
//		}
//
        recoverPlaintexts(candidates);
//		System.out.println("actual ciphertext: " + decodeFromAscii(ciphertext));
//		String ngram = "ä";
//		Double smthEst = model.laplaceSmoothing(ngramModel, ngram, corpus);
//		System.out.println(smthEst);
//		System.out.println(ngramModel.get(ngram));
//		System.out.println(corpus.length());
//		System.out.print(ngramModel.size());
//		Double pr = Math.log((1.0/(1235163.0 + 12081.0)));
//		System.out.println("Pr of ngram = " + pr);
//		System.out.println(languageModel);
//		System.out.println(Runtime.getRuntime().availableProcessors());
    }

}
