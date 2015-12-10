package am.financial.engine.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Date;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.financial.engine.service.common.Utils;
import am.financial.engine.service.model.CalculationFilter;
import am.financial.engine.service.model.CalculationResult;

public class ConcurrentCalculationServiceImplTest {

	private static final Logger LOGGER = LoggerFactory.getLogger(ConcurrentCalculationServiceImplTest.class);

	private ConcurrentCalculationServiceImpl calculationService;

	@Before
	public void init() {
		calculationService = new ConcurrentCalculationServiceImpl();
	}

	@Test
	public void calculateMeanForInstrumentTest() throws IOException {
		Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "calculateMeanForInstrumentTest.txt");
		Date currentDate = new Date();
		StringBuilder buffer = new StringBuilder();
		buffer.append(createDataRecordString("INSTRUMENT1", currentDate, 2.0));
		buffer.append(createDataRecordString("INSTRUMENT2", currentDate, 2.0));
		buffer.append(createDataRecordString("INSTRUMENT1", currentDate, 3.0));
		buffer.append(createDataRecordString("INSTRUMENT2", currentDate, 4.0));
		Files.write(filePath, buffer.toString().getBytes());
		// Configure service to process two records per task
		CalculationFilter filter = new CalculationFilter();
		filter.setInstrument("INSTRUMENT2");
		calculationService.setNrOfLinesPerTask(2);
		CalculationResult result;
		try {
			result = calculationService.calculateMeanForInstruments(filePath.toAbsolutePath().toString(), filter);
			Assert.assertNotNull(result);
			Assert.assertEquals(4, result.getTotalLines());
			Assert.assertEquals(2, result.getProcessedRecords());
			Assert.assertEquals(3.0, result.getMean().doubleValue(), 0);
		} catch (Exception e) {
			Assert.fail(e.toString());
		} finally {
			Files.delete(filePath);
		}
	}

	@Test
	public void calculateMeanForBigFileTest() throws IOException {
		Path filePath = Paths.get(System.getProperty("java.io.tmpdir"), "calculateMeanForBigFileTest.txt");
		if (Files.exists(filePath)) {
			Files.delete(filePath);
		}
		Files.createFile(filePath);
		Date currentDate = new Date();
		StringBuilder buffer = new StringBuilder();
		LOGGER.info("Writing data into: {} ..", filePath);
		for (int i = 1; i <= 10_000_000; i++) {
			buffer.append(createDataRecordString("INSTRUMENT1", currentDate, 2.0));
			buffer.append(createDataRecordString("INSTRUMENT2", currentDate, 2.0));
			buffer.append(createDataRecordString("INSTRUMENT3", currentDate, 3.5));
			buffer.append(createDataRecordString("INSTRUMENT4", currentDate, 4.0));
			if (i % 100000 == 0) {
				Files.write(filePath, buffer.toString().getBytes(), StandardOpenOption.APPEND);
				buffer.delete(0, buffer.length());
			}
		}
		CalculationFilter filter = new CalculationFilter();
		filter.setInstrument("INSTRUMENT3");
		CalculationResult result = calculationService.calculateMeanForInstruments(filePath.toAbsolutePath().toString(),
				filter);
		Assert.assertEquals(40_000_000L, result.getTotalLines());
		Assert.assertEquals(10_000_000, result.getProcessedRecords());
		Assert.assertEquals(3.5, result.getMean().doubleValue(), 0);
	}

	private String createDataRecordString(String instrumentName, Date date, double value) {
		StringBuffer buffer = new StringBuffer();
		buffer.append(instrumentName).append(",");
		buffer.append(Utils.getDefaultDateFormatter().format(date)).append(",");
		buffer.append(value);
		buffer.append(System.lineSeparator());
		return buffer.toString();
	}
}
