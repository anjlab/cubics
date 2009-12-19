package anjlab.cubics.aggregate.histogram;

import java.io.Serializable;

import anjlab.cubics.CustomAggregate;
import anjlab.cubics.CustomAggregateFactory;

public class HistogramAggregateFactory<T> implements CustomAggregateFactory<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4264578102784243017L;
	
	private double start;
	private double step;
	private double end;
	
	private Range[] ranges;
	
	public HistogramAggregateFactory(double start, double step, double end) {
		this.start = start;
		this.step = step;
		this.end = end;
	}
	
	public HistogramAggregateFactory(Range... ranges) {
		this.ranges = ranges;
	}
	
	public String getFormat() {
		return null;
	}

	public String getAggregateName() {
		return "histogram";
	}

	public CustomAggregate<T> createAggregate() {
		return ranges == null 
		     ? new HistogramAggregate<T>(start, step, end)
		     : new HistogramAggregate<T>(ranges);
	}
}