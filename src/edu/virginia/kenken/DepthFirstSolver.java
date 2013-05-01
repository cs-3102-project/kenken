package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class DepthFirstSolver extends Solver {
  private final int size;
  private final ArrayList<Cage> cages;
  private boolean solutionFound;
  private HashMap<Integer, HashSet<Integer>> solution;
  private int statesChecked;
  private HashMap<Integer, Integer> gainScores;

  public DepthFirstSolver(GUI gui, Problem problem) {
    super(gui, problem);

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
      // TODO Make this iterate upwards (currently set to iterate downwards to
      // improve naive information gain, since large cell guesses typically fail
      // faster)
      for (int j = size; j > 0; --j) {
        root.get(i).add(j);
      }
    }
    // Get easy stuff done first - mark all UnitCages and recurse through
    // their peers, marking them if possible too
    for (Cage c : cages) {
      c.preprocess(size, root);
      if (c.getCells().size() == 1) {
        trimPeers(c.getCells().get(0), c.getTotal(), root);
      }
    }

    // Assign expected information gain scores to cells
    gainScores = new HashMap<Integer, Integer>();
    int operationScore = -1;
    for (Cage c : cages) {
      switch (c.getClass().getSimpleName()) {
        case "AdditionCage":
          operationScore = 35;
          break;
        case "DivisionCage":
          operationScore = 50;
          break;
        case "ModuloCage":
          operationScore = 35;
          break;
        case "MultiplicationCage":
          operationScore = 50;
          break;
        case "SubtractionCage":
          operationScore = 35;
          break;
        case "UnitCage":
          operationScore = -1;
          break;
        default:
          System.out.println("Wtf");
          break;
      }

      if (operationScore < 0) {
        continue;
      }

      for (Integer cellID : c.getCells()) {
        gainScores.put(cellID,
          (int) (operationScore - 12 * Math.pow(1.5, c.getNumCells() - 1)));
      }
    }

    // Call the root instance of DFS on the cell with highest info gain
    DFS(maxGain(root), root);

    if (solution == null) {
      System.out.println("No solution found.");
    } else {
      // Update display with current state
      getGUI().showProgress(solution);

      // HashMap<Integer, Integer> matrix = new HashMap<Integer, Integer>();
      // for (int i = 0; i < size; ++i) {
      // for (int j = 0; j < size; ++j) {
      // matrix.put(i * size + j, (solution.get(i * size + j).size() == 1)
      // ? solution.get(i * size + j).iterator().next() : -1);
      // }
      // }
      // getProblem().checkGrid(matrix);
      getProblem().checkGrid(solution);
      System.out.println("States checked: " + statesChecked);
    }
  }

  /**
   * Recursively called DFS algorithm - should be called only on undetermined
   * cells.
   * 
   * @param cellID
   * @param state
   */
  private void DFS(int cellID, HashMap<Integer, HashSet<Integer>> state) {
    // Check whether this is a solution
    if (solutionFound) {
      return;
    }

    // Loop through possible values for this cell
    int markedInCage;
    boolean cagesSatisfied;
    HashMap<Integer, HashSet<Integer>> child;

    for (Integer v : state.get(cellID)) {
      // Quit if this branch's left sibling found a solution
      if (solutionFound) {
        return;
      }

      statesChecked += 1;
      if (statesChecked % 4096 == 0) {
        // Update display with current state
        getGUI().showProgress(state);
      }

      // Copy parent state into a new child state
      child = cloneState(state);

      // Mark cell with DFS hypothesis
      child.get(cellID).clear();
      child.get(cellID).add(v);

      // Trim peers
      trimPeers(cellID, v, child);

      // Check for cage conflicts (note that we don't need to check for
      // row/column conflicts since we previously called makeAndTrimPeers on the
      // HashSet we're iterating through)
      cagesSatisfied = true;
      for (Cage c : cages) {
        if (!cagesSatisfied) {
          break;
        }

        // Check this cage
        markedInCage = 0;
        for (Integer i : c.getCells()) {
          if (child.get(i).size() < 1) {
            // This might occur if a wrong solution is given to trimPeers
            cagesSatisfied = false;
            break;
          }
          if (child.get(i).size() == 1) {
            markedInCage += 1;
          }
        }
        if (cagesSatisfied && markedInCage == c.getNumCells()) {
          if (!c.isSatisfiedHashMapVersion(child, size)) {
            cagesSatisfied = false;
            break;
          }
        }
      }
      if (!cagesSatisfied) {
        continue;
      }

      // Check whether child is solution
      if (isSolution(child)) {
        solution = child;
        solutionFound = true;
        return;
      }

      // Recursively call DFS
      DFS(maxGain(child), child);
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
  private void trimPeers(int cellID, int value,
    HashMap<Integer, HashSet<Integer>> state) {
    int row = cellID / size;
    int col = cellID % size;
    int peerID;

    // Trim this cell's designated value from its peer cells
    // TODO Factor out the common loop bodies
    for (int i = 0; i < size; ++i) {
      peerID = row * size + i;
      if (peerID != cellID) {
        if (state.get(peerID).remove(value)) {
          // Peer newly became determined, so trim *its* peers
          if (state.get(peerID).size() == 1) {
            trimPeers(peerID, state.get(peerID).iterator().next(), state);
          }
        }
      }

      peerID = size * i + col;
      if (peerID != cellID) {
        if (state.get(peerID).remove(value)) {
          // Peer newly became determined, so trim *its* peers
          if (state.get(peerID).size() == 1) {
            trimPeers(peerID, state.get(peerID).iterator().next(), state);
          }
        }
      }
    }
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

  private int maxGain(HashMap<Integer, HashSet<Integer>> state) {
    // for (int i = 0; i < size * size; ++i) {
    // if (state.get(i).size() > 1) {
    // return i;
    // }
    // }
    // return -1;
    int maxGain = -1;
    int cellID = -1;
    int gain;
    for (int i = 0; i < size * size; ++i) {
      if (state.get(i).size() > 1) {
        gain = gainScores.get(i) + 100 * (size - state.get(i).size()) / size;
        if (gain > maxGain) {
          maxGain = gain;
          cellID = i;
        }
      }
    }
    return cellID;
  }
}
