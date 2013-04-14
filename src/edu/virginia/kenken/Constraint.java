package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

public class Constraint {
  private ArrayList<Integer> cells;
  private ArrayList<Integer> cellElements;

  public Constraint() {
    cells = new ArrayList<Integer>();
    cellElements = new ArrayList<Integer>();
  }

  public ArrayList<Integer> getCells() {
    return cells;
  }

  public void setCells(ArrayList<Integer> cells) {
    this.cells = cells;
  }

  public void add(Integer cellID) {
    cells.add(cellID);
    Collections.sort(cells);
  }

  public void addElement(Integer cellVal) {
    cellElements.add(cellVal);
  }

  public ArrayList<Integer> getCellElements() {
    return cellElements;
  }

}
