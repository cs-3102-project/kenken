package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;

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
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    return (Math
      .abs(entryGrid.get(getCellPositions().get(0)).get(
        getCellPositions().get(1))
        - entryGrid.get(getCellPositions().get(2)).get(
          getCellPositions().get(3))) == getTotal());
  }
}
