package edu.virginia.kenken;

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
  public void preprocess(int size, HashMap<Integer, HashSet<Integer>> state) {
    Iterator<Integer> it;
    int minPossible = getTotal() - size * (getNumCells() - 1);
    int value;
    for (Integer cellID : getCells()) {
      it = state.get(cellID).iterator();
      while (it.hasNext()) {
        value = it.next();
        if (value >= getTotal() || value < minPossible) {
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
  public boolean isSatisfied(int size, HashMap<Integer, Integer> entryGrid) {
    int guessSum = 0;
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      guessSum +=
        entryGrid.get(getCellPositions().get(i) * size
          + getCellPositions().get(i + 1));
    }
    return (guessSum == getTotal());
  }
}
