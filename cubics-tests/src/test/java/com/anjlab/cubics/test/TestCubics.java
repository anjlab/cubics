package com.anjlab.cubics.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.fail;

import java.util.Map;

import org.junit.Test;

import com.anjlab.cubics.Aggregate;
import com.anjlab.cubics.Cube;
import com.anjlab.cubics.FactModel;
import com.anjlab.cubics.Hierarchy;
import com.anjlab.cubics.Key;
import com.anjlab.cubics.Totals;
import com.anjlab.cubics.aggregate.histogram.Histogram;
import com.anjlab.cubics.aggregate.histogram.HistogramAggregateFactory;
import com.anjlab.cubics.aggregate.histogram.Histogram.HistogramMergeStrategy;
import com.anjlab.cubics.aggregate.pie.Pie;
import com.anjlab.cubics.aggregate.pie.PieAggregateFactory;
import com.anjlab.cubics.coerce.IntegerCoercer;


public class TestCubics {

	@Test
	public void createCube() {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		Cube<Fact> c = Cube.createCube(model, facts);
		
		Hierarchy<Fact> root = c.getRoot();

		String name = root.getDimensionName();
		//	Name of the root hierarchy is null
		assertNull(name);
		
		//	Two hierarchies: one for 2009 and one for 2010
		assertEquals(2, root.getChildren().size());
		
		Map<Key, Hierarchy<Fact>> children = root.getChildren();
		String hierarchyName = null;
		for (Hierarchy<Fact> child : children.values()) {
			Object[] path = child.getPath();
			assertNotNull(path);
			assertEquals(1, path.length);
			
			if (hierarchyName == null) {
				hierarchyName = child.getDimensionName();
			}
			
			//	All children should have the same name since they're 
			//	from the same dimension
			assertEquals(hierarchyName, child.getDimensionName());
			
			Totals<Fact> totals = child.getTotals();
			Aggregate<Fact> a = totals.getAggregate("duration");
			assertNotNull(a);
		}
		
		//	here <code>years</code> is the same as <code>root.getChildren()</code>
		Map<Key, Hierarchy<Fact>> years = c.getDimension("year");
		
		assertEquals(root.getChildren(), years);
	}
	
	@Test
	public void calculateCube() {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		Cube<Fact> c = Cube.createCube(model, facts);

		//	First-level hierarchy
		Hierarchy<Fact> h = c.getDimension("year").get(new Key(2009));
		
		assertNotNull(h);
		
		assertEquals("year", h.getDimensionName());
		assertEquals(2009, h.getDimensionValue());
		
		Aggregate<Fact> a = h.getTotals().getAggregate("duration");
		assertEquals(4, a.getCount());
		assertEquals(1000, a.getMin(), 0);
		assertEquals(2000, a.getMax(), 0);
		assertEquals(1250, a.getAverage(), 0);
		assertEquals(5000, a.getSum(), 0);
		
		assertEquals(2, h.getChildren().size());
		
		//	Second-level hierarchy
		h = c.getDimension("month").get(new Key(1, 2009));
		
		assertNotNull(h);
		
		assertEquals("month", h.getDimensionName());
		assertEquals(1, h.getDimensionValue());
		
		a = h.getTotals().getAggregate("duration");
		assertEquals(2, a.getCount());
		assertEquals(1000, a.getMin(), 0);
		assertEquals(2000, a.getMax(), 0);
		assertEquals(1500, a.getAverage(), 0);
		assertEquals(3000, a.getSum(), 0);
		
		assertEquals(1, h.getChildren().size());

		//	Last-level hierarchy
		h = c.getDimension("hour").get(new Key(0, 2009, 1, 1));
		
		assertNotNull(h);
		
		assertEquals("hour", h.getDimensionName());
		assertEquals(0, h.getDimensionValue());
		
		a = h.getTotals().getAggregate("duration");
		assertEquals(1, a.getCount());
		assertEquals(1000, a.getMin(), 0);
		assertEquals(1000, a.getMax(), 0);
		assertEquals(1000, a.getAverage(), 0);
		assertEquals(1000, a.getSum(), 0);
		
		assertEquals(0, h.getChildren().size());
	}
	
