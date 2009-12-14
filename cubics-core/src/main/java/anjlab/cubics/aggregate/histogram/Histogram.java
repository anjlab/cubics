package anjlab.cubics.aggregate.histogram;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anjlab.cubics.DataCollector;
import anjlab.cubics.JSONSerializable;

public class Histogram implements DataCollector<Range>, JSONSerializable {

	private Map<Range, Integer> data = new HashMap<Range, Integer>();
	private Range[] ranges;
	private int others;
	private int count;

	public Histogram(Range... ranges) {
		initData(ranges);
	}
	
	public Histogram(double start, double step, double end) {
		List<Range> ranges = new ArrayList<Range>();
		for (double i = start; i <= end; i += step) {
			ranges.add(new Range(i, true, i + step, false));
		}
		ranges.get(ranges.size() - 1).setRightInclusive(true);
		initData(ranges.toArray(new Range[ranges.size()]));
	}

	private void initData(Range... ranges) {
		for (Range range : ranges) {
			data.put(range, 0);
		}
		this.ranges = ranges;
	}

	public void add(Object value) {
		count++;
		for (Range range : ranges) {
			if (range.includes(value)) {
				data.put(range, data.get(range) + 1);
				return;
			}
		}
		others++;
	}

	public void merge(Histogram histogram) {
		for (Range range : histogram.ranges) {
			Integer other = histogram.data.get(range);
			if (other != null) {
				data.put(range, data.get(range) + other);
			}
		}
		others += histogram.others;
		count += histogram.count;
	}

	public Map<Range, Integer> getData() {
		return data;
	}

	public Range getRange(int index) {
		return ranges[index];
	}
	
	public int getRangesCount() {
		return ranges.length;
	}
	
	public int getOthers() {
		return others;
	}

	public int getCount() {
		return count;
	}

	public Integer getDefaultValue() {
		return 0;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (Range range : ranges) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(range.toString());
			builder.append(" = ");
			builder.append(data.get(range));
		}
		if (builder.length() > 0) {
			builder.append(", ");
		}
		builder.append("others = ");
		builder.append(others);
		builder.append(", count = ");
		builder.append(count);
		return builder.toString();
	}

	public Range coerceKey(String key) {
		return Range.parseRange(key);
	}

	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		
		builder.append("{");
		builder.append("keys:");
		builder.append("[");
		boolean first = true;
		for (Range range : ranges) {
			if (!first) {
				builder.append(",");
			} else {
				first = false;
			}
			builder.append("\"");
			builder.append(range);
			builder.append("\"");
		}
		builder.append("]");
		builder.append(",");
		builder.append("values:");
		builder.append("[");
		first = true;
		for (Range range : ranges) {
			if (!first) {
				builder.append(",");
			} else {
				first = false;
			}
			builder.append(data.get(range));
		}
		builder.append("]");
		builder.append(",");
		builder.append("others:");
		builder.append(others);
		builder.append(",");
		builder.append("count:");
		builder.append(count);
		builder.append("}");
		
		return builder.toString();
	}
}
