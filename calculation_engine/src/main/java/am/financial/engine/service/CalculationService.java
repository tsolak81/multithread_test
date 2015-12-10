package am.financial.engine.service;

import am.financial.engine.service.model.CalculationFilter;
import am.financial.engine.service.model.CalculationResult;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public interface CalculationService {

	CalculationResult calculateMeanForInstruments(final String filePath, CalculationFilter filter);
}
