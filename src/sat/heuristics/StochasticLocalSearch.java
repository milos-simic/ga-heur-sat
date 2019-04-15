package sat.heuristics;

import sat.Formula;
import sat.util.DiscreteDistribution;
import sat.util.Randomizer;

public class StochasticLocalSearch extends Heuristic {
	private double Cb;
	private double epsilon;
	private int maximalNumberOfSteps;
	
	public  StochasticLocalSearch(int maximalNumberOfSteps, double Cb, double epsilon) {
		this.Cb = Cb;
		this.epsilon = epsilon;
		this.maximalNumberOfSteps = maximalNumberOfSteps;
	}

	@Override
	public int[] improve(int[] solution, Formula formula) {
		int[] solutionCopy = solution.clone();
		int[][] clauses = formula.getClauses();
		for (int s = 0; s < this.maximalNumberOfSteps; s++) {
			if (formula.isSatisfiedBy(solutionCopy)) {
				break;
			}
			// Randomly select an unsatisfied clause.
			int i = Randomizer.select(formula.getIndicesOfUnsatisfiedClauses(solution));
			
			// Calculate the weights.
			double weights[] = new double[clauses[i].length];
			for (int j = 0; j < clauses[i].length; j++) {
				int letter = Math.abs(clauses[i][j]);
				int breakValue = formula.calculateBreak(solutionCopy, letter);
				weights[j] = Math.pow(this.epsilon + breakValue, -this.Cb);
			}
			
			// Using the weights, randomly draw a letter to negate.
			Integer[] L = new Integer[clauses[i].length];
			for (int j=0; j< clauses[i].length; j++) L[j] = clauses[i][j];
			DiscreteDistribution<Integer> dist = new DiscreteDistribution<>(L, weights);
			int literalToFlip = dist.draw();
			int letterToFlip = Math.abs(literalToFlip);
			solutionCopy[letterToFlip - 1] = 1 - solutionCopy[letterToFlip - 1];
			
		}
		return solutionCopy;
	}
	
	@Override
	public String toString() {
		String format = "SLS[Steps = %d, Cb = %.2f, epsilon = %.2f]";
		return String.format(format, this.maximalNumberOfSteps, this.Cb, this.epsilon);
	}

}
