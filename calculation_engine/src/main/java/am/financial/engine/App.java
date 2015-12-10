package am.financial.engine;

import java.math.BigDecimal;
import java.util.Calendar;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import am.financial.engine.service.CalculationService;
import am.financial.engine.service.factory.CalculationServiceFactory;
import am.financial.engine.service.factory.CalculationServiceFactoryImpl;
import am.financial.engine.service.model.CalculationFilter;
import am.financial.engine.service.model.CalculationResult;
import am.financial.engine.service.model.DataRecord;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public class App 
{
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

    public static void main( String[] args )
    {
		String filePath = "src/main/resources/example_input.txt";
		if (args.length > 0) {
			filePath = args[0];
		} else {
			LOGGER.warn("File path not provided as command line parameter, by default will be used: {}", filePath);
		}
		CalculationServiceFactory factory = new CalculationServiceFactoryImpl();
		CalculationService calculationService = factory.createCalculationService();

		// Calculate mean for INSTRUMENT1
		CalculationFilter filter = new CalculationFilter();
		filter.setInstrument("INSTRUMENT1");
		CalculationResult calculationResult = calculationService.calculateMeanForInstruments(filePath, filter);
		LOGGER.info("\nResult for INSTRUMENT1: {} \n", calculationResult);
		// Calculate mean for INSTRUMENT2 – for November 2014
		filter = new CalculationFilter();
		filter.setInstrument("INSTRUMENT2");
		Calendar calendar = Calendar.getInstance();
		calendar.set(2014, Calendar.NOVEMBER, 1);
		filter.setMonthOfYear(calendar.getTime());
		calculationResult = calculationService.calculateMeanForInstruments(filePath, filter);
		LOGGER.info("\nResult for INSTRUMENT2 – November 2014: {} \n", calculationResult);
		// Calculate mean for INSTRUMENT3 - only week days
		filter = new CalculationFilter();
		filter.setInstrument("INSTRUMENT3");
		filter.setOnlyWeekDays(true);
		calculationResult = calculationService.calculateMeanForInstruments(filePath, filter);
		LOGGER.info("\nResult for INSTRUMENT3 – week days: {} \n", calculationResult);
		// Get ten newest records
		calculationResult = calculationService.calculateMeanForInstruments(filePath, null);
		BigDecimal sumOfNewestRecords = BigDecimal.valueOf(0);
		LOGGER.info("\nTen newest records are:");
		for (DataRecord record : calculationResult.getNewestRecords()) {
			LOGGER.info(record.toString());
			sumOfNewestRecords = sumOfNewestRecords.add(record.getValue());
		}
		LOGGER.info("\nSum of newest records values: {} \n", sumOfNewestRecords);
    }
}
