package sat.heuristics;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import sat.Formula;
import sat.util.Randomizer;

public class Flip extends Heuristic {

	@Override
	public int[] improve(int[] solution, Formula formula) {
		// Randomly select a permutation of [1, 2, ..., n]
		// where n is the number of propositional letters in the given formula.
		List<Integer> letters = IntStream.rangeClosed(1, solution.length)
				                 .boxed().
				                 collect(Collectors.toList());
		Collections.shuffle(letters, Randomizer.getGenerator());
		int[] solutionCopy = solution.clone();
		int improvement = 1;
		while (improvement > 0 && !formula.isSatisfiedBy(solutionCopy)) {
			// The improvement is the decrease in the number of unsatisfied clauses.
			improvement = 0;
			for (int letter : letters) {
				int unsatisfiedBefore = formula.getNumberOfUnsatisfied(solutionCopy);
				solutionCopy[letter - 1] = 1 - solutionCopy[letter - 1];
				int unsatisfiedAfter = formula.getNumberOfUnsatisfied(solutionCopy);
				int gain = unsatisfiedBefore - unsatisfiedAfter;
				if (gain >= 0) {
					// Keep the flip if it decreases the number of unsatisfied clauses. 
					improvement += gain;
				} else {
					// Discard it if it doesn't.
					solutionCopy[letter - 1] = 1 - solutionCopy[letter - 1];
				}
			}
		}
		return solutionCopy;
	}
	
	@Override
	public String toString() {
		return "Flip";
	}

}
