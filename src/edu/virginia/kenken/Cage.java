package edu.virginia.kenken;

public class Cage extends Constraint {
  private int total;

  public Cage() {
    super();
  }

  public Cage(Cage src) {
    super();
    setCells(src.getCells());
  }

  public String getClueText() {
    return Integer.toString(total);
  }

  public int getTotal() {
    return total;
  }

  public void setTotal(int total) {
    this.total = total;
  }
}
