package edu.virginia.kenken;

import java.util.Collections;

public class ModuloCage extends Cage {
  public ModuloCage(Cage src) {
    super(src);
    setTotal(Collections.max(getCells()) % Collections.min(getCells()));
  }

  @Override
  public String getClueText() {
    return getTotal() + "%";
  }
}
