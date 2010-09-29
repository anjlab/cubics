package anjlab.cubics;

import java.io.Serializable;
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
public class FactModel<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6355008561675518624L;
	
	private String[] dimensions;
	private String[] measures;
	private FactValueProvider<T> valueProvider;
	private Map<String, List<CustomAggregateFactory<T>>> aggregateFactories;
	private Map<String, Calculator<T>> calculatedAttributes;
	
	/**
	 * Creates new instance of fact model.
	 * 
	 * @param valueProvider Fact attributes value provider for this model.
	 */
	public FactModel(FactValueProvider<T> valueProvider) {
		this.valueProvider = buildValueProviderForModel(valueProvider);
		this.aggregateFactories = new HashMap<String, List<CustomAggregateFactory<T>>>();
		this.calculatedAttributes = new HashMap<String, Calculator<T>>();
	}

	private FactValueProvider<T> buildValueProviderForModel(final FactValueProvider<T> valueProvider) { 
	    return new FactValueProvider<T>()
	    {
    	    /**
             * 
             */
            private static final long serialVersionUID = 2115193193687564221L;

            public Object getValue(String attribute, T instance) {
                Calculator<T> calculator;
                if (calculatedAttributes.size() > 0 
                    && (calculator = calculatedAttributes.get(attribute)) != null) {
                    //  Pass 'this' as parameter so that calculators could 
                    //  use values of another calculated attributes
                    return calculator.calculate(this, instance);
                }
    	        return valueProvider.getValue(attribute, instance);
    	    };
        };
	}
	
	/**
	 * Provides access to value provider of this model.
	 * Returned instance provides access to fact attributes as well as calculated attributes.
	 * In case of attributes name collision, this provider return value of calculated attribute.
	 *  
	 * @return
	 *     Returns value provider for this model.
	 */
	public FactValueProvider<T> getValueProvider() {
		return valueProvider;
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

    public void declareCalculatedAttribute(String attribute, Calculator<T> calculator) {
        calculatedAttributes.put(attribute, calculator);
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

}
