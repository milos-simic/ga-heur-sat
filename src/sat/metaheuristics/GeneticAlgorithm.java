package sat.metaheuristics;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.Comparator;
import java.util.List;

import java.util.stream.IntStream;

import javafx.util.Pair;
import sat.Formula;
import sat.heuristics.Heuristic;
import sat.util.DiscreteDistribution;
import sat.util.Randomizer;



public class GeneticAlgorithm{
	protected Formula formula;
	protected int populationSize;
	protected double individualMutationProbability;
	protected double geneMutationProbability;
	protected int numberOfCrossovers;
	protected int maximalNumberOfIterations;
	
	protected int numberOfPerformedIterations;
	
	protected GAIndividual[] population;
	protected List<Pair<GAIndividual, GAIndividual>> parents;
	protected GAIndividual[] children;
	
	protected double bestFoundValue;
	protected int[] bestFoundSolution;
	protected int firstIterationOfTheBestFound;
	protected int currentIteration;
	protected long startTime;
	protected long endTime;
	protected long timeOfTheBestFoundSolutuon;
	
	protected Heuristic heuristic = null;

	public GeneticAlgorithm(
			int populationSize,
			double individualMutationProbability,
			double geneMutationProbability,
			int numberOfCrossovers,
			int maximalNumberOfIterations,
			Heuristic heuristic) {
		this.populationSize = populationSize;
		this.individualMutationProbability = individualMutationProbability;
		this.geneMutationProbability = geneMutationProbability;
		this.numberOfCrossovers = numberOfCrossovers;
		this.maximalNumberOfIterations = maximalNumberOfIterations;
		this.heuristic = heuristic;
	}
	
	public GeneticAlgorithm(
			int populationSize,
			double individualMutationProbability,
			double geneMutationProbability,
			int numberOfCrossovers,
			int maximalNumberOfIterations) {
		
		this(populationSize, 
			 individualMutationProbability, 
			 geneMutationProbability, 
			 numberOfCrossovers, 
			 maximalNumberOfIterations, 
			 null);
	}
	
	public void solve(Formula formula) {
		this.solve(formula, false);
	}
	
	public void solve(Formula formula, boolean verbose) {
		// Try to solve the formula. If verbose == true, print data about iterations.
		this.formula = formula;
		// Clean the variables.
		this.prepare();
		// Remember the start time.
		this.startTime = System.nanoTime();
		// Initialize, improve, and evaluate the initial population.
		this.initializePopulation();
		this.evaluate(this.population);
		this.applyHeuristic(this.population);
		this.evaluate(this.population);
		this.numberOfPerformedIterations = 0;
		// Run until the maximal number of iterations gets performed
		// or the formula is satisfied.
		for (int i = 1; i <= this.maximalNumberOfIterations && !this.formulaIsSatisfied(); i++) {
			this.iteration(i);
			if (verbose) {
				System.out.println("Iteration " + i + ": " + this.bestFoundValue);
			}
			this.numberOfPerformedIterations = i;
		}
		this.endTime = System.nanoTime();
	}
	
	protected boolean formulaIsSatisfied() {
		return this.formula.isSatisfiedBy(this.bestFoundSolution);
	}

	protected void iteration(int i) {
		this.currentIteration = i;
		this.selectParents();
		this.performCrossovers();
		this.mutateChildren();
		this.applyHeuristic(this.children);
		this.evaluate(this.children);
		this.createNewPopulation();
	}
	
	protected void prepare() {
		// Clean the variables used throughout execution to make sure
		// that the run about to start is independent from the previous one.
		this.children = new GAIndividual[this.numberOfCrossovers];
		this.population = new GAIndividual[this.populationSize];
		this.bestFoundValue = -1;
		this.firstIterationOfTheBestFound = -1;
		this.bestFoundSolution = null;
		this.currentIteration = 0;
		this.startTime = System.nanoTime();
		this.endTime = System.nanoTime();

	}

	protected void initializePopulation() {
		for(int i=0; i < this.population.length; i++) {
			this.population[i] = new GAIndividual(this.formula);
			this.population[i].initialize();
		}
	}
	
	protected void evaluate(GAIndividual individual) {
		individual.evaluate();
		double value = individual.getValue();
		if (value > this.bestFoundValue) {
			// If a solution better than the currently best found has been discovered
			// remember it and its details (value, iteration number, time).
			this.bestFoundValue = value;
			this.bestFoundSolution = individual.getSolution().clone(); //copy()
			this.firstIterationOfTheBestFound = this.currentIteration;
			this.timeOfTheBestFoundSolutuon = System.nanoTime();
		}
	 }
	
	protected void evaluate(GAIndividual individuals[]) {
		for (GAIndividual individual : individuals) {
			this.evaluate(individual);
			if (this.formulaIsSatisfied()) {
				break;
			}
		}
	}
	

