package beamSearch;

import languageModel.NGramModel;

import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * The class responsible for recovering the plaintexts from a random XOR of ciphertexts
 * in the corpus.
 *
 * @author Shaquille Momoh
 **/
public class BeamSearch {


    /**
     * Creating an array to hold the ascii codes of all printable english characters.
    **/
    private static final int [] printableAscii = new int[96];
    private static final int ASCII_LENGTH = printableAscii.length;

    private static NGramModel model = new NGramModel();


    /**
     * A constructor that initialises the array that holds the ascii codes for the printable english ascii characters.
     **/
    public BeamSearch(){

        printableAscii[0] = 13;
        int asciiChar = 32;
        for(int i = 1; i < printableAscii.length; i++){
            printableAscii[i] = asciiChar;
            asciiChar++;
        }

    }

    public int[] getPrintableAscii(){
        return printableAscii;
    }

    /**
     * A method that encodes a string into an Ascii code represented as a byte array.
     *
     * @param text : The string to be encoded.
     * @return : The encoded version of the string.
     **/
    public static byte[] encodeStringToAscii(String text) {
        return text.getBytes(StandardCharsets.US_ASCII);
    }

    /**
     * A method to convert a char array from Ascii code to a readable string.
     *
     * @param text : The char array to be decoded.
     * @return : The decoded string.
     **/
    public static String decodeFromAscii(byte[] text) {
        String decoded = new String(text);
        return decoded;
    }

    /**
     * A method to check whether the XOR of two bytes is equal to the XOR of a byte of the ciphertext.
     *
     * @param one        : Byte to be XORed.
     * @param two        : Byte to be XORed.
     * @param ciphertext : the ciphertext byte.
     * @return : Returns true or false after evaluating if the XOR of the two bytes equal the
     * ciphertext byte.
     **/
    public static boolean checkXOR(byte one, byte two, byte ciphertext) {
        return (one ^ two) == ciphertext;
    }

    /**
     * A method to generate a random position from the corpus of length.
     * This is then used to test the beam search algorithm.
     *
     * @param n : the chose length of the ciphertext by the user.
     * @return ciphertext : the XOR of two random ciphertexts of length n.
     **/
    public byte[] getCipherText(int n, String corpus) {

        Random rand = new Random();
        int[] randomIndices = new int[2];
        int tmp = 0;

        for (int i = 0; i < 2; i++) {
            tmp = rand.nextInt(corpus.length() - n);
            randomIndices[i] = tmp;
        }

        String c_one = corpus.substring(randomIndices[0], randomIndices[0] + n + 1);
        String c_two = corpus.substring(randomIndices[1], randomIndices[1] + n + 1);

        byte[] c_one_arr = encodeStringToAscii(c_one);
        byte[] c_two_arr = encodeStringToAscii(c_two);
        byte[] ciphertext = new byte[c_one.length()];

        System.out.println(Arrays.toString(c_one_arr));
        System.out.println(c_one);
        System.out.println(Arrays.toString(c_two_arr));
        System.out.println(c_two);

        int length = ciphertext.length;
        for (int i = 0; i < length; i++) {
            ciphertext[i] = (byte) (c_one_arr[i] ^ c_two_arr[i]);
        }

        return ciphertext;

    }

