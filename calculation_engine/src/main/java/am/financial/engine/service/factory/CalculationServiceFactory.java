package am.financial.engine.service.factory;

import am.financial.engine.service.CalculationService;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public abstract class CalculationServiceFactory {

	public abstract CalculationService createCalculationService();
}
