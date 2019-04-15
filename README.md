# ga-heur-sat
SAT solvers which guide WalkSAT, FLIP and SLS heuristics with Genetic Algorihm and its Placebo

## Arguments

The main class in Starter in the package run. Its arguments are presented in the following table:

| Switch    | Allowed values       | Meaning | Default value | Note |
|-----------|----------------------|---------|---------------|------|
| --Meta    | {GA, Placebo}        | GAa afdaheuristic component | Any value other than GA will use the placebo |
| --Heur    | {sls, walksat, flip} | The heuristic component | |
| --Npop    | [1, 2, ...]          | The size of the population | |
| --Ncross  | [1, ..., Npop]       | The number of parent pairs | Cannot be larger than Npop |
| --IndMut  | [0, 1]               | The probability to choose an individual for mutation | |
| --GeneMut | [0, 1]               | The probability to mutate a gene in an individual chosen for mutation | |
| --MNI     | [1, 2, ...]          | The maximal number of iterations | 
| --Nsteps  | [1, 2, ...]          | The number of steps in sls and walksat | Ignored if the heuristic is flip
| --Cb      | [1, infinity]        | The break parameter for sls | Used only when the heuristic is sls
| --Eps     | (0, infinity]        | The epsilon parameter for sls | Used only when the heuristic is sls
| --q       | [0, 1]               | The random-move probability for walksat | Used only when the heuristic is walksat
| --seed    | any integer          | The seed for random-number generation   | |
| --instance|                      | The path to a file with a Boolean formula in the DIMACS format.

## Example of usage

If `theJarFile` is the jar file with all the classes from this project, the software can be used like this:

`--Meta GA --Heur flip --Npop 66 --Ncross 34 --IndMut 0.31 --GeneMut 0.28 --MNI 107 --seed 1 --instance uf50-01.cnf`

## Output