    /**
     * The main algorithm for returning the top (pruneNumber) candidates of plaintexts for the given XOR
     * of ciphertexts.
     *
     * @param corpus        : The corpus used to build language models and used in smoothing.
     * @param ngramModel    : A HashMap containing all ngrams for given corpus, to be used in smoothing.
     * @param languageModel : The language model containing the probability of all ngrams from a corpus.
     * @param n             : The maximum size of ngrams to be stored in the language model.
     * @param pruneNumber   : The number to prune each subsequent candidate list down to.
     * @param ciphertext    : The XOR of two ciphertexts, of which we are trying to recover the plaintexts.
     * @return candidates : The list of top candidate ciphertexts returned as a result of the algorithm.
     **/
    public static ArrayList<Tuple> beamSearch(String corpus, HashMap<String, Integer> ngramModel, HashMap<String,
                                                Double> languageModel, int n, int pruneNumber, byte[] ciphertext) {

        /**
         Adding the log probability of the empty string to the language model.
         This is 100% since every string starts from the empty string.
         **/
        languageModel.put("", Math.log(100));
        HashMap<String, Integer> ngrams = ngramModel;

        // The two candidate strings to be used repeatedly in the algorithm.
        String plaintext_one = "";
        String plaintext_two = "";
        char p_one_next;
        char p_two_next;
        Double cand_prob_one;
        Double cand_prob_two;

        /**
         The ArrayList to hold all the candidates of plaintexts.
         **/
        ArrayList<Tuple> candidates = new ArrayList<Tuple>();

        // Setting all the candidates to initially have empty strings and 0 probability.
        for (int ascii = 0; ascii < ASCII_LENGTH; ascii++) {
            candidates.add(new Tuple("", "", 0.0, 0.0));
        }

        // The main loop controlling the building of the candidates.
        // All candidates, when completed, should be the length of the ciphertext.
        for (int position = 0; position < ciphertext.length; position++) {

            // A temporary ArrayList to store all possible candidates before pruning
            // for top 'pruneNumber' candidates.
            ArrayList<Tuple> temp = new ArrayList<Tuple>();
            System.out.println("Candidates: " + candidates);

            for (int candNum = 0; candNum < candidates.size(); candNum++) {

                cand_prob_one = 0.0;
                cand_prob_two = 0.0;

                // For every candidate in 'candidates' we extend by one 256 times. Once for each Ascii character.
                // We then sort and keep the best 'pruneNumber' candidates before repeating.
                for (int ascii = 0; ascii < ASCII_LENGTH; ascii++) {

                    p_one_next = (char) printableAscii[ascii];
//					System.out.println("1 next: " + (char)ascii);
                    /**
                     By using our character in p_one_next and the character at 'position' in the ciphertext, we can
                     obtain the unique candidate for p_two_next, since p_one_next XOR p_two_next must equal the
                     character of the ciphertext at 'position'.
                     **/
                    p_two_next = (char) (printableAscii[ascii] ^ ciphertext[position]);

                    // Concatenating our two candidate plaintexts with the next character.
                    plaintext_one = (candidates.get(candNum)).getPlaintextOne() + p_one_next;
                    plaintext_two = (candidates.get(candNum)).getPlaintextTwo() + p_two_next;

                    int p_length = (plaintext_one.length() + plaintext_two.length()) / 2;
                    Double p_one_prob = 0.0;
                    Double p_two_prob = 0.0;
                    Double p_one_n_minus_prob = 0.0;
                    Double p_two_n_minus_prob = 0.0;
                    String p_one_ngram = "";
                    String p_two_ngram = "";
                    String pOne_n_minus_one_gram = "";
                    String pTwo_n_minus_one_gram = "";

                    // Logic used to control of the initialisation of the ngrams and (n-1)grams to be used in the
                    // probability logic below.
                    if (p_length > n) {

                        p_one_ngram = plaintext_one.substring(p_length - n, p_length);
                        p_two_ngram = plaintext_two.substring(p_length - n, p_length);
                        pOne_n_minus_one_gram = p_one_ngram.substring(0, p_one_ngram.length());
                        pTwo_n_minus_one_gram = p_two_ngram.substring(0, p_two_ngram.length());

                    } else if (p_length > 1) {

                        p_one_ngram = plaintext_one;
                        p_two_ngram = plaintext_two;
                        pOne_n_minus_one_gram = plaintext_one.substring(0, p_length);
                        pTwo_n_minus_one_gram = plaintext_two.substring(0, p_length);

                    } else {

                        p_one_ngram = plaintext_one;
                        p_two_ngram = plaintext_two;
                        pOne_n_minus_one_gram = plaintext_one.substring(p_length);
                        pTwo_n_minus_one_gram = plaintext_two.substring(p_length);

                    }

                    // Setting up variables for calculating the rolling probability of our candidate plaintexts.
                    // This logic also implements smoothing.
                    if (!languageModel.containsKey(p_one_ngram) && !languageModel.containsKey(p_two_ngram)) {

                        p_one_prob = model.laplaceSmoothing(ngrams, p_one_ngram, corpus);
                        p_two_prob = model.laplaceSmoothing(ngrams, p_two_ngram, corpus);
                        p_one_n_minus_prob = model.laplaceSmoothing(ngrams, pOne_n_minus_one_gram, corpus);
                        p_two_n_minus_prob = model.laplaceSmoothing(ngrams, pTwo_n_minus_one_gram, corpus);
                        System.out.println("both not in lm");
                    } else if (!languageModel.containsKey(p_one_ngram) && languageModel.containsKey(p_two_ngram)) {

                        p_one_prob = model.laplaceSmoothing(ngrams, p_one_ngram, corpus);
                        p_two_prob = languageModel.get(p_two_ngram);
                        p_one_n_minus_prob = model.laplaceSmoothing(ngrams, pOne_n_minus_one_gram, corpus);
                        p_two_n_minus_prob = languageModel.get(pTwo_n_minus_one_gram);
                        System.out.println("p1 not in lm");
                    } else if (languageModel.containsKey(p_one_ngram) && !languageModel.containsKey(p_two_ngram)) {

                        p_one_prob = languageModel.get(p_one_ngram);
                        p_two_prob = model.laplaceSmoothing(ngrams, p_two_ngram, corpus);
                        p_one_n_minus_prob = languageModel.get(pOne_n_minus_one_gram);
                        p_two_n_minus_prob = model.laplaceSmoothing(ngrams, pTwo_n_minus_one_gram, corpus);
                        System.out.println("p2 not in lm");
                    } else {

                        p_one_prob = languageModel.get(p_one_ngram);
                        p_two_prob = languageModel.get(p_two_ngram);
                        p_one_n_minus_prob = languageModel.get(pOne_n_minus_one_gram);
                        p_two_n_minus_prob = languageModel.get(pTwo_n_minus_one_gram);
                        System.out.println("both in lm");
                    }

                    System.out.println("p_length: " + p_length);
                    // Rolling probability of candidate plaintexts are calculated here.
                    // log(Pr(P1 || c_i)) = log(Pr(P1)) + log(Pr(ngram)) - log(Pr((n-1)gram))
                    Tuple t = (candidates.get(candNum));
                    if (p_length == 1) {
                        cand_prob_one = p_one_prob;
                        cand_prob_two = p_two_prob;

                    } else {
                        cand_prob_one = t.getPercentageOne() + p_one_prob - p_one_n_minus_prob;
                        cand_prob_two = t.getPercentageTwo() + p_two_prob - p_two_n_minus_prob;
                    }
                    System.out.println("P1: " + plaintext_one);
                    System.out.println("P1 prob = " + cand_prob_one);
//					System.out.println("p1 prob " + p_one_prob);
//					System.out.println("p2 prob " + p_two_prob);
//					System.out.println("p1 n- prob " + p_one_n_minus_prob);
//					System.out.println("p2 n- prob " + p_two_n_minus_prob);

                    System.out.println("P2: " + plaintext_two);
                    System.out.println("P2 prob = " + cand_prob_two);
//					System.out.println();
                    temp.add(new Tuple(plaintext_one, plaintext_two, cand_prob_one, cand_prob_two));

                }

            }

            // Sorting ('pruneNumber' x 256) candidates.
            Collections.sort(temp, new Comparator<Tuple>() {

                public int compare(Tuple t1, Tuple t2) {
                    Double t1_perc = t1.getPercentageOne() + t1.getPercentageTwo();
                    Double t2_perc = t2.getPercentageOne() + t2.getPercentageTwo();
                    return Double.compare(t1_perc, t2_perc);
                }

            });

            // Setting 'candidates' to be the top 'pruneNumber' plaintext candidates.
            temp = new ArrayList<Tuple>(temp.subList(0, pruneNumber + 1));

            candidates = temp;


        }

        return candidates;

    }

}
