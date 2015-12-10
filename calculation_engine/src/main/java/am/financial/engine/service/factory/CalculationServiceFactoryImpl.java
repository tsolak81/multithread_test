package am.financial.engine.service.factory;

import am.financial.engine.service.CalculationService;
import am.financial.engine.service.impl.ConcurrentCalculationServiceImpl;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public class CalculationServiceFactoryImpl extends CalculationServiceFactory {

	@Override
	public CalculationService createCalculationService() {
		return new ConcurrentCalculationServiceImpl();
	}

}
