package edu.virginia.kenken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

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
  public void preprocess(int size, HashMap<Integer, HashSet<Integer>> state) {
    Iterator<Integer> it;
    int value;
    for (Integer cellID : getCells()) {
      it = state.get(cellID).iterator();
      while (it.hasNext()) {
        value = it.next();
        if (getTotal() % value > 0 || getTotal() <= value) {
          it.remove();
        }
      }
    }
    return;
  }

  @Override
  public boolean isSatisfiedHashMapVersion(
    HashMap<Integer, HashSet<Integer>> entryGrid, int size) {
    int guessProduct = 1;
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      guessProduct *=
        entryGrid
          .get(getCellPositions().get(i) * size + getCellPositions().get(i + 1))
          .iterator().next();
    }
    return (guessProduct == getTotal());
  }

  @Override
  public boolean isSatisfied(int size, HashMap<Integer, Integer> entryGrid) {
    int guessProduct = 1;
    for (int i = 0; i < getCellPositions().size(); i = i + 2) {
      guessProduct *=
        entryGrid.get(getCellPositions().get(i) * size
          + getCellPositions().get(i + 1));
    }
    return (guessProduct == getTotal());
  }
}
