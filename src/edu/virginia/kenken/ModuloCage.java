package edu.virginia.kenken;

import java.util.ArrayList;
import java.util.Collections;

public class ModuloCage extends Cage {
  public ModuloCage(Cage src) {
    super(src);
    setTotal(Collections.max(src.getCellElements())
      % Collections.min(src.getCellElements()));
  }

  @Override
  public String getClueText() {
    return getTotal() + "%";
  }

  @Override
  public boolean isSatisfied(ArrayList<ArrayList<Integer>> entryGrid) {
    ArrayList<Integer> elements = new ArrayList<Integer>();
    elements.add(
      entryGrid.get(getCellPositions().get(0)).get(getCellPositions().get(1)),
      entryGrid.get(getCellPositions().get(2)).get(getCellPositions().get(3)));
    return (Collections.max(elements) % Collections.min(elements) == getTotal());
  }

}
