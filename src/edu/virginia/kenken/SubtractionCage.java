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

  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {

    int guessDifference =
      Math.abs(entryGrid.get(getCellPositions().get(0)).get(
        getCellPositions().get(1))
        - entryGrid.get(getCellPositions().get(2)).get(
          getCellPositions().get(3)));

    if (guessDifference == getTotal()) {
      return true;
    } else {
      return false;
    }
  }
}
