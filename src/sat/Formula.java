package sat;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

import sat.util.Randomizer;

public class Formula {
	// This class represents a Boolean logical formula.
	private int numberOfLetters;
	private int numberOfClauses;
	private int[][] clauses;
	private Map<Integer, Set<Integer>> clauseMap;
	
	public Formula(int numberOfLetters, int numberOfClauses, int[][] clauses) {
		this.numberOfLetters = numberOfLetters;
		this.numberOfClauses = numberOfClauses;
		this.clauses = clauses;
		this.makeClauseMap();
	}
	
	private void makeClauseMap() {
		// Create a map pairing each literal with clauses in which it occurs.
		this.clauseMap  =  new HashMap<>(2*this.numberOfLetters);
		
		// Initialize the sets of clauses in which literals appear.
		for(int letter=1; letter <= this.numberOfLetters; letter++) {
			this.clauseMap.put(letter, new HashSet<>());
			this.clauseMap.put(-letter, new HashSet<>());
		}
		
		// Fill the sets with the clauses in which their literals appear.
		for (int i = 0; i < this.numberOfClauses; i++) {
			for (int j = 0; j < this.clauses[i].length; j++) {
				int literal = this.clauses[i][j];
				this.clauseMap.get(literal).add(i);
			}
		}
	}


	public int getNumberOfLetters() {
		return numberOfLetters;
	}
	public void setNumberOfLetters(int numberOfLetters) {
		this.numberOfLetters = numberOfLetters;
	}
	public int getNumberOfClauses() {
		return numberOfClauses;
	}
	public void setNumberOfClauses(int numberOfClauses) {
		this.numberOfClauses = numberOfClauses;
	}
	public int[][] getClauses() {
		return clauses;
	}
	public void setClauses(int[][] clauses) {
		this.clauses = clauses;
	}
	
	public int getNumberOfSatisfied(int[] solution) {
		int numberOfSatisfied = 0;
		for(int i = 0; i < clauses.length; i++) {
			boolean satisfied = false;

			for(int j = 0; j < clauses[i].length && !satisfied; j++) {
				// A clause is satisfied by a solution if it contains at least
				// one literal present in the solution.
				int literal = clauses[i][j];
				int position = Math.abs(literal) - 1;
				int value = literal > 0? 1: 0;
				
				if (solution[position] == value) {
					satisfied = true;
				}
			}
			if (satisfied) {
				numberOfSatisfied++;
			}
		}
		return numberOfSatisfied;
	}
	
	public int getNumberOfUnsatisfied(int[] solution) {
		int numberOfSatisfied = this.getNumberOfSatisfied(solution);
		return this.numberOfClauses - numberOfSatisfied;
	}
	
	
	public double getPercentageOfSatisfied(int[] solution) {
		int numberOfSatisfied = this.getNumberOfSatisfied(solution);
		double percentage = numberOfSatisfied / ((double)this.numberOfClauses);
		return percentage;
	}
	
	public int[] getRandomValuation() {
		int n = this.numberOfLetters;
		int[] solution = new int[n];
		for(int i = 0; i < n; i++) {
			// Randomly determine the polarity of letter  (i+1)
			if (Randomizer.getDouble() < 0.5) {
				solution[i] = 0;
			} else {
				solution[i] = 1;
			}
		}
		return solution;
	}


	public static Formula read(String formulaFilepath) throws IOException {
		int numberOfLetters = 0;
		int numberOfClauses = 0;
		int clauseCounter = 0;
		int clauseLength = 0;
		int clauses[][] = new int[][] {};
		Scanner scanner;
		// Open the file
		FileInputStream fstream = new FileInputStream(formulaFilepath);
		BufferedReader br = new BufferedReader(new InputStreamReader(fstream));
		
		// Read the file line by line
		String strLine;
		while ((strLine = br.readLine()) != null)   {
		  if (strLine.contains("clause length")) {
			  // Read the number of literals in a clause.
			  scanner = new Scanner(strLine.split("=")[1]);
			  clauseLength = scanner.nextInt();
			  continue;
		  }
		  else if (strLine.startsWith("c")) {
			  // Skip the comment.
			  continue;
		  } else if (strLine.startsWith("p")) {
			  // Read the numbers of the letters and clauses in the formula.
			  String values = strLine.split("cnf")[1];
			  scanner = new Scanner(values);
			  numberOfLetters = scanner.nextInt();
			  numberOfClauses = scanner.nextInt();
			  clauses = new int[numberOfClauses][clauseLength];
			  clauseCounter = 0;
			  continue;
		  } else if (strLine.startsWith("%") || strLine.startsWith("0")) {
			  // Skip the empty lines in the end.
			  continue;
		  }
		  
		  // Read the literals present in the current-line's clause.
		  scanner = new Scanner(strLine);
		  for(int i = 0; i < clauseLength; i++) {
			  int literal = scanner.nextInt();
			  clauses[clauseCounter][i] = literal;
		  }
		  clauseCounter++;
		  if (clauseCounter == numberOfClauses) {
			  break;
		  }
		
		}

		fstream.close();

		
		return new Formula(numberOfLetters, numberOfClauses, clauses);
	}


