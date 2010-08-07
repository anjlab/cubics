package anjlab.cubics.test.median;

import java.io.PrintStream;
import java.util.Arrays;
import java.util.Random;

import junit.framework.Assert;

import org.junit.Test;


public class TestMedianFinder {

	@Test
	public void testTorbenAlgorithm() {
		int count = 0;
		
		//	Note: Uncomment opCount and loopCount counters in HistogramBasedMedianFinder class to get statistics 
		boolean warmUp = true;
		//	Note: Change nRanges from 2 to 100 with warmUp = true to analyze results with the spreadsheet
		for (int nRanges = 2; nRanges <= 2; nRanges += (nRanges >= 30 ? 5 : 1)) {
			System.out.println("testTorbenAlgorithm (nRanges = " + nRanges + ")");
			if (warmUp) {
				System.out.println("Warming up JVM...");
			}
			System.out.println("n\tmedian\tloops\tops\ttime\tcomps");
			Random rand = new Random(42);
			//	Note: Change max n to 1 million to analyze results with the spreadsheet
			for (int n = 2; n <= 2; n += 1) {
				double m[] = new double[n];
				for (int i = 0; i < n; i++) {
					m[i] = rand.nextDouble();
				}
				
				long startTime = System.currentTimeMillis();
				double torbenMedian = MedianFinder.torben(m);
				long endTime = System.currentTimeMillis();
				
				Arrays.sort(m);
				
				double median = n % 2 == 0 ? m[n / 2 - 1] : m[n / 2];
				
				Assert.assertEquals(median, torbenMedian, 0.000000001);
				
				if (n > 500 && MedianFinder.getLoopCount() < 4) {
					count++;
				}
				
				if (!warmUp) {
					System.out.println(n + "\t" + torbenMedian + "\t" + MedianFinder.getLoopCount() + "\t" + MedianFinder.getOpCount() + "\t" + (endTime - startTime) + "\t" + MedianFinder.getCompCount());
				}
			}
			if (warmUp) {
				nRanges = 1;	//	next nRanges would be 2 again
				warmUp = false;
			}
		}
		
		System.out.println(count);
	}
	
	@Test
	public void testTorbenGusevAlgorithm() throws Exception {
		PrintStream out = System.out;// new PrintStream(new File("results.txt"));
		
		//	Note: Uncomment opCount and loopCount counters in HistogramBasedMedianFinder class to get statistics 
		boolean warmUp = false;
		//	Note: Change nRanges from 2 to 100 with warmUp = true to analyze results with the spreadsheet
		for (int nRanges = 1000; nRanges <= 1000; nRanges += (nRanges >= 30 ? 5 : 1)) {
			out.println("testTorbenGusevAlgorithm (nRanges = " + nRanges + ")");
			if (warmUp) {
				System.out.println("Warming up JVM...");
			} else {
				System.out.println("nRanges = " + nRanges);
			}
			out.println("n\tmedian\tloops\tops\ttime\tcomps");
			Random rand = new Random(42);
			//	Note: Change max n to 1 million to analyze results with the spreadsheet
			for (int n = 2; n <= 2; n += 1) {
				double m[] = new double[n];
				for (int i = 0; i < n; i++) {
					m[i] = rand.nextDouble();
				}
				
				TorbenGusevAlgorithm algorithm = new TorbenGusevAlgorithm(nRanges);
				
				long startTime = System.currentTimeMillis();
				double torbenGusevMedian = algorithm.search(m);
				long endTime = System.currentTimeMillis();
				
				Arrays.sort(m);
				
				double median = n % 2 == 0 ? m[n / 2 - 1] : m[n / 2];
				
				Assert.assertEquals(median, torbenGusevMedian, algorithm.epsilon);

				if (!warmUp) {
					out.println(n + "\t" + torbenGusevMedian + "\t" + algorithm.getLoopCount() + "\t" + algorithm.getOpCount() + "\t" + (endTime - startTime) + "\t" + algorithm.getCompCount());
				}
			}
			if (warmUp) {
				nRanges = nRanges - 1;	//	next nRanges would be 2 again
				warmUp = false;
			}
		}
		
		out.close();
		
		System.out.println("Done");
	}
}