	@Test
	public void verifyCube() {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		Cube<Fact> c = Cube.createCube(model, facts);
		
		Hierarchy<Fact> root = c.getRoot();
		
		Hierarchy<Fact> h = root.getChildren().get(new Key(2009));
		assertNotNull(h);
		assertEquals(2, h.getChildren().size());
		Hierarchy<Fact> h2 = h.getChildren().get(new Key(1, 2009));
		assertNotNull(h2);
		assertEquals(1, h2.getChildren().size());
		Hierarchy<Fact> h3 = h2.getChildren().get(new Key(1, 2009, 1));
		assertNotNull(h3);
		assertEquals(2, h3.getChildren().size());
		Hierarchy<Fact> h4 = h3.getChildren().get(new Key(0, 2009, 1, 1));
		assertNotNull(h4);
		assertEquals(0, h4.getChildren().size());
		h4 = h3.getChildren().get(new Key(1, 2009, 1, 1));
		assertNotNull(h4);
		assertEquals(0, h4.getChildren().size());
		h2 = h.getChildren().get(new Key(2, 2009));
		assertNotNull(h2);
		assertEquals(2, h2.getChildren().size());
		h3 = h2.getChildren().get(new Key(1, 2009, 2));
		assertNotNull(h3);
		assertEquals(1, h3.getChildren().size());
		h3 = h2.getChildren().get(new Key(2, 2009, 2));
		assertNotNull(h3);
		assertEquals(1, h3.getChildren().size());
		
		h = root.getChildren().get(new Key(2010));
		assertNotNull(h);
		assertEquals(1, h.getChildren().size());
		h2 = h.getChildren().get(new Key(1, 2010));
		assertNotNull(h2);
		assertEquals(1, h2.getChildren().size());
		h3 = h2.getChildren().get(new Key(1, 2010, 1));
		assertNotNull(h3);
		assertEquals(1, h3.getChildren().size());
		h4 = h3.getChildren().get(new Key(0, 2010, 1, 1));
		assertNotNull(h4);
		assertEquals(0, h4.getChildren().size());
	}
	
	@Test
	public void calculateCube2() {
		FactModel<Fact> model = TestHelper.createFactModel2();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		assertEquals(2, c.getRoot().getChildren().size());
		assertEquals(4, c.getDimension("month").get(new Key(1)).getTotals().getAggregate("duration").getCount());
	}

	@Test
	public void calculateHistogram() {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		model.declareCustomAggregate(
				new HistogramAggregateFactory<Fact>(HistogramMergeStrategy.NumericRanges, 0, 1000, 10000), 
				"duration");
		
		Cube<Fact> c = Cube.createCube(model, facts);
		
		Histogram histogram = (Histogram) 
			c.getRoot().getTotals().getAggregate("duration").getValue("histogram");

		assertNotNull(histogram);
		assertEquals(10, histogram.getData().size());
		Long value;
		
		value = histogram.getData().get(histogram.getRange(0));	//	[0;	1000)
		assertNotNull(value);
		assertEquals(new Long(0), value);
		value = histogram.getData().get(histogram.getRange(1));	//	[1000; 2000)
		assertNotNull(value);
		assertEquals(new Long(4), value);
		value = histogram.getData().get(histogram.getRange(2));	//	[2000; 3000)
		assertNotNull(value);
		assertEquals(new Long(1), value);
		value = histogram.getData().get(histogram.getRange(2));	//	[3000; 4000)
		assertNotNull(value);
		assertEquals(new Long(1), value);
		assertEquals(0, histogram.getOthers());
		assertEquals(6, histogram.getCount());
	}

	@Test
	public void calculatePie() {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		model.declareCustomAggregate(new PieAggregateFactory<Fact>(new IntegerCoercer()), "succeeded");
		
		Cube<Fact> c = Cube.createCube(model, facts);
		
		Pie pie = (Pie) c.getRoot().getTotals().getAggregate("succeeded").getValue("pie");

		assertNotNull(pie);
		assertEquals(2, pie.getData().size());
		assertEquals(new Long(5), pie.getData().get(1));
		assertEquals(new Long(1), pie.getData().get(0));
		assertEquals(6, pie.getCount());
	}
	
	@Test
	public void calculateCubeWithoutFacts() {
		FactModel<Fact> model = TestHelper.createFactModel();
		
		Cube<Fact> c = Cube.createCube(model);
		
		Hierarchy<Fact> root = c.getRoot();
		
		assertEquals(0, root.getChildren().size());
		
		Iterable<Fact> facts = TestHelper.createTestFacts();
		
		try {
		    c.addFact(facts.iterator().next());
		    
		    fail("Can not add facts to cube aftet its been calculated, because totals has aleady been merged");
		} catch (IllegalStateException e) {
		    assertEquals("The cube has already been calculated.", e.getMessage());
		}
	}
}
