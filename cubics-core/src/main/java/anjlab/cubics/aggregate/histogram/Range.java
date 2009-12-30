package anjlab.cubics.aggregate.histogram;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Range implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -80844878348293940L;
	
	private Comparable<?> left;
	private Comparable<?> right;
	
	private boolean leftInclusive;
	private boolean rightInclusive;

	public Range(Comparable<?> left, boolean leftInclusive, Comparable<?> right, boolean rightInclusive) {
		this.left = left;
		this.leftInclusive = leftInclusive;
		this.right = right;
		this.rightInclusive = rightInclusive;
	}
	
	private Range() {
	}

	public Comparable<?> getLeft() {
		return left;
	}
	public void setLeft(Comparable<?> left) {
		this.left = left;
	}
	public Comparable<?> getRight() {
		return right;
	}
	public void setRight(Comparable<?> right) {
		this.right = right;
	}
	public boolean isLeftInclusive() {
		return leftInclusive;
	}
	public void setLeftInclusive(boolean leftInclusive) {
		this.leftInclusive = leftInclusive;
	}
	public boolean isRightInclusive() {
		return rightInclusive;
	}
	public void setRightInclusive(boolean rightInclusive) {
		this.rightInclusive = rightInclusive;
	}

	@Override
	public boolean equals(Object obj) {
		if (! (obj instanceof Range)) {
			return false;
		}
		return this.toString().equals(obj.toString());
	}
	
	@Override
	public int hashCode() {
		return this.toString().hashCode();
	}
	
	@Override
	public String toString() {
		return (leftInclusive ? "[" : "(")
		     +  left
		     +  "; "
		     +  right
		     + (rightInclusive ? "]" : ")");
	}

	public boolean includes(Range range) {
		return includes(range.left) && includes(range.right);
	}
	
	public boolean includes(Object value) {
		int compareResult;

		compareResult = compare(left, value);
		if (compareResult > 0 || (!leftInclusive && compareResult == 0)) {
			return false;
		}

		compareResult = compare(right, value);
		if (compareResult < 0 || (!rightInclusive && compareResult == 0)) {
			return false;
		}

		return true;
	}

	public final static double epsilon = 0.000000001;
	
	public static Range[] createRanges(double start, double step, double end) {
		List<Range> ranges = new ArrayList<Range>();
		if (end < start || compareDoubles(end, start) == 0) {
			return new Range[0];
		}
		for (double i = start; i < end; i += step) {
			double right = i + step;
			if (right > end) {
				right = end;
			}
			ranges.add(new Range(i, true, right, false));
		}
		ranges.get(ranges.size() - 1).setRightInclusive(true);
		return ranges.toArray(new Range[ranges.size()]);
	}

	@SuppressWarnings("unchecked")
	public static int compare(Comparable<?> boundary, Object value) {
		if (boundary instanceof Number && value instanceof Number) {
			Double doubleBoundary = ((Number) boundary).doubleValue();
			Double doubleValue = ((Number) value).doubleValue();
			return doubleBoundary.compareTo(doubleValue);
		}
		
		return ((Comparable<Object>) boundary).compareTo(value);
	}

	public static Range parseRange(String key) {
		Range result = new Range();
		result.leftInclusive = key.startsWith("[");
		result.rightInclusive = key.endsWith("]");
		result.left = Double.parseDouble(key.substring(1, key.indexOf(";")).trim());
		result.right = Double.parseDouble(key.substring(key.indexOf(";") + 1, key.length() - 1).trim());
		return result;
	}

	public static int compareDoubles(double a, double b) {
		return Math.abs(a - b) <= epsilon ? 0 : Double.compare(a, b);
	}
}
