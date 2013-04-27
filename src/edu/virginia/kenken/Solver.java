package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

public class Solver {
  private static final int ATTEMPTS_PER_MINUTE = 40000000;

  private final Problem problem;
  private ArrayList<ArrayList<Integer>> solution;
  private final int size;

  // DFS variables
  private ArrayList<Boolean> markedCells;
  private ArrayList<ArrayList<Integer>> possibleNumbers;

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
    solution.clear();
    for (int i = 0; i < size; ++i) {
      solution.add(new ArrayList<Integer>(Collections.nCopies(size, 0)));
    }

    // Hold the legal numbers for each cell (flattened first 2D array)
    possibleNumbers = new ArrayList<ArrayList<Integer>>();

    // Hold the relation between each cell and corresponding cage
    ArrayList<Cage> cellCages = problem.getCellCages();
    ArrayList<Cage> cages = problem.getCages();

    // Holds marked cells (immutable)
    markedCells =
      new ArrayList<Boolean>(Collections.nCopies(size * size, false));

    // Initialize data structures:
    for (int i = 0; i < size * size; ++i) {
      possibleNumbers.add(new ArrayList<Integer>());
      for (int j = 1; j <= size; ++j) {
        possibleNumbers.get(i).add(j);
      }
    }

    // Mark all cells that are enclosed in UnitCages
    for (Cage c : cages) {
      if (c.getCells().size() == 1) {
        markCell(c.getCells().get(0), c.getTotal());
      }
    }

    solution =
      depthFirst(cellCages, cages, possibleNumbers, markedCells, solution, 0);

    for (int i = 0; i < size; ++i) {
      System.out.println(solution.get(i));
    }
  }

  private ArrayList<ArrayList<Integer>> depthFirst(
    ArrayList<Cage> cellsAndCages, ArrayList<Cage> cages,
    ArrayList<ArrayList<Integer>> legalNumbers, ArrayList<Boolean> markedCells,
    ArrayList<ArrayList<Integer>> currSolution, int cellToProcess) {
    // Make a new copy of every element (the copy constructor creates a shallow
    // copy
    ArrayList<Boolean> newMarkedCells = new ArrayList<Boolean>();
    ArrayList<ArrayList<Integer>> newLegalNumbers =
      new ArrayList<ArrayList<Integer>>();
    ArrayList<ArrayList<Integer>> newSolution =
      new ArrayList<ArrayList<Integer>>();

    for (int i = 0; i < size * size; ++i) {
      newMarkedCells.add(markedCells.get(i));
      newLegalNumbers.add(legalNumbers.get(i));
    }

    for (int i = 0; i < size; ++i) {
      newSolution.add(new ArrayList<Integer>());
      for (int j = 0; j < size; ++j) {
        newSolution.get(i).add(currSolution.get(i).get(j));
      }
    }

    if (cellToProcess == size * size) {
      return currSolution;
    }
    if (newMarkedCells.get(cellToProcess) == true) {
      return depthFirst(cellsAndCages, cages, newLegalNumbers, newMarkedCells,
        newSolution, cellToProcess + 1);
    }
    for (Integer i : newLegalNumbers.get(cellToProcess)) {

      // See if this will create any conflict (cage)
      // If it does not satisfy the cage, then move to next iteration
      newSolution.get(cellToProcess / size).set(cellToProcess % size, i);
      if (cellsAndCages.get(cellToProcess).isFilled(newSolution)
        && !cellsAndCages.get(cellToProcess).isSatisfied(newSolution)) {
        // If this is the last available symbol and it fails, then try another
        // branch
        if (newLegalNumbers.get(cellToProcess).size() == 1) {
          return null;
        }
        continue;
      }

      // remove legal numbering from row and columns

      // Go through rows and columns
      for (int j = 0; j < size; ++j) {
        newLegalNumbers.get(cellToProcess - (cellToProcess % size) + j).remove(
          i);
        newLegalNumbers.get(cellToProcess / size * j).remove(i);
      }

      // Remove legal numbers for this cell, except for the actual number
      newLegalNumbers.get(cellToProcess).clear();
      newLegalNumbers.get(cellToProcess).add(i);
      newMarkedCells.set(cellToProcess, true);

      if (cellsAndCages.get(cellToProcess).isFilled(newSolution)
        && cellsAndCages.get(cellToProcess).isSatisfied(newSolution)) {
        break;
      }
      if (depthFirst(cellsAndCages, cages, newLegalNumbers, newMarkedCells,
        newSolution, cellToProcess + 1) == null) {
        continue;
      }
    }
    return newSolution;
  }

  public ArrayList<ArrayList<Integer>> getSolution() {
    return solution;
  }

  private void markCell(int cellID, int value) {
    // Mark the cell as marked
    System.out.println(cellID);
    markedCells.set(cellID, true);

    solution.get(cellID / size).set(cellID % size, value);

    // Go through rows and columns
    for (int j = 0; j < size; ++j) {
      possibleNumbers.get(cellID - (cellID % size) + j).remove(value);
      possibleNumbers.get(cellID / size * j).remove(value);
    }

    // Remove legal numbers for this cell, except for the actual number
    possibleNumbers.get(cellID).clear();
    possibleNumbers.get(cellID).add(value);
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
