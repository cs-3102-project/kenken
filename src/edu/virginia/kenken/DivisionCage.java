package edu.virginia.kenken;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class DivisionCage extends Cage {
  public DivisionCage(Cage src) {
    super(src);
    setTotal(Collections.max(src.getCellElements())
      / Collections.min(src.getCellElements()));
  }

  @Override
  public void preprocess(int size, HashMap<Integer, HashSet<Integer>> state) {
    Iterator<Integer> it;
    int value;
    for (Integer cellID : getCells()) {
      it = state.get(cellID).iterator();
      while (it.hasNext()) {
        value = it.next();
        if (value * getTotal() > size && value > getTotal()
          && value % getTotal() > 0) {
          it.remove();
        }
      }
    }
    return;
  }

  @Override
  public String getClueText() {
    return getTotal() + "/";
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    int a =
      entryGrid
        .get(getCellPositions().get(0) * size + getCellPositions().get(1))
        .iterator().next();
    int b =
      entryGrid
        .get(getCellPositions().get(2) * size + getCellPositions().get(3))
        .iterator().next();
    return (Math.max(a, b) % Math.min(a, b) == 0 && Math.max(a, b)
      / Math.min(a, b) == getTotal());
  }

  @Override
  public boolean isSatisfied(int size, HashMap<Integer, Integer> entryGrid) {
    int a =
      entryGrid.get(getCellPositions().get(0) * size
        + getCellPositions().get(1));
    int b =
      entryGrid.get(getCellPositions().get(2) * size
        + getCellPositions().get(3));
    return (Math.max(a, b) / Math.min(a, b) == getTotal());
  }
}
