package anjlab.cubics.coerce;

import anjlab.cubics.Coercer;

public class IntegerCoercer implements Coercer<Integer> {

	public Integer coerce(String s) {
		return Integer.parseInt(s);
	}
	
}
