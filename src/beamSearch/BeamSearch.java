package beamSearch;

import languageModel.LanguageModel;
import languageModel.NGram;
import languageModel.NGramModel;
import languageModel.ParseCorpus;

import java.nio.charset.StandardCharsets;
import java.util.*;


/**
 * The class responsible for recovering the plaintexts from a random XOR of ciphertexts
 * in the corpus.
 *
 * @author Shaquille Momoh
 **/
public class BeamSearch {

    private static final int ASCII_LENGTH = 256;

    // Creating an array that will hold asci codes for all printable english ascii characters.
    private static int[] printableAscii = new int[96];
    private static ParseCorpus parse = new ParseCorpus();
    private static LanguageModel lm = new LanguageModel();
    private static NGramModel model = new NGramModel();
    private static NGram ngram = new NGram();

    /**
     * A constructor that initialises the array to hold the ascii codes for all printable english ascii text.
     */
    public BeamSearch() {
        // Setting first element to be carriage return.
        printableAscii[0] = 13;
        int ascii = 32;

        for (int i = 1; i < printableAscii.length; i++) {
            printableAscii[i] = ascii;
            ascii++;
        }
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
    public static byte[] getCipherText(int n, String corpus) {

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

        // Testing code that prints out the plaintexts and their byte codes.
        System.out.println(Arrays.toString(c_one_arr));
        System.out.println(c_one);
        System.out.println(Arrays.toString(c_two_arr));
        System.out.println(c_two);

        try {
            Thread.sleep(3000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

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
        Double emptyStringProb = Math.log(1.0);
        languageModel.put("", emptyStringProb);
        HashMap<String, Integer> ngrams = ngramModel;

        // The two candidate strings to be used repeatedly in the algorithm.
        String plaintext_one = "";
        String plaintext_two = "";
        char p_one_next;
        char p_two_next;
        Double cand_prob_one = 0.0;
        Double cand_prob_two = 0.0;
        Double p_one_prob = 0.0;
        Double p_two_prob = 0.0;
        Double p_one_nminus_prob = 0.0;
        Double p_two_nminus_prob = 0.0;
//        Double p_one_next_prob = 0.0;
//        Double p_two_next_prob = 0.0;
        String p_one_ngram = "";
        String p_two_ngram = "";
        String pOne_n_minus_one_gram = "";
        String pTwo_n_minus_one_gram = "";

        /**
         The ArrayList to hold all the candidates of plaintexts.
         **/
        ArrayList<Tuple> candidates = new ArrayList<Tuple>();

        // Setting all the candidates to initially have empty strings and 0 probability.
        for (int ascii = 0; ascii < printableAscii.length; ascii++) {
            candidates.add(new Tuple("", "", emptyStringProb, emptyStringProb));
        }

        // The main loop controlling the building of the candidates.
        // All candidates, when completed, should be the length of the ciphertext.
        for (int position = 0; position < ciphertext.length; position++) {

            // A temporary ArrayList to store all possible candidates before pruning
            // for top 'pruneNumber' candidates.
            ArrayList<Tuple> temp = new ArrayList<Tuple>();
            System.out.println("Candidates: " + candidates);

            for (int candNum = 0; candNum < candidates.size(); candNum++) {

                Tuple t = (candidates.get(candNum));

//                cand_prob_one = 0.0;
//                cand_prob_two = 0.0;

                // For every candidate in 'candidates' we extend by one 256 times. Once for each Ascii character.
                // We then sort and keep the best 'pruneNumber' candidates before repeating.
                for (int ascii = 0; ascii < printableAscii.length; ascii++) {

                    p_one_next = (char) printableAscii[ascii];
//					System.out.println("1 next: " + (char)ascii);
                    /**
                     By using our character in p_one_next and the character at 'position' in the ciphertext, we can
                     obtain the unique candidate for p_two_next, since p_one_next XOR p_two_next must equal the
                     character of the ciphertext at 'position'.
                     **/
                    p_two_next = (char) (printableAscii[ascii] ^ ciphertext[position]);

                    // Concatenating our two candidate plaintexts with the next character.
                    plaintext_one = t.getPlaintextOne() + p_one_next;
                    plaintext_two = t.getPlaintextTwo() + p_two_next;


                    Double[] candProb = model.calculateCandidateProbability(n, corpus, ngrams, languageModel,
                            plaintext_one, plaintext_two, t);

                    System.out.println("1: " + plaintext_one);
                    System.out.println("P1 = " + candProb[0]);

                    System.out.println("2: " + plaintext_two);
                    System.out.println("P2 = " + candProb[1]);


//					System.out.println();
                    temp.add(new Tuple(plaintext_one, plaintext_two, candProb[0], candProb[1]));

                }

            }

//            System.out.println("++++++++++++++++++++ Before sorting ++++++++++++++++++++\n" + temp);
            // Sorting ('pruneNumber' x 96) candidates.
            Collections.sort(temp, new Comparator<Tuple>() {
                @Override
                public int compare(Tuple t1, Tuple t2) {
                    Double t1_perc = t1.getPercentageOne() + t1.getPercentageTwo();
                    Double t2_perc = t2.getPercentageOne() + t2.getPercentageTwo();
                    return Double.compare(t1_perc, t2_perc);
                }

            });

            Collections.reverse(temp);
//            Collections.sort(temp, Collections.reverseOrder(new Tuple.TupleComparator()));

//            System.out.println("++++++++++++++++++++ After sorting ++++++++++++++++++++\n" + temp);
            // Setting 'candidates' to be the top 'pruneNumber' plaintext candidates.
            temp = new ArrayList<Tuple>(temp.subList(0, pruneNumber));

            candidates = temp;


        }

        return candidates;

    }


}
