package anjlab.cubics.aggregate.pie;

import anjlab.cubics.Aggregate;
import anjlab.cubics.Coercer;
import anjlab.cubics.CustomAggregate;

public class PieAggregate<T> implements CustomAggregate<T> {

	private Pie pie;
	
	public PieAggregate(Coercer<?> coercer) {
		this.pie = new Pie(coercer);
	}

	public void add(Aggregate<T> aggregate, Object value) {
		pie.add(value);
	}

	public Pie getValue() {
		return pie;
	}

	public void merge(Aggregate<T> aggregate, CustomAggregate<T> other) {
		pie.merge(((PieAggregate<T>)other).pie);
	}

}
