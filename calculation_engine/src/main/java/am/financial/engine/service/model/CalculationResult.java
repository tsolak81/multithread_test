package am.financial.engine.service.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

import am.financial.engine.service.common.Utils;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public class CalculationResult {

	/**
	 * Number of input lines
	 */
	private long totalLines;

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
	private List<DataRecord> newestRecords;

	public CalculationResult(long totalLines, long processedRecords, BigDecimal valuesSum,
			Queue<DataRecord> newestRecordsQueue) {
		Utils.assertNotNull(valuesSum, "Values sum must not be null");
		this.totalLines = totalLines;
		this.processedRecords = processedRecords;
		this.valuesSum = valuesSum;
		this.newestRecords = new ArrayList<DataRecord>();
		DataRecord dataRecord;
		while ((dataRecord = newestRecordsQueue.poll()) != null) {
			newestRecords.add(dataRecord);
		}
	}

	public BigDecimal getMean() {
		if (processedRecords == 0) {
			return BigDecimal.ZERO;
		} else {
			return valuesSum.divide(BigDecimal.valueOf(processedRecords), 2, RoundingMode.HALF_UP);
		}
	}

	@Override
	public String toString() {
		return String.format("CalculationResult [totalLines=%s, processedRecords=%s, valuesSum=%s, getMean()=%s]",
				totalLines, processedRecords, valuesSum, getMean());
	}

	public long getTotalLines() {
		return totalLines;
	}

	public long getProcessedRecords() {
		return processedRecords;
	}

	public BigDecimal getValuesSum() {
		return valuesSum;
	}

	public List<DataRecord> getNewestRecords() {
		return newestRecords;
	}

}
