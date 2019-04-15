package sat.heuristics;

import sat.Formula;
import sat.util.Randomizer;

public class WalkSAT extends Heuristic {
	
	private int maximalNumberOfSteps;
	private double randomMoveProbability;
	
	public WalkSAT(int maximalNumberOfSteps, double randomMoveProbability) {
		this.maximalNumberOfSteps = maximalNumberOfSteps;
		this.randomMoveProbability = randomMoveProbability;
	}

	@Override
	public int[] improve(int[] solution, Formula formula) {
		int [] solutionCopy = solution.clone();
		int[][] clauses = formula.getClauses();
		for (int s = 0; s < this.maximalNumberOfSteps; s++) {
			if (formula.isSatisfiedBy(solutionCopy)) {
				break;
			}
			// Randomly select an unsatisfied clause.
			int i = Randomizer.select(formula.getIndicesOfUnsatisfiedClauses(solution));
			int positionToFlip;
			if (Randomizer.getDouble() < this.randomMoveProbability) {
				// Randomly flip one of its letters.
				int j = Randomizer.getInt(clauses[i].length);
				positionToFlip = Math.abs(clauses[i][j]) - 1;
			} else {
				// Negate the letter which yields the maximal gain in the number
				// of satisfied clauses.
				int maxGain = -clauses.length - 1;
				positionToFlip = -1;
				for (int j = 0; j < clauses[i].length; j++) {
					int letter = Math.abs(clauses[i][j]);
					int gain = formula.calculateGain(solutionCopy, letter);
					if (gain > maxGain) {
						maxGain = gain;
						positionToFlip = letter - 1;
					}
				}
				solutionCopy[positionToFlip] = 1 - solutionCopy[positionToFlip];
			}
		}
		return solutionCopy;
	}
	
	@Override
	public String toString() {
		return String.format("WalkSAT[Steps = %d, Random Move Prob = %.2f]", this.maximalNumberOfSteps, this.randomMoveProbability);
	}

}
