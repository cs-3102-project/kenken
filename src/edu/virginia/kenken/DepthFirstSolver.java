package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DepthFirstSolver {
  private final Problem problem;
  private final int size;
  private final ArrayList<Cage> cages;
  private boolean solutionFound;
  private HashMap<Integer, HashSet<Integer>> solution;
  private int statesChecked;

  public DepthFirstSolver(Problem problem) {
    this.problem = problem;
    size = problem.getSize();
    cages = problem.getCages();
    solutionFound = false;
    statesChecked = 0;
  }

  public void solve() {
    // Initialize grid of guesses to all empty
    HashMap<Integer, HashSet<Integer>> root =
      new HashMap<Integer, HashSet<Integer>>();
    for (int i = 0; i < size * size; ++i) {
      root.put(i, new HashSet<Integer>());
      for (int j = 1; j <= size; ++j) {
        root.get(i).add(j);
      }
    }

    // Get easy stuff done first - mark all UnitCages and recurse through
    // their peers, marking them if possible too
    for (Cage c : cages) {
      if (c.getCells().size() == 1) {
        markAndTrimPeers(c.getCells().get(0), c.getTotal(), root);
      }
    }

    // Seed DFS with the first undetermined cell
    // TODO Seed DFS with the cell that has highest information gain

    // Call the root instance of DFS on the seed cell
    DFS(0, firstSeedable(root), root);

    if (solution == null) {
      System.out.println("No solution found.");
    } else {
      problem.checkGrid(make2D(solution));
      printState(solution);
    }
  }

  /**
   * Recursively called DFS algorithm - should be called only on undetermined
   * cells.
   * 
   * @param cellID
   * @param state
   */
  private void DFS(int level, int cellID,
    HashMap<Integer, HashSet<Integer>> state) {
    // Check whether this is a solution
    if (solutionFound) {
      return;
    }

    // Loop through possible values for this cell
    int markedInCage;
    boolean cagesSatisfied;
    ArrayList<ArrayList<Integer>> matrix;
    HashMap<Integer, HashSet<Integer>> child;

    for (Integer v : state.get(cellID)) {
      // Quit if this branch's left sibling found a solution
      if (solutionFound) {
        return;
      }

      statesChecked += 1;
      if (statesChecked % 1000000 == 0) {
        System.out.println("DFS has checked " + statesChecked * 0.000001
          + " million states");
      }

      // Copy parent state into a new child state
      child = cloneState(state);

      // Mark cell with DFS hypothesis
      child.get(cellID).clear();
      child.get(cellID).add(v);

      // Trim peers
      markAndTrimPeers(cellID, v, child);

      // Check for cage conflicts (note that we don't need to check for
      // row/column conflicts since we previously called makeAndTrimPeers on the
      // HashSet we're iterating through)
      matrix = make2D(child);
      cagesSatisfied = true;
      for (Cage c : cages) {
        if (!cagesSatisfied) {
          break;
        }

        // Check this cage
        markedInCage = 0;
        for (Integer i : c.getCells()) {
          if (child.get(i).size() < 1) {
            // This should never occur but might if a wrong solution is given to
            // markAndTrimPeers
            cagesSatisfied = false;
            break;
          }
          if (child.get(i).size() == 1) {
            markedInCage += 1;
          }
        }
        if (markedInCage == c.getNumCells()) {
          if (!c.isSatisfied(matrix)) {
            cagesSatisfied = false;
            break;
          }
        }
      }
      if (!cagesSatisfied) {
        continue;
      }

      // If child is solution, stop. Otherwise, recurse DFS.
      if (isSolution(child)) {
        solution = child;
        solutionFound = true;
        return;
      }

      // TODO Seed DFS with the cell that has highest information gain
      int seed = firstSeedable(child);

      // Recursively call DFS
      if (seed != -1) {
        DFS(level + 1, seed, child);
      }
    }
  }

  /**
   * Mark the given cell, remove its value from its peers' sets of possible
   * values, and recursively continue marking peers whose sizes of sets of
   * possible values become 1.
   * 
   * @param cellID
   *          Cell to mark
   * @param value
   *          Value to mark
   * @param state
   *          Current state
   */
  private void markAndTrimPeers(int cellID, int value,
    HashMap<Integer, HashSet<Integer>> state) {
    // Make value the only possibility for this cell
    state.get(cellID).clear();
    state.get(cellID).add(value);

    int row = cellID / size;
    int col = cellID % size;
    int peerID;

    // Trim this cell's designated value from its peer cells
    // TODO Factor out the common loop bodies
    for (int i = 0; i < size; ++i) {
      peerID = row * size + i;
      if (peerID != cellID) {
        if (state.get(peerID).remove(value)) {
          if (state.get(peerID).size() == 1) {
            markAndTrimPeers(peerID, state.get(peerID).iterator().next(), state);
          }
        }
      }

      peerID = size * i + col;
      if (peerID != cellID) {
        if (state.get(peerID).remove(value)) {
          if (state.get(peerID).size() == 1) {
            markAndTrimPeers(peerID, state.get(peerID).iterator().next(), state);
          }
        }
      }
    }
  }

  private ArrayList<Integer>
    sortPeersByInformationGain(ArrayList<Integer> peers) {
    // TODO Make this actually do what it says on the tin
    return peers;
  }

  /**
   * Check whether all cells in the state have 1 possible value.
   * 
   * @param state
   * @return Whether state is a solution
   */
  private boolean isSolution(HashMap<Integer, HashSet<Integer>> state) {
    boolean allCellsMarked = true;
    for (HashSet<Integer> s : state.values()) {
      if (s.size() > 1) {
        allCellsMarked = false;
        break;
      }
    }
    return allCellsMarked;
  }

  // TODO Standardize the data representation so this conversion isn't necessary
  /**
   * Convert HashMap representation of state into 2D ArrayList.
   * 
   * @param state
   * @return 2D version of input state
   */
  private ArrayList<ArrayList<Integer>> make2D(
    HashMap<Integer, HashSet<Integer>> state) {
    ArrayList<ArrayList<Integer>> matrix = new ArrayList<ArrayList<Integer>>();
    for (int i = 0; i < size; ++i) {
      matrix.add(new ArrayList<Integer>());
      for (int j = 0; j < size; ++j) {
        matrix.get(i).add(
          (state.get(i * size + j).size() == 1) ? state.get(i * size + j)
            .iterator().next() : -1);
      }
    }
    return matrix;
  }

  private HashMap<Integer, HashSet<Integer>> cloneState(
    HashMap<Integer, HashSet<Integer>> state) {
    HashMap<Integer, HashSet<Integer>> clone =
      new HashMap<Integer, HashSet<Integer>>();
    HashSet<Integer> possibleValues;
    for (Integer i : state.keySet()) {
      possibleValues = new HashSet<Integer>();
      for (Integer j : state.get(i)) {
        possibleValues.add(j);
      }
      clone.put(i, possibleValues);
    }
    return clone;
  }

  private void printState(HashMap<Integer, HashSet<Integer>> state) {
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        System.out.print(state.get(i * size + j).iterator().next());
      }
      System.out.println("");
    }
  }

  // TODO Remove this function after information gain is implemented
  private int firstSeedable(HashMap<Integer, HashSet<Integer>> state) {
    for (int i = 0; i < size * size; ++i) {
      if (state.get(i).size() > 1) {
        return i;
      }
    }
    return -1;
  }
}