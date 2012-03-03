package com.anjlab.cubics.test;

import static com.anjlab.cubics.aggregate.histogram.Range.parseRange;
import static org.junit.Assert.assertEquals;

import org.junit.Assert;
import org.junit.Test;

import com.anjlab.cubics.aggregate.histogram.Histogram;
import com.anjlab.cubics.aggregate.histogram.Range;


public class TestHistogram {

	@Test
	public void testMergeWithNumericRanges() {
		
		/*
		 *      +----+----+----+----+
		 *      |  1 |  5 |  2 |  1 |
		 *      +----+----+----+----+
		 *      200  2200 4200 6200 8200
		 */
		Histogram histogramA = createHistogramA();

		/*
		 *      +----+----+----+----+----+----+
		 *      |  1 |  2 | 10 | 10 |  5 |  1 |
		 *      +----+----+----+----+----+----+
		 *      0    100  200  300  400  500  600
		 */
		Histogram histogramB = createHistogramB();

		histogramA.merge(histogramB);
		
		assertEquals(10, histogramA.getRangesCount());
		
		assertRange(histogramA, 0, "[0;     820)", 29);
		assertRange(histogramA, 1, "[820;  1640)", 0);
		assertRange(histogramA, 2, "[1640; 2460)", 1);
		assertRange(histogramA, 3, "[2460; 3280)", 2);
		assertRange(histogramA, 4, "[3280; 4100)", 2);
		assertRange(histogramA, 5, "[4100; 4920)", 1);
		assertRange(histogramA, 6, "[4920; 5740)", 1);
		assertRange(histogramA, 7, "[5740; 6560)", 1);
		assertRange(histogramA, 8, "[6560; 7380)", 1);
		assertRange(histogramA, 9, "[7380; 8200]", 0);
		
		/*
		 *      +----+----+----+----+----+----+----+----+----+----+
		 *      |  1 |  2 |  5 | 10 | 40 | 30 | 15 |  5 |  1 |  0 |
		 *      +----+----+----+----+----+----+----+----+----+----+
		 *      0    1000 2000 3000 4000 5000 6000 7000 8000 9000 10000
		 */
		Histogram histogramC = createHistogramC();

		histogramC.merge(histogramA);
		
		Assert.assertEquals(10, histogramC.getRangesCount());
		
		assertRange(histogramC, 0, "[0.0; 1000.0)", 30);
		assertRange(histogramC, 1, "[1000.0; 2000.0)", 2);
		assertRange(histogramC, 2, "[2000.0; 3000.0)", 7);
		assertRange(histogramC, 3, "[3000.0; 4000.0)", 12);
		assertRange(histogramC, 4, "[4000.0; 5000.0)", 41);
		assertRange(histogramC, 5, "[5000.0; 6000.0)", 32);
		assertRange(histogramC, 6, "[6000.0; 7000.0)", 17);
		assertRange(histogramC, 7, "[7000.0; 8000.0)", 5);
		assertRange(histogramC, 8, "[8000.0; 9000.0)", 1);
		assertRange(histogramC, 9, "[9000.0; 10000.0]", 0);

	}

	private Histogram createHistogramC() {
		Histogram histogramC = TestHelper.createHistogram(0, 1000, 10000);

		histogramC.add(0,     1);
		histogramC.add(1000,  2);
		histogramC.add(2000,  5);
		histogramC.add(3000, 10);
		histogramC.add(4000, 40);
		histogramC.add(5000, 30);
		histogramC.add(6000, 15);
		histogramC.add(7000,  5);
		histogramC.add(8000,  1);
		histogramC.add(9000,  0);
		
		return histogramC;
	}

	private Histogram createHistogramA() {
		Histogram histogramA = TestHelper.createHistogram(200, 2000, 8200);

		histogramA.add(200,  1);
		histogramA.add(2200, 5);
		histogramA.add(4200, 2);
		histogramA.add(6200, 1);
		
		return histogramA;
	}

	private Histogram createHistogramB() {
		Histogram histogramB = TestHelper.createHistogram(0, 100, 600);

		histogramB.add(0,    1);
		histogramB.add(100,  2);
		histogramB.add(200, 10);
		histogramB.add(300, 10);
		histogramB.add(400,  5);
		histogramB.add(500,  1);
		
		return histogramB;
	}

	private void assertRange(Histogram histogram, int rangeIndex, String expectedRangeBoundaries, long expectedRangeValue) {
		Range range = parseRange(expectedRangeBoundaries);
		assertEquals(range, histogram.getRange(rangeIndex));
		assertEquals(new Long(expectedRangeValue), histogram.getData().get(range));
	}

	@Test
	public void testLeftAndRightOthers() {
		Histogram histogram = TestHelper.createHistogram(20, 10, 80);

		for (int i = 0; i <= 100; i++) {
			histogram.add(i);
		}
		
		assertEquals(40, histogram.getOthers());
		assertEquals(20, histogram.getLeftOthers());
		assertEquals(20, histogram.getRightOthers());
	}
}
