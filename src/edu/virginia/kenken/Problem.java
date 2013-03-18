package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class Problem {

  private final int size;
  
  // specifies number of double cell cages
  private final int numDoubleBlocks = 20;

  public Problem(int size) {
    this.size = size;

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
    
    // creation of cages (only two cell blocks)
    // TODO Fill in the rest of the board with legal cages (>2 cells each)
    Random rand = new Random();
    ArrayList<ArrayList<Integer>> grid = new ArrayList<ArrayList<Integer>>(size);
    
    // initialize the arrayList
    for(int i = 0; i < size; ++i)
    {
      grid.add(new ArrayList<Integer>(Collections.nCopies(size, 0)));
    }
    
    int blockCount = 1;   // keeps tracks of different cages in the grid
    while (blockCount <= numDoubleBlocks)
    {
            
      // random vertical or horizontal insertion
      int currCell = rand.nextInt(size*size);
      while(grid.get(currCell/size).get(currCell%size) != 0)
      {
        currCell = rand.nextInt(size*size);
      }
      if(rand.nextBoolean()) {
        while(currCell%size == 0)           // don't want left edge
        {
          currCell = rand.nextInt(size*size);
        }
        if(grid.get(currCell/size).get(currCell%size - 1) == 0)
        {
          grid.get(currCell/size).set(currCell%size, blockCount);
          grid.get(currCell/size).set(currCell%size - 1, blockCount);
          ++blockCount;
        }
        else
        {
          continue;
        }
      }
      else
      {
        while(currCell/size == size - 1)    // don't want bottom row
        {
          currCell = rand.nextInt(size*size);
        }
        if(grid.get(currCell/size + 1).get(currCell%size) == 0)
        {
          grid.get(currCell/size).set(currCell%size, blockCount);
          grid.get(currCell/size + 1).set(currCell%size, blockCount);
          ++blockCount;
        }
        else
        {
          continue;
        }
      }
    }
    
    System.out.println("CAGES -- INCOMPLETE");
    for(int i = 0; i < size; ++i)
    {
      for(int j = 0; j < size; ++j)
      {
        System.out.print(grid.get(i).get(j)+"\t");
      }
      System.out.println();
    }
  }
  
  public int getSize() {
    return size;
  }

}
