package anjlab.cubics.test;

import org.junit.Assert;
import org.junit.Test;

import anjlab.cubics.aggregate.histogram.Range;

public class TestRange {

	@Test
	public void testRangeIncludes() {
		Range range = Range.parseRange("[4000;5000)");
		Assert.assertTrue(range.includes(4003));
		Assert.assertTrue(range.includes(4000));
		Assert.assertTrue(range.includes(4999));
		Assert.assertFalse(range.includes(5000));
		Assert.assertFalse(range.includes(5001));
		Assert.assertFalse(range.includes(3999));
	}
	
}
