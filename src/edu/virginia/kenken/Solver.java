package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

public class Solver {
  private static final int ATTEMPTS_PER_MINUTE = 40000000;

  private final Problem problem;
  private final ArrayList<ArrayList<Integer>> solution;
  private boolean isSolved;

  public Solver(Problem problem) {
    this.problem = problem;
    solution = new ArrayList<ArrayList<Integer>>();
  }

  public void solveBruteForce() {
    if (isSolved) {
      System.out.println("The board has already been solved.");
      return;
    }

    // Start with a legal, non-random board
    for (int i = 0; i < problem.getSize(); ++i) {
      solution.add(new ArrayList<Integer>());
      for (int j = 0; j < problem.getSize(); ++j) {
        solution.get(i).add((i + j) % problem.getSize() + 1);
      }
    }

    int factorial = 1;
    for (int i = 2; i <= problem.getSize(); ++i) {
      factorial *= i;
    }
    long expectedAttempts = (long) (factorial * factorial * 0.5);
    System.out.println("ETA: " + expectedAttempts / ATTEMPTS_PER_MINUTE
      + " minutes (" + expectedAttempts + " attempts)");
    long attempts = 0;
    while (!problem.checkGrid(solution)) {
      attempts += 1;
      if (attempts % 1000000 == 0) {
        System.out.println("Brute force has made " + attempts / 1000000
          + " million attempts");
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
    }

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
