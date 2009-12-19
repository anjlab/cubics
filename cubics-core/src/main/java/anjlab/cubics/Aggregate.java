package anjlab.cubics;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Formatter;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

/**
 * Basic aggregate calculator.
 * 
 * Implements 5 aggregated functions: min, max, average, sum, count.
 * 
 * @author dmitrygusev
 * 
 * @param <T>
 *            Type of the fact bean.
 */
public class Aggregate<T> implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3880336888204291753L;
	
	/**	
	 * Built-in aggregate names.
	 * Should be in a sorted order. 
	 * @see #hasValue(String)
	 */
	private static final String[] NAMES = new String[]   { "avg", "count", "max",  "min",  "sum" };
	private static final String[] FORMATS = new String[] { "%.1f", "%d",   "%.1f", "%.1f",  "%.1f" };

	private double average;
	private double min;
	private double max;
	private int count;
	private double sum;

	private Map<String, CustomAggregate<T>> customAggregates;
	
	public Aggregate(List<CustomAggregateFactory<T>> aggregateFactories) {
		average = Double.NaN;
		min = Double.NaN;
		max = Double.NaN;
		sum = Double.NaN;

		if (aggregateFactories != null) {
			//	Initialize custom aggregates
			this.customAggregates = new HashMap<String, CustomAggregate<T>>();
			for (CustomAggregateFactory<T> factory : aggregateFactories) {
				CustomAggregate<T> aggregate = factory.createAggregate();
				this.customAggregates.put(factory.getAggregateName(), aggregate);
			}
		}
	}

	public void add(Object value) {
		if (value != null && value instanceof Number) {
			processNumber(value);
		}
		
		if (customAggregates != null) {
			for (CustomAggregate<T> aggregate : customAggregates.values()) {
				aggregate.add(this, value);
			}
		}
	}

	private void processNumber(Object value) {
		double doubleValue = ((Number) value).doubleValue();

		count++;

		if (Double.isNaN(average)) {
			average = doubleValue;
			min = doubleValue;
			max = doubleValue;
			sum = doubleValue;
		} else {
			if (doubleValue < min) {
				min = doubleValue;
			}
			if (doubleValue > max) {
				max = doubleValue;
			}
			sum += doubleValue;
			average = average + (doubleValue - average) / count;
		}
	}

	public double getAverage() {
		return average;
	}

	public double getMin() {
		return min;
	}

	public double getMax() {
		return max;
	}

	public int getCount() {
		return count;
	}

	public double getSum() {
		return sum;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		if (customAggregates != null) {
			for (String aggregateName : customAggregates.keySet()) {
				builder.append(", ");
				builder.append(aggregateName);
				builder.append(": ");
				builder.append(customAggregates.get(aggregateName).getValue());
			}
		}
		return String.format(Locale.ENGLISH,
				"count: %d, min: %.1f, max: %.1f, sum: %.1f, avg: %.1f", 
				count, min, max, sum, average) + builder.toString();
	}

	public void merge(Aggregate<T> other) {
		if (Double.isNaN(count)) {
			count = other.count;
		} else if (!Double.isNaN(other.count)) {
			count += other.count;
		}
		if (Double.isNaN(sum)) {
			sum = other.sum;
		} else if (!Double.isNaN(other.sum)) {
			sum += other.sum;
		}
		if (Double.isNaN(min)) {
			min = other.min;
		} else if (!Double.isNaN(other.min)) {
			if (other.min < min) {
				min = other.min;
			}
		}
		if (Double.isNaN(max)) {
			max = other.max;
		} else if (!Double.isNaN(other.max)) {
			if (other.max > max) {
				max = other.max;
			}
		}
		if (Double.isNaN(average)) {
			average = other.average;
		} else if (!Double.isNaN(other.average)) {
			average = (average + other.average) / 2;
		}
		
		if (customAggregates != null) {
			for (String aggregate : customAggregates.keySet()) {
				customAggregates.get(aggregate).merge(this, other.customAggregates.get(aggregate));
			}
		}
	}

	/**
	 * Gets built-in aggregate function names.
	 * 
	 * @return Returns names array.
	 */
	public static String[] getNames() {
		return NAMES;
	}

	/**
	 * Gets string formats for built-in aggregate functions.
	 * 
	 * Item indexes in resulting array corresponds to item indexes in {
	 * {@link #getNames()}.
	 * 
	 * @return Returns formats array.
	 * 
	 * @see Formatter
	 */
	public static String[] getFormats() {
		return FORMATS;
	}

	/**
	 * Gets current value of aggregate function result.
	 * 
	 * @param aggregate
	 *             Aggregate function name.
	 * @return Value of aggregate function.
	 */
	public Object getValue(String aggregate) {
		if ("min".equals(aggregate)) {
			return getMin();
		}
		if ("max".equals(aggregate)) {
			return getMax();
		}
		if ("avg".equals(aggregate)) {
			return getAverage();
		}
		if ("count".equals(aggregate)) {
			return getCount();
		}
		if ("sum".equals(aggregate)) {
			return getSum();
		}
		if (customAggregates != null) {
			if (customAggregates.containsKey(aggregate)) {
				return customAggregates.get(aggregate).getValue();
			}
		}
		throw new RuntimeException(
				"This aggregate can't provide value for \"" + aggregate + "\"");
	}

	public boolean hasValue(String aggregate) {
		return Arrays.binarySearch(getNames(), aggregate) != -1
			|| (customAggregates != null && customAggregates.containsKey(aggregate));
	}
}
