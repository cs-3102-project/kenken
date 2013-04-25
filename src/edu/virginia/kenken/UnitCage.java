package edu.virginia.kenken;

public class UnitCage extends Cage {
  public UnitCage(Cage src) {
    super(src);
    setTotal(src.getCellElements().get(0));
  }

  @Override
  public String getClueText() {
    return getTotal() + "";
  }
}
