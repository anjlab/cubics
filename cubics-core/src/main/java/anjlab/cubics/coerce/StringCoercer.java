package anjlab.cubics.coerce;

import java.io.Serializable;

import anjlab.cubics.Coercer;

public class StringCoercer implements Coercer<String>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1980761902665490262L;

	public String coerce(String s) {
		return s;
	}
	
}
