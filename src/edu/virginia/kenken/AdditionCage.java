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

  public boolean isSatisifed(ArrayList<ArrayList<Integer>> entryGrid) {
    int guessSum = 0;
    for (int i = 0; i < getCellPositions().size() / 2; i = i + 2) {
      guessSum +=
        entryGrid.get(getCellPositions().get(i)).get(
          getCellPositions().get(i + 1));
    }
    if (guessSum == getTotal()) {
      return true;
    } else {
      return false;
    }
  }
}
