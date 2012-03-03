package com.anjlab.cubics.test.renders;

import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.junit.Test;

import com.anjlab.cubics.Cube;
import com.anjlab.cubics.FactModel;
import com.anjlab.cubics.aggregate.histogram.HistogramAggregateFactory;
import com.anjlab.cubics.aggregate.histogram.Histogram.HistogramMergeStrategy;
import com.anjlab.cubics.aggregate.pie.PieAggregateFactory;
import com.anjlab.cubics.coerce.IntegerCoercer;
import com.anjlab.cubics.renders.html.HtmlRender2;
import com.anjlab.cubics.test.Fact;
import com.anjlab.cubics.test.TestHelper;



public class TestHtmlRender2 {

	@Test
	public void renderCube() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		HtmlRender2<Fact> render = new HtmlRender2<Fact>(c);
		
		StringBuilder builder = render.render();
		assertNotNull(builder);

		String html = HtmlRender2.saveToHTMLFile(builder, 
		        "target/index2.1.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
		        "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);
		
		assertWellFormedXML(builder);
	}

	@Test
	public void renderWithOptions() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		
		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		HtmlRender2<Fact> render = new HtmlRender2<Fact>(c);

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
		
        String html = HtmlRender2.saveToHTMLFile(builder, 
                "target/index2.2.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
                "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);

		assertWellFormedXML(builder);
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
		
		HtmlRender2<Fact> render = new HtmlRender2<Fact>(c);

		render.getAggregatesOptions("succeeded").
			add("pie-1-%").
			setFormat("pie-1-%", "%.1f").
			add("pie-1-!").
			setFormat("pie-1-!", "%.2f");
		
		render.getAggregatesOptions("duration").
			add("histogram-[1000;2000)");
		
		StringBuilder builder = render.render();
		assertNotNull(builder);
		
        String html = HtmlRender2.saveToHTMLFile(builder, 
                "target/index2.3.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
                "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);
		
		assertWellFormedXML(builder);
	}

	@Test
	public void renderEmptyCube() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		Cube<Fact> c = Cube.createCube(model);
		
		HtmlRender2<Fact> render = new HtmlRender2<Fact>(c);
		
		StringBuilder builder = render.render();
		assertNotNull(builder);
		
        String html = HtmlRender2.saveToHTMLFile(builder, 
                "target/index2.4.html",
                "../../cubics-renders/src/main/resources/com/anjlab/cubics/",
                "../src/test/resources/jquery-1.4.2.min.js");
		
		System.out.println(html);
		
		assertWellFormedXML(builder);
	}

    private static void assertWellFormedXML(StringBuilder builder) {
        String s = builder.toString().replaceAll("&nbsp;", "&#160;");
        InputStream inputStream = new ByteArrayInputStream(s.getBytes());

        try
        {
            getDocumentBuilder().parse(inputStream);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    
    private static DocumentBuilder documentBuilder;

    private static DocumentBuilder getDocumentBuilder() throws ParserConfigurationException {
        if (documentBuilder == null) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            documentBuilder = factory.newDocumentBuilder();
        }
        return documentBuilder;
    }

}
