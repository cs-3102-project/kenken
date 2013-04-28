package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
  public void preprocess(HashMap<Integer, HashSet<Integer>> state) {
    Iterator<Integer> it;
    for (Integer cellID : getCells()) {
      it = state.get(cellID).iterator();
      while (it.hasNext()) {
        if (it.next() >= getTotal()) {
          it.remove();
        }
      }
    }
    return;
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    int guessSum = 0;
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      guessSum +=
        entryGrid
          .get(getCellPositions().get(i) * size + getCellPositions().get(i + 1))
          .iterator().next();
    }
    return (guessSum == getTotal());
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
