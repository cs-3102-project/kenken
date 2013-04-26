package edu.virginia.kenken;

import java.util.ArrayList;

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

  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    return true;
  }
}
