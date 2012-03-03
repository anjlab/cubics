package com.anjlab.cubics.aggregate.histogram;

import java.io.Serializable;

import com.anjlab.cubics.CustomAggregateFactory;
import com.anjlab.cubics.aggregate.histogram.Histogram.HistogramMergeStrategy;
import com.anjlab.cubics.aggregate.histogram.Histogram.NumericRangesMergeStrategy;
import com.anjlab.cubics.aggregate.histogram.Histogram.SameRangesMergeStrategy;


public class HistogramAggregateFactory<T> implements CustomAggregateFactory<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4264578102784243017L;
	
	private double start;
	private double step;
	private double end;
	
	private Range[] ranges;
	
	private HistogramMergeStrategy mergeStrategy;
	
	public HistogramAggregateFactory(HistogramMergeStrategy mergeStrategy, double start, double step, double end) {
		this.mergeStrategy = mergeStrategy;
		this.start = start;
		this.step = step;
		this.end = end;
	}
	
	public HistogramAggregateFactory(HistogramMergeStrategy mergeStrategy, Range... ranges) {
		this.mergeStrategy = mergeStrategy;
		this.ranges = ranges;
	}
	
	public String getFormat() {
		return null;
	}

	public String getAggregateName() {
		return "histogram";
	}

	public HistogramAggregate<T> createAggregate() {
		MergeStrategy<Histogram> mergeStrategy = 
			this.mergeStrategy == HistogramMergeStrategy.SameRanges
				? new SameRangesMergeStrategy()
				: new NumericRangesMergeStrategy();
				
		return ranges == null 
		     ? new HistogramAggregate<T>(mergeStrategy, start, step, end)
		     : new HistogramAggregate<T>(mergeStrategy, ranges);
	}
}