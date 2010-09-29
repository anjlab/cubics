package anjlab.cubics.renders.html;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;

import anjlab.cubics.Aggregate;
import anjlab.cubics.Cube;
import anjlab.cubics.DataCollector;
import anjlab.cubics.Hierarchy;
import anjlab.cubics.JSONSerializable;
import anjlab.cubics.Key;
import anjlab.cubics.Totals;
import anjlab.cubics.aggregate.histogram.Histogram;
import anjlab.cubics.aggregate.pie.Pie;
import anjlab.cubics.renders.AbstractRender;
import anjlab.cubics.renders.NaturalKeyComparator;
import anjlab.cubics.renders.Options;


/**
 * {@link Cube} HTML render.
 *  
 * @author dmitrygusev
 *
 */
public class HtmlRender<T> extends AbstractRender<T, StringBuilder> {

	private final NaturalKeyComparator comparator;

	private StringBuilder builder;

	
	/**
	 * 
	 * @param cube Cube to render.
	 */
	public HtmlRender(Cube<T> cube) {
	    super(cube);
	    
	    this.comparator = new NaturalKeyComparator();
	}

	/**
	 * {@inheritDoc}
	 * 
	 * @return HTML representation of the cube.
	 */
	public StringBuilder render() {
		builder = new StringBuilder();

		append("<table class='cubics' cellspacing='0'>\n");
		append("<tr>");
		append("<th rowspan='2'>", dimensionsOptions.getLabel("all") ,"</th>");
		//	Use original dimensions in original order 
		for (String dimension : dimensions) {
			append("<th rowspan='2'>", dimensionsOptions.getLabel(dimension), "</th>");
		}
		
		Options<T> aggregatesOptions;
		
		int hm = 0;
		for (String measure : measuresOptions.getAttributes()) {
			aggregatesOptions = getAggregatesOptions(measure);
			append("<th colspan='", aggregatesOptions.getAttributes().size(),
					"' id='hm-", hm, "'>", measuresOptions.getLabel(measure), "</th>");
			hm++;
		}
		append("</tr>\n");
		append("<tr>\n");
		int am = 0;
		hm = 0;
		for (String measure : measuresOptions.getAttributes()) {
			aggregatesOptions = getAggregatesOptions(measure);
			for (String aggregate : aggregatesOptions.getAttributes()) {
				append("<th class='am-", am, " hm-", hm, "'>");
				append(aggregatesOptions.getLabel(aggregate), "</th>");
				am++;
			}
			hm++;
		}
		append("</tr>\n");
		
		Hierarchy<T> root = cube.getRoot();
		append("<tr><td id='ix' class='x c-e' rowspan='", (root.getSizeWithTotals() - 1), "'>all</td>");
		
		renderHierarchy(root, -1, "x");
		
		if (root.getChildren().size() > 0) {	//	Not empty cube
			deleteFromEnd("<tr>".length());
		}
		
		append("</table>");
		
		return builder;
	}
	
	private void renderHierarchy(Hierarchy<T> hierarchy, int level, String path) {
		Map<Key, Hierarchy<T>> dimension = hierarchy.getChildren();
		Key[] keys = dimension.keySet().toArray(new Key[dimension.size()]);
		Arrays.sort(keys, comparator);

		int colspan;
		
		int index = 0;

		String parentId = null;
		
		for (Key key : keys) {
			append("<td ");

			boolean lastChild = dimension.get(key).getChildren().size() == 0;
			
			index = appendCssClass(
					path, index, 
					lastChild ? "c-ne" : null,
					dimension.get(key).getDimensionValue() instanceof Number ? "c-n" : null);

			if (lastChild) {
				//	XXX for now this is the only string concatenation, remove this in future releases
				parentId = path + "-" + index;
				append(" id='i", parentId, '\'');
			}
			
			int rowspan = dimension.get(key).getSizeWithTotals() - 1;
			if (rowspan > 1) {
			    append(" rowspan='", rowspan, "'");
			}
			append(">");
			append(dimension.get(key).getDimensionValue());
			append("</td>");
			if (lastChild) {
				append("</tr>");
				colspan = dimensions.length - (level + 1);
				append("\n<tr><td ");
				index = appendCssClass(path, index, "c-t");
				if (colspan > 1) {
				    append(" colspan='", colspan, "'");
				}
				append(">Totals:</td>");
				index = renderTotals(0, dimension.get(key).getTotals(), path, index, false, parentId);
				append("</tr>");
				append("\n<tr>");
			} else {
				renderHierarchy(dimension.get(key), level + 1, path + "-" + index);
			}
		}
		
		int offsetFromEnd;
		
		if (keys.length == 0) {
			append("</tr>");
			offsetFromEnd = 0;
		} else {
			offsetFromEnd = "</tr>".length();
		}
		
		insert(offsetFromEnd, "\n<tr><td ");
		index = insertCssClass(offsetFromEnd, path, index, 
				true /* set this to true to make totals collapsed only with parent dimension */,
				true,
				"c-t");
		colspan = dimensions.length - level;
		if (colspan > 1) {
		    insert(offsetFromEnd, " colspan='", colspan, "'");
		}
		insert(offsetFromEnd, ">Totals:</td></tr>");
		index = renderTotals(offsetFromEnd + "\n<tr>".length(), hierarchy.getTotals(), path, index, 
				true /* set this to true to make totals collapsed only with parent dimension */,
				path);
	}

