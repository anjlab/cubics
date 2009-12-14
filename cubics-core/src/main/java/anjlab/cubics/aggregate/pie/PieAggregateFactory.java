package anjlab.cubics.aggregate.pie;

import anjlab.cubics.Coercer;
import anjlab.cubics.CustomAggregate;
import anjlab.cubics.CustomAggregateFactory;

public class PieAggregateFactory<T> implements CustomAggregateFactory<T> {

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
