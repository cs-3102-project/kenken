package edu.virginia.kenken;

import java.util.ArrayList;

public class MultiplicationCage extends Cage {
  public MultiplicationCage(Cage src) {
    super(src);
    int product = 1;
    for (Integer d : src.getCellElements()) {
      product *= d;
    }
    setTotal(product);
  }

  @Override
  public String getClueText() {
    return getTotal() + "x";
  }

  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    int guessProduct = 1;
    for (int i = 0; i < getCellPositions().size() / 2; i = i + 2) {
      guessProduct *=
        entryGrid.get(getCellPositions().get(i)).get(
          getCellPositions().get(i + 1));
    }
    if (guessProduct == getTotal()) {
      return true;
    } else {
      return false;
    }
  }
}
