package run;


import sat.Formula;
import sat.heuristics.Flip;
import sat.heuristics.Heuristic;
import sat.heuristics.StochasticLocalSearch;
import sat.heuristics.WalkSAT;
import sat.metaheuristics.GeneticAlgorithm;
import sat.metaheuristics.Placebo;
import sat.util.Randomizer;

public class Starter {

	public static void main(String[] args) {
		// The default values for the parameters.
		int populationSize = 10;
		int numberOfCrossovers = 7;
		double individualMutationProbability = 0.1;
		double geneMutationProbability = 0.1;
		int maximalNumberOfIterations = 20;
		String chosenMetaheuristic = "GA";
		String chosenHeuristic = "none";
		int numberOfSteps = 10;
		double Cb = 1;
		double epsilon = 0;
		double q = 0.5;
		int seed = 0;
		String formulaFilepath = "";
		
		// Overriding the default values with those 
		// supplied by the command line.
		// The command-line arguments should be specified following this format:
		// --paramCode paramValue
		for (int i = 0; i < args.length; i+=2) {
			String arg = args[i];
			String value = args[i+1];
			switch (arg) {
			case "--Npop":
				// The number of individuals in the population.
				populationSize = Integer.parseInt(value);
				break;
			case "--Ncross":
				// The number of crossovers (parent pairs).
				numberOfCrossovers = Integer.parseInt(value);
				break;
			case "--IndMut":
				// The probability of mutating an individual.
				individualMutationProbability = Double.parseDouble(value);
				break;
			case "--GeneMut":
				// The probability of mutating a gene.
				geneMutationProbability = Double.parseDouble(value);
				break;
			case "--MNI":
				maximalNumberOfIterations = Integer.parseInt(value);
				break;
			case "--Meta":
				// The metaheuristic to use (GA, Placebo).
				chosenMetaheuristic = value;
				break;
			case "--Heur":
				// The heuristic to use (sls, walksat, or flip)
				chosenHeuristic = value;
				break;
			case "--Nsteps":
				// The number of steps in sls and walksat.
				numberOfSteps = Integer.parseInt(value);
				break;
			case "--Cb":
				// A coefficient of sls.
				Cb = Double.parseDouble(value);
				break;
			case "--Eps":
				// A coefficient of sls.
				epsilon = Double.parseDouble(value);
				break;
			case "--q":
				// A coefficient of walksat.
				q = Double.parseDouble(value);
				break;
			case "--seed":
				seed = Integer.parseInt(value);
				break;
			case "--instance":
				// The path of the file containing the formula
				// in the DIMACS format.
				formulaFilepath = value;
				break;
			default:
				System.out.println(arg + " is an unknown argument and will be ignored.");
				break;
			}
		}
		
		// Initialize the random-number generator.
		if (seed == 0) {
			seed = (int) (Math.random() * Integer.MAX_VALUE);
		}
		Randomizer.initialize(seed);
		
		// Instantiate the metaheuristic (M) and heuristic (H).
		GeneticAlgorithm alg;
		Heuristic heuristic;
		if (chosenMetaheuristic.equals("GA")) {
			alg = new GeneticAlgorithm(
					populationSize,
					individualMutationProbability,
					geneMutationProbability,
					numberOfCrossovers,
					maximalNumberOfIterations);
		} else {
			alg = new Placebo(
					populationSize,
					numberOfCrossovers,
					maximalNumberOfIterations);
		}
		
		if (chosenHeuristic.equals("walksat")) {
			heuristic = new WalkSAT(numberOfSteps, q);
		} else if (chosenHeuristic.equals("sls")) {
			heuristic = new StochasticLocalSearch(numberOfSteps, Cb, epsilon);
		} else  if (chosenHeuristic.equals("flip")) {
			heuristic = new Flip();
		} else{
			heuristic = null;
		}

		alg.setHeuristic(heuristic);
		
		try {
			// Run the M[H] algorithm on the given formula.
			Formula formula = Formula.read(formulaFilepath);
			
			alg.solve(formula);
			
			System.out.println(alg.report());

		} catch (Exception e) {
			e.printStackTrace(System.out);
		}
		
	}

}
