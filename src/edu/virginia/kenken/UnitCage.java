package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class UnitCage extends Cage {
  public UnitCage(Cage src) {
    super(src);
    setTotal(src.getCellElements().get(0));
  }

  @Override
  public String getClueText() {
    return getTotal() + "";
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    return (entryGrid
      .get(getCellPositions().get(0) * size + getCellPositions().get(1))
      .iterator().next() == getTotal());
  }

  @Override
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    return (entryGrid.get(getCellPositions().get(0)).get(
      getCellPositions().get(1)) == getTotal());
  }
}
