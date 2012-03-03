package com.anjlab.cubics.coerce;

import java.io.Serializable;

import com.anjlab.cubics.Coercer;


public class IntegerCoercer implements Coercer<Integer>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 135760587855882465L;

	public Integer coerce(String s) {
		return Integer.parseInt(s);
	}
	
}
