package edu.virginia.kenken;

import java.util.*;

public class DivisionCage extends Cage {
  public DivisionCage(Cage src) {
    super(src);
    setTotal(Collections.max(src.getCellElements())
      / Collections.min(src.getCellElements()));
  }

  @Override
  public String getClueText() {
    return getTotal() + "/";
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    ArrayList<Integer> elements = new ArrayList<Integer>();
    elements.add(
      entryGrid
        .get(getCellPositions().get(0) * size + getCellPositions().get(1))
        .iterator().next(),
      entryGrid
        .get(getCellPositions().get(2) * size + getCellPositions().get(3))
        .iterator().next());
    return (Collections.max(elements) / Collections.min(elements) == getTotal());
  }

  @Override
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    ArrayList<Integer> elements = new ArrayList<Integer>();
    elements.add(
      entryGrid.get(getCellPositions().get(0)).get(getCellPositions().get(1)),
      entryGrid.get(getCellPositions().get(2)).get(getCellPositions().get(3)));
    return (Collections.max(elements) / Collections.min(elements) == getTotal());
  }
}
