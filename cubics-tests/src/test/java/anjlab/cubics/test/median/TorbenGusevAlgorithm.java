package anjlab.cubics.test.median;

import java.util.Arrays;

/**
 * Implementation of Median Search algorithm.
 * 
 * Based on N. Devillard's implementation of Torben Mogensen's algorithm 
 * ({@linkplain http://ndevilla.free.fr/median/median.pdf}).
 * 
 * @author Dmitry Gusev <dmitry.gusev@gmail.com>
 * 
 */
public class TorbenGusevAlgorithm {

	int opCount = 0;
	int loopCount = 0;
	int compCount = 0;
	public int N = 15;
	public double epsilon = 0.000000001;
	
	public TorbenGusevAlgorithm() {
		N = 15;
		epsilon = 0.000000001;
	}
	
	public TorbenGusevAlgorithm(int n) {
		this();
		this.N = n;
	}
	
	public int getLoopCount() {
		return loopCount;
	}
	
	public int getOpCount() {
		return opCount;
	}

	public int getCompCount() {
		return compCount;
	}
	
	private class Histogram {
		public double start;
		public double end;
		private double step;
		public double[] ranges = new double[N + 1];
		public long[] counts = new long[N + 1];
		public double latestInRange;
		public long totalCount;
		public long others;
		public int rangeCount;
		public void init(double start, double end) {
//			opCount++;
			this.start = start;
			this.end = end;
			Arrays.fill(counts, 0);
			others = 0;
			totalCount = 0;
			if (end < start || almostSame(start, end)) {
				rangeCount = 0;
			} else {
				this.step = (end - start) / N;
				int idx = 0;
				for (double i = start; i < end; i += step) {
//					opCount++;
					double right = i + step;
					if (right > end) {
						right = end;
					}
					ranges[idx++] = right;
				}
				rangeCount = idx;
			}

		}
		public boolean almostSame(double a, double b) {
			return Math.abs(a - b) < epsilon;
		}
		public void add(double value) {
			totalCount++;

			compCount++;
			if (value < start) {
				others++;
				return;
			} else if (value > end) {
				compCount++;
				others++;
				return;
			}
			compCount++;

			latestInRange = value;

			//	binary search
			
			int low = 0;
			int high = rangeCount - 1;

			compCount++;
			while (low <= high) {
//				opCount++;
				int mid = (high + low) >>> 1;
				compCount++;
				if (value <= ranges[mid]) {
					compCount += 2;
					if (mid > 0 && value < ranges[mid - 1]) {
						//	continue search
						high = mid - 1;
					} else {
						//	we've found the range: ranges[mid]
						counts[mid]++;
						return;
					}
				} else if (value > ranges[mid]) {
					compCount++;
					low = mid + 1;
				} else {
					compCount++;
				}
			}
		}
	}
	
	public double search(double[] m) {
		int n = m.length;

		int correction = n % 2 == 0 ? 1 : 0;

		double min, max;

		min = max = m[0];
		for (int i = 1; i < n; i++) {
//			opCount += 2;
			if (m[i] < min) min = m[i];
			if (m[i] > max) max = m[i];
		}

		long leftOutRange = 0;
		long rightOutRange = 0;

		Histogram histogram = new Histogram();
		
		while (true) {
			loopCount++;

			histogram.init(min, max);
			
			for (int i = 0; i < n; i++) {
				histogram.add(m[i]);
			}

			long countInRanges = histogram.totalCount - histogram.others;

			if (countInRanges == 1) {
				return histogram.latestInRange;
			}

			long incCount = 0;
			
			for (int i = 0; i < histogram.rangeCount; i++)  {
				long rangeCount = histogram.counts[i];
				
				incCount += rangeCount;
				
				long rightOutCurrentRange = (countInRanges + rightOutRange) - incCount;
			
//				opCount++;
				
				if (incCount + leftOutRange >= rightOutCurrentRange - correction) {
					//	Median is in this range. Repeat search in this range
					min = i == 0 ? histogram.start : histogram.ranges[i - 1];
					max = histogram.ranges[i];
			
					if (histogram.almostSame(min, max)) {
						return min;
					}
					
					leftOutRange += (incCount - rangeCount); 
					rightOutRange = rightOutCurrentRange;
					
					break;
				}
			}
		}
	}
	
}
