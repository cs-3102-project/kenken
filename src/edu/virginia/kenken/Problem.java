package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Problem {

  private final int size;
  private final ArrayList<ArrayList<Integer>> grid;
  private int numCages;
  private ArrayList<Cage> cages;

  private final Random rand;

  public Problem(int size) {
    this.size = size;
    grid = new ArrayList<ArrayList<Integer>>();
    numCages = 0;
    cages = new ArrayList<Cage>();
    rand = new Random();

    ArrayList<ArrayList<Integer>> cells = new ArrayList<ArrayList<Integer>>();

    // Start with a legal, non-random board

    for (int i = 0; i < size; ++i) {
      cells.add(new ArrayList<Integer>());
      for (int j = 0; j < size; ++j) {
        cells.get(i).add((i + j) % size + 1);
      }
    }

    // Shuffle rows

    Collections.shuffle(cells);

    // Transpose board matrix

    int tmp;
    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < i; ++j) {
        tmp = cells.get(i).get(j);
        cells.get(i).set(j, cells.get(j).get(i));
        cells.get(j).set(i, tmp);
      }
    }

    // Shuffle rows (which were the columns before transposition) again

    Collections.shuffle(cells);

    // Print matrix (for testing only)

    for (int i = 0; i < size; ++i) {
      for (int j = 0; j < size; ++j) {
        System.out.print(cells.get(i).get(j));
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

    cages = new ArrayList<Cage>(numCages);
    Cage cage;

    // Each iteration generates a new cage
    while (true) {
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
      cage.addPosition(curX, curY);
      cage.addElement(cells.get(curY).get(curX));
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
          cage.addPosition(nextX, nextY);
          cage.addElement(cells.get(nextY).get(nextX));
          grid.get(nextY).set(nextX, curID);
          curX = nextX;
          curY = nextY;
          cageSize += 1;
        } else {
          break;
        }
      }

      // Assign operator to cage
      switch (cage.getCellsPositions().size()) {
        case 1:
          cages.add(new UnitCage(cage));
          break;
        case 2:
          // TODO Make modulo/division fairer
          opCutoff = rand.nextFloat();
          if (opCutoff < 0.3) {
            cages.add(new SubtractionCage(cage));
          } else if (opCutoff < 0.6) {
            cages.add(new AdditionCage(cage));
          } else if (opCutoff < 1) {
            cages.add(new MultiplicationCage(cage));
          } else {
            if (rand.nextBoolean()) {
              cages.add(new ModuloCage(cage));
            } else {
              cages.add(new DivisionCage(cage));
            }
          }
          break;
        default:
          cages.add(rand.nextBoolean() ? new MultiplicationCage(cage)
            : new AdditionCage(cage));
          break;
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

  public boolean checkGrid(ArrayList<ArrayList<Integer>> gridToCheck) {
    for (Cage c : cages) {
      if (!c.isSatisfied(gridToCheck))
        return false;
    }
    return true;
  }
}
