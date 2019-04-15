package sat.util;

import java.util.List;
import java.util.Random;

public class Randomizer {
	// This class is responsible for raw random selections.
	private static Random generator;
	
	public static Random getGenerator() {
		return generator;
	}

	public static void initialize(int seed) {
		generator = new Random(seed);
	}
	
	public static double getDouble() {
		return generator.nextDouble();
	}
	
	public static int getInt(int upper) {
		return generator.nextInt(upper);
	}

	public static int select(List<Integer> list) {
		int i = getInt(list.size());
		return list.get(i);
	}
	

}
