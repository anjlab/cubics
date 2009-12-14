package anjlab.cubics;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Describes the dimensions and measures of the fact bean. 
 * 
 * @author dmitrygusev
 * 
 * @param <T> Type of the fact bean.
 */
public class FactModel<T> {

	private String[] dimensions;
	private String[] measures;
	private BeanClass<T> beanClass;
	private Map<String, List<CustomAggregateFactory<T>>> aggregateFactories;
	
	/**
	 * Creates new instance of fact model.
	 * 
	 * @param beanClass {@link Class} of the fact class.
	 */
	public FactModel(Class<T> beanClass) {
		this.beanClass = new BeanClass<T>(beanClass);
		this.aggregateFactories = new HashMap<String, List<CustomAggregateFactory<T>>>();
	}
	
	/**
	 * Sets the names of dimensions.
	 * 
	 * The order is meaning, it defines the dimensions hierarchy.
	 * 
	 * @param names Names of the dimensions.
	 */
	public void setDimensions(String... names) {
		this.dimensions = names;
	}

	/**
	 * Sets the names of measures.
	 * 
	 * @param names Names of measures.
	 */
	public void setMeasures(String... names) {
		this.measures = names;
	}

	/**
	 * Gets model dimensions.
	 * 
	 * @return Returns model dimensions.
	 */
	public String[] getDimensions() {
		return dimensions;
	}

	/**
	 * Gets model measures.
	 * 
	 * @return Returns model measures.
	 */
	public String[] getMeasures() {
		return measures;
	}

	public BeanClass<T> getBeanClass() {
		return beanClass;
	}

	/**
	 * Adds custom aggregate to specified <code>measure</code>.
	 * @param factory Factory which creates custom aggregate.
	 * @param measures Measures to add custom aggregate to. If empty or null, then aggregate will be added
	 * to all model measures (in this case measures should be set to the model prior to custom aggregate declaration).
	 */
	public void declareCustomAggregate(CustomAggregateFactory<T> factory, String... measures) {
		if (measures == null || measures.length == 0) {
			measures = this.measures;
		}
		for (String measure : measures) {
			if (! aggregateFactories.containsKey(measure)) {
				aggregateFactories.put(measure, new ArrayList<CustomAggregateFactory<T>>());
			}
			aggregateFactories.get(measure).add(factory);
		}
	}

	public Map<String, List<CustomAggregateFactory<T>>> getCustomAggregateFactories() {
		return aggregateFactories;
	}
}
