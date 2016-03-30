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

    private static final int ASCII_LENGTH = 256;

    // Creating an array that will hold ascii codes for all printable english ascii characters.
    private static int[] printableAscii = new int[96];
    private static NGramModel model = new NGramModel();
    private static Tuple tuple = new Tuple();

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
     * A method that removes duplicate tuples from the given ArrayList.
     * @param candidates
     */
    public void removeDuplicateCandidates(ArrayList<Tuple> candidates) {
        for (int i = 0; i < candidates.size(); i++) {
            for (int j = i + 1; j < candidates.size(); j++) {
                if (tuple.equals(candidates.get(i), candidates.get(j))) {
                    candidates.remove(j);
                }
            }
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
     * @param ciphertext : The ciphertext byte.
     * @return : Returns true or false after evaluating if the XOR of the two
     * bytes equal the
     * ciphertext byte.
     **/
    public static boolean checkXOR(byte one, byte two, byte ciphertext) {
        return (one ^ two) == ciphertext;
    }

    /**
     * A method that generates k strings from the random positons in the given corpus.
     *
     * @param ptxtLength : The length of the strings to be generated.
     * @param k          : The number of strings to be generated.
     * @param corpus     : The corpus to get the strings from.
     * @return : An array from the strings from random positions.
     */
    public static String[] getPlaintextValues(int ptxtLength, int k, String corpus) {
        Random rand = new Random();
        String[] plaintexts = new String[k];
        int randVal = 0;

        for (int i = 0; i < k; i++) {
            randVal = rand.nextInt(corpus.length() - ptxtLength);
            plaintexts[i] = corpus.substring(randVal, randVal + ptxtLength);
        }

        return plaintexts;
    }

    /**
     * A method to returns the xor of k random plaintexts taken from the corpus.
     * This is then used to test the beam search algorithm.
     *
     * @param plaintexts : The array of plaintexts taken from a random position in the corpus.
     * @param k : The number of times the keystream is reused.
     * @return : The XOR of two random ciphertexts of length n.
     **/
    public static byte[] getXOROfPlaintext(String[] plaintexts, int k) {

        byte[][] xorOfPlaintexts = new byte[k][];
        int ptxtLength = plaintexts[0].length();

        for (int i = 0; i < k; i++) {
            xorOfPlaintexts[i] = encodeStringToAscii(plaintexts[i]);
//            System.out.println("P" + i + " " + plaintexts[i]);
        }

        byte[] ptxtByteArr = new byte[ptxtLength];


        for (int i = 0; i < ptxtLength; i++) {
            byte tmp = 0;
            for (int j = 0; j < k; j++) {
                tmp = (byte) (tmp ^ xorOfPlaintexts[j][i]);
            }
            ptxtByteArr[i] = tmp;
        }

        return ptxtByteArr;

    }

    /**
     * The main algorithm for returning the top (pruneNumber) candidates of plaintexts for the given XOR
     * of ciphertexts.
     *
     * @param corpus        : The corpus used to build language models and used in smoothing.
     * @param ngramArr      : A HashMap containing all ngrams for given corpus, to be used in smoothing.
     * @param languageModel : The language model containing the probability of all ngrams from a corpus.
     * @param n             : The maximum size of ngrams to be stored in the language model.
     * @param pruneNumber   : The number to prune each subsequent candidate list down to.
     * @param ciphertext    : The XOR of two ciphertexts, of which we are trying to recover the plaintexts.
     * @return candidates : The list of top candidate ciphertexts returned as a result of the algorithm.
     **/
    public ArrayList<Tuple> beamSearch(String corpus, Map<String, Integer>[] ngramArr, HashMap<String,
            Double> languageModel, int n, int pruneNumber, byte[] ciphertext) {


        // Adding the log probability of the empty string to the language model.
        // This is always 1.0 since every string starts from the empty string.
        Double emptyStringProb = 1.0;
        languageModel.put("", emptyStringProb);

        // The two candidate strings to be used repeatedly in the algorithm.
        String plaintext_one;
        String plaintext_two;
        char p_one_next;
        char p_two_next;
        int vocabSize = model.getVocabSize(ngramArr);



        // The ArrayList to hold all the candidates of plaintexts.
        ArrayList<Tuple> candidates = new ArrayList<Tuple>();

        // Setting all the candidates to initially have empty strings and 0 log probability.
        for (int ascii = 0; ascii < printableAscii.length; ascii++) {
            candidates.add(new Tuple("", "", Math.log(emptyStringProb), Math.log(emptyStringProb)));
        }

        // The main loop controlling the building of the candidates.
        // All candidates, when completed, should be the length of the ciphertext.
        for (int position = 0; position < ciphertext.length; position++) {

            // A temporary ArrayList to store all possible candidates before pruning
            // for top 'pruneNumber' candidates.
            ArrayList<Tuple> temp = new ArrayList<Tuple>();

            for (int candNum = 0; candNum < candidates.size(); candNum++) {

                Tuple t = (candidates.get(candNum));

                // For every candidate in 'candidates' we extend by one 256 times. Once for each Ascii character.
                // We then sort and keep the best 'pruneNumber' candidates before repeating.
                for (int ascii = 0; ascii < printableAscii.length; ascii++) {

                    p_one_next = (char) printableAscii[ascii];


                    // By using our character in p_one_next and the character at 'position' in the ciphertext, we can
                    // obtain the unique candidate for p_two_next, since p_one_next XOR p_two_next must equal the
                    // character of the ciphertext at 'position'.
                    p_two_next = (char) (printableAscii[ascii] ^ ciphertext[position]);

                    // Concatenating our two candidate plaintexts with the next character.
                    plaintext_one = t.getPlaintextOne() + p_one_next;
                    plaintext_two = t.getPlaintextTwo() + p_two_next;

                    // Calculating the probability of each plaintext in the current candidate and storing themm in
                    // an array.
                    Double[] candProb = model.calculateCandidateProbability(n, corpus, ngramArr, languageModel, t,
                            plaintext_one, plaintext_two, vocabSize);
//
//                    if (candProb[0] == 0.0 || candProb[1] == 0.0) {
//
//                    }

                    temp.add(new Tuple(plaintext_one, plaintext_two, candProb[0], candProb[1]));

                }

            }

            // Sorting the list of candidates by their probability.
            Collections.sort(temp, new Comparator<Tuple>() {
                @Override
                public int compare(Tuple t1, Tuple t2) {
                    Double t1_perc = t1.getProbOne() + t1.getProbTwo();
                    Double t2_perc = t2.getProbOne() + t2.getProbTwo();
                    return Double.compare(t1_perc, t2_perc);
                }

            });

            // Putting the list in descending order - from most probable to least.
            Collections.reverse(temp);

            // Removing duplicates from the list of all candidates before it is pruned.
            removeDuplicateCandidates(temp);

            // Setting 'candidates' to be the top 'pruneNumber' plaintext candidates but only if the .
            if (pruneNumber < temp.size()) {
                temp = new ArrayList<>(temp.subList(0, pruneNumber));
            }

            candidates = temp;

        }

        return candidates;

    }


}
