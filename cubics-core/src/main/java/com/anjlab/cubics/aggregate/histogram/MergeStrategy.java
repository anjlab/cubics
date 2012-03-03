package com.anjlab.cubics.aggregate.histogram;

import com.anjlab.cubics.aggregate.histogram.Histogram.NumericRangesMergeStrategy;
import com.anjlab.cubics.aggregate.histogram.Histogram.SameRangesMergeStrategy;

/**
 * Provides interface for implementing merge strategies for aggregates.  
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of aggregate.
 * 
 * @see SameRangesMergeStrategy
 * @see NumericRangesMergeStrategy
 */
public interface MergeStrategy<T> {

	/**
	 * Merges aggregated values from <code>source</code> to <code>target</code>.
	 * 
	 * @param target 
	 * @param source
	 */
	public void merge(T target, T source);
	
}
