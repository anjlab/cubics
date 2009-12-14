package anjlab.cubics;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains hierarchy totals.
 * 
 * @author dmitrygusev
 *
 * @param <T> Type of the bean class.
 */
public class Totals<T> {

	private Map<String, Aggregate<T>> aggregates;
	
	public Totals(String[] measures, Map<String, List<CustomAggregateFactory<T>>> aggregateFactories) {
		this.aggregates = new HashMap<String, Aggregate<T>>();
		for (String measure : measures) {
			aggregates.put(measure, 
					new Aggregate<T>(
							aggregateFactories.containsKey(measure)
								? aggregateFactories.get(measure)
								: null));
		}
	}

	public void merge(Totals<T> totals) {
		for (String measure : aggregates.keySet()) {
			aggregates.get(measure).merge(totals.aggregates.get(measure));
		}
	}
	
	/**
	 * Gets aggregate for the given <code>measure</code>.
	 * 
	 * @param measure Measure name.
	 * @return Aggregate for the given measure.
	 */
	public Aggregate<T> getAggregate(String measure) {
		return aggregates.get(measure);
	}

	@Override
	public String toString() {
		return aggregates.toString();
	}
}
