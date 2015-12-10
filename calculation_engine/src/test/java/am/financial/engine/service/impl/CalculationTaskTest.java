package am.financial.engine.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.junit.Assert;
import org.junit.Test;

import am.financial.engine.service.common.Utils;
import am.financial.engine.service.model.CalculationFilter;
import am.financial.engine.service.model.DataRecord;
import am.financial.engine.service.model.TaskCalculationResult;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public class CalculationTaskTest {

	@Test
	public void callTest() throws Exception {
		Date currentDate = new Date();
		LinkedList<String> linesQueue = new LinkedList<String>();
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDate, 1.5));
		linesQueue.add(createDataRecordString("INSTRUMENT2", currentDate, 2.0));
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDate, 4.5));

		CalculationTask calculationTask = new CalculationTask(linesQueue, null);
		// Service call
		TaskCalculationResult taskCalculationResult = calculationTask.call();
		Assert.assertEquals(3, taskCalculationResult.getProcessedRecords());
		Assert.assertEquals(BigDecimal.valueOf(1.5 + 2.0 + 4.5), taskCalculationResult.getValuesSum());
		// Check with filter
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDate, 1.5));
		linesQueue.add(createDataRecordString("INSTRUMENT2", currentDate, 2.0));
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDate, 4.5));
		// Add instrument filter
		CalculationFilter filter = new CalculationFilter();
		filter.setInstrument("INSTRUMENT1");
		CalculationTask calculationTask2 = new CalculationTask(linesQueue, filter);
		// Service call
		TaskCalculationResult taskCalculationResult2 = calculationTask2.call();
		Assert.assertEquals(2, taskCalculationResult2.getProcessedRecords());
		Assert.assertEquals(BigDecimal.valueOf(1.5 + 4.5), taskCalculationResult2.getValuesSum());
	}

	@Test
	public void callTestNewestRecords() throws Exception {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(new Date());
		// Remove time part
		toBeginningOfTheDay(calendar);
		Date currentDate = calendar.getTime();
		calendar.add(Calendar.DAY_OF_YEAR, -1);
		Date currentDateMinusDay = calendar.getTime();
		calendar.setTime(currentDate);
		calendar.add(Calendar.MONTH, -1);
		Date currentDateMinusMonth = calendar.getTime();
		calendar.setTime(currentDate);
		calendar.add(Calendar.YEAR, -1);
		Date currentDateMinusYear = calendar.getTime();
		
		LinkedList<String> linesQueue = new LinkedList<String>();
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDate, 2.25));
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDateMinusDay, 2.25));
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDateMinusYear, 4.5));
		linesQueue.add(createDataRecordString("INSTRUMENT1", currentDateMinusYear, 4.5));
		for (int i = 0; i < 10; i++) {
			linesQueue.add(createDataRecordString("INSTRUMENT1", currentDateMinusMonth, 1.1));
		}
		
		CalculationTask calculationTask = new CalculationTask(linesQueue, null);
		// Service call
		TaskCalculationResult taskCalculationResult = calculationTask.call();

		PriorityQueue<DataRecord> newestRecords = taskCalculationResult.getNewestRecords();
		Assert.assertEquals(10, newestRecords.size());
		// First eight records must be 'currentDateMinusMonth'
		for (int i = 0; i < 8; i++) {
			Assert.assertEquals(currentDateMinusMonth, newestRecords.poll().getDate());
		}
		// Next one must be 'currentDateMinusDay'
		Assert.assertEquals(currentDateMinusDay, newestRecords.poll().getDate());
		// Last one must be 'currentDate'
		Assert.assertEquals(currentDate, newestRecords.poll().getDate());
	}

	private String createDataRecordString(String instrumentName, Date date, double value) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(instrumentName).append(",");
		buffer.append(Utils.getDefaultDateFormatter().format(date)).append(",");
		buffer.append(value);
		return buffer.toString();
	}
	
	public static void toBeginningOfTheDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }
}
