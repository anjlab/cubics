package anjlab.cubics.aggregate.histogram;

import static anjlab.cubics.aggregate.histogram.Range.compare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anjlab.cubics.DataCollector;
import anjlab.cubics.JSONSerializable;

public class Histogram implements DataCollector<Range>, JSONSerializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9174472731559232821L;
	
	private Map<Range, Long> data;
	private Range[] ranges;
	private long others;
	private long count;

	private MergeStrategy<Histogram> mergeStrategy;
	
	public Histogram(MergeStrategy<Histogram> mergeStrategy, double start, double step, double end) {
		this.mergeStrategy = mergeStrategy;
		initData(Range.createRanges(start, step, end));
	}

	public Histogram(MergeStrategy<Histogram> mergeStrategy, Range... ranges) {
		this.mergeStrategy = mergeStrategy;
		initData(ranges);
	}

	private void initData(Range... ranges) {
		data = new HashMap<Range, Long>();
		for (Range range : ranges) {
			data.put(range, 0L);
		}
		this.ranges = ranges;
	}

	public void add(Object value) {
		add(value, 1);
	}

	public void add(Object value, int numberOfValues) {
		count += numberOfValues;
		for (Range range : ranges) {
			if (range.includes(value)) {
				data.put(range, data.get(range) + numberOfValues);
				return;
			}
		}
		others += numberOfValues;
	}

	public void merge(Histogram other) {
		long expectedCount = this.count + other.count;
		
		mergeStrategy.merge(this, other);
		
		if (count != expectedCount) {
			throw new IllegalStateException(
					"Number of values differs after merge (" + expectedCount + " != " + count + ")");
		}
	}

	public Map<Range, Long> getData() {
		return data;
	}

	double getIntegralValue(Range boundaries) {
		List<Range> valueRanges = new ArrayList<Range>(); 
		
		for (Range range : ranges) {
			if (range.includes(boundaries.getLeft()) 
					|| range.includes(boundaries.getRight())
					|| boundaries.includes(range.getLeft())
					|| boundaries.includes(range.getRight())) 
			{
				valueRanges.add(range);
			}
		}
		
		Double value = 0d;
		
		for (Range range : valueRanges) {
			Long rangeValue = data.get(range);
			
			if (rangeValue == null) {
				continue;
			}
			
			if (boundaries.includes(range)) {
				value += rangeValue;
			} else {
				value += getPartialValue(range, rangeValue, boundaries);
			}
		}
		
		return value;
	}

	private double getPartialValue(Range valueRange, Long rangeValue, Range boundaries) {
		Number left = (Number) boundaries.getLeft();
		Number right = (Number) boundaries.getRight();

		Number rightVR = (Number) valueRange.getRight();
		Number leftVR = (Number) valueRange.getLeft();

		if (compare(valueRange.getLeft(), left) > 0) {
			left = leftVR;
		}
		
		if (compare(valueRange.getRight(), right) < 0) {
			right = rightVR;
		}

		double integralValue = 
			(right.doubleValue() - left.doubleValue()) 
				/ (rightVR.doubleValue() - leftVR.doubleValue()) * rangeValue;
		
		return integralValue;
	}

	
	public Range getRange(int index) {
		return ranges[index];
	}
	
	public int getRangesCount() {
		return ranges.length;
	}
	
	public long getOthers() {
		return others;
	}

	public long getCount() {
		return count;
	}

	public Long getDefaultValue() {
		return 0L;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Range range : ranges) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(range.toString());
			builder.append(" = ");
			builder.append(data.get(range));
		}
		if (builder.length() > 0) {
			builder.append(", ");
		}
		builder.append("others = ");
		builder.append(others);
		builder.append(", count = ");
		builder.append(count);
		return builder.toString();
	}

	public Range coerceKey(String key) {
		return Range.parseRange(key);
	}

	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("{");
		builder.append("keys:");
		builder.append("[");
		boolean first = true;
		for (Range range : ranges) {
			if (!first) {
				builder.append(",");
			} else {
				first = false;
			}
			builder.append("\"");
			builder.append(range);
			builder.append("\"");
		}
		builder.append("]");
		builder.append(",");
		builder.append("values:");
		builder.append("[");
		first = true;
		for (Range range : ranges) {
			if (!first) {
				builder.append(",");
			} else {
				first = false;
			}
			builder.append(data.get(range));
		}
		builder.append("]");
		builder.append(",");
		builder.append("others:");
		builder.append(others);
		builder.append(",");
		builder.append("count:");
		builder.append(count);
		builder.append("}");
		
		return builder.toString();
	}

	public enum HistogramMergeStrategy {
		ComparableRanges, 
		NumericRanges
	}
	
	static class ComparableRangesMergeStrategy implements MergeStrategy<Histogram>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7883554568413539013L;

		public void merge(Histogram target, Histogram source) {
			for (Range range : source.ranges) {
				Long sourceValue = source.data.get(range);
				if (sourceValue != null) {
					target.data.put(range, target.data.get(range) + sourceValue);
				}
			}
			target.others += source.others;
			target.count += source.count;	
		}

	}
	
	static class NumericRangesMergeStrategy implements MergeStrategy<Histogram>, Serializable {

		/**
		 * 
		 */
		private static final long serialVersionUID = -7335493757218285558L;

		public void merge(Histogram target, Histogram source) {
			Number left = (Number) min(getLeftSide(target), getLeftSide(source));
			Number right = (Number) max(getRightSide(target), getRightSide(source));
			
			//	TODO Calculate number of ranges or pass through parameters?
			int n = 10;

			Map<Range, Double> mergedData = new HashMap<Range, Double>();
			
			double start = left.doubleValue();
			double end = right.doubleValue();
			double step = (end - start) / n;
			
			Range[] mergedRanges = Range.createRanges(start, step, end);
			
			for (Range range : mergedRanges) {
				double targetValue = target.getIntegralValue(range);
				double sourceValue = source.getIntegralValue(range);
				mergedData.put(range, targetValue + sourceValue);
			}
			
			target.ranges = mergedRanges;
			target.data = new HashMap<Range, Long>();
			
			target.count = roundMergedData(mergedData, target.data);
			
			target.others += source.others;
		}

		private long roundMergedData(Map<Range, Double> mergedData, Map<Range, Long> result) {
			long count = 0;
			
			double diff = 0;
			
			for (Range range : mergedData.keySet()) {

				Double doubleValue = mergedData.get(range);
				
				long longValue = Math.round(doubleValue + diff);
				
				diff += doubleValue - longValue;
				
				count += longValue;
				
				result.put(range, longValue);
			}
			
			return count;
		}

		private Comparable<?> getRightSide(Histogram histogram) {
			if (histogram.ranges.length == 0) {
				return null;
			}
			return histogram.ranges[histogram.ranges.length - 1].getRight();
		}

		private Comparable<?> getLeftSide(Histogram histogram) {
			if (histogram.ranges.length == 0) {
				return null;
			}
			return histogram.ranges[0].getLeft();
		}

		private Comparable<?> min(Comparable<?> a, Comparable<?> b) {
			if (a == null) {
				return b;
			}
			if (b == null) {
				return a;
			}
			return Range.compare(a, b) <= 0 ? a : b;
		}

		private Comparable<?> max(Comparable<?> a, Comparable<?> b) {
			if (a == null) {
				return b;
			}
			if (b == null) {
				return a;
			}
			return Range.compare(a, b) >= 0 ? a : b;
		}
	}
}