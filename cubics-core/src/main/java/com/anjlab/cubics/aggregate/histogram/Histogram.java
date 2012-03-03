package com.anjlab.cubics.aggregate.histogram;

import static com.anjlab.cubics.aggregate.histogram.Range.compare;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anjlab.cubics.DataCollector;
import com.anjlab.cubics.JSONSerializable;


public class Histogram implements DataCollector<Range>, JSONSerializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -9174472731559232821L;
	
	private Map<Range, Long> data;
	private Range[] ranges;
	private long leftOthers;
	private long rightOthers;
	private long count;
	private Range totalRange;

	private Object latestInRange; 
	
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
		this.totalRange = ranges.length == 0 
							? new Range(0, false, 0, false) //	empty range
							: new Range(ranges[0].getLeft(), true, ranges[ranges.length - 1].getRight(), true);
	}

	public void add(Object value) {
		add(value, 1);
	}

	public void add(final Object value, int numberOfValues) {
		count += numberOfValues;
		
		if (Range.compare(totalRange.getLeft(), value) > 0)  {
			leftOthers += numberOfValues;
			return;
		}

		if (Range.compare(totalRange.getRight(), value) < 0)  {
			rightOthers += numberOfValues;
			return;
		}

		int rangeIndex = Arrays.binarySearch(ranges, null, new Comparator<Range>() {
			public int compare(Range o1, Range o2) {
				if (o1.includes(value)) {
					return 0;
				}
				return Range.compare(o1.getLeft(), value);
			}
		});
		
		Range range = ranges[rangeIndex];
		data.put(range, data.get(range) + numberOfValues);
		latestInRange = value;
	}

	public void merge(Histogram other) {
		long expectedCount = this.count + other.count;
		
		mergeStrategy.merge(this, other);
		
		if (expectedCount != count) {
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
	
	/**
	 * 
	 * @return Returns number of values that are out of the leftmost range.
	 */
	public long getLeftOthers() {
		return leftOthers;
	}
	
	/**
	 * 
	 * @return Returns number of values that are out of the rightmost range.
	 */
	public long getRightOthers() {
		return rightOthers;
	}
	
	/**
	 * 
	 * @return Returns number of values that are out of ranges.
	 */
	public long getOthers() {
		return leftOthers + rightOthers;
	}

	public long getCount() {
		return count;
	}

	public Long getDefaultValue() {
		return 0L;
	}

	public Object getLatestInRange() {
		return latestInRange;
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
		builder.append("leftOthers = ");
		builder.append(leftOthers);
		builder.append(", rightOthers = ");
		builder.append(rightOthers);
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
		builder.append(getOthers());
		builder.append(",");
		builder.append("leftOthers:");
		builder.append(leftOthers);
		builder.append(",");
		builder.append("rightOthers:");
		builder.append(rightOthers);
		builder.append(",");
		builder.append("count:");
		builder.append(count);
		builder.append("}");
		
		return builder.toString();
	}

	public enum HistogramMergeStrategy {
		SameRanges, 
		NumericRanges
	}
	
	static class SameRangesMergeStrategy implements MergeStrategy<Histogram>, Serializable {

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
			target.leftOthers += source.leftOthers;
			target.rightOthers += source.rightOthers;
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

			long expectedCount = target.count + source.count;

			long countInRanges = roundValues(target.data, mergedData);
			
			Range leftDiffRange = new Range(min(getLeftSide(target), getLeftSide(source)), true, max(getLeftSide(target), getLeftSide(source)), false);
			
			double countInLeftDiffRange = target.getIntegralValue(leftDiffRange) + source.getIntegralValue(leftDiffRange);
			
			target.leftOthers += source.leftOthers - countInLeftDiffRange;
			
			//	Some values may be lost due to range borders rounding. Add those values to the rightOthers
			target.rightOthers = expectedCount - countInRanges - target.leftOthers;
			
			target.count = expectedCount;
		}

		private long roundValues(Map<Range, Long> target, Map<Range, Double> source) {
			long count = 0;
			
			double diff = 0;
			
			for (Range range : source.keySet()) {

				Double doubleValue = source.get(range);
				
				long longValue = Math.round(doubleValue + diff);
				
				diff += doubleValue - longValue;
				
				count += longValue;
				
				target.put(range, longValue);
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