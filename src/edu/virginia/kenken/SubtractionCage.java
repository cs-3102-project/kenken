package edu.virginia.kenken;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class SubtractionCage extends Cage {
  public SubtractionCage(Cage src) {
    super(src);
    setTotal(Collections.max(src.getCellElements())
      - Collections.min(src.getCellElements()));
  }

  @Override
  public String getClueText() {
    return getTotal() + "-";
  }

  @Override
  public void preprocess(int size, HashMap<Integer, HashSet<Integer>> state) {
    Iterator<Integer> it;
    int value;
    for (Integer cellID : getCells()) {
      it = state.get(cellID).iterator();
      while (it.hasNext()) {
        value = it.next();
        if (value > size - getTotal() && value <= getTotal()) {
          it.remove();
        }
      }
    }
    return;
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    return (Math.abs(entryGrid
      .get(getCellPositions().get(0) * size + getCellPositions().get(1))
      .iterator().next()
      - entryGrid
        .get(getCellPositions().get(2) * size + getCellPositions().get(3))
        .iterator().next()) == getTotal());
  }

  @Override
  public boolean isSatisfied(int size, HashMap<Integer, Integer> entryGrid) {
    return (Math.abs(entryGrid.get(getCellPositions().get(0) * size
      + getCellPositions().get(1))
      - entryGrid.get(getCellPositions().get(2) * size
        + getCellPositions().get(3))) == getTotal());
  }
}
