package anjlab.cubics;

import java.util.HashMap;
import java.util.Map;

/**
 * Represents the cube of aggregates.
 *
 * @author dmitrygusev
 *
 * @param <T> Type of the fact bean.
 */
public class Cube<T> {

	private FactModel<T> model;
	private Map<String, Map<Key, Hierarchy<T>>> dimensions;
		
	private Cube(FactModel<T> model, Iterable<T> facts) {
		this.model = model;
		initDimensions(model);
		calculateCube(facts);
	}

	private void calculateCube(Iterable<T> facts) {
		BeanClass<T> beanClass = model.getBeanClass();
		for (T fact : facts) {
			
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

				hierarchy.addFact(fact);
				
				parentHierarchy = hierarchy;
			}
			if (increaseRequired) {
				parentHierarchy.increaseSize();
			}
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
		return new Cube<T>(model, facts);
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
