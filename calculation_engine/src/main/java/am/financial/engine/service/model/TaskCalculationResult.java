package am.financial.engine.service.model;

import java.math.BigDecimal;
import java.util.PriorityQueue;

import am.financial.engine.service.common.Utils;

public class TaskCalculationResult {

	/**
	 * Number of processed record lines after filtering
	 */
	private long processedRecords;

	/**
	 * Sum of processed values
	 */
	private BigDecimal valuesSum;

	/**
	 * Keep maximum top ten newest data records in this queue
	 */
	private PriorityQueue<DataRecord> newestRecords;

	public TaskCalculationResult(long processedRecords, BigDecimal valuesSum, PriorityQueue<DataRecord> newestRecords) {
		Utils.assertNotNull(valuesSum, "Values sum must not be null");
		this.processedRecords = processedRecords;
		this.valuesSum = valuesSum;
		this.newestRecords = newestRecords;
	}

	public long getProcessedRecords() {
		return processedRecords;
	}

	public BigDecimal getValuesSum() {
		return valuesSum;
	}

	public PriorityQueue<DataRecord> getNewestRecords() {
		return newestRecords;
	}


}