	private int renderTotals(int delta, Totals<T> totals, String path, int index, boolean forTotals, String parentId) {
		int aggregateIndex = 0;
		for (String measure : measuresOptions.getAttributes()) {
			Aggregate<T> a = totals.getAggregate(measure);
			
			Options<T> options = getAggregatesOptions(measure);
			
			for (String aggregate : options.getAttributes()) {
				insert(delta, "<td ");
				Object value;
				if (aggregate.contains("-")) {
					String[] parts = aggregate.split("-");
					if (a.hasValue(parts[0])) {
						Object complexValue = a.getValue(parts[0]);
						if (complexValue instanceof DataCollector<?>)  {
							DataCollector<?> collector = (DataCollector<?>) complexValue;
							Object key = collector.coerceKey(parts[1]);
							Long intValue = collector.getData().get(key);

							if (intValue == null) {
								intValue = collector.getDefaultValue();
							}

							if (parts.length == 3) {
								if ("%".equals(parts[2])) {
									value = intValue * 100d / collector.getCount();
								} else if ("!".equals(parts[2])) {
									value = intValue * 1d / collector.getCount();
								} else {
									throw new RuntimeException(
											"Unsupported option \"" + parts[2] + "\" in aggregate specification \"" 
											+ aggregate + "\". Could be \"%\" or \"!\".");
								}
							} else if (parts.length == 2) {
								value = intValue;
							} else {
								throw new RuntimeException("Unsupported aggregate specification: " + aggregate);
							}
						} else {
							value = a.getValue(aggregate);
						}
					} else {
						value = a.getValue(aggregate);
					}
				} else {
					value = a.getValue(aggregate);
				}
				
				index = insertCssClass(delta, path, index, forTotals, false, "c-ne", value instanceof Number ? "c-n" : "c-cd");
				insert(delta, " c-m");
				insert(delta, " m-", aggregateIndex);
				insert(delta, " i", parentId);

				if (value instanceof JSONSerializable) {
					insert(delta, "'");
					insert(delta, " data-json='", ((JSONSerializable)value).toJSON(), "'>");
					if (value instanceof Pie) {
						insert(delta, "<a class='c-pb'/>");
					} else if (value instanceof Histogram) {
						insert(delta, "<a class='c-hb'/>");
					}
				} else {
					insert(delta, "'>", formatValue(value, options.getFormat(aggregate)));
				}
				
				insert(delta, "</td>");
				aggregateIndex++;
			}
		}
		return index;
	}

	private Object formatValue(Object value, String format) {
		return format == null || format.length() == 0 
			 ? value 
			 : String.format(format, value);
	}

	private int insertCssClass(int delta, String path, int index, boolean forTotals, boolean closeAttribute, String... additionalCssClasses) {
		index++;
		
		int lastIndexOf = path.lastIndexOf('-');
		String parentClass = forTotals 
			? (lastIndexOf == -1 
					? "" 
					: path.substring(0, lastIndexOf)) 
			: path;
			
		insert(delta, "class='", path, "-", index, 
				(parentClass.length() == 0 ? "" : " "), parentClass);
		
		if (additionalCssClasses != null) {
			for (String cssClass : additionalCssClasses) {
				if (cssClass != null) {
					insert(delta, " ", cssClass);
				}
			}
		}
		if (closeAttribute) {
			insert(delta, "'");
		}
		return index;
	}
	
	private int appendCssClass(String path, int index, String... additionalCssClasses) {
		index++;
		append("id='i", path, "-", index,"' class='", path, "-", index, " ", path, " c-e");
		if (additionalCssClasses != null) {
			for (String cssClass : additionalCssClasses) {
				if (cssClass != null) {
					append(" ", cssClass);
				}
			}
		}
		append("'");
		return index;
	}

	private void deleteFromEnd(int count) {
		builder.delete(builder.length() - count, builder.length());
	}

	private void append(Object... objects) {
		for (Object object : objects) {
			builder.append(object);
		}
	}

	private void insert(int delta, Object... objects) {
		for (Object object : objects) {
			builder.insert(builder.length() - delta, object);
		}
	}

    public static String saveToHTMLFile(StringBuilder builder, String filename, String cubicsResources, String jquery)
    		throws FileNotFoundException, IOException {
    	builder.insert(0, "<a href='javascript:expandAll();'>Expand All</a>&nbsp;");
    	builder.insert(0, "<a href='javascript:expandOne();'>More &#xBB;</a>&nbsp;");
    	builder.insert(0, "<a href='javascript:collapseOne();'>&#xAB; Less</a>&nbsp;");
    	builder.insert(0, "<a href='javascript:collapseAll();'>Collapse All</a>&nbsp;");
    	builder.insert(0, "<body>");
    	builder.insert(0, "<style> td { vertical-align:top; } </style>\n");
    	builder.insert(0, "<script src='" + cubicsResources + "js/cube.js'></script>");
    	builder.insert(0, "<script src='" + jquery + "'></script>");
    	builder.insert(0, "<link rel='stylesheet' href='" + cubicsResources + "css/cube.css' type='text/css'>");
    	builder.insert(0, "<html>");
    	String html = builder.toString();
    	builder.append("<div id='debug'></div>");
    	builder.append("</body></html>");
    	
    	FileOutputStream fos = new FileOutputStream(filename);
    	fos.write(builder.toString().getBytes());
    	fos.close();
    	return html;
    }

}
