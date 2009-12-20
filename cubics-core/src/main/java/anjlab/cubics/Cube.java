package anjlab.cubics;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the cube of aggregates.
 *
 * @author dmitrygusev
 *
 * @param <T> Type of the fact bean.
 */
public class Cube<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3761391818016806526L;
	
	private FactModel<T> model;
	private Map<String, Map<Key, Hierarchy<T>>> dimensions;
	private BeanClass<T> beanClass;
		
	private Cube(FactModel<T> model, Iterable<T> facts) {
		this.model = model;
		this.beanClass = model.getBeanClass();
		initDimensions(model);
		calculateCube(facts);
	}

	private void calculateCube(Iterable<T> facts) {
		if (facts == null) {
			return;
		}
		
		for (T fact : facts) {
			addFact(fact);
		}
	}

	public void addFact(T fact) {
		boolean increaseRequired = false;
		
		Hierarchy<T> parentHierarchy = null;
		for (String dimensionName : model.getDimensions()) {
			Object dimensionValue = beanClass.getValue(dimensionName, fact);

			Map<Key, Hierarchy<T>> slice = this.dimensions.get(dimensionName);

			Key key = new Key(
					dimensionValue,
					parentHierarchy == null 
						? null 
						: parentHierarchy.getPath());
			
			if (! slice.containsKey(key)) {
				slice.put(key, new Hierarchy<T>(
						this, parentHierarchy, dimensionName, dimensionValue));
			}
			
			Hierarchy<T> hierarchy = slice.get(key);

			if (parentHierarchy != null) {
				if (! parentHierarchy.getChildren().containsKey(key)) {
					parentHierarchy.getChildren().put(key, hierarchy);
					increaseRequired = true;
				}
			}
			
			parentHierarchy = hierarchy;
		}

		Hierarchy<T> lowestHierarchy = parentHierarchy;

		lowestHierarchy.addFact(fact);
		
		if (increaseRequired) {
			lowestHierarchy.increaseSize();
		}
	}

	private void initDimensions(FactModel<T> model) {
		this.dimensions = new HashMap<String, Map<Key, Hierarchy<T>>>(model.getDimensions().length);
		for (String dimension : model.getDimensions()) {
			this.dimensions.put(dimension, new HashMap<Key, Hierarchy<T>>());
		}
	}

	/**
	 * Creates and fills the cube.
	 * 
	 * @param <T> Type of the fact bean.
	 * @param model The {@link FactModel} for the corresponding bean type <code>T</code>.
	 * @param facts The source facts used to calculate the cube.
	 * @return New cube.
	 */
	public static <T> Cube<T> createCube(FactModel<T> model, Iterable<T> facts) {
		Cube<T> cube = new Cube<T>(model, facts);
		
		//	Force merge totals
		cube.getRoot();
		
		return cube;
	}

	/**
	 * Creates empty cube for specified model.
	 * 
	 * @param <T> Type of the fact bean.
	 * @param model The {@link FactModel} for the corresponding bean type <code>T</code>.
	 * @return New cube.
	 */
	public static <T> Cube<T> createCube(FactModel<T> model) {
		return createCube(model, null);
	}

	private Hierarchy<T> root;
	
	/**
	 * Gets the root hierarchy whose children collection contains list of all
	 * hierarchies of the first cube's dimension.
	 *  
	 * @return The root hierarchy.
	 */
	public Hierarchy<T> getRoot() {
		if (root == null) {
			root = new Hierarchy<T>(
				this,
				null,
				null,
				null,
				getDimension(model.getDimensions()[0]));
		}
		
		return root;
	}

	/**
	 * Gets a collection of all hierarchies in cube for the specified <code>dimension</code>.  
	 * 
	 * @param dimension The name of dimension.
	 * @return List of dimension hierarchies.
	 */
	public Map<Key, Hierarchy<T>> getDimension(String dimension) {
		return dimensions.get(dimension);
	}

	/**
	 * Gets the fact model for the cube. 
	 * @return Cube's fact model.
	 */
	public FactModel<T> getModel() {
		return model;
	}

}
