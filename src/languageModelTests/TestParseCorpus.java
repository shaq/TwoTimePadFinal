package languageModelTests;

import languageModel.ParseCorpus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class TestParseCorpus {

    private Scanner sc;
    private ParseCorpus parse;
    private File f;

    @Before
    public void setUp() throws Exception {
        f = new File("src/parse_test.txt");
        sc = new Scanner(f);
        parse = new ParseCorpus();
    }

    @Test
    public void test() throws IOException {
        assertEquals(parse.fileToString(f), "This is a test");
    }

}
