package anjlab.cubics;

import java.util.Map;

import anjlab.cubics.aggregate.histogram.Histogram;
import anjlab.cubics.aggregate.pie.Pie;

/**
 * Data collector used by multi-value aggregate functions (such as {@link Histogram} or {@link Pie}) to 
 * publish collected data.
 *  
 * @author dmitrygusev
 *
 * @param <K> Type of key.
 */
public interface DataCollector<K> {
	
	/**
	 * Data collected by this data provider.
	 * 
	 * @return The data.
	 */
	public abstract Map<K, Integer> getData();
	
	/**
	 * Number of values used to collect data.
	 * 
	 * @return Number of values.
	 */
	public abstract int getCount();

	/**
	 * If this instance {@link #getData()} doesn't have value for specified key, then 
	 * this value will be used instead.
	 * 
	 * @return Default value.
	 */
	public abstract Integer getDefaultValue();

	/**
	 * Coerce string <code>key</code> representation to object of type <code>K</code>.
	 *  
	 * @param key String value to convert.
	 * @return Key object, supported by this data collector.
	 */
	public abstract K coerceKey(String key);
	
}
