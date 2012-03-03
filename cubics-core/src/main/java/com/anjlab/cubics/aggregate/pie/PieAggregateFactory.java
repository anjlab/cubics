package com.anjlab.cubics.aggregate.pie;

import java.io.Serializable;

import com.anjlab.cubics.Coercer;
import com.anjlab.cubics.CustomAggregate;
import com.anjlab.cubics.CustomAggregateFactory;


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
