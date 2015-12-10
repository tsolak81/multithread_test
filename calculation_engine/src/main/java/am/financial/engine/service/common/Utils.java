package am.financial.engine.service.common;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 * 
 *       Provides static utility methods
 *
 */
public final class Utils {
	private Utils() {
	}

	public static final String DEFAULT_DATE_FORMAT = "dd-MMM-yyyy";

	private static final SimpleDateFormatThreadSafe dateFormat = new SimpleDateFormatThreadSafe(DEFAULT_DATE_FORMAT);

	public static void assertNotNull(Object object, String message) {
		if (object == null) {
			throw new IllegalArgumentException(message);
		}
	}

	public static void assertNotEmpty(String string, String message) {
		if (string == null || string.trim().isEmpty()) {
			throw new IllegalArgumentException(message);
		}
	}

	public static SimpleDateFormatThreadSafe getDefaultDateFormatter() {
		return dateFormat;
	}

}