	protected void createNewPopulation() {
		// Combine the population of children with some individuals
		// from the generation of their parents.
		
		int elitism = this.populationSize - this.numberOfCrossovers;
		
		// Find the best individuals in the parent generation
		GAIndividual[] old = Arrays.stream(this.population).sorted(new Comparator<GAIndividual>() {
			public int compare(GAIndividual o1, GAIndividual o2) {
				if (o1.getValue() < o2.getValue()) {
					return +1;
				} else if (o1.getValue() > o2.getValue()) {
					return -1;
				}
				return 0;
			}
		}).limit(elitism).toArray(GAIndividual[]::new);
		// Store them into the population array.
		for (int i = 0; i < old.length; i++) {
			this.population[i] = old[i];
		}
		// Fill the rest of the array with children.
		for (int i = 0; i < this.children.length; i++) {
			this.population[old.length + i] = this.children[i];
		}
		
	}

	protected void applyHeuristic(GAIndividual[] individuals) {
		// Apply the heuristic, provided that it is not null.
		if (this.heuristic == null) {
			return;
		}
		for (GAIndividual individual : individuals) {
			individual.applyHeuristic(this.heuristic);
		}
	}

	protected void mutateChildren() {
		// Randomly select individuals for mutation
		for(int i = 0; i < this.children.length; i++) {
			if (Randomizer.getDouble() < this.individualMutationProbability) {
				// Trigger gene mutation.
				this.children[i].mutate(this.geneMutationProbability);
			}
		}
		
	}

	protected void performCrossovers() {
		// Perform uniform crossovers on the pairs of parents.
		for (int i = 0; i < this.parents.size(); i++) {
			Pair<GAIndividual,GAIndividual> pair = this.parents.get(i);
			GAIndividual parent1 = pair.getKey();
			GAIndividual parent2 = pair.getValue();
			GAIndividual child = parent1.crossover(parent2);
			this.children[i] = child;
		}
		
	}

	protected void selectParents() {
		this.parents = new ArrayList<Pair<GAIndividual, GAIndividual>>(this.numberOfCrossovers);
		double[] values = new double[this.population.length];
		// Use the individuals' fitness values as weights 
		// to create a discrete distribution over them.
		for(int i = 0; i < this.population.length; i++) {
			values[i] = this.population[i].getValue();
		}
		
		Integer[] indices = IntStream.range(0, this.population.length).boxed().toArray(Integer[]::new);
		DiscreteDistribution<Integer> dist = new DiscreteDistribution<Integer>(indices,  values);
		
		for(int i = 0; i < this.numberOfCrossovers; i++) {
			// Randomly draw two individuals from the distribution.
			int j1 = dist.draw();
			int j2 = dist.draw();
			// Make sure that they are different.
			while (j2 == j1) {
				j2 = dist.draw();
			}
			// Pair them.
			GAIndividual parent1 = this.population[j1];
			GAIndividual parent2 = this.population[j2];
			parents.add(new Pair<GAIndividual, GAIndividual>(parent1, parent2));
		}
	}

	public void setHeuristic(Heuristic heuristic) {
		this.heuristic = heuristic;
	}


	public double getBestFoundValue() {
		return this.bestFoundValue;
	}
	
	public int[] getBestFoundSolution() {
		return this.bestFoundSolution;
	}
	
	public int getFirstIterationOfTheBestFound() {
		return this.firstIterationOfTheBestFound;
	}
	
	public int getNumberOfPerformedIterations() {
		return numberOfPerformedIterations;
	}

	@Override
	public String toString() {
		return this.describe() + "\n'---Subordinate heuristic: " + this.heuristic;
	}

	public String describe() {
		// Describe the metaheuristic by reporting its name and parameter settings.
		String formatString = "GA[Population size = %d, MNI = %d, NumberOfCrossovers = %d, Individual Mutation = %.2f, Gene Mutation = %.2f]";
		return String.format(formatString, populationSize, maximalNumberOfIterations, numberOfCrossovers, individualMutationProbability, geneMutationProbability);
	}
	
	public String report() {
		// Report the results alongside with other details.
		String format = "The best found solution: %s\nThe best found value: %.5f\nThe first iteration of BFS: %d\nTime of the best found solution (seconds): %.5f\nTotal number of iterations: %d\nDuration (seconds): %.5f";
		return String.format(format, 
				Arrays.toString(this.bestFoundSolution),
				this.bestFoundValue,
				this.firstIterationOfTheBestFound,
				(this.timeOfTheBestFoundSolutuon - this.startTime) / 1000000000.0,
				this.numberOfPerformedIterations,
				(this.endTime - this.startTime) / 1000000000.0
			);
	}
}
	
