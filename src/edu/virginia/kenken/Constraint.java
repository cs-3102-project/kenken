package edu.virginia.kenken;

import java.util.ArrayList;

public class Constraint {
  private ArrayList<Integer> cells;
  private ArrayList<Integer> cellElements;
  private ArrayList<Integer> cellPositions;

  public Constraint() {
    cells = new ArrayList<Integer>();
    cellElements = new ArrayList<Integer>();
    cellPositions = new ArrayList<Integer>();
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

  public void addPosition(Integer cellX, Integer cellY) {
    cellPositions.add(cellX);
    cellPositions.add(cellY);
  }

  public void addElement(Integer cellVal) {
    cellElements.add(cellVal);
  }

  public ArrayList<Integer> getCellElements() {
    return cellElements;
  }

  public ArrayList<Integer> getCellPositions() {
    return cellPositions;
  }

}
