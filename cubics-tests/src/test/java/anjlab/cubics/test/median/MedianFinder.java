package anjlab.cubics.test.median;


public class MedianFinder {

	private static int opCount;
	private static int loopCount;

	private static int compCount;
	
	public static double torben(double m[]) {
		opCount = 0;
		loopCount = 0;
		compCount = 0;
		
		int n = m.length;
		int i, less, greater, equal;
		double min, max, guess, maxltguess, mingtguess;
		min = max = m[0];
		for (i = 1; i < n; i++) {
			if (m[i] < min)
				min = m[i];
			if (m[i] > max)
				max = m[i];
		}
		while (true) {
			loopCount++;
			guess = (min + max) / 2;
			less = 0;
			greater = 0;
			equal = 0;
			maxltguess = min;
			mingtguess = max;
			for (i = 0; i < n; i++) {
				compCount ++;
				if (m[i] < guess) {
					less++;
					compCount ++;
					if (m[i] > maxltguess)
						maxltguess = m[i];
				} else if (m[i] > guess) {
					compCount ++;
					greater++;
					compCount ++;
					if (m[i] < mingtguess)
						mingtguess = m[i];
				} else
					equal++;
			}
			if (less <= (n + 1) / 2 && greater <= (n + 1) / 2)
				break;
			else if (less > greater)
				max = maxltguess;
			else
				min = mingtguess;
		}
		if (less >= (n + 1) / 2)
			return maxltguess;
		else if (less + equal >= (n + 1) / 2)
			return guess;
		else
			return mingtguess;
	}

	public static int getOpCount() {
		return opCount;
	}

	public static int getLoopCount() {
		return loopCount;
	}
	
	public static int getCompCount() {
		return compCount;
	}
}
