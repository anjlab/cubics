package anjlab.cubics.test;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import anjlab.cubics.BeanClass;


public class TestBeanClass {

	@Test
	public void readFields() {
		BeanClass<Fact> beanClass = new BeanClass<Fact>(Fact.class);
		
		Fact fact = new Fact(1, 2, 3, 4, 5, 6);
		
		assertEquals(1, beanClass.getValue("year", fact));
		assertEquals(2, beanClass.getValue("month", fact));
		assertEquals(3, beanClass.getValue("day", fact));
		assertEquals(4, beanClass.getValue("hour", fact));
		assertEquals(5, beanClass.getValue("duration", fact));
		assertEquals(6, beanClass.getValue("succeeded", fact));
	}
}
