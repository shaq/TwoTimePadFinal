package languageModelTests;

import languageModel.ParseCorpus;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

public class TestParseCorpus {

    private Scanner sc;
    private File f;

    @Before
    public void setUp() throws Exception {
        f = new File("src/parse_test.txt");
        sc = new Scanner(f);
    }

    @Test
    public void test() throws FileNotFoundException {
        assertEquals(ParseCorpus.readFile(sc), "This is a test");
    }

}
