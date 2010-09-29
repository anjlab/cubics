package anjlab.cubics.renders;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import anjlab.cubics.Aggregate;
import anjlab.cubics.Cube;
import anjlab.cubics.CustomAggregateFactory;

public abstract class AbstractRender<T, R> implements Render<T, R> {

    protected Cube<T> cube;
    protected String[] dimensions;
    protected Map<String, Options<T>> aggregatesOptions;
    protected Options<T> measuresOptions;
    protected Options<T> dimensionsOptions;

    /**
     * 
     * @param cube Cube to render.
     */
    public AbstractRender(Cube<T> cube) {
        this.cube = cube;
        this.dimensions = cube.getModel().getDimensions();

        String[] measures = cube.getModel().getMeasures();

        this.aggregatesOptions = new HashMap<String, Options<T>>(measures.length);
        this.measuresOptions = createOptions(toList(measures));
        this.dimensionsOptions = createOptions(toList(dimensions, "all"));
    }

    protected static <T> List<T> toList(T[] array, T... more) {
    	List<T> result = new ArrayList<T>(array.length);
    	result.addAll(Arrays.asList(array));
    	if (more != null && more.length > 0) {
    		result.addAll(Arrays.asList(more));
    	}
    	return result;
    }

    protected Options<T> createOptions(List<String> attributes) {
    	return new Options<T>(attributes, null);
    }

    /* (non-Javadoc)
     * @see anjlab.cubics.renders.Render#getAggregatesOptions(java.lang.String)
     */
    public Options<T> getAggregatesOptions(String measure) {
    	if (! aggregatesOptions.containsKey(measure)) {
    		List<String> names = new ArrayList<String>();
    		names.addAll(Arrays.asList(Aggregate.getNames()));
    		
    		List<String> formats = new ArrayList<String>();
    		formats.addAll(Arrays.asList(Aggregate.getFormats()));
    
    		List<CustomAggregateFactory<T>> factories = 
    			cube.getModel().getCustomAggregateFactories().get(measure);
    
    		if (factories != null) {
    			for (CustomAggregateFactory<T> factory : factories) {
    				names.add(factory.getAggregateName());
    				formats.add(factory.getFormat());
    			}
    		}
    		
    		aggregatesOptions.put(measure, new Options<T>(names, formats));
    	}
    	return aggregatesOptions.get(measure);
    }

    /* (non-Javadoc)
     * @see anjlab.cubics.renders.Render#getMeasuresOptions()
     */
    public Options<T> getMeasuresOptions() {
    	return measuresOptions;
    }

    /* (non-Javadoc)
     * @see anjlab.cubics.renders.Render#getDimensionsOptions()
     */
    public Options<T> getDimensionsOptions() {
    	return dimensionsOptions;
    }

}