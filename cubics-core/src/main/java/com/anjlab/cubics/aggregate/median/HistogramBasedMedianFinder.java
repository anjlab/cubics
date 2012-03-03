package com.anjlab.cubics.aggregate.median;

import java.util.Arrays;

/**
 * Histogram-based implementation of median search algorithm.
 * 
 * Based on N. Devillard's implementation of Torben Mogensen's algorithm 
 * ({@linkplain http://ndevilla.free.fr/median/median.pdf}).
 * 
 * @author Dmitry Gusev <dmitry.gusev@gmail.com>
 */
public class HistogramBasedMedianFinder {

	private final int K;	//	number of ranges to create in histogram
	private final double epsilon;
	
	public HistogramBasedMedianFinder() {
		this(15, 0.000000001d);
	}
	
	public HistogramBasedMedianFinder(int k, double epsilon) {
		this.K = k;
		this.epsilon = epsilon;
	}
	
	public double search(double[] m) {
		int n = m.length;

		int correction = n % 2 == 0 ? 1 : 0;

		double min, max;

		min = max = m[0];
		for (int i = 1; i < n; i++) {
			if (m[i] < min) min = m[i];
			if (m[i] > max) max = m[i];
		}

		long leftOutRange = 0;
		long rightOutRange = 0;

		Histogram histogram = new Histogram();
		
		while (true) {
			histogram.init(min, max);
			
			for (int i = 0; i < n; i++) {
				histogram.add(m[i]);
			}

			long countInRanges = histogram.totalCount - histogram.others;

			if (countInRanges == 1) {
				return histogram.recentlyAdded;
			}

			long incCount = 0;
			
			for (int i = 0; i < histogram.rangeCount; i++)  {
				long rangeCount = histogram.counts[i];
				
				incCount += rangeCount;
				
				long rightOutCurrentRange = (countInRanges + rightOutRange) - incCount;
			
				if (incCount + leftOutRange >= rightOutCurrentRange - correction) {
					//	Median is in this range. Repeat search in this range
					min = i == 0 ? histogram.start : histogram.ranges[i - 1];
					max = histogram.ranges[i];
			
					if (almostSame(min, max)) {
						return min;
					}
					
					leftOutRange += (incCount - rangeCount); 
					rightOutRange = rightOutCurrentRange;
					
					break;
				}
			}
		}
	}
	
	public boolean almostSame(double a, double b) {
		return Math.abs(a - b) < epsilon;
	}
	
	private class Histogram {
		public double start;
		public double end;
		private double step;
		public double[] ranges = new double[K + 1];
		public long[] counts = new long[K + 1];
		public double recentlyAdded;
		public long totalCount;
		public long others;
		public int rangeCount;
		public void init(double start, double end) {
			this.start = start;
			this.end = end;
			Arrays.fill(counts, 0);
			others = 0;
			totalCount = 0;
			if (end < start || almostSame(start, end)) {
				rangeCount = 0;
			} else {
				this.step = (end - start) / K;
				int idx = 0;
				for (double i = start; i < end; i += step) {
					double right = i + step;
					if (right > end) {
						right = end;
					}
					ranges[idx++] = right;
				}
				rangeCount = idx;
			}

		}
		public void add(double value) {
			totalCount++;

			if (value < start || value > end) {
				others++;
				return;
			}

			recentlyAdded = value;

			//	binary search
			
			int low = 0;
			int high = rangeCount - 1;

			while (low <= high) {
				int mid = (high + low) >>> 1;
				if (value <= ranges[mid]) {
					if (mid > 0 && value < ranges[mid - 1]) {
						high = mid - 1;	//	continue search
					} else {
						counts[mid]++;	//	we've found the range
						return;
					}
				} else if (value > ranges[mid]) {
					low = mid + 1;
				}
			}
		}
	}	
}
