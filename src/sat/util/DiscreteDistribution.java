package sat.util;

import java.util.ArrayList;
import java.util.List;

import javafx.util.Pair;


public class DiscreteDistribution<T> {
	// This class models a discrete distribution 
	// over a set of elements of the type T, according to their weights.
	private List<Pair<T, Double>> weightedItems;
	private double totalSum;
	
	public DiscreteDistribution(List<Pair<T, Double>> weightedItems){
		this.weightedItems = weightedItems;
		// Calculate the total weight.
		this.totalSum = weightedItems.stream()
									 .mapToDouble(pair -> pair.getValue())
									 .sum();
		
	}
	
	public DiscreteDistribution(T items[], double weights[]) {
		// Convert the array of weights into a list of (item, weight) pairs.
		int n = items.length;
		this.weightedItems = new ArrayList<Pair<T, Double>>();
		for(int i = 0; i < n; i++) {
			T item = items[i];
			double weight = weights[i];
			Pair<T, Double> pair = new Pair<T, Double>(item, weight);
			this.weightedItems.add(pair);
		}
		this.totalSum = weightedItems.stream()
				 .mapToDouble(pair -> pair.getValue())
				 .sum();
	}
	
	public T draw() {
		// Draw an item from the distribution represented by the items' weights.
		double threshold = Randomizer.getDouble() * this.totalSum;
		double partialSum = 0;
		for (int i = 0; i < this.weightedItems.size(); i++) {
			partialSum += this.weightedItems.get(i).getValue();
			if (partialSum > threshold) {
				return weightedItems.get(i).getKey();
			}
		}
		return this.weightedItems.get(weightedItems.size() - 1).getKey();
	}
}
