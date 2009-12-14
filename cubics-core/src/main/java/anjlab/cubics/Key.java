package anjlab.cubics;

/**
 * Represents hierarchy key within dimension.
 * 
 * @author dmitrygusev
 *
 */
public class Key {
	private String key;
	
	private Object[] parentPath;
	private Object dimensionValue;
	
	public Key(Object dimensionValue, Object... parentPath) {
		this.dimensionValue = dimensionValue;
		this.parentPath = parentPath;
		
		StringBuilder builder = new StringBuilder();
		if (parentPath != null) {
			for (Object pathPart : parentPath) {
				builder.append(pathPart);
				builder.append('>');
			}
		}
		builder.append(dimensionValue);
		key = builder.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Key)) {
			return false;
		}
		Key other = (Key) obj;
		
		return this.key.equals(other.key);
	}

	@Override
	public String toString() {
		return key;
	}
	
	@Override
	public int hashCode() {
		return key.hashCode();
	}
	
	public Object getDimensionValue() {
		return dimensionValue;
	}
	public Object[] getParentPath() {
		return parentPath;
	}
}
