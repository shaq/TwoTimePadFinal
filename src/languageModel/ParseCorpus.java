package languageModel;

import javax.swing.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;

/**
 * A class that allows a user to select the file they want to parse,
 * via  a GUI. The selected file is then processed concurrently into n-grams
 * which are then stored concurrently into a ConcurrentHashMap.
 *
 * @author Shaquille Momoh
 */
public class ParseCorpus {
    /**
     * chooser allows users to select a file by navigating through
     * directories
     */
    private static JFileChooser chooser = new JFileChooser(System.getProperties().getProperty("user.dir"));

    /**
     * Read a file into a string.
     *
     * @return returns a string containing all text in the file.
     */
    public static String readFile(Scanner input) {
        return input.useDelimiter("\\Z").next();
    }

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
     * A method to allow user to input the value of n, for generating n-grams,
     * via  GUI.
     *
     * @return n : he value of n, for generating n-grams.
     */
    public static Integer getN() {
        String input = JOptionPane.showInputDialog("Enter N:", "3");
        Integer n = Integer.parseInt(input);
        return n;
    }


    /**
     * Brings up chooser for user to select a file or  a directory to be parsed.
     *
     * @return : The file or directory that was chosen.
     * @throws IOException
     */
    public File getCorpus() throws IOException {
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setDialogTitle("Select a File or a Directory as your Corpus");
        int retval = chooser.showOpenDialog(null);

        if (retval == JFileChooser.APPROVE_OPTION) {
            File f = chooser.getSelectedFile();
            return f;
        }

        return null;

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
