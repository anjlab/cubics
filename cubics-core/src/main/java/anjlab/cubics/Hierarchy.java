package anjlab.cubics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents hierarchy in the calculated cube.
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of the fact bean.
 */
public class Hierarchy<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4399125530700587624L;
	
	private Hierarchy<T> parent;
	private String dimensionName;
	private Object dimensionValue;
	private Cube<T> cube;
	private Map<Key, Hierarchy<T>> children;
	private Totals<T> totals;

	private int size;

	public Hierarchy(Cube<T> cube, Hierarchy<T> parent, String dimensionName, Object dimensionValue, Map<Key, Hierarchy<T>> children) {
		this.parent = parent;
		this.dimensionName = dimensionName;
		this.dimensionValue = dimensionValue;
		this.cube = cube;
		this.children = children;
		this.totals = new Totals<T>(cube.getModel().getMeasures(), cube.getModel().getCustomAggregateFactories());
		
		mergeTotals();
	}

	public Hierarchy(Cube<T> cube, Hierarchy<T> parent, String dimensionName, Object dimensionValue) {
		this(cube, parent, dimensionName, dimensionValue, new HashMap<Key, Hierarchy<T>>());
	}

	private void mergeTotals() {
		for (Hierarchy<T> child : children.values()) {
			child.mergeTotals();
			totals.merge(child.totals);
		}
	}

	/**
	 * Gets hierarchy totals.
	 * 
	 * @return Returns totals for this hierarchy.
	 */
	public Totals<T> getTotals() {
		return totals;
	}

	/**
	 * Gets children hierarchies according to cube's dimensions. 
	 * 
	 * @return Returns children hierarchies.
	 * @see Cube<T>
	 */
	public Map<Key, Hierarchy<T>> getChildren() {
		return children;
	}

	/**
	 * Returns the name of hierarchy dimension.
	 * 
	 * Name of the root hierarchy is <code>null</code>.
	 * 
	 * @return Name of hierarchy dimension.
	 */
	public String getDimensionName() {
		return dimensionName;
	}
	
	public void addFact(T fact) {
		for (String measure : cube.getModel().getMeasures()) {
			totals.getAggregate(measure).add(
						cube.getModel().getBeanClass().getValue(measure, fact));
		}
	}

	public int getSize() {
		return size;
	}
	
	public int getSizeWithTotals() {
		if (children.size() == 0) {
			return 2;
		}
		
		int result = 0;
		
		for (Hierarchy<T> h : children.values()) {
			result += h.getSizeWithTotals();
		}
		
		return result + 1;
	}
	
	/**
	 * Gets hierarchy dimension dimensionValue.
	 * 
	 * @return Returns hierarchy dimension dimensionValue.
	 */
	public Object getDimensionValue() {
		return dimensionValue;
	}

	private String asString = null;
	
	@Override
	public String toString() {
		if (asString == null) {
			String s = dimensionName + ":" + dimensionValue;
			asString = parent == null ? s : parent.toString() + " > " + s;
		}
		return asString;
	}

	private Object[] path;
	
	/**
	 * Gets the path of this hierarchy according to dimensions values.
	 * 
	 * @return Returns array of dimension values preceding this hierarchy (inclusive).
	 */
	public Object[] getPath() {
		if (path == null) {
			if (parent == null) {
				path = new Object[] {dimensionValue};
			} else {
				path = new Object[parent.getPath().length + 1];
				for (int i = 0; i < parent.path.length; i++) {
					path[i] = parent.path[i];
				}
				path[path.length - 1] = dimensionValue;
			}
		}
		return path;
	}

	public Hierarchy<T> getParent() {
		return parent;
	}

	public void increaseSize() {
		size++;
		if (this.parent != null) {
			this.parent.increaseSize();
		}
	}

}
