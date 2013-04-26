package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

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

  public boolean isSatisifed(ArrayList<ArrayList<Integer>> entryGrid) {
    ArrayList<Integer> elements = new ArrayList<Integer>();
    elements.add(entryGrid.get(getCellsPositions().get(0)).get(getCellsPositions().get(1)),
      entryGrid.get(getCellsPositions().get(2)).get(getCellsPositions().get(3)));
    if (Collections.max(elements) / Collections.min(elements) == getTotal()) {
      return true;
    } else {
      return false;
    }
  }

}
