package edu.virginia.kenken;

import java.util.Collections;

public class DivisionCage extends Cage {
  public DivisionCage(Cage src) {
    super(src);
    setTotal(Collections.max(src.getCellElements())
      / Collections.min(src.getCellElements()));
  }

  @Override
  public String getClueText() {
    return getTotal() + "/";
  }
}
