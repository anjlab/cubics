package anjlab.cubics;

public interface CustomAggregateFactory<T> {

	public CustomAggregate<T> createAggregate();

	/**
	 * Gets the name of aggregated function.
	 * 
	 * @return Returns the name of aggregated function. 
	 */
	public abstract String getAggregateName();
	
	/**
	 * 
	 * @return
	 */
	public String getFormat();
	
}
