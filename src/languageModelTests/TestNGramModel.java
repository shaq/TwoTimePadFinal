package languageModelTests;

import languageModel.NGramModel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestNGramModel {

    HashMap<String, Double> languageModel;
    HashMap<String, Integer> ngrams;
    private NGramModel model;

    @Before
    public void setUp() throws Exception {
        model = new NGramModel();
        languageModel = new HashMap<String, Double>();
        ngrams = new HashMap<String, Integer>();
    }

    @Test
    public void testEstimateProbabilities() {
        ngrams.put("test1", 1);
        ngrams.put("test2", 2);
        Double expected = Math.log(1.0 / 2.0);
        assertEquals(expected, model.estimateProbability(ngrams, "test1", null));
    }

}
