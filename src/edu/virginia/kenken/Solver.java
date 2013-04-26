package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

public class Solver {
  private Problem problem;
  private ArrayList<ArrayList<Integer>> solution;
  private boolean isSolved;

  public Solver(Problem problem) {
    this.problem = problem;
    solution = new ArrayList<ArrayList<Integer>>();
  }

  public void solve() {
    if (isSolved) {
      System.out.println("The board has already been solved.");
      return;
    }

    // Use the same shuffling algorithm that was used to create legal grids
    do {
      for (int i = 0; i < problem.getSize(); ++i) {
        solution.add(new ArrayList<Integer>());
        for (int j = 0; j < problem.getSize(); ++j) {
          solution.get(i).add((i + j) % problem.getSize() + 1);
        }
      }

      // Shuffle rows

      Collections.shuffle(solution);

      // Transpose board matrix

      int tmp;
      for (int i = 0; i < problem.getSize(); ++i) {
        for (int j = 0; j < i; ++j) {
          tmp = solution.get(i).get(j);
          solution.get(i).set(j, solution.get(j).get(i));
          solution.get(j).set(i, tmp);
        }
      }

      // Shuffle rows (which were the columns before transposition) again

      Collections.shuffle(solution);
    } while (!problem.checkGrid(solution));

    for (int i = 0; i < problem.getSize(); ++i) {
      for (int j = 0; j < problem.getSize(); ++j) {
        System.out.print(solution);
      }
      System.out.print("\n");
    }
    isSolved = true;
  }

  public ArrayList<ArrayList<Integer>> getSolution() {
    return solution;
  }
}
