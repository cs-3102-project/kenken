package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class BruteForceSolver extends Solver {
  private HashMap<Integer, HashSet<Integer>> solution;
  private final int size;
  private long statesChecked;

  public BruteForceSolver(GUI gui, Problem problem) {
    super(gui, problem);

    size = problem.getSize();
    solution = new HashMap<Integer, HashSet<Integer>>();
    statesChecked = -1;
  }

  public void solve() {
    if (solution.size() > 0) {
      System.out.println("The board has already been solved.");
      return;
    }

    HashMap<Integer, HashSet<Integer>> attempt =
      new HashMap<Integer, HashSet<Integer>>();
    HashMap<Integer, HashSet<Integer>> template =
      new HashMap<Integer, HashSet<Integer>>();

    // Start with a legal, non-random board
    ArrayList<Integer> rowPermutation = new ArrayList<Integer>();
    ArrayList<Integer> colPermutation = new ArrayList<Integer>();
    for (int i = 0; i < size; ++i) {
      rowPermutation.add(i + 1);
      colPermutation.add(i + 1);
      for (int j = 0; j < size; ++j) {
        attempt.get(i * size + j).add((i + j) % size + 1);
        template.get(i * size + j).add((i + j) % size + 1);
      }
    }
    statesChecked = 1;
    while (!getProblem().checkGrid(attempt)) {
      statesChecked += 1;

      if (statesChecked % 65536 == 0) {
        getGUI().showProgress(attempt);
      }

      // Get next permutations of rows and columns
      if (!nextPermutation(rowPermutation)) {
        rowPermutation = new ArrayList<Integer>();
        for (int k = 0; k < size; ++k) {
          rowPermutation.add(k + 1);
        }
        nextPermutation(colPermutation);
      }

      // Reassign attempt grid values as specified by permutations
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          attempt.put(
            i * size + j,
            template.get((colPermutation.get(i) - 1) * size
              + rowPermutation.get(j) - 1));
        }
      }
    }

    solution = attempt;
    getGUI().showProgress(solution);
  }

  // public long getStatesChecked() {
  // return statesChecked;
  // }

  // public HashMap<Integer, HashSet<Integer>> getSolution() {
  // return solution;
  // }

  // public void printSolution() {
  // for (int i = 0; i < size; ++i) {
  // System.out.println(solution.get(i));
  // }
  // }

  /**
   * @param p
   *          Input list
   * @return Whether input is not the last permutation
   */
  private static boolean nextPermutation(ArrayList<Integer> p) {
    int a = p.size() - 2;
    while (a >= 0 && p.get(a) >= p.get(a + 1)) {
      a--;
    }
    if (a < 0) {
      return false;
    }

    int b = p.size() - 1;
    while (p.get(b) <= p.get(a)) {
      b--;
    }

    int t = p.get(a);
    p.set(a, p.get(b));
    p.set(b, t);

    for (int i = a + 1, j = p.size() - 1; i < j; ++i, --j) {
      t = p.get(i);
      p.set(i, p.get(j));
      p.set(j, t);
    }
    return true;
  }
}
