package edu.virginia.kenken;

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
  public void preprocess(int size, HashMap<Integer, HashSet<Integer>> state) {
    state.get(getCells().get(0)).clear();
    state.get(getCells().get(0)).add(getTotal());
    return;
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    return (entryGrid
      .get(getCellPositions().get(0) * size + getCellPositions().get(1))
      .iterator().next() == getTotal());
  }

  @Override
  public boolean isSatisfied(int size, HashMap<Integer, Integer> entryGrid) {
    return (entryGrid.get(getCellPositions().get(0) * size
      + getCellPositions().get(1)) == getTotal());
  }
}