	public int calculateDamage(int[] solution, int letter) {
		// Determine the net decrease of the number of satisfied clauses
		// after negating the given letter.
		return this.calculateBreak(solution, letter) - this.calculateMake(solution, letter);
	}
	
	public int calculateGain(int[] solution, int letter) {
		// Determine the net increase in the numer of satisfied clauses after 
		// negating the given letter.
		return this.calculateMake(solution, letter) - this.calculateBreak(solution, letter);
	}


	public int calculateBreak(int[] solution, int letter) {
		// Determine the number of clauses satisfied by the given solution,
		// which become unsatisfied after negating the letter.
		
		int literal = solution[letter - 1] > 0? letter : (-letter);
		// Get the clauses containing the letter with a given value.
		// Note that they are all satisfied because of that.
		Set<Integer> possiblyBrokenClauses = this.clauseMap.get(literal);
		
		int numberOfBrokenClauses = 0;
		for (int i : possiblyBrokenClauses) {
			boolean broken = true;
			for (int clauseLitteral : this.clauses[i]) {
				// If the literal present in the clause is also present
				// in the solution, then the clause doesn't break.
				
				// Ignore the letter that was negated.
				if (clauseLitteral == literal) {
					continue;
				}
				// Check if another letter in the solution
				// keeps the clause satisfied. If so, it doesn't break.
				if (clauseLitteral > 0) {
					if (solution[clauseLitteral - 1] == 1) {
						broken = false;
					} 
				} else {
					if (solution[(-clauseLitteral) - 1] == 0) {
						broken = false;
					}
				}
				
				if (!broken) break;
			}
			if (broken) {
				numberOfBrokenClauses++;
			}

		}
		return numberOfBrokenClauses;
	}


	public int calculateMake(int[] solution, int letter) {
		// Determine the number of clauses not satisfied by the given solution,
		// which become satisfied after negating the letter.
		
		int literal = solution[letter - 1] > 0? letter : (-letter);
		// Get the clauses that would be made satisfied after the flip.
		Set<Integer> possiblyMadeClauses = this.clauseMap.get(-literal);
		
		int numberOfMadeClauses = 0;
		// Count only the clauses not satisfied by the solution.
		for (int i : possiblyMadeClauses) {
			boolean made = true;
			for (int clauseLiteral : this.clauses[i]) {
				// The clause is already satisfied if one of its literals
				// is present in the solution.
				// Note that there is no need to check if clauseLiteral == literal
				// since clauses under consideration contain -literal and the assumption is 
				//that the formula is well-formed, i.e. no clause contains complementary literals.
				if (clauseLiteral > 0) {
					if (solution[clauseLiteral - 1] == 1) {
						made = false;
					} 
				} else {
					if (solution[(-clauseLiteral) - 1] == 0) {
						made = false;
					}
				}
				
				if (!made) break;
			}
	
			if (made) {
				numberOfMadeClauses++;
			}
		}
		return numberOfMadeClauses;
	}

	public boolean isSatisfiedBy(int[] solution) {
		return this.getNumberOfSatisfied(solution) == this.numberOfClauses;
	}
	
	
	public List<Integer> getIndicesOfUnsatisfiedClauses(int[] solution) {
		List<Integer> unsatisfiedIndices = new ArrayList<>();
		for(int i = 0; i < this.clauses.length; i++) {
			int[] clause = this.clauses[i];
			boolean satisfied = false;
			for (int j = 0; j < clause.length && !satisfied; j++) {
				// A clause is satisfied if it contains at least one literal
				// present in the solution.
				int literal = clause[j];
				int letter = Math.abs(literal);
				if (solution[letter - 1] == (literal > 0? 1: 0)) {
					satisfied = true;
				}
			}
			if (!satisfied) {
				unsatisfiedIndices.add(i);
			}
		}
		return unsatisfiedIndices;
	}

	public static Formula getRandomFormula(int numberOfLetters, int numberOfClauses, int clauseLength) {
		int[][] clauses = new int[numberOfClauses][clauseLength];
		for (int i = 0; i < numberOfClauses; i++) {
			for (int j = 0; j < clauseLength; j++) {
				int polarity = 1;
				if (Randomizer.getDouble() < 0.5) {
					polarity = -1;
				}
				int letter = Randomizer.getInt(numberOfLetters) + 1;
				int literal = letter * polarity;
				int k = 0;
				for (k  = 0; k < j + 1; k++) {
					if (Math.abs(clauses[i][k]) == letter) {
						break;
					}
				}
				while (k < j + 1) {
					polarity = 1;
					if (Randomizer.getDouble() < 0.5) {
						polarity = -1;
					}
					letter = Randomizer.getInt(numberOfLetters) + 1;
					literal = letter * polarity;
					k = 0;
					for (k  = 0; k < j + 1; k++) {
						if (Math.abs(clauses[i][k]) == letter) {
							break;
						}
					}
				}
				
				clauses[i][j] = literal;
			}
		}
		return new Formula(numberOfLetters, numberOfClauses, clauses);
	}


	
	


	
	
	

}
