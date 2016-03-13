package languageModelTests;

import languageModel.NGram;
import languageModel.ParseCorpus;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestNGram {

    NGram ngram;
    HashMap<String, Double> model;
    HashMap<String, Integer> ngrams;

    @Before
    public void setUp() throws Exception {
        ngram = new NGram();
        model = new HashMap<String, Double>();
        ngrams = new HashMap<String, Integer>();
    }

    @Test
    public void testGetN() {
        Integer n = ParseCorpus.getN();
        Integer expected = Integer.parseInt("3");
        assertEquals(expected, n);
    }

//    @Test
//    public void testAddNGrams() {
//        ngrams.put("test", 1);
//        assertEquals(ngrams, NGram.addNGrams("test", 4, 4));
//    }
//
//    @Test
//    public void testGetNumberOfNGrams() {
//        ngrams.put("This is a test", 3);
//        int expected = 3;
//        assertEquals(expected, NGram.getNumberOfNGrams(ngrams));
//    }

}
