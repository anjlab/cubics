package com.anjlab.cubics.aggregate.pie;

import java.io.Serializable;

import com.anjlab.cubics.Aggregate;
import com.anjlab.cubics.Coercer;
import com.anjlab.cubics.CustomAggregate;


public class PieAggregate<T> implements CustomAggregate<T>, Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6245734627730461611L;
	
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
