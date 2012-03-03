package com.anjlab.cubics;

import java.util.Map;

import com.anjlab.cubics.aggregate.histogram.Histogram;
import com.anjlab.cubics.aggregate.pie.Pie;


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
	public abstract Map<K, Long> getData();
	
	/**
	 * Number of values used to collect data.
	 * 
	 * @return Number of values.
	 */
	public abstract long getCount();

	/**
	 * If this instance {@link #getData()} doesn't have value for specified key, then 
	 * this value will be used instead.
	 * 
	 * @return Default value.
	 */
	public abstract Long getDefaultValue();

	/**
	 * Coerce string <code>key</code> representation to object of type <code>K</code>.
	 *  
	 * @param key String value to convert.
	 * @return Key object, supported by this data collector.
	 */
	public abstract K coerceKey(String key);
	
}
