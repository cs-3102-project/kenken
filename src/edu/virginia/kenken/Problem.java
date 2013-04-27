package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Random;

public class Problem {

  private final int size;
  private final ArrayList<ArrayList<Integer>> grid;
  private final ArrayList<ArrayList<Integer>> solution;
  private int numCages;
  private ArrayList<Cage> cages;
  private final ArrayList<Cage> cellCages;
  private final Random rand;

  public Problem(int size) {
    this.size = size;
    grid = new ArrayList<ArrayList<Integer>>();
    numCages = 0;
    cages = new ArrayList<Cage>();
    rand = new Random();
    cellCages =
      new ArrayList<Cage>(Collections.nCopies(size * size, new Cage()));
    solution = new ArrayList<ArrayList<Integer>>();

    // Start with a legal, non-random board

    for (int i = 0; i < size; ++i) {
      solution.add(new ArrayList<Integer>());
      for (int j = 0; j < size; ++j) {
        solution.get(i).add((i + j) % size + 1);
      }
    }

    // Shuffle rows

    Collections.shuffle(solution);

    // Transpose board matrix

    int tmp;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < i; ++j) {
        tmp = solution.get(i).get(j);
        solution.get(i).set(j, solution.get(j).get(i));
        solution.get(j).set(i, tmp);
      }
    }

    // Shuffle rows (which were the columns before transposition) again

    Collections.shuffle(solution);

    // Print matrix (for testing only)

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        System.out.print(solution.get(i).get(j));
      }
      System.out.print("\n");
    }

    // Initialize cageIDs

    for (int i = 0; i < size; ++i) {
      grid.add(new ArrayList<Integer>(Collections.nCopies(size, -1)));
    }

    ArrayList<String> directions = new ArrayList<String>();
    directions.add("N");
    directions.add("E");
    directions.add("S");
    directions.add("W");

    int curID = 0;
    int curX = -1;
    int curY = -1;
    int nextX = -1;
    int nextY = -1;

    int cageSize;
    int maxCageSize = -1;
    float cageCutoff;
    float opCutoff;

    boolean boardFull;
    boolean growable;

    // TODO Remove all references to sizeDistribution (it's just for testing)
    ArrayList<Integer> sizeDistribution = new ArrayList<Integer>();
    sizeDistribution.add(0);
    sizeDistribution.add(0);
    sizeDistribution.add(0);
    sizeDistribution.add(0);

    cages = new ArrayList<Cage>();
    Cage cage;

    // ArrayList used to keep track of which cells belong to the current cage
    ArrayList<Integer> cageCells = new ArrayList<Integer>();

    // Each iteration generates a new cage
    while (true) {
      cageCells.clear();
      // Select first available uncaged cell to be "root node" of new cage
      boardFull = true;
      for (int i = 0; i < size; ++i) {
        for (int j = 0; j < size; ++j) {
          if (grid.get(i).get(j) < 0) {
            curX = j;
            curY = i;
            boardFull = false;
            break;
          }
        }
        if (!boardFull) {
          break;
        }
      }

      // ...Unless all cells are caged already; then quit
      if (boardFull) {
        break;
      }

      // Predetermine the maximum number of cells this cage will contain,
      // assuming nothing gets in the way of its growth
      cageCutoff = rand.nextFloat();
      if (cageCutoff < 0.07) {
        maxCageSize = 1;
      } else if (cageCutoff < 0.55) {
        maxCageSize = 2;
      } else if (cageCutoff < 0.9) {
        maxCageSize = 3;
      } else {
        maxCageSize = 4;
      }

      // Add current cell to new cage
      cage = new Cage();

      // Add method is used for positioning of the cells based on ID. Do not
      // change!
      cage.add(curY * size + curX);
      cageCells.add(curY * size + curX);
      cage.addPosition(curY, curX);
      cage.addElement(solution.get(curY).get(curX));
      grid.get(curY).set(curX, curID);
      cageSize = 1;

      // Grow cage, cell by cell
      while (true) {
        // Stop when maximum cage size is reached
        if (cageSize >= maxCageSize) {
          break;
        }

        growable = false;

        // Randomly choose growth direction
        Collections.shuffle(directions);
        for (String s : directions) {
          switch (s) {
            case "N":
              nextX = curX;
              nextY = curY - 1;
              break;
            case "E":
              nextX = curX + 1;
              nextY = curY;
              break;
            case "S":
              nextX = curX;
              nextY = curY + 1;
              break;
            case "W":
              nextX = curX - 1;
              nextY = curY;
              break;
          }
          if (nextX >= 0 && nextX < size && nextY >= 0 && nextY < size) {
            if (grid.get(nextY).get(nextX) == -1) {
              growable = true;
              break;
            }
          }
        }

        // If next cell is valid, add it to cage and move to it
        if (growable && cageSize < maxCageSize) {
          cage.add(nextY * size + nextX);
          cageCells.add(nextY * size + nextX);
          cage.addPosition(nextY, nextX);
          cage.addElement(solution.get(nextY).get(nextX));
          grid.get(nextY).set(nextX, curID);
          curX = nextX;
          curY = nextY;
          cageSize += 1;
        } else {
          break;
        }
      }

      // Assign operator to cage
      Cage operationCage;
      switch (cage.getCells().size()) {
        case 1:
          operationCage = new UnitCage(cage);
          break;
        case 2:
          // TODO Make modulo/division fairer
          opCutoff = rand.nextFloat();
          if (opCutoff < 0.3) {
            operationCage = new SubtractionCage(cage);
          } else if (opCutoff < 0.6) {
            operationCage = new AdditionCage(cage);
          } else if (opCutoff < 1) {
            operationCage = new MultiplicationCage(cage);
          } else {
            if (rand.nextBoolean()) {
              operationCage = new ModuloCage(cage);
            } else {
              operationCage = new DivisionCage(cage);
            }
          }
          break;
        default:
          operationCage =
            (rand.nextBoolean() ? new MultiplicationCage(cage)
              : new AdditionCage(cage));
          break;
      }
      cages.add(operationCage);

      // Assign each cell, referenced by ID, to the appropriate cage
      for (Integer i : cageCells) {
        cellCages.set(i, operationCage);
      }

      sizeDistribution
        .set(cageSize - 1, sizeDistribution.get(cageSize - 1) + 1);
      curID += 1;
    }

    numCages = curID + 1;

    System.out.println("Number of cages: " + numCages);
    System.out.println("Cage size distribution: " + sizeDistribution);
  }

  public int getSize() {
    return size;
  }

  public ArrayList<ArrayList<Integer>> getGrid() {
    return grid;
  }

  public int getNumCages() {
    return numCages;
  }

  public ArrayList<Cage> getCages() {
    return cages;
  }

  public boolean checkGrid(ArrayList<ArrayList<Integer>> attempt) {
    // TODO Ensure rows and columns are also valid
    for (Cage c : cages) {
      if (!c.isSatisfied(attempt)) {
        return false;
      }
    }

    boolean generatedSolutionFound = true;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        if (attempt.get(i).get(j) != solution.get(i).get(j)) {
          generatedSolutionFound = false;
          break;
        }
      }
      if (!generatedSolutionFound) {
        break;
      }
    }

    if (generatedSolutionFound) {
      System.out.println("Generated solution found!");
    } else {
      System.out.println("Different solution found!");
    }
    return true;
  }

  // Method to check for valid row and columns
  public boolean checkRowAndColumn(ArrayList<ArrayList<Integer>> attempt) {
    // Create HashSet; when adding duplicates, the add method will return false
    HashSet<Integer> test = new HashSet<Integer>();

    // First check rows
    for (int i = 0; i < size; ++i) {
      test.clear();
      for (int j = 0; j < size; ++j) {
        if (!test.add(attempt.get(i).get(j))) {
          return false;
        }
      }
    }

    // Then check columns
    for (int i = 0; i < size; ++i) {
      test.clear();
      for (int j = 0; j < size; ++j) {
        if (!test.add(attempt.get(j).get(i))) {
          return false;
        }
      }
    }
    return true;
  }

  public ArrayList<Cage> getCellCages() {
    return cellCages;
  }

}
