package beamSearch;

import languageModel.LanguageModel;
import languageModel.NGram;
import languageModel.ParseCorpus;
import languageModel.Split;
import org.apache.commons.cli.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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
     * A method that checks if the plaintexts were recovered in the top t per-cent of candidates.
     * @param plaintexts : The original plaintexts to recover.
     * @param candidates : The list of plaintext candidates.
     * @param t : The percentage of candidates to search to see if the plaintexts were recovered.
     * @return : A boolean to indicate whether the plaintexts were recovered in the top t per-cent.
     */
    public static boolean recoveredPlaintextsSuccefully(String[] plaintexts, ArrayList<Tuple> candidates, int t) {
        boolean recovered = false;
        int numOfCands = candidates.size();
        int topCands = (int)(numOfCands*(t/100.0f));

        for (int i = 0; i < topCands; i++) {
            Tuple tuple = candidates.get(i);
            for (int j = 0; j < plaintexts.length; j++) {
                if (tuple.getPlaintextOne().equals(plaintexts[j]) || tuple.getPlaintextTwo().equals(plaintexts[j])) {
                    recovered = true;
                } else {
                    recovered = false;
                }
            }
            if(recovered){
                break;
            }

        }

        return recovered;
    }

    /**
     * Main method for testing.
     *
     * @param args
     * @throws IOException
     * @throws InterruptedException
     */
    public static void main(String[] args) throws IOException, InterruptedException, ExecutionException, ParseException {
        // Command line options
        Options options = new Options();

        options.addOption("n", true, "The maximum size of n-grams to be created.");
        options.addOption("c", true, "The full path-name of the corpus to be used in the creation of the language model");
        options.addOption("P", true, "The prune number used in the pruning operation during Beam Search.");
        options.addOption("p", true, "The length of the xor of ciphertext (length of plaintext candidates)");
        options.addOption("k", true, "How many times the keystream was re-used (either 2 or 3)");

        CommandLineParser parser = new DefaultParser();
        CommandLine cmd = parser.parse(options, args);

        File corpus = null;
        int n;
        int pruneNumber;
        int ptxtCandLength;
        int keystreamReuse;

        if (cmd.hasOption("c")) {
            String corpusPath = cmd.getOptionValue("c");
            System.out.println(corpusPath);
            corpus = new File(corpusPath);
        } else {
            System.err.println("No corpus specified");
            System.exit(1);
        }

        if (cmd.hasOption("n")) {
            n = Integer.parseInt(cmd.getOptionValue("n"));
        } else {
            n = 3;
            System.out.println("n set to the default value of " + n);
        }

        if (cmd.hasOption("p")) {
            ptxtCandLength = Integer.parseInt(cmd.getOptionValue("p"));
        } else {
            ptxtCandLength = 10;
            System.out.println("Length of the xor of ciphertexts set to default value of " + ptxtCandLength);
        }

        if (cmd.hasOption("P")) {
            pruneNumber = Integer.parseInt(cmd.getOptionValue("P"));
        } else {
            pruneNumber = 100;
            System.out.println("Pruning number set to the default value of " + pruneNumber);
        }

        if (cmd.hasOption("k")) {
            keystreamReuse = Integer.parseInt(cmd.getOptionValue("k"));
        } else {
            keystreamReuse = 2;
            System.out.println("Number of times key was reused set to the default value of " + keystreamReuse);
        }


        String stringCorpus = parse.fileToString(corpus);
        System.out.println("corpus length: " + stringCorpus.length());
        ConcurrentHashMap<String, Integer> ngramModel;
        String[] plaintexts = beam.getPlaintextValues(ptxtCandLength, keystreamReuse, stringCorpus);
        System.out.println("Plaintexts to recover:\n" + Arrays.toString(plaintexts));
        byte[] xorOfCiphertext = beam.getXOROfPlaintext(plaintexts, keystreamReuse);
        ngramModel = parse.processFiles(corpus, n);
        ConcurrentHashMap<String, Integer>[] mapArr = split.splitMap(ngramModel, n);
        HashMap<String, Double> languageModel = lm.createModel(mapArr, stringCorpus);

        ArrayList<Tuple> candidates;
        candidates = beam.beamSearch(stringCorpus, mapArr, languageModel, n, pruneNumber, xorOfCiphertext);
        System.out.println("\n\nMost probable plaintext candidates:");
        getTopPlaintextCandidates(candidates);
        int t = 1;
        System.out.println("\nRecovered plaintexts successfully in top " + t + "% of " + pruneNumber +
                " possible candidates? " + recoveredPlaintextsSuccefully(plaintexts, candidates, t));

    }

}
