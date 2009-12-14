package anjlab.cubics.aggregate.pie;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anjlab.cubics.Coercer;
import anjlab.cubics.DataCollector;
import anjlab.cubics.JSONSerializable;

public class Pie implements DataCollector<Object>, JSONSerializable {

	private Map<Object, Integer> data = new HashMap<Object, Integer>();
	private int count;
	private Coercer<?> coercer;
	
	public Pie(Coercer<?> coercer) {
		this.coercer = coercer;
	}

	public void add(Object value) {
		Integer prev = data.get(value);
		data.put(value, prev == null ? 1 : prev + 1);
		count++;
	}

	public void merge(Pie pie) {
		for (Object key : pie.data.keySet()) {
			Integer prev = data.get(key);
			Integer curr = pie.data.get(key);
			data.put(key, prev == null ? curr : prev + curr);
		}
		count += pie.count;
	}

	public Map<Object, Integer> getData() {
		return data;
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
		for (Object key : getSortedKeys()) {
			if (builder.length() > 0) {
				builder.append(", ");
			}
			builder.append(key);
			builder.append(" = ");
			builder.append(data.get(key));
		}
		if (builder.length() > 0) {
			builder.append(", ");
		}
		builder.append("count = ");
		builder.append(count);
		return builder.toString();
	}

	@SuppressWarnings("unchecked")
	private List<Object> getSortedKeys() {
		List<Object> result = new ArrayList<Object>();
		result.addAll(data.keySet());
		
		if (result.size() > 0) {
			if (result.get(0) instanceof Comparable<?>) {
				Collections.sort((List)result);
			}
		}
		
		return result;
	}

	public Object coerceKey(String key) {
		return coercer.coerce(key);
	}

	public String toJSON() {
		StringBuilder builder = new StringBuilder();
		
		List<Object> keys = getSortedKeys();
		
		builder.append("{");
		builder.append("keys:");
		builder.append("[");
		boolean first = true;
		for (Object key : keys) {
			if (!first) {
				builder.append(",");
			} else {
				first = false;
			}
			builder.append("\"");
			builder.append(key);
			builder.append("\"");
		}
		builder.append("]");
		builder.append(",");
		builder.append("values:");
		builder.append("[");
		first = true;
		for (Object key : keys) {
			if (!first) {
				builder.append(",");
			} else {
				first = false;
			}
			builder.append(data.get(key));
		}
		builder.append("]");
		builder.append(",");
		builder.append("count:");
		builder.append(count);
		builder.append("}");
		
		return builder.toString();
	}

}
