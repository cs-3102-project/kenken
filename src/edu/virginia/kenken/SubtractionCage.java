package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

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

  public boolean isSatisifed(ArrayList<ArrayList<Integer>> entryGrid) {

    int guessDifference =
      Math.abs(entryGrid.get(getCellsPositions().get(0)).get(getCellsPositions().get(1))
        - entryGrid.get(getCellsPositions().get(2)).get(getCellsPositions().get(3)));

    if (guessDifference == getTotal()) {
      return true;
    } else {
      return false;
    }
  }
}
