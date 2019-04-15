package sat.metaheuristics;
import sat.util.Randomizer;

public class Placebo extends GeneticAlgorithm {
	public Placebo(int populationSize, int numberOfCrossovers, int maximalNumberOfIterations) {
		super(populationSize, 0, 0, numberOfCrossovers, maximalNumberOfIterations);
	}
	
	@Override
	protected void iteration(int i) {
		this.currentIteration = i;
		// Randomly mutate the current population.
		GAIndividual[] forMutation = new GAIndividual[this.numberOfCrossovers];
		for (int j = 0; j < this.numberOfCrossovers; j++) {
			int k = Randomizer.getInt(forMutation.length);
			forMutation[j] = this.population[k].clone();
		}
		for (GAIndividual individual: forMutation) {
			if (Randomizer.getDouble() < Randomizer.getDouble()) {
				individual.mutate(Randomizer.getDouble());
			}
		}
		// Apply heuristic.
		this.applyHeuristic(forMutation);
		this.evaluate(forMutation);
		
		// Randomly choose the individuals to get into the new population.
		GAIndividual[] newPopulation = new GAIndividual[this.populationSize];
		for (int j = 0; j < this.populationSize; j++) {
			int k = Randomizer.getInt(this.populationSize + this.numberOfCrossovers);
			if (k < this.populationSize) {
				newPopulation[j] = this.population[k].clone();
			} else {
				newPopulation[j] = forMutation[k - this.populationSize].clone();
			}
		}
	}
	
	@Override
	public String describe() {
		// Describe the metaheuristic by reporting its name and parameter settings.
		String formatString = "Placebo[Number of individuals = %d, Number of crossovers = %d, MNI = %d]";
		return String.format(formatString, populationSize, numberOfCrossovers, maximalNumberOfIterations);
	}
}
