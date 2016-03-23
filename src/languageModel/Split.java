package languageModel;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class that splits files up into chunks then processes them chunk by chunk to generate n-grams.
 * This greatly speeds up the time taken to generate n-grams.
 *
 * @author Shaquille Momoh
 */
public class Split {
    private NGram ngram = new NGram();
    private ParseCorpus parse = new ParseCorpus();
    private int n;
    private File file;
    private ConcurrentHashMap<String, Integer> languageModel;

    public Split() {
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
        this.file = file;
        this.languageModel = languageModel;
        this.n = n;
    }

    /**
     * A method that returns an Array containing maps of n-grams from 1 to n.
     *
     * @param map : The map to split.
     * @param n   : The maximum n-gram size.
     * @return
     */
    public ConcurrentHashMap<String, Integer>[] splitMap(Map<String, Integer> map, int n) {

        ConcurrentHashMap<String, Integer>[] mapArr = new ConcurrentHashMap[n];
        for (int i = 0; i < n; i++) {
            mapArr[i] = new ConcurrentHashMap<>();
        }

        Set<Map.Entry<String, Integer>> entries = map.entrySet();

        for (Map.Entry<String, Integer> entry : entries) {
            int entryLength = entry.getKey().length();
            String key = entry.getKey();
            Integer val = entry.getValue();
            ConcurrentHashMap<String, Integer> tmpMap = mapArr[entryLength - 1];
            tmpMap.put(key, val);
            mapArr[entryLength - 1] = tmpMap;
        }

        return mapArr;
    }

    /**
     * A method that prints out the given array of maps in a clear format.
     * @param mapArr
     */
    public void mapArrToString(Map<String, Integer>[] mapArr) {
        for (int map = 0; map < mapArr.length; map++) {
            int n = map + 1;
            System.out.println(n + "-grams: \n" + mapArr[map]);
        }
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
            System.out.println("available cores: " + availableCores);
            return availableCores;
        } else {
            int threads = availableCores - 2;
            System.out.println("Thread number: " + threads);
            return threads;
        }
    }

    /**
     * A method that returns the size of chunks to be taken by each thread, given noumber of threads
     * and length of file.
     *
     * @param noThreads : The number of threads to be used.
     * @return : The chunks size.
     * @throws IOException
     */
    public long getChunkNumber(int noThreads) throws IOException {
        long corpusLength = 0;
        if(file.isDirectory()){
            File[] dir = file.listFiles();
            for (int i = 0; i < dir.length; i++) {
                corpusLength += dir[i].length();
            }
        } else corpusLength = file.length();

        long chunks = corpusLength / noThreads;
        System.out.println("Chunk number:" + chunks);
        return chunks;
    }

    /**
     * A method that processes the given portion of the file (start - end).
     * Called simultaneously from several threads.
     *
     * @param start : The start of the chunk to be processed.
     * @param end   : The end of the chunk to be processed.
     * @return : A HashMap containing ngrams and counts for the part processed.
     */
    public ConcurrentHashMap<String, Integer> processPart(long start, long end) throws Exception {

        String text = parse.fileToString(file);
        ConcurrentHashMap<String, Integer> ngrams = new ConcurrentHashMap<>();
        ngram.addNGrams(ngrams, text, 1, n);
        System.out.println("Computing the part from " + start + " to " + end);
        Thread.sleep(1000);
        System.out.println("Finished the part from " + start + " to " + end);

        return ngrams;
    }

    /**
     * A method that creates a task that will process the given portion of the file when executed.
     *
     * @return : The task that is created.
     */
    public Callable<ConcurrentHashMap<String, Integer>> processPartTask(final long start, final long end) {
        return new Callable<ConcurrentHashMap<String, Integer>>() {
            public ConcurrentHashMap<String, Integer> call()
                    throws Exception {
                return processPart(start, end);
            }
        };
    }


    /**
     * A method that splits a file up into given chunks.
     * It will then process each chunk in parallel using the given number of threads.
     * Each thread will create a HashMap for the chunk that was just processed.
     * The first thread that is created will hold the consolidated counts of all ngrams in the corpus, which is
     * then returned.
     *
     * @param noOfThreads : The number of threads to be run in parallel
     * @param chunkSize   : The chunk size that each thread will process
     * @return : A ConcurrentHashMap containing all the n-grams for the given file, along with their counts.
     * @throws InterruptedException
     */
    public ConcurrentHashMap<String, Integer> processAll(int noOfThreads, long chunkSize, int corpusLength)
            throws InterruptedException, ExecutionException {
        int count = (int) ((corpusLength + chunkSize - 1) / chunkSize);
        List<Callable<ConcurrentHashMap<String, Integer>>> tasks = new ArrayList<>(count);
        for (int i = 0; i < count; i++)
            tasks.add(processPartTask(i * chunkSize, Math.min(corpusLength, (i + 1) * chunkSize)));
        ExecutorService es = Executors.newFixedThreadPool(noOfThreads);

        List<Future<ConcurrentHashMap<String, Integer>>> results = es.invokeAll(tasks);
        ArrayList<ConcurrentHashMap<String, Integer>> list = new ArrayList<>();
        es.shutdown();


        for (Future<ConcurrentHashMap<String, Integer>> result : results) {
            list.add(result.get());
        }

        languageModel.putAll(list.get(0));
        return languageModel;

    }


}
