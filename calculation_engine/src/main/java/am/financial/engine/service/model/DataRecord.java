package am.financial.engine.service.model;

import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;

import am.financial.engine.service.common.Utils;

public class DataRecord {

	private String instrumentName;

	private Date date;

	private BigDecimal value;

	public DataRecord() {
	}

	/**
	 * @param recordLine
	 *            - comma separated string of record variables
	 */
	public DataRecord(String recordLine) {
		Utils.assertNotEmpty(recordLine, "Record line string must not be null or empty");
		String[] lineVariables = recordLine.split(",");
		if(lineVariables.length != 3) {
			throw new IllegalArgumentException("Invalid record line variables length: " + lineVariables.length);
		}
		instrumentName = lineVariables[0];
		try {
			date = Utils.getDefaultDateFormatter().parse(lineVariables[1]);
		} catch (ParseException e) {
			throw new IllegalArgumentException("Failed parse date: " + lineVariables[1] + " " + e);
		}
		try {
			value = new BigDecimal(lineVariables[2].trim());
		} catch (Exception e) {
			throw new IllegalArgumentException("Failed parse value: " + lineVariables[2] + " " + e);
		}
	}


	@Override
	public String toString() {
		return String.format("DataRecord [instrumentName=%s, date=%s, value=%s]", instrumentName, Utils
				.getDefaultDateFormatter().format(date), value);
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((date == null) ? 0 : date.hashCode());
		result = prime * result + ((instrumentName == null) ? 0 : instrumentName.hashCode());
		result = prime * result + ((value == null) ? 0 : value.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DataRecord other = (DataRecord) obj;
		if (date == null) {
			if (other.date != null)
				return false;
		} else if (!date.equals(other.date))
			return false;
		if (instrumentName == null) {
			if (other.instrumentName != null)
				return false;
		} else if (!instrumentName.equals(other.instrumentName))
			return false;
		if (value == null) {
			if (other.value != null)
				return false;
		} else if (!value.equals(other.value))
			return false;
		return true;
	}

	public String getInstrumentName() {
		return instrumentName;
	}

	public void setInstrumentName(String instrumentName) {
		this.instrumentName = instrumentName;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

	public BigDecimal getValue() {
		return value;
	}

	public void setValue(BigDecimal value) {
		this.value = value;
	}

}
