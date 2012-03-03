package com.anjlab.cubics.aggregate.pie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.anjlab.cubics.Coercer;
import com.anjlab.cubics.DataCollector;
import com.anjlab.cubics.JSONSerializable;


public class Pie implements DataCollector<Object>, JSONSerializable, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2464289887223684813L;
	
	private Map<Object, Long> data = new HashMap<Object, Long>();
	private long count;
	private Coercer<?> coercer;
	
	public Pie(Coercer<?> coercer) {
		this.coercer = coercer;
	}

	public void add(Object value) {
		Long prev = data.get(value);
		data.put(value, prev == null ? 1 : prev + 1);
		count++;
	}

	public void merge(Pie pie) {
		for (Object key : pie.data.keySet()) {
			Long prev = data.get(key);
			Long curr = pie.data.get(key);
			data.put(key, prev == null ? curr : prev + curr);
		}
		count += pie.count;
	}

	public Map<Object, Long> getData() {
		return data;
	}

	public long getCount() {
		return count;
	}

	public Long getDefaultValue() {
		return 0L;
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

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
