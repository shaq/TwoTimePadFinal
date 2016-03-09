package languageModelTests;

import languageModel.LanguageModel;
import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;

public class TestLanguageModel {

    HashMap<String, Double> languageModel;
    HashMap<String, Integer> ngrams;
    private LanguageModel model;

    @Before
    public void setUp() throws Exception {
        model = new LanguageModel();
        languageModel = new HashMap<String, Double>();
        ngrams = new HashMap<String, Integer>();
    }

    @Test
    public void testCreateModel() {
        languageModel.put("test1", Math.log(2.0));
        languageModel.put("text2", Math.log(3.0));
        ngrams.put("test1", 2);
        ngrams.put("text2", 3);
        assertEquals(languageModel, model.createModel(ngrams, null));
    }

}
