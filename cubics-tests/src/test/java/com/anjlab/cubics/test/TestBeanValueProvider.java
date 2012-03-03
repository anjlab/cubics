package com.anjlab.cubics.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.anjlab.cubics.BeanValueProvider;
import com.anjlab.cubics.FactValueProvider;



public class TestBeanValueProvider {

	@Test
	public void readFields() {
		FactValueProvider<Fact> beanClass = new BeanValueProvider<Fact>(Fact.class);
		
		Fact fact = new Fact(1, 2, 3, 4, 5, 6);
		
		assertEquals(1, beanClass.getValue("year", fact));
		assertEquals(2, beanClass.getValue("month", fact));
		assertEquals(3, beanClass.getValue("day", fact));
		assertEquals(4, beanClass.getValue("hour", fact));
		assertEquals(5, beanClass.getValue("duration", fact));
		assertEquals(6, beanClass.getValue("succeeded", fact));
	}
}
