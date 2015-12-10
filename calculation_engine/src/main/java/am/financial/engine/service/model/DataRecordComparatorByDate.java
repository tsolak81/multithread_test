package am.financial.engine.service.model;

import java.util.Comparator;

/**
 * Sort by date
 */
public class DataRecordComparatorByDate implements Comparator<DataRecord> {

	@Override
	public int compare(DataRecord o1, DataRecord o2) {
		return o1.getDate().compareTo(o2.getDate());
	}

}
