# ga-heur-sat
SAT solvers which guide WalkSAT, FLIP and SLS heuristics with Genetic Algorihm and its Placebo

## Arguments

The main class in Starter in the package run. Its arguments are presented in the following table:

| Switch    | Allowed values       | Default value | Meaning | Note |
|-----------|----------------------|---------|---------------|------|
| --Meta    | {GA, Placebo}        | GA      | The metaheuristic component | Any value other than GA will use the placebo |
| --Heur    | {sls, walksat, flip, none} | none | The heuristic component | |
| --Npop    | [1, 2, ...]          | 10  | The size of the population | |
| --Ncross  | [1, ..., Npop]       | 7   | The number of parent pairs | Cannot be larger than Npop |
| --IndMut  | [0, 1]               | 0.1 | The probability to choose an individual for mutation | |
| --GeneMut | [0, 1]               | 0.1 | The probability to mutate a gene in an individual chosen for mutation | |
| --MNI     | [1, 2, ...]          | 20  |The maximal number of iterations | 
| --Nsteps  | [1, 2, ...]          | 10  |The number of steps in sls and walksat | Ignored if the heuristic is flip
| --Cb      | [1, infinity]        | 1   |The break parameter for sls | Used only when the heuristic is sls
| --Eps     | (0, infinity]        | 0   |The epsilon parameter for sls | Used only when the heuristic is sls
| --q       | [0, 1]               | 0.5 |The random-move probability for walksat | Used only when the heuristic is walksat
| --seed    | any integer          | 0   |The seed for random-number generation   | If 0, the current time will be used as the seed |
| --instance|                      |     |The path to a file with a Boolean formula in the DIMACS format.

## Example of usage

If `theJarFile` is the jar file with all the classes from this project, the software can be used like this:

`--Meta GA --Heur flip --Npop 66 --Ncross 34 --IndMut 0.31 --GeneMut 0.28 --MNI 107 --seed 1 --instance uf50-01.cnf`

## Example of the output

    The best found solution: [1, 1, 0, 1, 1, 0, 0, 1, 1, 1, 0, 1, 1]
    The best found value: 1.00000
    The first iteration of BFS: 0
    Time of the best found solution (seconds): 0.44888
    Total number of iterations: 0
    Duration (seconds): 0.44891

The lines are in the format `description: value`. The first time and iteration of the BFS (the best found solution) refer to the timing and iteration when the algorithm found the solution with which it finished its execution and which was better than all the other solutions it constructed.

