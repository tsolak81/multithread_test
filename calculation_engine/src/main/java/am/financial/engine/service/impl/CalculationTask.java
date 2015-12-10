package am.financial.engine.service.impl;

import java.math.BigDecimal;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.financial.engine.service.common.Utils;
import am.financial.engine.service.model.CalculationFilter;
import am.financial.engine.service.model.DataRecord;
import am.financial.engine.service.model.DataRecordComparatorByDate;
import am.financial.engine.service.model.TaskCalculationResult;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 * 
 *       Processes provided record lines and return calculation result
 *
 */
class CalculationTask implements Callable<TaskCalculationResult> {

	private static final Logger LOGGER = LoggerFactory.getLogger(CalculationTask.class);

	private CalculationFilter filter;

	private Queue<String> linesQueue;

	public CalculationTask(Queue<String> linesQueue, CalculationFilter filter) {
		Utils.assertNotNull(linesQueue, "linesQueue must not be null");
		this.linesQueue = linesQueue;
		this.filter = filter;
	}

	@Override
	public TaskCalculationResult call() throws Exception {
		PriorityQueue<DataRecord> newestRecords = new PriorityQueue<>(11, new DataRecordComparatorByDate());
		BigDecimal valuesSum = new BigDecimal(0);
		final int linesCount = linesQueue.size();
		long processedRecordsCount = 0;
		String line;
		while ((line = linesQueue.poll()) != null) {
			try {
				DataRecord dataRecord = new DataRecord(line);
				// If filter is not null, check whether record matches to filter
				if (filter == null || filter.checkFilter(dataRecord)) {
					processedRecordsCount++;
					valuesSum = valuesSum.add(dataRecord.getValue());
					addDataRecordToNewestQueue(newestRecords, dataRecord);
				}
			} catch (Exception e) {
				LOGGER.error("Failed parse line: {}, error: {}", line, e.toString());
			}
		}
		LOGGER.debug("Task processed: {} lines, number of records after filter: {}, values sum: {}", new Object[] {
				linesCount, processedRecordsCount, valuesSum });

		return new TaskCalculationResult(processedRecordsCount, valuesSum, newestRecords);
	}

	/**
	 * Adds the data record to queue, and if records queue size greater than 10, removes older one
	 * 
	 * @param dataRecord
	 */
	private void addDataRecordToNewestQueue(PriorityQueue<DataRecord> newestRecords, DataRecord dataRecord) {
		newestRecords.add(dataRecord);
		if (newestRecords.size() > 10) {
			newestRecords.poll();
		}
	}

}
