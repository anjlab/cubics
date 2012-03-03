package com.anjlab.cubics.test;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import com.anjlab.cubics.BeanValueProvider;
import com.anjlab.cubics.FactModel;
import com.anjlab.cubics.aggregate.histogram.Histogram;
import com.anjlab.cubics.aggregate.histogram.HistogramAggregate;
import com.anjlab.cubics.aggregate.histogram.HistogramAggregateFactory;
import com.anjlab.cubics.aggregate.histogram.Range;
import com.anjlab.cubics.aggregate.histogram.Histogram.HistogramMergeStrategy;


public class TestHelper {

	@Test
	public void dummy() {
		//	junit needs this
	}
	
	public static Histogram createHistogram(double start, double step, double end) {
		HistogramAggregateFactory<Fact> factory = 
				new HistogramAggregateFactory<Fact>(
					HistogramMergeStrategy.NumericRanges, 
					Range.createRanges(start, step, end));
		
		HistogramAggregate<Fact> aggregate = (HistogramAggregate<Fact>) factory.createAggregate();
		
		Histogram histogram = aggregate.getValue();
		return histogram;
	}

	public static FactModel<Fact> createFactModel() {
		FactModel<Fact> model = new FactModel<Fact>(new BeanValueProvider<Fact>(Fact.class));
		model.setDimensions("year", "month", "day", "hour");
		model.setMeasures("duration", "succeeded");
		return model;
	}

	public static List<Fact> createTestFacts() {
		List<Fact> result = new ArrayList<Fact>();
		
		result.add(new Fact(2009, 01, 01, 00, 1000, 1));
		result.add(new Fact(2009, 01, 01, 01, 2000, 0));
		result.add(new Fact(2009, 02, 01, 00, 1000, 1));
		result.add(new Fact(2009, 02, 02, 00, 1000, 1));
		result.add(new Fact(2010, 01, 01, 00, 1000, 1));
		result.add(new Fact(2010, 01, 01, 00, 3000, 1));
		
		return result;
	}

	public static FactModel<Fact> createFactModel2() {
		FactModel<Fact> model = new FactModel<Fact>(new BeanValueProvider<Fact>(Fact.class));
		model.setDimensions("month");
		model.setMeasures("duration", "succeeded");
		return model;
	}

}
