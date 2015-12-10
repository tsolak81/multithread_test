package am.financial.engine.service.model;

import java.util.Calendar;
import java.util.Date;

/**
 * @author Tsolak Barseghyan
 * @date Apr 5, 2015
 *
 */
public class CalculationFilter {

	private String instrument;

	private Date monthOfYear;

	private boolean onlyWeekDays;

	public boolean checkFilter(DataRecord dataRecord) {
		if (instrument != null && !instrument.equals(dataRecord.getInstrumentName())) {
			return false;
		}
		if (monthOfYear != null && !isSameMonthOfYear(monthOfYear, dataRecord.getDate())) {
			return false;
		}
		if (onlyWeekDays && !isWeekDay(dataRecord.getDate())) {
			return false;
		}
		return true;
	}

	private boolean isSameMonthOfYear(Date date1, Date date2) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date1);
		int y1 = calendar.get(Calendar.YEAR);
		int m1 = calendar.get(Calendar.MONTH);
		calendar.setTime(date2);
		int y2 = calendar.get(Calendar.YEAR);
		int m2 = calendar.get(Calendar.MONTH);
		return y1 == y2 && m1 == m2;
	}

	private boolean isWeekDay(Date date) {
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(date);
		if (calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
				&& calendar.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY) {
			return true;
		} else {
			return false;
		}
	}

	public Date getMonthOfYear() {
		return monthOfYear;
	}

	public void setMonthOfYear(Date monthOfYear) {
		this.monthOfYear = monthOfYear;
	}

	public boolean isOnlyWeekDays() {
		return onlyWeekDays;
	}

	public void setOnlyWeekDays(boolean onlyWeekDays) {
		this.onlyWeekDays = onlyWeekDays;
	}

	public String getInstrument() {
		return instrument;
	}

	public void setInstrument(String instrument) {
		this.instrument = instrument;
	}

	@Override
	public String toString() {
		return String.format("CalculationFilter [instrument=%s, monthOfYear=%s, onlyWeekDays=%s]", instrument,
				monthOfYear, onlyWeekDays);
	}
}
