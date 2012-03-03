/**
 * 
 */
package com.anjlab.cubics.renders;

import java.util.Comparator;

import com.anjlab.cubics.Key;


public final class NaturalKeyComparator implements Comparator<Key> {
	private static final String CAN_T_COMPARE_KEYS_FROM_DIFFERENT_DIMENSIONS = "Can't compare keys from different dimensions";

	public int compare(Key o1, Key o2) {
		if ((o1.getParentPath() == null && o2.getParentPath() != null) ||
			(o1.getParentPath() != null && o2.getParentPath() == null)) {
			throw new RuntimeException(CAN_T_COMPARE_KEYS_FROM_DIFFERENT_DIMENSIONS);
		}

		if (o1.getParentPath() == null && o2.getParentPath() == null) {
			return compareValues(o1.getDimensionValue(), o2.getDimensionValue());
		}
		
		if (o1.getParentPath().length != o2.getParentPath().length) {
			throw new RuntimeException(CAN_T_COMPARE_KEYS_FROM_DIFFERENT_DIMENSIONS);
		}
		
		for (int i = 0; i < o1.getParentPath().length; i++) {
			int result = compareValues(o1.getParentPath()[i], o2.getParentPath()[i]);
			if (result != 0) {
				return result;
			}
		}
		
		return compareValues(o1.getDimensionValue(), o2.getDimensionValue());
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private int compareValues(Object a, Object b) {
		if (a instanceof Comparable<?> && b instanceof Comparable<?>) {
			return ((Comparable)a).compareTo((Comparable)b);
		}
		throw new RuntimeException("Key is not comparable");
	}
}