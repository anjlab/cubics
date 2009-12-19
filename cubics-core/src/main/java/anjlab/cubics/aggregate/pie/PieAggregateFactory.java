package anjlab.cubics.aggregate.pie;

import java.io.Serializable;

import anjlab.cubics.Coercer;
import anjlab.cubics.CustomAggregate;
import anjlab.cubics.CustomAggregateFactory;

public class PieAggregateFactory<T> implements CustomAggregateFactory<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1305840793258529875L;
	
	private Coercer<?> coercer;

	public PieAggregateFactory(Coercer<?> coercer) {
		this.coercer = coercer;
	}
	
	public CustomAggregate<T> createAggregate() {
		return new PieAggregate<T>(coercer);
	}

	public String getAggregateName() {
		return "pie";
	}

	public String getFormat() {
		return null;
	}

}
