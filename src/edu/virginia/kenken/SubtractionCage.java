package edu.virginia.kenken;

import java.util.Collections;

public class SubtractionCage extends Cage {
  public SubtractionCage(Cage src) {
    super(src);
    setTotal(Collections.max(getCells()) - Collections.min(getCells()));
  }
}
