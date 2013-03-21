package edu.virginia.kenken;

import java.util.ArrayList;

public class Constraint {
  private ArrayList<Integer> cells;

  public Constraint() {
    cells = new ArrayList<Integer>();
  }

  public ArrayList<Integer> getCells() {
    return cells;
  }

  public void setCells(ArrayList<Integer> cells) {
    this.cells = cells;
  }

  public void add(Integer cellID) {
    cells.add(cellID);
  }
}
