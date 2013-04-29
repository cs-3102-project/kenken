package edu.virginia.kenken;

import java.util.ArrayList;

public class Constraint {
  private ArrayList<Integer> cells;
  private final ArrayList<Integer> cellElements;
  private ArrayList<Integer> cellPositions;

  public Constraint() {
    // Contains the cells (row-major) that this cage holds
    cells = new ArrayList<Integer>();
    // Note: cellElements is only applicable to cages that have not been
    // assigned to operations yet
    cellElements = new ArrayList<Integer>();
    // Stores the position of each cell in the cage in alternating col, row
    // order
    cellPositions = new ArrayList<Integer>();
  }

  public ArrayList<Integer> getCells() {
    return cells;
  }

  public void setCells(ArrayList<Integer> cells) {
    this.cells = cells;
  }

  public void setCellPositions(ArrayList<Integer> cellPositions) {
    this.cellPositions = cellPositions;
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

  public int getNumCells() {
    return cells.size();
  }

}
