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

  @Override
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    int guessProduct = 1;
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      guessProduct *=
        entryGrid.get(getCellPositions().get(i)).get(
          getCellPositions().get(i + 1));
    }
    return (guessProduct == getTotal());
  }
}
