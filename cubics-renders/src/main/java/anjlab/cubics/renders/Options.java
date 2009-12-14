package anjlab.cubics.renders;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class Options<T> {

	private List<String> attributes;
	private Map<String, String> formats;
	private Map<String, String> labels;
	
	public Options(List<String> attributes, List<String> formats) {
		this.attributes = new ArrayList<String>(attributes.size());
		this.attributes.addAll(attributes);
		
		this.formats = new HashMap<String, String>();
		for (int i = 0; i < attributes.size(); i++) {
			this.formats.put(
					attributes.get(i), 
					formats == null || formats.isEmpty() ? null : formats.get(i));
		}
		
		this.labels = new HashMap<String, String>();
	}
	
	/**
	 * Set new order for attributes in which they appear in resulting table.
	 * 
	 * @param orderedAttributes Attributes names in required order.
	 * @return This instance.
	 */
	public Options<T> reorder(String... orderedAttributes) {
		int idx = 0;
		for (String attribute : orderedAttributes) {
			int oldIdx = this.attributes.indexOf(attribute);
			if (oldIdx != idx) {
				if (oldIdx != -1) {
					this.attributes.remove(oldIdx);
				}
				this.attributes.add(idx, attribute);
			}
			idx++;
		}
		return this;
	}

	/**
	 * Excludes <code>attributes</code> from options.
	 * 
	 * @param attributes Attributes to exclude.
	 * @return This instance.
	 */
	public Options<T> exclude(String... attributes) {
		for (String attribute : attributes) {
			this.attributes.remove(attribute);
		}
		return this;
	}

	/**
	 * Sets new format for the <code>attribute</code>.
	 * 
	 * @param attribute Attribute to set format for.
	 * @param newFormat New format value.
	 * @return This instance.
	 * @See Formatter
	 */
	public Options<T> setFormat(String attribute, String newFormat) {
		formats.put(attribute, newFormat);
		return this;
	}

	public List<String> getAttributes() {
		return Collections.unmodifiableList(attributes);
	}

	public String getFormat(String attribute) {
		return formats.get(attribute);
	}
	
	/**
	 * Sets new display label for the <code>attribute</code>.
	 * 
	 * @param attribute Attribute to set label for.
	 * @param newFormat Label.
	 * @return This instance.
	 */
	public Options<T> setLabel(String attribute, String label) {
		labels.put(attribute, label);
		return this;
	}
	
	public String getLabel(String attribute) {
		if (labels.containsKey(attribute)) {
			return labels.get(attribute);
		}
		return attribute;
	}

	/**
	 * Adds new attribute to the attribute list.
	 * 
	 * @param attribute Attribute to add.
	 * @return This instance.
	 */
	public Options<T> add(String attribute) {
		this.attributes.add(attribute);
		return this;
	}
}
