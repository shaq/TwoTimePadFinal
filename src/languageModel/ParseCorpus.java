package languageModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * A class that text file parses a text file, and processes it by
 * generating n-grams, using the concurrency methods from the class 'Split'.
 *
 * @author Shaquille Momoh
 */
public class ParseCorpus {

    /**
     * Converts an file to String.
     *
     * @param f : The file to be converted.
     * @return The converted file as a String.
     * @throws IOException
     */
    public String fileToString(File f) throws IOException {

        InputStream is = null;
        StringBuilder builder;
        builder = new StringBuilder();

        int ch;
        if(f.isDirectory()){
            File[] dir = f.listFiles();
            for (int i = 0; i < dir.length; i++) {
                is = new FileInputStream(dir[i]);
                while ((ch = is.read()) != -1) {
                    builder.append((char) ch);
                }
            }
        } else {
            is = new FileInputStream(f);
            while ((ch = is.read()) != -1) {
                builder.append((char) ch);
            }
        }
        return builder.toString();
    }

    /**
     * A method used to process the corpus into n-grams concurrently.
     *
     * @return The processed n-grams stored in a ConcurrentHashMap
     * @throws IOException, InterruptedException
     */
    public ConcurrentHashMap<String, Integer> processFiles(File f, int n) throws IOException, InterruptedException,
            ExecutionException {

        long corpusLength = 0;
        ConcurrentHashMap<String, Integer> languageModel = new ConcurrentHashMap<>();

        if (f.isDirectory()) {
            File[] dir = f.listFiles();
            for (int i = 0; i < dir.length; i++) {
                corpusLength += dir[i].length();
            }
        } else {
            corpusLength = f.length();
        }

        Split split = new Split(f, languageModel, n);
        int noThreads = split.getThreadNumber();
        long chunks = split.getChunkNumber(noThreads);
        split.processAll(noThreads, chunks, (int)corpusLength);
        return languageModel;

    }
}
