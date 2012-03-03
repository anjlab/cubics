package com.anjlab.cubics.aggregate.histogram;

import java.io.Serializable;

import com.anjlab.cubics.Aggregate;
import com.anjlab.cubics.CustomAggregate;



public class HistogramAggregate<T> implements CustomAggregate<T>, Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -6304099435196589610L;
	
	private Histogram histogram;
	
	public HistogramAggregate(MergeStrategy<Histogram> mergeStrategy, double start, double step, double end) {
		histogram = new Histogram(mergeStrategy, start, step, end);
	}

	public HistogramAggregate(MergeStrategy<Histogram> mergeStrategy, Range[] ranges) {
		histogram = new Histogram(mergeStrategy, ranges);
	}

	public void merge(Aggregate<T> aggregate, CustomAggregate<T> other) {
		histogram.merge(((HistogramAggregate<T>)other).histogram);
	}
	
	public Histogram getValue() {
		return histogram;
	}
	
	public void add(Aggregate<T> aggregate, Object value) {
		if (! (value instanceof Comparable<?>)) {
			return;
		}
		histogram.add(value);
	}
}
