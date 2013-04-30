package edu.virginia.kenken;

import java.util.HashMap;
import java.util.HashSet;

public class Cage extends Constraint {
  private int total;

  public Cage() {
    super();
  }

  public Cage(Cage src) {
    super();
    setCells(src.getCells());
    setCellPositions(src.getCellPositions());
  }

  public String getClueText() {
    return Integer.toString(total);
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }

  public void preprocess(HashMap<Integer, HashSet<Integer>> state) {
    return;
  }

  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> state, int size) {
    System.out.println("This was supposed to be abstract.");
    return false;
  }

  public boolean isSatisfied(int size, HashMap<Integer, Integer> entryGrid) {
    System.out.println("This was supposed to be abstract.");
    return false;
  }

  // TODO Make size a field instead of a parameter
  public boolean isFilled(int size, HashMap<Integer, Integer> entryGrid) {
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      if (entryGrid.get(getCellPositions().get(i) * size
        + getCellPositions().get(i + 1)) < 1) {
        return false;
      }
    }
    return true;
  }
}
