package edu.virginia.kenken;

import java.util.ArrayList;

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
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    return (entryGrid.get(getCellPositions().get(0)).get(
      getCellPositions().get(1)) == getTotal());
  }
}
