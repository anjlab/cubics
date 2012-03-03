package com.anjlab.cubics.test.renders;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.anjlab.cubics.Cube;
import com.anjlab.cubics.FactModel;
import com.anjlab.cubics.aggregate.histogram.HistogramAggregateFactory;
import com.anjlab.cubics.aggregate.histogram.Histogram.HistogramMergeStrategy;
import com.anjlab.cubics.aggregate.pie.PieAggregateFactory;
import com.anjlab.cubics.coerce.IntegerCoercer;
import com.anjlab.cubics.renders.html.HtmlRender;
import com.anjlab.cubics.test.Fact;
import com.anjlab.cubics.test.TestHelper;



public class TestHtmlRender {

	@Test
	public void renderCube() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		HtmlRender<Fact> render = new HtmlRender<Fact>(c);
		
		StringBuilder builder = render.render();
		assertNotNull(builder);

		String html = HtmlRender.saveToHTMLFile(builder, 
		        "target/index.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
		        "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);
	}

	@Test
	public void renderWithOptions() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		
		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		HtmlRender<Fact> render = new HtmlRender<Fact>(c);

		render.getAggregatesOptions("succeeded").
			reorder("count").
			exclude("min", "max", "sum", "avg");

		render.getAggregatesOptions("duration").
			exclude("count");

		render.getMeasuresOptions().
			reorder("succeeded").
			setLabel("duration", "duration, ms");
		
		render.getDimensionsOptions().
			setLabel("all", "All");
		
		StringBuilder builder = render.render();
		assertNotNull(builder);
		
        String html = HtmlRender.saveToHTMLFile(builder, 
                "target/index2.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
                "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);

	}
	
	@Test
	public void renderHistogramAndPie() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		
		model.declareCustomAggregate(
				new HistogramAggregateFactory<Fact>(HistogramMergeStrategy.NumericRanges, 0, 1000, 10000), 
				"duration");
		
		model.declareCustomAggregate(new PieAggregateFactory<Fact>(new IntegerCoercer()), "succeeded");

		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		HtmlRender<Fact> render = new HtmlRender<Fact>(c);

		render.getAggregatesOptions("succeeded").
			add("pie-1-%").
			setFormat("pie-1-%", "%.1f").
			add("pie-1-!").
			setFormat("pie-1-!", "%.2f");
		
		render.getAggregatesOptions("duration").
			add("histogram-[1000;2000)");
		
		StringBuilder builder = render.render();
		assertNotNull(builder);
		
        String html = HtmlRender.saveToHTMLFile(builder, 
                "target/index3.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
                "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);
	}

	@Test
	public void renderEmptyCube() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		Cube<Fact> c = Cube.createCube(model);
		
		HtmlRender<Fact> render = new HtmlRender<Fact>(c);
		
		StringBuilder builder = render.render();
		assertNotNull(builder);
		assertTrue("Rendering failed", builder.toString().endsWith("</tr></table>"));
		
        String html = HtmlRender.saveToHTMLFile(builder, 
                "target/index4.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
                "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);
	}

}
