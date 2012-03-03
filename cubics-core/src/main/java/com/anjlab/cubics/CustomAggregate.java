package com.anjlab.cubics;

public interface CustomAggregate<T> {
	
	/**
	 * Update aggregate results by new <code>value</code>.
	 * 
	 * @param aggregate
	 *            Basic aggregates for this measure.
	 * @param value
	 *            The next value in turn.
	 */
	public abstract void add(Aggregate<T> aggregate, Object value);

	/**
	 * Merges values of this aggregates functions with values from
	 * <code>other</code> aggregate instance.
	 * 
	 * @param aggregate Basic aggregates for this measure.
	 * @param other
	 *            CustomAggregate to merge with.
	 */
	public abstract void merge(Aggregate<T> aggregate, CustomAggregate<T> other);

	/**
	 * Gets current value of this aggregate.
	 * 
	 * @return Returns current value of this aggregate function.
	 */
	public abstract Object getValue();

}