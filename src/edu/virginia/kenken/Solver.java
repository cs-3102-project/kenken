package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Solver {
  private static final int ATTEMPTS_PER_MINUTE = 40000000;

  private final Problem problem;
  private ArrayList<ArrayList<Integer>> solution;
  private final int size;

  public Solver(Problem problem) {
    this.problem = problem;
    size = problem.getSize();
    solution = new ArrayList<ArrayList<Integer>>();
  }

  public void solveBruteForce() {
    if (solution.size() > 0) {
      System.out.println("The board has already been solved.");
      return;
    }

    ArrayList<ArrayList<Integer>> attempt = new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> template =
      new ArrayList<ArrayList<Integer>>();

    // Start with a legal, non-random board
    ArrayList<Integer> rowPermutation = new ArrayList<Integer>();
    ArrayList<Integer> colPermutation = new ArrayList<Integer>();
    for (int i = 0; i < size; ++i) {
      rowPermutation.add(i + 1);
      colPermutation.add(i + 1);
      attempt.add(new ArrayList<Integer>());
      template.add(new ArrayList<Integer>());
      for (int j = 0; j < size; ++j) {
        attempt.get(i).add((i + j) % size + 1);
        template.get(i).add((i + j) % size + 1);
      }
    }

    int factorial = 1;
    for (int i = 2; i <= size; ++i) {
      factorial *= i;
    }
    long expectedAttempts = (long) (factorial * factorial * 0.5);
    System.out.println("ETA: " + expectedAttempts / ATTEMPTS_PER_MINUTE
      + " minutes (" + expectedAttempts + " attempts)");
    long attempts = 0;
    while (!problem.checkGrid(attempt)) {
      attempts += 1;
      if (attempts % 1000000 == 0) {
        System.out.println("Brute force has made " + attempts / 1000000
          + " million attempts");
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
          attempt.get(i).set(
            j,
            template.get(colPermutation.get(i) - 1).get(
              rowPermutation.get(j) - 1));
        }
      }
    }

    solution = attempt;

    for (int i = 0; i < size; ++i) {
      System.out.println(solution.get(i));
    }
  }

  public void solveDepthFirstSearch() {
    // Declare data structures:
    // Hold the legal numbers for each cell (flattened first 2D array)
    ArrayList<ArrayList<Integer>> legalNumbers =
      new ArrayList<ArrayList<Integer>>();

    // Hold the relation between each cell and corresponding cage
    HashMap<Integer, Cage> cellsAndCages = problem.getCellsAndCages();

    // Holds marked cells (immutable)
    ArrayList<Boolean> markedCells = new ArrayList<Boolean>();

    // Initialize data structures:
    for (int i = 0; i < size * size; ++i) {
      legalNumbers.add(new ArrayList<Integer>(Arrays.asList(1, 2, 3, 4, 5, 6,
        7, 8, 9)));
    }

    // Mark all cells that are enclosed in UnitCages
    int toRemove;
    for (int i = 0; i < size * size; ++i) {
      if (cellsAndCages.get(i).getClass().getName().equals("UnitCage")) {

        // Mark the cell as marked
        markedCells.set(i, true);
        toRemove = cellsAndCages.get(i).getTotal();

        // Go through rows and columns
        for (int j = 0; j < size; ++j) {
          legalNumbers.get((i % size) + j).remove(toRemove);
          legalNumbers.get(i / size + j * size).remove(toRemove);
        }

        // Remove legal numbers for this cell, except for the actual number
        legalNumbers.get(i).clear();
        legalNumbers.get(i).add(toRemove);
      }
    }

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
