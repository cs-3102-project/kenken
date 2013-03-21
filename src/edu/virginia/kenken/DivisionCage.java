package edu.virginia.kenken;

import java.util.Collections;

public class DivisionCage extends Cage {
  public DivisionCage(Cage src) {
    super(src);
    setTotal(Collections.max(getCells()) / Collections.min(getCells()));
  }

  @Override
  public String getClueText() {
    return getTotal() + "/";
  }
}
