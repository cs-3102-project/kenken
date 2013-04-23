package edu.virginia.kenken;

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
}
