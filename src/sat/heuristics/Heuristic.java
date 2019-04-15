package sat.heuristics;

import sat.Formula;

public abstract class Heuristic {
	public abstract int[] improve(int[] solution, Formula formula);
}
