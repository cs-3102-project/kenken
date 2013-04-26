package edu.virginia.kenken;

import java.util.ArrayList;

public class AdditionCage extends Cage {
  public AdditionCage(Cage src) {
    super(src);
    int sum = 0;
    for (Integer d : src.getCellElements()) {
      sum += d;
    }
    setTotal(sum);
  }

  @Override
  public String getClueText() {
    return getTotal() + "+";
  }

  @Override
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    int guessSum = 0;
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      guessSum +=
        entryGrid.get(getCellPositions().get(i)).get(
          getCellPositions().get(i + 1));
    }
    return (guessSum == getTotal());
  }
}
