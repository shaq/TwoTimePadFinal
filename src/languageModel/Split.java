package languageModel;

import sun.beans.editors.DoubleEditor;

import java.io.*;
import java.util.Map.Entry;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class that splits files up into chunks then processes them chunk by chunk to generate n-grams.
 * This greatly speeds up the time taken to generate n-grams.
 *
 * @author Shaquille Momoh
 */
public class Split {
    private LanguageModel lm = new LanguageModel();
    private NGram ngram = new NGram();
    private int n;
    private File file;
    private static ConcurrentHashMap<String, Integer> nGramModel;
    private static ConcurrentHashMap<String, Double> languageModel;

    public Split() {
    }

    public Split(File file, ConcurrentHashMap<String, Double> languageModel, int n, boolean isLangModel)  {
        this.languageModel = languageModel;
        this.file = file;
        this.n = n;
        isLangModel = true;
    }

    /**
     * A constructor that initialises the corpus to be processed and a
     * CondurrentHashMap where the n-grams will be stored.
     *
     * @param file          : The corpus to be split.
     * @param languageModel : Where the n-grams will be stored.
     * @param n             : The maximum size of n-gram to be stored.
     */
    public Split(File file, ConcurrentHashMap<String, Integer> languageModel, int n) {
        this.nGramModel = languageModel;
        this.file = file;
        this.n = n;
    }

    /**
     * A method that returns the number of threads to be used given the number of cores available on
     * the execution machine.
     *
     * @return : The number of threads.
     */
    public int getThreadNumber() {
        int availableCores = Runtime.getRuntime().availableProcessors();

        if (availableCores <= 2) {
            return availableCores;
        } else {
            return availableCores - 2;
        }
    }

    /**
     * A method that returns the size of chunks to be taken by each thread, given noumber of threads
     * and length of file.
     *
     * @param noOfThreads : The number of threads to be used.
     * @return : The chunks size.
     * @throws IOException
     */
    public long getFileChunkNumber(int noOfThreads) throws IOException {
        return this.file.length() / noOfThreads;
    }

    public int getNGramChunkNumber(int noOfThreads) {
         return languageModel.size() / noOfThreads;
    }

    public String fileToString(InputStream is) throws IOException {
        StringBuilder builder;
        builder = new StringBuilder();

        int ch;
        while ((ch = is.read()) != -1) {
            builder.append((char) ch);
        }

        return builder.toString();
    }

    /**
     * A method that processes the given portion of the file (start - end).
     * Called simultaneously from several threads.
     *
     * @param start : The start of the chunk to be processed.
     * @param end   : The end of the chunk to be processed.
     * @return : An informative message about the status of the file.
     */
    public String processPartCorpus(long start, long end) throws Exception {
        InputStream is = new FileInputStream(file);
        is.skip(start);

        String text = fileToString(is);

        ngram.addNGrams(nGramModel, text, 1, n);
        System.out.println("Computing the part from " + start + " to " + end);
        Thread.sleep(1000);
        System.out.println("Finished the part from " + start + " to " + end);

        is.close();
        return "Chunk closed";
    }

    public ArrayList<Entry<String,Integer>> getLanguageModelChunk(int start, int end)  {
        ArrayList<Entry<String, Integer>> ngramList = new ArrayList<>(nGramModel.entrySet());
        return new ArrayList<>(ngramList.subList(start, end));
    }

    public Map<String, Double> processPartLanguageModel(int start, int end) throws IOException, InterruptedException {
        ArrayList<Entry<String, Integer>> ngramList = getLanguageModelChunk(start, end);
        InputStream is = new FileInputStream(file);
        String corpus = fileToString(is);
        Map<String, Double> map = lm.createModel(nGramModel, ngramList, corpus);
        System.out.println("Computing the chunk from " + start + " to " + end);
        Thread.sleep(1000);
        System.out.println("Finished the chunk from " + start + " to " + end);
        return map;
    }

    /**
     * A method that creates a task that will process the given portion of the file when executed.
     *
     * @return : The task that is created.
     */
    public Callable<String> processPartTaskCorpus(final long start, final long end) {
        return new Callable<String>() {
            public String call()
                    throws Exception {
                return processPartCorpus(start, end);
            }
        };
    }

    public Callable<Map<String, Double>> processPartTaskNGram(final int start, final int end) {
        return new Callable<Map<String, Double>>() {
            public Map<String, Double> call()
                    throws Exception {
                return processPartLanguageModel(start, end);
            }
        };
    }


    /**
     * A method that splits a file up into given chunks.
     * It will then process each chunk in parallel using the given number of threads.splir
     *
     * @param noOfThreads : The number of threads to be run in parallel
     * @param chunkSize   : The chunk size that each thread will process
     * @return : A ConcurrentHashMap containing all the n-grams for the given file, along with their counts.
     * @throws InterruptedException
     */
    public ConcurrentHashMap<String, Integer> processAllCorpus(int noOfThreads, long chunkSize) throws InterruptedException {
        int count = (int) ((file.length() + chunkSize - 1) / chunkSize);
        List<Callable<String>> tasks = new ArrayList<Callable<String>>(count);
        for (int i = 0; i < count; i++)
            tasks.add(processPartTaskCorpus(i * chunkSize, Math.min(file.length(), (i + 1) * chunkSize)));
        ExecutorService es = Executors.newFixedThreadPool(noOfThreads);

        es.invokeAll(tasks);
        es.shutdown();
        return nGramModel;
    }


    public ConcurrentHashMap<String, Double> processAllNGrams(int noOfThreads, int chunkSize) throws InterruptedException,
            ExecutionException {

        int count = noOfThreads;
        List<Callable<Map<String, Double>>> tasks = new ArrayList<>(count);
        for(int i = 0; i < count; i++)
            tasks.add(processPartTaskNGram(i * chunkSize, Math.min(languageModel.size(), (i + 1) * chunkSize)));
        ExecutorService es = Executors.newFixedThreadPool(noOfThreads);

        List<Future<Map<String, Double>>> results = es.invokeAll(tasks);

        for(Future<Map<String, Double>> result : results){
            languageModel.putAll(result.get());
        }

        es.shutdown();
        return languageModel;

    }

}
