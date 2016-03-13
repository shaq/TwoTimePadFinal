package languageModel;

import java.io.*;
import java.util.*;
import java.util.concurrent.*;

/**
 * A class that splits files up into chunks then processes them chunk by chunk to generate n-grams.
 * This greatly speeds up the time taken to generate n-grams.
 *
 * @author Shaquille Momoh
 */
public class Split {
    NGram ngram = new NGram();
    private int n;
	private File file;
    private ConcurrentHashMap<String, Integer> languageModel;

    public Split(){}

    /**
     * A constructor that initialises the corpus to be processed and a
     * CondurrentHashMap where the n-grams will be stored.
     * @param file : The corpus to be split.
     * @param languageModel : Where the n-grams will be stored.
     * @param n : The maximum size of n-gram to be stored.
     */
	public Split(File file, ConcurrentHashMap<String, Integer> languageModel, int n) {
        this.file = file;
        this.languageModel = languageModel;
        this.n = n;
	}

    /**
     * A method that returns the number of threads to be used given the number of cores available on
     * the execution machine.
     * @return : The number of threads.
     */
	public int getThreadNumber(){
		int availableCores = Runtime.getRuntime().availableProcessors();

		if(availableCores <= 2) {
			return availableCores;
		} else {
			return availableCores - 2;
		}
	}

    /**
     * A method that returns the size of chunks to be taken by each thread, given noumber of threads
     * and length of file.
     * @param noThreads : The number of threads to be used.
     * @return : The chunks size.
     * @throws IOException
     */
	public long getChunkNumber(int noThreads) throws IOException {
		return this.file.length() / noThreads;
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
     *
     * A method that processes the given portion of the file (start - end).
     * Called simultaneously from several threads.
     *
     * @param start : The start of the chunk to be processed.
     * @param end : The end of the chunk to be processed.
     * @return : An informative message about the status of the file.
     */
	public String processPart(long start, long end) throws Exception {
        InputStream is = new FileInputStream(file);
        is.skip(start);

        String text = fileToString(is);

        ngram.addNGrams(languageModel, text, 1, n);
		System.out.println("Computing the part from " + start + " to " + end);
		Thread.sleep(1000);
		System.out.println("Finished the part from " + start + " to " + end);

		is.close();
		return "Chunk closed";
	}

    /**
     * A method that creates a task that will process the given portion of the file when executed.
     *
     * @return : The task that is created.
     */
	public Callable<String> processPartTask(final long start, final long end) {
		return new Callable<String>() {
			public String call()
				throws Exception
			{
				return processPart(start, end);
			}
		};
	}


    /**
     * A method that splits a file up into given chunks.
     * It will then process each chunk in parallel using the given number of threads.
     * @param noOfThreads : The number of threads to be run in parallel
     * @param chunkSize : The chunk size that each thread will process
     * @return : A ConcurrentHashMap containing all the n-grams for the given file, along with their counts.
     * @throws InterruptedException
     */
	public ConcurrentHashMap<String, Integer> processAll(int noOfThreads, long chunkSize) throws InterruptedException {
		int count = (int)((file.length() + chunkSize - 1) / chunkSize);
		List<Callable<String>> tasks = new ArrayList<Callable<String>>(count);
		for(int i = 0; i < count; i++)
			tasks.add(processPartTask(i * chunkSize, Math.min(file.length(), (i+1) * chunkSize)));
		ExecutorService es = Executors.newFixedThreadPool(noOfThreads);

		es.invokeAll(tasks);
		es.shutdown();
        return languageModel;
	}


}
