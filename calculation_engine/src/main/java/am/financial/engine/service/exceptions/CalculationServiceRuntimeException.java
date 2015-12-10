package am.financial.engine.service.exceptions;

public class CalculationServiceRuntimeException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public CalculationServiceRuntimeException(String message, Throwable cause) {
		super(message, cause);
	}

	public CalculationServiceRuntimeException(String message) {
		super(message);
	}
}
