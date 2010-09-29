package anjlab.cubics;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Represents the cube of aggregates.
 *
 * @author dmitrygusev
 *
 * @param <T> Type of the fact record.
 */
public class Cube<T> implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3761391818016806526L;
	
	private final FactModel<T> model;
	private Map<String, Map<Key, Hierarchy<T>>> dimensions;
	private final FactValueProvider<T> valueProvider;
	
	private static final Map<String, Integer> calendarFields = buildCalendarFields();
    private static final ThreadLocal<Calendar> calendar = getCalendar();
	
	private Cube(FactModel<T> model, Iterable<T> facts) {
		this.model = model;
		this.valueProvider = model.getValueProvider();
		initDimensions(model);
		calculateCube(facts);
	}

	private static ThreadLocal<Calendar> getCalendar() {
	    ThreadLocal<Calendar> calendar = new ThreadLocal<Calendar>();
        calendar.set(Calendar.getInstance());
        return calendar;
    }

    private static Map<String, Integer> buildCalendarFields() {
        Map<String, Integer> calendarFields = new HashMap<String, Integer>(7);
        calendarFields.put("y", Calendar.YEAR);
        calendarFields.put("M", Calendar.MONTH);
        calendarFields.put("d", Calendar.DATE);
        calendarFields.put("H", Calendar.HOUR_OF_DAY);
        calendarFields.put("m", Calendar.MINUTE);
        calendarFields.put("s", Calendar.SECOND);
        return calendarFields;
    }

    private void calculateCube(Iterable<T> facts) {
		if (facts == null) {
			return;
		}
		
		for (T fact : facts) {
			addFact(fact);
		}
	}

	public void addFact(T fact) {
	    if (root != null) {
	        throw new IllegalStateException("The cube has already been calculated.");
	    }
	    
		boolean increaseRequired = false;
		
		Hierarchy<T> parentHierarchy = null;
		for (String dimensionName : model.getDimensions()) {
		    Object dimensionValue;
		    
		    if (dimensionName.contains("-")) {
		        String[] parts = dimensionName.split("-");
		        Object value = valueProvider.getValue(parts[0], fact);
		        
		        if (value instanceof Date) {
		            calendar.get().setTime((Date) value);
		            Integer calendarField = getCalendarField(parts[1]);
		            if (calendarField == null) {
		                throw new RuntimeException(
		                        "Unsupported option \"" + parts[1] + "\" in dimension specification \"" 
                                + dimensionName + "\". Could be one of " + calendarFields.keySet() + ".");
		            }
                    dimensionValue = calendar.get().get(calendarField);
		        } else {
		            dimensionValue = valueProvider.getValue(dimensionName, fact);
		        }
		    } else {
		        dimensionValue = valueProvider.getValue(dimensionName, fact);
		    }

			Map<Key, Hierarchy<T>> slice = this.dimensions.get(dimensionName);

			Key key = new Key(
					dimensionValue,
					parentHierarchy == null 
						? null 
						: parentHierarchy.getPath());
			
			if (! slice.containsKey(key)) {
				slice.put(key, new Hierarchy<T>(
						this, parentHierarchy, dimensionName, dimensionValue));
			}
			
			Hierarchy<T> hierarchy = slice.get(key);

			if (parentHierarchy != null) {
				if (! parentHierarchy.getChildren().containsKey(key)) {
					parentHierarchy.getChildren().put(key, hierarchy);
					increaseRequired = true;
				}
			}
			
			parentHierarchy = hierarchy;
		}

		Hierarchy<T> lowestHierarchy = parentHierarchy;

		lowestHierarchy.addFact(fact);
		
		if (increaseRequired) {
			lowestHierarchy.increaseSize();
		}
	}

    private Integer getCalendarField(String fieldName) {
        return calendarFields.get(fieldName);
    }

    private void initDimensions(FactModel<T> model) {
		this.dimensions = new HashMap<String, Map<Key, Hierarchy<T>>>(model.getDimensions().length);
		for (String dimension : model.getDimensions()) {
			this.dimensions.put(dimension, new HashMap<Key, Hierarchy<T>>());
		}
	}

	/**
	 * Creates and fills the cube.
	 * 
	 * @param <T> Type of the fact bean.
	 * @param model The {@link FactModel} for the corresponding bean type <code>T</code>.
	 * @param facts The source facts used to calculate the cube.
	 * @return New cube.
	 */
	public static <T> Cube<T> createCube(FactModel<T> model, Iterable<T> facts) {
		Cube<T> cube = new Cube<T>(model, facts);
		
		//	Force merge totals
		cube.getRoot();
		
		return cube;
	}

	/**
	 * Creates empty cube for specified model.
	 * 
	 * @param <T> Type of the fact bean.
	 * @param model The {@link FactModel} for the corresponding bean type <code>T</code>.
	 * @return New cube.
	 */
	public static <T> Cube<T> createCube(FactModel<T> model) {
        Cube<T> cube = new Cube<T>(model, null);
        
        return cube;
	}

	private Hierarchy<T> root;
	
	/**
	 * Gets the root hierarchy whose children collection contains list of all
	 * hierarchies of the first cube's dimension.
	 *  
	 * @return The root hierarchy.
	 */
	public Hierarchy<T> getRoot() {
		if (root == null) {
			root = new Hierarchy<T>(
				this,
				null,
				null,
				null,
				getDimension(model.getDimensions()[0]));
		}
		
		return root;
	}

	/**
	 * Gets a collection of all hierarchies in cube for the specified <code>dimension</code>.  
	 * 
	 * @param dimension The name of dimension.
	 * @return List of dimension hierarchies.
	 */
	public Map<Key, Hierarchy<T>> getDimension(String dimension) {
		return dimensions.get(dimension);
	}

	/**
	 * Gets the fact model for the cube. 
	 * @return Cube's fact model.
	 */
	public FactModel<T> getModel() {
		return model;
	}

}
