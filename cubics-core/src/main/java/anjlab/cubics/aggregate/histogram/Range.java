package anjlab.cubics.aggregate.histogram;

public class Range {

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

	@SuppressWarnings("unchecked")
	private int compare(Comparable<?> boundary, Object value) {
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
}
