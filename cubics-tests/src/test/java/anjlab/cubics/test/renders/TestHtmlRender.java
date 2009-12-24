package anjlab.cubics.test.renders;

import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.junit.Test;

import anjlab.cubics.Cube;
import anjlab.cubics.FactModel;
import anjlab.cubics.aggregate.histogram.HistogramAggregateFactory;
import anjlab.cubics.aggregate.histogram.Histogram.HistogramMergeStrategy;
import anjlab.cubics.aggregate.pie.PieAggregateFactory;
import anjlab.cubics.coerce.IntegerCoercer;
import anjlab.cubics.renders.HtmlRender;
import anjlab.cubics.test.Fact;
import anjlab.cubics.test.TestHelper;


public class TestHtmlRender {

	@Test
	public void renderCube() throws Exception {
		FactModel<Fact> model = TestHelper.createFactModel();
		Iterable<Fact> facts = TestHelper.createTestFacts();
		Cube<Fact> c = Cube.createCube(model, facts);
		
		HtmlRender<Fact> render = new HtmlRender<Fact>(c);
		
		StringBuilder builder = render.render();
		assertNotNull(builder);

		String html = saveToHTMLFile(builder, "../cubics-renders/target/classes/index.html");
		
		System.out.println(html);
	}

	private String saveToHTMLFile(StringBuilder builder, String filename)
			throws FileNotFoundException, IOException {
		builder.insert(0, "<a href='javascript:expandAll();'>Expand All</a>&nbsp;");
		builder.insert(0, "<a href='javascript:expandOne();'>More &#xBB;</a>&nbsp;");
		builder.insert(0, "<a href='javascript:collapseOne();'>&#xAB; Less</a>&nbsp;");
		builder.insert(0, "<a href='javascript:collapseAll();'>Collapse All</a>&nbsp;");
		builder.insert(0, "<body>");
		builder.insert(0, "<style> td { vertical-align:top; } </style>\n");
		builder.insert(0, "<script src='anjlab/cubics/js/cube.js'></script>");
		builder.insert(0, "<script src='anjlab/cubics/js/jquery-1.3.2.js'></script>");
		builder.insert(0, "<link rel='stylesheet' href='anjlab/cubics/css/cube.css' type='text/css'>");
		builder.insert(0, "<html>");
		String html = builder.toString();
		builder.append("<div id='debug'/>");
		builder.append("</body></html>");
		
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(builder.toString().getBytes());
		fos.close();
		return html;
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
		
		String html = saveToHTMLFile(builder, "../cubics-renders/target/classes/index2.html");
		
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
		
		String html = saveToHTMLFile(builder, "../cubics-renders/target/classes/index3.html");
		
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
		
		String html = saveToHTMLFile(builder, "../cubics-renders/target/classes/index4.html");
		
		System.out.println(html);
	}

}
