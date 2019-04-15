package sat.metaheuristics;


import sat.Formula;
import sat.heuristics.Heuristic;
import sat.util.Randomizer;

public class GAIndividual implements Cloneable {
	private int[] solution;
	private Formula formula;;
	private double value;
	
	public GAIndividual(Formula formula) {
		this.formula = formula;
	}
	
	public GAIndividual(Formula formula, int[] solution) {
		this.formula = formula;
		this.solution = solution.clone();
	}
	
	public void initialize() {
		this.solution = this.formula.getRandomValuation();
	}

	public void evaluate() {
		this.value = formula.getPercentageOfSatisfied(solution);
		//this.value = formula.getNumberOfSatisfied(solution);
	}

	public int[] getSolution() {
		return solution.clone();
	}
	
	public void setSolution(int[] solution) {
		this.solution = solution;
	}

	public double getValue() {
		return value;
	}

	public GAIndividual crossover(GAIndividual other) {
		int[] newSolution = new int[this.formula.getNumberOfLetters()];
		int[] otherSolution = other.getSolution();
		for (int i = 0; i < this.formula.getNumberOfLetters(); i++) {
			if (Randomizer.getDouble() < 0.5) {
				newSolution[i] = this.solution[i];
			} else {
				newSolution[i] = otherSolution[i];
			}
		}
		GAIndividual child = new GAIndividual(formula, newSolution);
		return child;
		
	}

	public void mutate(double geneMutationProbability) {
		for (int i = 0; i < this.solution.length; i++) {
			if (Randomizer.getDouble() < geneMutationProbability) {
				this.solution[i] = 1 - this.solution[i];
			}
		}
		
	}

	public void applyHeuristic(Heuristic heuristic) {
		this.solution = heuristic.improve(this.solution, this.formula);
		
	}
	
	 public GAIndividual clone() {
		GAIndividual clone = new GAIndividual(this.formula, this.solution.clone());
		return clone;
		 
	 }

}
