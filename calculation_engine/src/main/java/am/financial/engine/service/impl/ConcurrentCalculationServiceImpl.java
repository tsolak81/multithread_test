package am.financial.engine.service.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.financial.engine.service.CalculationService;
import am.financial.engine.service.common.Utils;
import am.financial.engine.service.exceptions.CalculationServiceRuntimeException;
import am.financial.engine.service.model.CalculationFilter;
import am.financial.engine.service.model.CalculationResult;
import am.financial.engine.service.model.DataRecord;
import am.financial.engine.service.model.DataRecordComparatorByDate;
import am.financial.engine.service.model.TaskCalculationResult;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public class ConcurrentCalculationServiceImpl implements CalculationService {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentCalculationServiceImpl.class);

	private final Charset charset = Charset.forName("UTF-8");

	private final int THREAD_POOL_SIZE;

	/**
	 * Number of lines which will be processed by a task, by default 100000
	 */
	private int nrOfLinesPerTask = 100000;

	public ConcurrentCalculationServiceImpl() {
		THREAD_POOL_SIZE = Runtime.getRuntime().availableProcessors();
		LOGGER.debug("Number of processors available to the jvm: {}", THREAD_POOL_SIZE);
	}

	@Override
	public CalculationResult calculateMeanForInstruments(final String filePath, final CalculationFilter filter) {
		LOGGER.info("Calculation method called with file path '{}' and filter: {}", filePath, filter);
		final Long t1 = System.currentTimeMillis();
		Path inputFilePath = getInputFilePath(filePath);
		long linesCount = 0;
		int nrOfTasks = 0;
		ThreadPoolExecutor threadPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_POOL_SIZE);
		CompletionService<TaskCalculationResult> completionService = new ExecutorCompletionService<>(threadPool);
		LinkedList<String> linesQueue = new LinkedList<String>();
		try (BufferedReader reader = Files.newBufferedReader(inputFilePath, charset)) {
			String line;
			while ((line = reader.readLine()) != null) {
				linesCount++;
				linesQueue.offer(line);
				// If enough number of lines loaded, pass them for processing
				if (linesQueue.size() >= nrOfLinesPerTask) {
					LOGGER.debug("Number of readen lines: {}, number of created tasks: {}", linesCount, nrOfTasks);
					// Do not submit new task while active threads count equals to threads pool size
					while (threadPool.getActiveCount() == THREAD_POOL_SIZE) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
						}
					}
					completionService.submit(new CalculationTask(linesQueue, filter));
					nrOfTasks++;
					linesQueue = new LinkedList<String>();
				}
			} // end of while
				// Process remaining lines
			if (linesQueue.size() > 0) {
				completionService.submit(new CalculationTask(linesQueue, filter));
				nrOfTasks++;
			}
		} catch (IOException e) {
			LOGGER.error("File reading error: {}", e.toString());
			throw new CalculationServiceRuntimeException(e.getMessage(), e);
		}
		threadPool.shutdown();

		try {
			int timeout = 120;
			boolean terminated = threadPool.awaitTermination(timeout, TimeUnit.SECONDS);
			if (!terminated) {
				throw new CalculationServiceRuntimeException("Tasks failed terminate in spesified timeout: " + timeout
						+ "s.");
			}
		} catch (InterruptedException e) {
			LOGGER.error(e.toString());
		}
		CalculationResult calculationResult = calculateResult(nrOfTasks, completionService, linesCount);
		final Long t2 = System.currentTimeMillis();
		LOGGER.info("Calculation took {} ms, processed result: {}", new Object[] { t2 - t1, calculationResult });
		LOGGER.debug("Top ten newest records: {}", calculationResult.getNewestRecords());
		return calculationResult;
	}

	private CalculationResult calculateResult(int nrOfTasks,
			CompletionService<TaskCalculationResult> completionService, long linesCount) {
		PriorityQueue<DataRecord> newestRecords = new PriorityQueue<>(11, new DataRecordComparatorByDate());
		long processedRecords = 0;
		BigDecimal valuesSum = new BigDecimal(0);

		for (int i = 0; i < nrOfTasks; i++) {
			try {
				final Future<TaskCalculationResult> future = completionService.take();
				TaskCalculationResult taskCalculationResult = future.get();
				processedRecords += taskCalculationResult.getProcessedRecords();
				valuesSum = valuesSum.add(taskCalculationResult.getValuesSum());
				calculateNewestRecords(newestRecords, taskCalculationResult.getNewestRecords());
			} catch (InterruptedException | ExecutionException e) {
				LOGGER.error("Error occured at getting task execution result {}", e.toString());
				throw new CalculationServiceRuntimeException(e.getMessage(), e);
			}
		}
		CalculationResult calculationResult = new CalculationResult(linesCount, processedRecords, valuesSum,
				newestRecords);
		return calculationResult;
	}

	/**
	 * Add to 'newestRecords' provided 'recordsToAdd', and after remove older ones, to keep top 10 newest
	 * 
	 * @param newestRecords
	 * @param recordsToAdd
	 */
	private void calculateNewestRecords(PriorityQueue<DataRecord> newestRecords, PriorityQueue<DataRecord> recordsToAdd) {
		if (recordsToAdd != null) {
			newestRecords.addAll(recordsToAdd);
			while (newestRecords.size() > 10) {
				newestRecords.poll();
			}
		}
	}

	private Path getInputFilePath(String filePath) {
		Utils.assertNotEmpty(filePath, "Valid input file path must be provided");
		Path inputFile = Paths.get(filePath);
		if (!Files.exists(inputFile)) {
			throw new CalculationServiceRuntimeException(String.format("File '%s' not exists",
					inputFile.toAbsolutePath()));
		}
		if (!Files.isReadable(inputFile)) {
			throw new CalculationServiceRuntimeException(String.format("File '%s' not readable",
					inputFile.toAbsolutePath()));
		}
		return inputFile;
	}

	public int getNrOfLinesPerTask() {
		return nrOfLinesPerTask;
	}

	public void setNrOfLinesPerTask(int nrOfLinesPerTask) {
		this.nrOfLinesPerTask = nrOfLinesPerTask;
	}

}
